/* 
 * SMART FP7 - Search engine for MultimediA enviRonment generated contenT
 * Webpage: http://smartfp7.eu
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * The Original Code is Copyright (c) 2012-2013 of Atos 
 * All Rights Reserved
 *
 * Contributor(s):
 *  Sinan Yurtsever,
 *  Nines Sanguino, maria.sanguino at atos dot net
 */
 package eu.smartfp7.linkeddatamanager.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * Invoker that performs the invocation of a REST service.
 */
public class RestInvoker {
	
	private final static Logger log = Logger.getLogger(RestInvoker.class);
	
	public String executeService(Map<String, String> params, String serviceUrl, String method) {

		// TODO: Also method (GET, POST, PUT, DELETE)
    	if(method==null){
    		method = "GET";
    	}
    	
    	log.info("### Invoking Rest Service: " + serviceUrl);  	
    	String result = "Error executing service: " +serviceUrl;
    	
    	try {
    	    URL url = new URL(serviceUrl);
    	    HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
    	    connection.setRequestMethod(method);
    	    connection.setUseCaches(false);
    	    connection.setDoOutput(true);
    	    BufferedReader in = (new BufferedReader(new InputStreamReader(connection.getInputStream())));
    	    StringBuffer strbuf = new StringBuffer();
    	    String line;
    	    while ((line = in.readLine()) != null) {
    	    	strbuf.append(line + " ");
    	    }
    	    result = strbuf.toString();
    	   
    	} catch (Exception e) {
    	    e.printStackTrace();
       	}
		return result;
	}

}
