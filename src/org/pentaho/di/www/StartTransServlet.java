/*
 * Copyright (c) 2007 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the GNU Lesser General Public License, Version 2.1. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.gnu.org/licenses/lgpl-2.1.txt. The Original Code is Pentaho 
 * Data Integration.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the GNU Lesser Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.di.www;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.geospatial.SRS;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.srstransformation.SRSTransformationMeta;

public class StartTransServlet extends HttpServlet
{
    private static final long serialVersionUID = -5879200987669847357L;
    public static final String CONTEXT_PATH = "/startTrans";
    private static LogWriter log = LogWriter.getInstance();
    private static ErrorsHandler errorsHandler;

    private static Map<String, String[]> fileMetaClasses = new HashMap<String, String[]>()
    {
        {
            put("gml", new String[] {"org.pentaho.di.trans.steps.gmlfileinput.GMLFileInputMeta",
                    "org.pentaho.di.trans.steps.gmlfileoutput.GMLFileOutputMeta"});
            put("shp", new String[] {"org.pentaho.di.trans.steps.gisfileinput.GISFileInputMeta",
                    "org.pentaho.di.trans.steps.gisfileoutput.GISFileOutputMeta"});
            put("kml", new String[] {"org.pentaho.di.trans.steps.kmlfileinput.KMLFileInputMeta",
                    "org.pentaho.di.trans.steps.kmlfileoutput.KMLFileOutputMeta"});
        }
    };

    public StartTransServlet()
    {
    }

    private StepMeta getFileStepMeta(String stepName, String fileName, String format) throws IOException
    {
        try
        {
            Class<?> fileMetaClass = Class.forName(fileMetaClasses.get(format)[stepName == "input" ? 0 : 1]);
            Object fileMeta = fileMetaClass.newInstance();
            Method setFileNameMethod = fileMetaClass.getDeclaredMethod("setFileName", String.class);
            setFileNameMethod.invoke(fileMeta, new Object[] {fileName + "." + format});
            if (format == "shp")
            {
                Method setCharsetMethod = fileMetaClass.getDeclaredMethod("setGisFileCharset", String.class);
                setCharsetMethod.invoke(fileMeta, new Object[] {Config.defGisCharset});
            }
            return new StepMeta(stepName, (StepMetaInterface) fileMeta);
        } catch (Exception e)
        {
            errorsHandler.add("Reflection error", e.getMessage());
        }
        return null;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        if (!request.getContextPath().equals(CONTEXT_PATH))
            return;

        if (log.isDebug())
            log.logDebug(toString(), Messages.getString("StartTransServlet.Log.StartTransRequested"));

        errorsHandler = new ErrorsHandler(response, log, request.getHeader("Origin"));

        Map<String, String> params = new HashMap<String, String>();
        List<String> inFilesNames = new ArrayList<String>();
        String newTmpDir = System.getProperty("user.dir") + "\\" + Config.tmpFilesDir + CommonFunctions.randomString()
                + "\\";
        (new File(newTmpDir)).mkdir();
        (new File(newTmpDir + "out")).mkdir();

        try
        {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items)
                if (item.isFormField())
                    params.put(item.getFieldName(), item.getString());
                else
                {
                    String fileName = FilenameUtils.getName(item.getName());
                    if (fileName.equals(""))
                        continue;
                    InputStream fileContent = item.getInputStream();
                    String format = fileName.split("\\.")[1];
                    boolean isPermittedFormat = Arrays.asList(Config.basicFormats).contains(format);
                    for (Map.Entry<String, String[]> entry : Config.supportingFormats.entrySet())
                        isPermittedFormat = isPermittedFormat || Arrays.asList(entry.getValue()).contains(format);
                    if (!isPermittedFormat)
                        continue;

                    if (Arrays.asList(Config.basicFormats).contains(format))
                        inFilesNames.add(fileName);
                    File targetFile = new File(newTmpDir + fileName);
                    FileUtils.copyInputStreamToFile(fileContent, targetFile);
                }
        } catch (FileUploadException e)
        {
            errorsHandler.add("Request's params parsing error", e.getMessage());
        }
        if (params.get("in_type").equals("wfs"))
        {
            WFSClient.loadFeature(new URL(params.get("input")), newTmpDir);
            inFilesNames.add(Config.WFSTmpInFileName);
        }
        CommonFunctions.validateParams(params, inFilesNames, errorsHandler);
        try
        {
            if (errorsHandler.count() != 0)
                throw new Exception();

            TransMeta transMeta;
            transMeta = new TransMeta(Config.transFilesDir
                    + Config.transFiles[Integer.parseInt(params.get("trans_id"))]);

            StepMeta srsStep = transMeta.findStep("trans");
            SRSTransformationMeta srsMeta = (SRSTransformationMeta) srsStep.getStepMetaInterface();
            srsMeta.setSourceSRS(SRS.createFromEPSG(params.get("in_srs")));
            srsMeta.setTargetSRS(SRS.createFromEPSG(params.get("out_srs")));

            TransHopMeta inputToSrsHop = new TransHopMeta();
            inputToSrsHop.setEnabled();
            inputToSrsHop.setToStep(srsStep);
            transMeta.addTransHop(inputToSrsHop);

            String outFormat = Config.basicFormats[Integer.parseInt(params.get("out_format"))];
            TransHopMeta srsToOutputHop = new TransHopMeta();
            srsToOutputHop.setEnabled();
            srsToOutputHop.setFromStep(srsStep);
            transMeta.addTransHop(srsToOutputHop);

            Iterator<String> itr = inFilesNames.iterator();
            while (itr.hasNext())
            {
                String tmp = itr.next();
                String fileNameSufx = tmp.split("\\.")[0], inFormat = tmp.split("\\.")[1];
                if (!CommonFunctions.isSupportingFilesExists(inFormat, fileNameSufx, newTmpDir))
                    continue;

                StepMeta inputStep = getFileStepMeta("input", newTmpDir + fileNameSufx, inFormat);
                transMeta.addStep(inputStep);
                inputToSrsHop.setFromStep(inputStep);

                RowMetaInterface inputfields = transMeta.getStepFields("input");
                String[] fieldNames = inputfields.getFieldNamesAndTypes(100);

                Pattern p = Pattern.compile("(\\w+)\\s+\\(Geometry\\)");
                for (Integer i = 0; i < fieldNames.length; ++i)
                {
                    Matcher m = p.matcher(fieldNames[i]);
                    if (m.matches())
                    {
                        srsMeta.setFieldName(m.group(1));
                        break;
                    }
                }
                if (new File(newTmpDir + "out//" + fileNameSufx + "." + outFormat).exists())
                    fileNameSufx = CommonFunctions.randomString();
                StepMeta outputStep = getFileStepMeta("output", newTmpDir + "out//" + fileNameSufx, outFormat);
                transMeta.addStep(outputStep);
                srsToOutputHop.setToStep(outputStep);

                Trans trans = new Trans(transMeta);
                trans.execute(null);
                trans.waitUntilFinished();

                transMeta.removeStep(transMeta.indexOfStep(inputStep));
                transMeta.removeStep(transMeta.indexOfStep(outputStep));
            }
        } catch (KettleXMLException e)
        {
            errorsHandler.add("Trans file loading error", e.getMessage());
        } catch (KettleStepException e)
        {
            errorsHandler.add("Input file fields reading error", e.getMessage());
        } catch (KettleException e)
        {
            errorsHandler.add("Trans execution error", e.getMessage());
        } catch (Exception e)
        {
        }
        CommonFunctions.compressFiles(newTmpDir, errorsHandler);
        if (errorsHandler.count() == 0)
        {
            response.setContentType("application/x-please-download-me");
            response.setHeader("Content-Disposition", "attachment; filename=out.zip");
            ServletOutputStream out_file = response.getOutputStream();
            FileInputStream in = new FileInputStream(newTmpDir + "out.zip");
            IOUtils.copy(in, out_file);
            in.close();
            out_file.flush();
            out_file.close();
        } else
            errorsHandler.print();
        CommonFunctions.deleteDirectory(new File(newTmpDir));
    }

    public String toString()
    {
        return "Start transformation";
    }
}
