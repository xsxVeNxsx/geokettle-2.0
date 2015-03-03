package org.pentaho.di.www;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.trans.steps.gisfileinput.GISFileInputMeta;

public class GetTransListServlet  extends HttpServlet
{
	private static final long serialVersionUID = -305350993299364110L;

	public static final String CONTEXT_PATH = "/kettle/transList";
    
	private static final String[] transList = {"Перепроектирование"};
    private static LogWriter log = LogWriter.getInstance();
    private TransformationMap transformationMap;
	
    public GetTransListServlet(TransformationMap transformationMap)
    {
        this.transformationMap = transformationMap;
    }
    
    public void writeJSON(HttpServletResponse response, JSONArray json, String callbackName) throws IOException
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
 		JSONArray json = new JSONArray(Arrays.asList(transList));
 		writeJSON(response, json, request.getParameter("jsonp"));
    }
}
