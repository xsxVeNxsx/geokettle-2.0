package org.pentaho.di.www;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CommonFunctions
{
    public static void compressFiles(String dir, ErrorsHandler e) throws IOException
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
        if (children.length == 0)
            e.add("Compress output files error", "There are not output files");
        if (!(new File(dir + "out.zip").exists()))
            e.add("Compress output files error", "Has been not created out.zip");
    }
    
    public static String randomString()
    {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    public static void deleteDirectory(File dir)
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
        } else
            dir.delete();
    }

    public static boolean isSupportingFilesExists(String format, String name, String dir)
    {
        if (!Config.supportingFormats.containsKey(format))
            return true;
        for (Integer i = 0; i < Config.supportingFormats.get(format).length; ++i)
            if (!(new File(dir + name + "." + Config.supportingFormats.get(format)[i])).exists())
                return false;
        return true;
    }
    
    public static void validateParams(Map<String, String> params, List<String> files, ErrorsHandler e) throws IOException
    {
        for (Integer i = 0; i < Config.startTransRequestParams.length; ++i)
            if (!params.containsKey(Config.startTransRequestParams[i]))
                e.add("Bad request",
                        "There is no expected param " + params.containsKey(Config.startTransRequestParams[i]));
        if (files.size() == 0)
            e.add("Bad request", "There is no input files");
    }
}
