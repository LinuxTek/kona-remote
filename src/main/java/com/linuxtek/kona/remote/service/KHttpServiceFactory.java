/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.service;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

public abstract class KHttpServiceFactory {
    private static Logger logger = Logger.getLogger(KHttpServiceFactory.class);

    private HashMap<String,HttpInvokerProxyFactoryBean> proxyMap = 
        new HashMap<String,HttpInvokerProxyFactoryBean>();

    private String baseUrl = null;

    protected KHttpServiceFactory(String baseUrl) {
        this.baseUrl = baseUrl;
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
        //executor.setConnectTimeout(0);
        //executor.setReadTimeout(0);
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
}
