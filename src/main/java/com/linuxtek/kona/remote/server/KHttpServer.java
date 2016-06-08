/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.server;

import java.util.HashMap;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.SessionHandler;

import org.apache.log4j.Logger;

import com.linuxtek.kona.remote.servlet.KServiceServlet;

public class KHttpServer {

    private static Logger logger = Logger.getLogger(KHttpServer.class);

    Server server = null;
    Context context = null;

    public KHttpServer(Integer port, String contextPath, 
            String springContextConfig) {
        server = new Server(port);
        //context = new Context(server, contextPath, Context.SESSIONS);
        context = new Context(server, contextPath, true, true);

        logger.debug("setting context session handler ...");
        context.setSessionHandler(new SessionHandler());

        context.addServlet(KServiceServlet.class, "/*");
        HashMap<String,String> initParams = new HashMap<String,String>();
        initParams.put("contextConfigLocation", springContextConfig);
        context.setInitParams(initParams);

    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }


    /**
     * Starts Jetty web application server to handle RemoteService 
     * requests.  
     * <p>
     * Requires 3 params:
     * </p>
     * <ol>
     *  <li>port (e.g. 8080)</li>
     *  <li>context path (e.g. /)</li>
     *  <li>URI of spring context file (e.g. classpath:spring-context.xml)</li>
     * </ol>
     */
    public static void main(String args[]) {
        if (args.length != 3) {
            System.err.println("\nUsage: KHttpServer <port> "
                + "<context path> <springContextConfig>\n");

            System.exit(1);
        }

        Integer port = Integer.parseInt(args[0]);
        String contextPath = args[1];
        String springContextConfig = args[2];

        KHttpServer server = 
            new KHttpServer(port, contextPath, springContextConfig);

        try { server.start(); } catch (Exception e) { logger.error(e); }
    }
}
