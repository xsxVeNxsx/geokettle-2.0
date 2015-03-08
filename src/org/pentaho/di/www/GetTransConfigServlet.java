package org.pentaho.di.www;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.www.Config;

public class GetTransConfigServlet  extends HttpServlet
{
	private static final long serialVersionUID = -305350993299364110L;

	public static final String CONTEXT_PATH = "/transConfig";
    private static LogWriter log = LogWriter.getInstance();
    
    public void writeJSON(HttpServletResponse response, JSONObject json, String callbackName) throws IOException
    {
	    response.setContentType("text/javascript;charset=UTF-8");           
	    response.setHeader("Cache-Control", "no-cache");
	    PrintWriter out = response.getWriter();
	    out.print(callbackName + "(" + json + ")"); 
     }
	
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	if (!request.getContextPath().equals(CONTEXT_PATH)) return;
        
 		if (log.isDebug()) log.logDebug(toString(),  Messages.getString("TransListServlet.Log.TransListRequested"));
 		Map<String, String[]> toJson = new HashMap<String, String[]>() 
 		{{
 		    put("transNames", Config.transNames);
 		    put("basicFormats", Config.basicFormats);
 		}};
 		writeJSON(response, new JSONObject(toJson), request.getParameter("jsonp"));
    }
}
