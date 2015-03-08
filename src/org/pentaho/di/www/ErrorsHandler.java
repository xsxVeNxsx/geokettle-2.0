package org.pentaho.di.www;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.pentaho.di.core.logging.LogWriter;

public class ErrorsHandler 
{
    public static void error(HttpServletResponse response, LogWriter log,  Map<String, String> params, 
                                                                            Exception e) throws IOException
    {
        response.setContentType("text/javascript;charset=UTF-8");
        response.setHeader("Location", params.get("location"));
        PrintWriter out = response.getWriter();
        Map<String, String> toJson = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            toJson.put(entry.getKey(), entry.getValue());
        }
        out.print(new JSONObject(toJson)); 
        log.logBasic("StartTrans", e.getMessage());
        JSONArray jsonArr = new JSONArray();
        for (Integer i = 0; i < errors.size(); ++i)
            jsonArr.put(new JSONObject(errors.get(i)));
        String script = "window.parent.postMessage('" + jsonArr + "', '*');";
        out.print("<script>" + script + "</script>");
    }
}
