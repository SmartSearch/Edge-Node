/* 
 * SMART FP7 - Search engine for MultimediA enviRonment generated contenT
 * Webpage: http://smartfp7.eu
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * The Original Code is Copyright (c) 2012-2013 Athens Information Technology
 * All Rights Reserved
 *
 * Contributor:
 *  Nikolaos Katsarakis nkat@ait.edu.gr
 */
 
package eu.smartfp7.EdgeNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import eu.smartfp7.utils.Json;

/**
 * Servlet implementation class RetrieveXML
 */
@WebServlet("/retrieveXML")
public class RetrieveXML extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Hashtable<String, CouchDbClient> allClients;

	// CouchDB access parameters
	private int port;
	private String server, user, pass;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RetrieveXML() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			super.init(config);
		} catch (ServletException e) {
			System.err.println("Can not initialize servlet");
			return;
		}

		// Read couchdb properties from file and initialise the client for feeds
		Properties dbProps = new Properties();
		try {
			dbProps.load(getServletContext().getResourceAsStream("/WEB-INF/couchdb.properties"));
		} catch (IOException e1) {
			System.err.println("Can not open couchdb properties file");
			return;
		}

		server = dbProps.getProperty("server");
		port = Integer.parseInt(dbProps.getProperty("port"));
		user = dbProps.getProperty("user");
		pass = dbProps.getProperty("pass");

		// List of connections to databases (one client per)
		allClients = new Hashtable<String, CouchDbClient>();
		// Create one connection by default (to the database of feeds)
		allClients.put("feeds", new CouchDbClient("feeds", false, "http", server, port, user, pass));
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// Close all connections to CouchDb
		for (CouchDbClient client : allClients.values())
			client.shutdown();
		// Clear client list
		allClients.clear();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/xml;charset=UTF-8");

		PrintWriter out = response.getWriter();

		// Get the requested database
		String feedName = request.getParameter("feed");
		if (feedName != null) {
			feedName = feedName.toLowerCase();
			System.out.println("feed=" + feedName);
		} else {
			try {
				response.sendRedirect("retrieveXML.html");
			} catch (IOException e1) {
				System.out.println("doGet IOException: Can not redirect");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.println("<?xml version=\"1.0\"?><error>feed parameter not specified</error>");
			}
			return;
		}

		// Check if the database (corresponding feed) already exists
		if (!allClients.get("feeds").context().getAllDbs().contains(feedName)) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			out.println("<?xml version=\"1.0\"?><error>Feed " + feedName + " does not exist!</error>");
			return;
		}

		// Client for current request
		CouchDbClient curClient;
		if (!allClients.containsKey(feedName)) {
			// No request for this feed has arrived so far, add a new client to
			// the list
			curClient = new CouchDbClient(feedName, true, "http", server, port, user, pass);
			allClients.put(feedName, curClient);
		} else {
			// We've had requests for this feed before, set the corresponding
			// client as active
			curClient = allClients.get(feedName);
		}
		System.out.println("Connected to DB: " + curClient.context().info().getDbName());

		// Check if a maximum number of results has been specified
		String limitStr = request.getParameter("limit");
		Integer limit = 100;
		if (limitStr != null) {
			try {
				limit = Integer.parseInt(limitStr);
			} catch (NumberFormatException e) {
				// Ignore if improperly formatted
			}
		}

		if (!curClient.contains("_design/get_data")) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			out.println("<?xml version=\"1.0\"?><error>Design document for feed " + feedName
					+ " does not exist!</error>");
			return;
		}

		Long startMillis = null, endMillis = null;
		String startDate = request.getParameter("start_date");
		if (startDate != null) {
			try {
				startMillis = Long.parseLong(startDate);
			} catch (NumberFormatException e) {
				try {
					startMillis = javax.xml.bind.DatatypeConverter.parseDateTime(startDate).getTimeInMillis();
				} catch (IllegalArgumentException e1) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					out.println("<?xml version=\"1.0\"?><error>Could not parse start date</error>");
					return;
				}
			}
		}

		String endDate = request.getParameter("end_date");
		if (endDate != null) {
			try {
				endMillis = Long.parseLong(endDate);
			} catch (NumberFormatException e) {
				try {
					endMillis = javax.xml.bind.DatatypeConverter.parseDateTime(endDate).getTimeInMillis();
				} catch (IllegalArgumentException e1) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					out.println("<?xml version=\"1.0\"?><error>Could not parse end date</error>");
					return;
				}
			}
		}

		List<JsonObject> resList = null;

		String low_level = request.getParameter("low_level_events");
		if (low_level!=null)
		{
			try {
				curClient.view("get_data/with_low_level_event").limit(1).query(JsonObject.class);
			}
			catch (org.lightcouch.NoDocumentException e)
			{
				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				out.println("<?xml version=\"1.0\"?><error>Can not search feed '" + feedName + "' for low level events, ensure that the '_design/get_data' document contains a view named 'with_low_level_event'</error>");
				return;				
			}
		}
		String viewStr;
		if (low_level!=null)
			viewStr="get_data/with_low_level_event";
		else
			viewStr="get_data/by_date";

		if (startMillis != null && endMillis != null)
			resList = curClient.view(viewStr).startKey(startMillis).endKey(endMillis)
					.query(JsonObject.class);
		else if (startMillis == null && endMillis == null)
			resList = curClient.view(viewStr).limit(limit).descending(true).query(JsonObject.class);

		if (resList == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			out.println("<?xml version=\"1.0\"?><error>no data could be found</error>");
			return;
		}

		// Convert the response to an XML-RDF document
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < resList.size(); i++) {
			JsonObject item = resList.get(i);
			JsonElement el = item.get("value");
			String data = el.toString();
			if (low_level!=null)
			{
				String time = Common.millis2String(item.get("key").getAsLong());
				// Add time and a low_level_event element before each data
				data = "{\"time\":\"" + time + "\",\"low_level_event\": " + data + "}";
				if (i > 0)
					data = "," + data;
			}
			else
			{
				// Add a measurement element before each data
				if (i == 0)
					data = "\"measurement\": " + data;
				else
					data = ",\"measurement\": " + data;				
			}
			result.append(data);
		}

		if (low_level!=null)
		{
			// Surround the results with the rdf and measurement tags
			result.insert(0, "{\"rdf\": { \"measurement\": [");
			result.append("]}}");						
		}
		else
		{
			// Surround the result with the rdf tag
			result.insert(0, "{\"rdf\": {");
			result.append("}}");			
		}

		String finalres = Json.convertToXml(result.toString());
		out.print(finalres);
	}
}
