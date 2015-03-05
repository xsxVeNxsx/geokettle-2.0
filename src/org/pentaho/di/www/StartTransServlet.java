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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.mail.Part;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.di.www.Config;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.geospatial.SRS;
import org.pentaho.di.core.logging.Log4jStringAppender;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.gisfileinput.GISFileInputMeta;
import org.pentaho.di.trans.steps.gisfileoutput.GISFileOutputMeta;
import org.pentaho.di.trans.steps.gmlfileinput.GMLFileInputMeta;
import org.pentaho.di.trans.steps.gmlfileoutput.GMLFileOutputMeta;
import org.pentaho.di.trans.steps.kmlfileinput.KMLFileInputMeta;
import org.pentaho.di.trans.steps.kmlfileoutput.KMLFileOutputMeta;
import org.pentaho.di.trans.steps.srstransformation.SRSList;
import org.pentaho.di.trans.steps.srstransformation.SRSTransformationMeta;
import org.pentaho.di.trans.steps.srstransformation.SRSTransformator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;

import javax.servlet.ServletOutputStream;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;

import java.security.SecureRandom;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

import jdk.internal.util.xml.impl.Pair;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class StartTransServlet extends HttpServlet
{
    private static final long serialVersionUID = -5879200987669847357L;
    public static final String CONTEXT_PATH = "/kettle/startTrans";
    private static LogWriter log = LogWriter.getInstance();
    
    private static Map<String, String[]> fileMetaClasses = new HashMap<String, String[]>() 
    {{
    	put("gml", new String[] {"org.pentaho.di.trans.steps.gmlfileinput.GMLFileInputMeta",
		 						"org.pentaho.di.trans.steps.gmlfileoutput.GMLFileOutputMeta"});
    	put("shp", new String[] {"org.pentaho.di.trans.steps.gisfileinput.GISFileInputMeta",
	 							"org.pentaho.di.trans.steps.gisfileoutput.GISFileOutputMeta"});
    	put("kml", new String[] {"org.pentaho.di.trans.steps.kmlfileinput.KMLFileInputMeta",
		 						"org.pentaho.di.trans.steps.kmlfileoutput.KMLFileOutputMeta"});
    }};
    
    public StartTransServlet()
    {
    }
    
	public String randomString() 
	{
	    return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	public void deleteDirectory(File dir)
	{
        if (dir.isDirectory()) 
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; ++i) 
            {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }
	
	private boolean isSupportingFilesExists(String format, String name, String dir) 
	{
		if (!Config.supportingFormats.containsKey(format))
			return true;
		for (Integer i = 0; i < Config.supportingFormats.get(format).length; ++i)
			if (!(new File(dir + name + "." + Config.supportingFormats.get(format)[i])).exists())
				return false;
		return true;
	}
	
	private StepMeta getFileStepMeta(String stepName, String fileName, String format)
	{
		try {
	        Class<?> fileMetaClass = Class.forName(fileMetaClasses.get(format)[stepName == "input" ? 0 : 1]);
	        Object fileMeta = fileMetaClass.newInstance();
	        Method setFileNameMethod = fileMetaClass.getDeclaredMethod("setFileName", String.class);
	        setFileNameMethod.invoke(fileMeta, new Object[]{fileName + "." + format});
			return new StepMeta(stepName, (StepMetaInterface)fileMeta);
		} catch (Exception e) {}
		return null;
	}
	
	private void compressFiles(String dir) throws IOException
	{
	    String[] children = new File(dir + "out\\").list();
	    byte[] buffer = new byte[1024];
	    FileOutputStream fos = new FileOutputStream(dir + "out.zip");
	    ZipOutputStream zos = new ZipOutputStream(fos);
        for (int i = 0; i < children.length; ++i) 
        {
            ZipEntry ze = new ZipEntry(children[i]);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(dir + "out\\" + children[i]);
            int len;
            while ((len = in.read(buffer)) > 0)
                zos.write(buffer, 0, len);

            in.close();
            zos.closeEntry();
        }
	   
        zos.close();
	}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (!request.getContextPath().equals(CONTEXT_PATH)) return;
        
        if (log.isDebug()) log.logDebug(toString(), Messages.getString("StartTransServlet.Log.StartTransRequested"));
       
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String> inFilesNames = new HashMap<String, String>();
        String newTmpDir = System.getProperty("user.dir") + "\\" + Config.tmpFilesDir + randomString() + "\\";
        (new File(newTmpDir)).mkdir();
        
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items)
                if (item.isFormField()) 
                    params.put(item.getFieldName(), item.getString());
                else 
                {
                    String fileName = FilenameUtils.getName(item.getName());
                    InputStream fileContent = item.getInputStream();
                    String format = fileName.split("\\.")[1];
                    
                    boolean isPermittedFormat = Arrays.asList(Config.basicFormats).contains(format);
                    for (Map.Entry<String, String[]> entry : Config.supportingFormats.entrySet())
                    	isPermittedFormat = isPermittedFormat || Arrays.asList(entry.getValue()).contains(format);
                    if (!isPermittedFormat)
                    	continue;
                    
                    if (Arrays.asList(Config.basicFormats).contains(format))
                    	inFilesNames.put(format, fileName.split("\\.")[0]); 
                    File targetFile = new File(newTmpDir + fileName);
                    FileUtils.copyInputStreamToFile(fileContent, targetFile);
                }
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }
        
        TransMeta transMeta = null;
		try {
			transMeta = new TransMeta(transFilesDir + transFiles.get(Integer.parseInt(params.get("trans_id"))));
		} catch (KettleXMLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
        StepMeta srsStep = transMeta.findStep("trans");
		SRSTransformationMeta srsMeta = (SRSTransformationMeta)srsStep.getStepMetaInterface();	
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
		
        for (Map.Entry<String, String> entry : inFilesNames.entrySet())
        {
			if (!isSupportingFilesExists(entry.getKey(), entry.getValue(), newTmpDir))
				continue;
			String inFormat = entry.getKey();
			StepMeta inputStep = getFileStepMeta("input", newTmpDir + entry.getValue(), inFormat);
			transMeta.addStep(inputStep);
			inputToSrsHop.setFromStep(inputStep);
			
			
			try {
				RowMetaInterface inputfields = transMeta.getStepFields("input");
				String[] fieldNames	= inputfields.getFieldNamesAndTypes(100);
				
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
			} catch (KettleStepException e1) {
				e1.printStackTrace();
			}

			StepMeta outputStep = getFileStepMeta("output", newTmpDir + "out//" + entry.getValue(), outFormat);
			transMeta.addStep(outputStep);
			srsToOutputHop.setToStep(outputStep);

			try {
				Trans trans = new Trans(transMeta);
				trans.execute(null);
				trans.waitUntilFinished();
			} catch (KettleException e) {
				e.printStackTrace();
			}   
			transMeta.removeStep(transMeta.indexOfStep(inputStep));
			inputStep = null;
			transMeta.removeStep(transMeta.indexOfStep(outputStep));
			outputStep = null;
        }
        compressFiles(newTmpDir);
		response.setContentType("application/x-please-download-me");
		response.setHeader("Content-Disposition", "attachment; filename=out.zip");
        ServletOutputStream out_file = response.getOutputStream();
		FileInputStream in = new FileInputStream(newTmpDir + "out.zip");
		IOUtils.copy(in, out_file);
		in.close();
		out_file.flush();
		out_file.close();
		deleteDirectory(new File(newTmpDir));
    }

    public String toString()
    {
        return "Start transformation";
    }
}
