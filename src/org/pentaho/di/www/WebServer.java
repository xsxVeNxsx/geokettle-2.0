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

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.logging.LogWriter;

public class WebServer
{
    private static LogWriter log = LogWriter.getInstance();

    public static final int PORT = 80;

    private Server server;

    private TransformationMap transformationMap;
    private JobMap jobMap;
    private List<SlaveServerDetection> detections;
    private SocketRepository socketRepository;

    private String hostname;
    private int port;

    public WebServer(TransformationMap transformationMap, JobMap jobMap, SocketRepository socketRepository,
            List<SlaveServerDetection> detections, String hostname, int port, boolean join) throws Exception
    {
        this.transformationMap = transformationMap;
        this.jobMap = jobMap;
        this.socketRepository = socketRepository;
        this.detections = detections;
        this.hostname = hostname;
        this.port = port;

        startServer();

        // Start the monitoring of the registered slave servers...
        //
        startSlaveMonitoring();

        if (join)
        {
            server.join();
        }
    }

    public WebServer(TransformationMap transformationMap, JobMap jobMap, SocketRepository socketRepository,
            List<SlaveServerDetection> slaveServers, String hostname, int port) throws Exception
    {
        this(transformationMap, jobMap, socketRepository, slaveServers, hostname, port, true);
    }

    public Server getServer()
    {
        return server;
    }

    public void startServer() throws Exception
    {
        server = new Server();

        // Add all the servlets...
        //
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        // Start transformation
        //
        Context startTrans = new Context(contexts, StartTransServlet.CONTEXT_PATH, Context.SESSIONS);
        startTrans.addServlet(new ServletHolder(new StartTransServlet()), "/*");

        // Trans config
        //
        Context transList = new Context(contexts, GetTransConfigServlet.CONTEXT_PATH, Context.SESSIONS);
        transList.addServlet(new ServletHolder(new GetTransConfigServlet()), "/*");

        server.setHandlers(new Handler[] {contexts});

        // Start execution
        createListeners();

        server.start();
    }

    public void stopServer()
    {
        try
        {
            if (server != null)
            {
                // Clean up all the server sockets...
                //
                socketRepository.closeAll();

                // Stop the server...
                //
                server.stop();
            }
        } catch (Exception e)
        {
            log.logError(Messages.getString("WebServer.Error.FailedToStop.Title"),
                    Messages.getString("WebServer.Error.FailedToStop.Msg", "" + e));
        }
    }

    private void createListeners()
    {
        SocketConnector connector = new SocketConnector();
        connector.setPort(port);
        connector.setHost(hostname);
        connector.setName(Messages.getString("WebServer.Log.KettleHTTPListener", hostname));
        log.logBasic(toString(), Messages.getString("WebServer.Log.CreateListener", hostname, "" + port));

        server.setConnectors(new Connector[] {connector});
    }

    /**
     * @return the hostname
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * @param hostname
     *            the hostname to set
     */
    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * @return the slave server detections
     */
    public List<SlaveServerDetection> getDetections()
    {
        return detections;
    }

    /**
     * This method registers a timer to check up on all the registered slave
     * servers every X seconds.<br>
     */
    private void startSlaveMonitoring()
    {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask()
        {

            public void run()
            {
                for (SlaveServerDetection slaveServerDetection : detections)
                {
                    SlaveServer slaveServer = slaveServerDetection.getSlaveServer();

                    // See if we can get a status...
                    //
                    try
                    {
                        // TODO: consider making this lighter or retaining more
                        // information...
                        slaveServer.getStatus(); // throws the exception
                        slaveServerDetection.setActive(true);
                        slaveServerDetection.setLastActiveDate(new Date());
                    } catch (Exception e)
                    {
                        slaveServerDetection.setActive(false);
                        slaveServerDetection.setLastInactiveDate(new Date());

                        // TODO: kick it out after a configurable period of
                        // time...
                    }
                }
            }
        };
        timer.schedule(timerTask, 20000, 20000);

    }

    /**
     * @return the socketRepository
     */
    public SocketRepository getSocketRepository()
    {
        return socketRepository;
    }

    /**
     * @param socketRepository
     *            the socketRepository to set
     */
    public void setSocketRepository(SocketRepository socketRepository)
    {
        this.socketRepository = socketRepository;
    }
}
