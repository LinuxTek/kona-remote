/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.linuxtek.kona.remote.service.KService;



@SuppressWarnings("serial")
public class KServiceServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(KServiceServlet.class);
    
	private static KServiceServlet instance = null;

    private XmlWebApplicationContext context = null;
    
    public static KServiceServlet getInstance() {
    	return instance;
    }

	@Override
	public void init() {
        context = (XmlWebApplicationContext) WebApplicationContextUtils
                    .getWebApplicationContext(getServletContext());

		if (context == null) {
            // see if we have a contextConfigLocation context param
            ServletContext sc = getServletContext();
            String config = sc.getInitParameter("contextConfigLocation");

            if (config != null) {
                logger.debug("found config: " + config);
                XmlWebApplicationContext c = new XmlWebApplicationContext();
                c.setConfigLocation(config);
                c.afterPropertiesSet();
                /*
                c.getBeanFactory().registerScope("session", 
                        new SessionScope());
                c.getBeanFactory().registerScope("request", 
                        new RequestScope());
                */
                context = c;
            } else {
                logger.debug("no context config location found");
            }

        }

        if (context == null) {
			throw new IllegalStateException(
                "No Spring web application context found");
        }
        
        context.addApplicationListener(
        		new ApplicationListener<ContextRefreshedEvent>() {
            @Override
        	public void onApplicationEvent(ContextRefreshedEvent e) {
        		onContextRefreshed();
        	}
        });

        instance = this;
		logger.debug("KServiceServlet initialized");
	}
    
    
	protected void onContextRefreshed() {
		
	}

    protected XmlWebApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.debug("Got service request: "
            + "\nHttpServletRequest:\n" + req
            + "\nHttpServletResponse:\n" + resp);

        String servicePath = getServicePath(req);

        String contentType = req.getContentType();
        logger.debug("KServiceServlet: servicePath: {}  contentType: {}", servicePath, contentType);
    
        doInvoker(req, resp);
    }

    

    private HttpInvokerServiceExporter getExporter(Object bean) {
    	HttpInvokerServiceExporter exporter = null;
    	
    	Class<? extends Object> service = getServiceInterface(bean.getClass());

    	if (service == null) {
    		throw new IllegalStateException("Service interface not found for bean: " + bean);
    	}

    	logger.debug("Service interface: " + service.getName());

    	exporter = new HttpInvokerServiceExporter();
    	exporter.setServiceInterface(service);
    	exporter.setService(bean);
    	exporter.afterPropertiesSet();
    	return exporter;
    }


    protected void doInvoker(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.debug("KServletService: doInvoker called ...");
        
        Object bean = getContextBean(context, req);
        HttpInvokerServiceExporter exporter = getExporter(bean);
        
        logger.debug("doInvoker: got exporter object: " + exporter);
        
        try {
        	logger.debug("doInvoker: calling exporter.handleRequest for service: " 
        		+ exporter.getServiceInterface().getName());
        	exporter.handleRequest(req, resp);
        } catch (Throwable t) {
        	//logger.error(t,t);
            throw new IOException(t.getMessage(), t);
        }
        
        logger.debug("exporter.handleRequest() completed for service: " + exporter.getServiceInterface().getName());
	}



	/**
	 * Parse the service name from the request URL.
	 * 
	 * @param request
	 * @return bean name
	 */
	public static String getServicePath(HttpServletRequest request) {
		String url = request.getRequestURI();

        String service = request.getPathInfo();
        try {
            service = URLDecoder.decode(service, "UTF-8");
        } catch (UnsupportedEncodingException e) { 
        	logger.error(e.getMessage(), e); 
        }

        if (service.startsWith("/")) {
            service = service.substring(1);
        }

        // This is a hack to handle the case where the context-path
        // of this servlet includes the "rpc/" prefix (e.g. "/kona/rpc/*")
        // and so the service name doesn't contain the prefix.
        if (!service.startsWith("rpc/")) {
            service = "rpc/" + service;
        }

        logger.debug("Service for URL [" + url + "] is: " + service);
		return service;
	}

	/**
	 * Look up a spring bean with the specified name in the current web
	 * application context.
	 * 
	 * @param servicePath
	 *            bean servicePath
	 * @return the bean
	 */
	public Object getBean(HttpServletRequest req, String servicePath) {
        return getContextBean(context, servicePath);
	}
    
	/**
	 * Determine Spring bean to handle request based on request URL, e.g. a
	 * request ending in /myService will be handled by bean with name
	 * "myService".
	 * 
	 * @param request
	 * @return handler bean
	 */
	public static Object getContextBean(XmlWebApplicationContext context, HttpServletRequest req) {
		String servicePath = getServicePath(req);
		return getContextBean(context, servicePath);
	}
    
	public static Object getContextBean(String servicePath) {
		XmlWebApplicationContext context = instance.getApplicationContext();
        return getContextBean(context, servicePath);
	}
    
	public static Object getContextBean(XmlWebApplicationContext context, String servicePath) {
        Object bean = null;

        if (!context.isActive()) {
            logger.debug("Context is not active; refreshing context ...");
        	context.refresh();
        }
        
        if (context.containsBean(servicePath)) {
            bean = context.getBean(servicePath);
        } 

        /*
        if (bean instanceof KServiceRequest) {
            KServiceRequest service = (KServiceRequest) bean;
            return (service);
        }
        */

        if (bean == null) {
			throw new IllegalArgumentException(
                "Spring bean not found: " + servicePath);
        }
        
    	if (!(bean instanceof KService)) {
			throw new IllegalArgumentException(
                "Spring bean is not a KService: " 
                + servicePath + " (" + bean + ")");
        }
    	
    	logger.debug("Bean for service path [" + servicePath + "]: " + bean);

		return bean;
	}

    private Class<? extends Object> getServiceInterface(Class<?> clazz) {
        logger.debug("getServiceInterface: checking interfaces for class: " + clazz.getName());

        String interfaceName = "com.linuxtek.kona.remote.service.KService";

        Class<?> result = null;
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> c : interfaces) {
            if (!c.getName().equals(interfaceName)) {
                return getServiceInterface(c);
            } else {
                result = clazz;
                break;
            }
        }
        
        return result;
    }
}
