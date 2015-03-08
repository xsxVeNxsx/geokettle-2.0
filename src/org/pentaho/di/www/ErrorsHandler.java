package org.pentaho.di.www;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pentaho.di.core.logging.LogWriter;

public class ErrorsHandler
{
    private List<Map<String, String>> errors = new ArrayList<Map<String, String>>();
    private HttpServletResponse response;
    private LogWriter log;
    private String requestOrigin;

    public ErrorsHandler(HttpServletResponse _response, LogWriter _log, String _requestOrigin)
    {
        response = _response;
        log = _log;
        requestOrigin = _requestOrigin;
    }

    public Integer count()
    {
        return errors.size();
    }

    public void add(String errorTitle, String errorText) throws IOException
    {
        Map<String, String> newError = new HashMap<String, String>();
        newError.put("ErrorTitle", errorTitle);
        newError.put("ErrorText", errorText);
        errors.add(newError);
    }

    public void print() throws IOException
    {
        Iterator<Map<String, String>> itr = errors.iterator();
        while (itr.hasNext())
        {
            Map<String, String> tmp = itr.next();
            log.logError(tmp.get("ErrorTitle"), tmp.get("ErrorText"));
        }
        PrintWriter out = response.getWriter();
        JSONArray jsonArr = new JSONArray();
        for (Integer i = 0; i < errors.size(); ++i)
            jsonArr.put(new JSONObject(errors.get(i)));
        String script = "window.parent.postMessage('" + jsonArr + "', '*');";
        out.print("<script>" + script + "</script>");
    }
}
