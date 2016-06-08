/*
 * Copyright (C) 2015 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

/**
 * 
 */
public class KBaseServiceFactory {
	private static Logger logger = Logger.getLogger(KBaseServiceFactory.class);
    
    private Map<String,Object> serviceCache = null;
    
    private HashMap<String,HttpInvokerProxyFactoryBean> proxyMap = 
            new HashMap<String,HttpInvokerProxyFactoryBean>();

    private static KBaseServiceFactory factory = null;

    private String basePackage;
    private String baseUrl = null;

    protected KBaseServiceFactory(String baseUrl) {
        this.baseUrl = baseUrl;
        logger.debug("ServiceFactory created: baseUrl: " + baseUrl);
        serviceCache = new HashMap<String,Object>();
    }
    
    protected KBaseServiceFactory(String baseUrl, String basePackage) {
        this(baseUrl);
        this.basePackage = basePackage;
    }

    public static KBaseServiceFactory getInstance(String baseUrl) {
        if (factory == null || !factory.getBaseUrl().equals(baseUrl))
            factory = new KBaseServiceFactory(baseUrl);
        return (factory);
    }
    
    public static KBaseServiceFactory getInstance(String baseUrl, String basePackage) {
        factory = getInstance(baseUrl);
        factory.setBasePackage(basePackage);
        return factory;
        
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    
    public String getBaseUrl() {
        return (baseUrl);
    }
    
    @SuppressWarnings("rawtypes")
	protected HttpInvokerProxyFactoryBean getProxy(String serviceName,
            Class serviceInterface) {
        HttpInvokerProxyFactoryBean proxy = proxyMap.get(serviceName);
        
        if (proxy == null) {
            proxy = new HttpInvokerProxyFactoryBean();
            String serviceUrl = baseUrl + serviceName;
            proxy.setServiceUrl(serviceUrl);
            proxy.setServiceInterface(serviceInterface);
            proxy.setHttpInvokerRequestExecutor(getHttpInvokerRequestExecutor());
            proxy.afterPropertiesSet();
            proxyMap.put(serviceName, proxy);
        }
        return (proxy);
    }
    
    private HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
        HttpComponentsHttpInvokerRequestExecutor executor =
        		new HttpComponentsHttpInvokerRequestExecutor();
        executor.setConnectTimeout(0);
        executor.setReadTimeout(0);
        return executor;
    }

    protected <I extends KService> I getService(String serviceName,
            Class<I> serviceInterface) {
        I service = null;

        HttpInvokerProxyFactoryBean proxy = 
            getProxy(serviceName, serviceInterface);

        if (proxy != null) {
            service = serviceInterface.cast(proxy.getObject());
        }

        if (service == null) {
            throw new IllegalStateException(
                "Unable to instantiate service: " + serviceName);
        }

        logger.debug("fetching service [" + serviceName + "]: " + service);

        return (service);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends KService> T getService(String name) {
        Object o = serviceCache.get(name);
        if (o != null) {
            return (T) o;
        }
        
        try {
            Class<? extends KService> c = getServiceClass(name);
            Field f = c.getField("SERVICE_PATH");
            String servicePath = (String) f.get(null);
            o = getService(servicePath, c);
            serviceCache.put(name, o);
        } catch (ClassNotFoundException e) {
            logger.error(e);
        } catch (NoSuchFieldException e) {
            logger.error(e);
        } catch (SecurityException e) {
            logger.error(e);
        } catch (IllegalArgumentException e) {
            logger.error(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
        return (T) o;
    }
    
    @SuppressWarnings("unchecked")
    private Class<? extends KService> getServiceClass(String name) throws ClassNotFoundException {
        if (basePackage == null) {
            basePackage = this.getClass().getPackage().getName();
        }
        
        Class<? extends KService> c = null;
        try {
            //logger.debug("checking class for service name: " + name);
            c = (Class<? extends KService>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            if (basePackage != null) {
                name = basePackage + "." + name;
                //logger.debug("got exception: checking class for service name: " + name);
                c = (Class<? extends KService>) Class.forName(name);
            }
        }
        if (c == null) {
            //logger.debug("no class found for name: " + name);
        }
        return c;
    }
}
