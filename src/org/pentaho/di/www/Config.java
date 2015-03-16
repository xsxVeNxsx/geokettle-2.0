package org.pentaho.di.www;

import java.util.HashMap;
import java.util.Map;

public class Config
{
    public static final String[] transNames = {"Перепроектирование"};
    public static final String[] transFiles = {"srs.ktr"};
    public static final String[] basicFormats = {"gml", "shp", "kml", "tab"};
    public static final Map<String, String> orgDataFormats = new HashMap()
    {
        {
            put("gml", "GML");
            put("kml", "KML");
            put("shp", "ESRI Shapefile");
            put("tab", "MapInfo File");
        }
    };
    public static final String[] startTransRequestParams = {"in_type", "in_srs", "out_srs", "out_format", "trans_id"};
    public static final String defGisCharset = "windows-1251";
    public static final Map<String, String[]> supportingFormats = new HashMap<String, String[]>()
    {
        {
            put("shp", new String[] {"dbf", "shx"});
            put("tab", new String[] {"dat", "map", "id"});
        }
    };
    public static final String tmpFilesDir = "tmp\\";
    public static final String transFilesDir = "dvo_trans\\";
    public static final String compressComand = "C:\\Program Files (x86)\\WinRAR\\rar.exe A -ep1 %sout.rar %sout\\*";
    public static final String WFSTmpInFileName = "WFS_IN.gml";
}
