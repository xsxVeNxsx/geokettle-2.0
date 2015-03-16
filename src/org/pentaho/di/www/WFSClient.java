package org.pentaho.di.www;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.geotools.GML;
import org.geotools.GML.Version;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;

public class WFSClient
{
    public static boolean loadFeature(URL url, String fileDir) throws UnsupportedEncodingException
    {
        Map<String, String> query_pairs = new HashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs)
        {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8").toLowerCase(),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        WFSDataStoreFactory dsf = new WFSDataStoreFactory();
        String getCapabilities = "http://" + url.getHost() + ":" + Integer.toString(url.getPort()) + url.getPath()
                + "?service=wfs&request=GetCapabilities&version=" + query_pairs.get("version");
        Map<String, String> connectionParameters = new HashMap<String, String>();
        connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilities);
        connectionParameters.put("WFSDataStoreFactory:OUTPUTFORMAT",
                "text/xml; subType=gml/3.1.1/profiles/gmlsf/1.0.0/0");

        try
        {
            WFSDataStore dataStore = dsf.createDataStore(connectionParameters);
            SimpleFeatureSource source = dataStore.getFeatureSource(query_pairs.get("typename"));
            SimpleFeatureCollection fc = source.getFeatures();
            OutputStream out = new FileOutputStream(fileDir + Config.WFSTmpInFileName);
            GML encode = new GML(query_pairs.get("version") == "1.0.0" ? Version.WFS1_0 : Version.WFS1_1);
            String namespace = fc.getSchema().getName().getNamespaceURI();
            encode.setNamespace("pref", namespace);
            encode.encode(out, fc);
            out.close();
        } catch (IOException ex)
        {
            System.out.print(ex.getMessage());
            return false;
        }
        return true;
    }
}
