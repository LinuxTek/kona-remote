/*
 * Copyright (C) 2011 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.servlet;

import java.io.Serializable;
import java.lang.reflect.Field;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.linuxtek.kona.remote.service.KServiceException;


/*
	http://stackoverflow.com/questions/9668420/spring-remoting-http-invoker-exception-handling
    
<bean id="exceptionTranslatorInterceptor" class="com.YOURCOMPANY.interceptor.ServiceExceptionTranslatorInterceptor"/>

<bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
    <property name="beanNames" value="YOUR_SERVICE" />
    <property name="order" value="1" />
    <property name="interceptorNames">
        <list>
            <value>exceptionTranslatorInterceptor</value>
        </list>
    </property>
</bean>
*/
public class KServiceExceptionTranslatorInterceptor 
		implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 1L;

	@Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            throw translateException(e);
        }
    }

    static RuntimeException translateException(Throwable e) {
        KServiceException serviceException = new KServiceException();

        try {
            serviceException.setStackTrace(e.getStackTrace());
            serviceException.setMessage(e.getClass().getName() +
                    ": " + e.getMessage());
            getField(Throwable.class, "detailMessage").set(serviceException, 
                    e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                getField(Throwable.class, "cause").set(serviceException,
                        translateException(cause));
            }
        } catch (IllegalArgumentException e1) {
            // Should never happen, ServiceException is an instance of Throwable
        } catch (IllegalAccessException e2) {
            // Should never happen, we've set the fields to accessible
        } catch (NoSuchFieldException e3) {
            // Should never happen, we know 'detailMessage' and 'cause' are
            // valid fields
        }
        return serviceException;
    }

    static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field f = clazz.getDeclaredField(fieldName);
        if (!f.isAccessible()) {
            f.setAccessible(true);
        }
        return f;
    }

}
