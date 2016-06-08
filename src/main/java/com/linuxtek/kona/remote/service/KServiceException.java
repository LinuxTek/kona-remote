/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.service;

import java.util.Map;
import java.util.LinkedHashMap;
import java.io.Serializable;

/**
 * KServiceException.
 */

@SuppressWarnings("serial")
public class KServiceException extends RuntimeException 
    implements Serializable {

    private String message = null;

    private Map<String,String> errorMap = 
        new LinkedHashMap<String,String>();

    public KServiceException() {
    }

    public KServiceException(String message) {
        super(message);
        addError("_SYSTEM", message);
    }

    public KServiceException(String message, Map<String,String> errorMap) {
        super(message);
        this.errorMap = errorMap;
    }

    public KServiceException(String ex, Throwable cause) {
        super(ex, cause);
    }

    public KServiceException(Throwable cause) {
        super(cause);
    }

    public Map<String,String> getErrorMap() {
        return errorMap;
    }

    public void addError(String field, String error) {
        errorMap.put(field, error);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
