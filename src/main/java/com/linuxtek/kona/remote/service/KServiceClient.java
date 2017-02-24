/*
 * Copyright (C) 2012 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.service;

import java.io.Serializable;

public class KServiceClient implements Serializable {
	private static final long serialVersionUID = 1L;
    
	private Long appId = null;
	private Long userId = null;
	private String clientId = null;
	private String deviceUuid = null;
	private String hostname = null;
	private String browser = null;
	private Double latitude = null;
	private Double longitude = null;
	private String accessToken = null;
	private String sessionId = null;
	private String requestUrl = null;
    private String appVersion = null;
    private String appBuild = null;
    
	public Long getAppId() {
		return appId;
	}

	
	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
    
	public String getDeviceUuid() {
		return deviceUuid;
	}
    
	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}
    
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getAppBuild() {
		return appBuild;
	}
	public void setAppBuild(String appBuild) {
		this.appBuild = appBuild;
	}
	
    
}
