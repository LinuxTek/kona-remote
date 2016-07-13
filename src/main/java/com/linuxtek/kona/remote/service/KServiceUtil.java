/*
 * Copyright (C) 2012 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.remote.service;

import javax.servlet.http.HttpServletRequest;

import com.linuxtek.kona.http.KServletUtil;

public class KServiceUtil {

	public static KServiceClient getServiceClient(HttpServletRequest req) {
		KServiceClient client = new KServiceClient();
		client.setAccessToken(KServletUtil.getAccessToken(req));
		client.setSessionId(KServletUtil.getSessionId(req));
		client.setHostname(KServletUtil.getClientHostname(req));
		client.setBrowser(KServletUtil.getClientBrowser(req));
		client.setLatitude(KServletUtil.getClientLatitude(req));
		client.setLongitude(KServletUtil.getClientLongitude(req));
		client.setRequestUrl(KServletUtil.getFullRequestURL(req));
		return client;
	}

}
