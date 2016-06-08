/*
 * Copyright (C) 2013 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Associates a {@link KService} with a relative path. 
 * 
 * Based on com.google.gwt.user.client.rpc.RemoteServiceRelativePath
 */
@Documented
@Target(ElementType.TYPE)
public @interface KServiceRelativePath {
  /**
   * The relative path for the {@link KService} implementation.
   * 
   * @return relative path for the {@link KService} implementation
   */
  String value();
}
