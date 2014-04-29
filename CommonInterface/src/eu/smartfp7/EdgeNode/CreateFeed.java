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
 *  Menelaos Bakopoulos mbak@ait.edu.gr
 */
 
package eu.smartfp7.EdgeNode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.xml.sax.SAXException;

import eu.smartfp7.utils.Json;
import eu.smartfp7.utils.Xml;

/**
 * Servlet implementation class CreateFeed
 */
@WebServlet("/createFeed")
public class CreateFeed extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// Members for XML parsing and validation
	private Validator validator = null;

	// CouchDB access parameters
	private CouchDbClient dbClient = null;
	private String server, user, pass;
	private int port;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateFeed() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) {
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
		// Test for incorrect user/pass or CouchDB server offline
		try {
			dbClient = new CouchDbClient("feeds", true, "http", server, port, user, pass);
		} catch (Exception e) {
			System.out
					.println("Could not connect to CouchDB, check that the server is running and that the correct username/pass is set");
			System.out.println("Current configuration: server=" + server + ", port=" + port + ", user= " + user
					+ ", pass= " + pass);
			return;
		}

		// Initialise the XSD validator
		SchemaFactory factory = null;
		Schema schema = null;

		// 1. Lookup a factory for the W3C XML Schema language
		factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		if (factory == null) {
			System.err.println("Cannot initialize schema factory");
			return;
		}

		// 2. Compile the schema.
		try {
			schema = factory.newSchema(getServletContext().getResource("/SMART_Datafeed_Schema_v0.3.xsd"));
		} catch (SAXException e) {
			System.err.println("Cannot read XSD file");
		} catch (MalformedURLException e) {
			System.err.println("Invalid URL");
		} catch (NullPointerException npe) {
			System.err.println("Could not find required xsd file");
		}

		if (schema != null) {
			// 3. Get a validator from the schema.
			validator = schema.newValidator();
		}
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		if (dbClient != null)
			dbClient.shutdown();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect("createFeed.html");
		} catch (IOException e1) {
			System.out.println("doGet IOException: Can not redirect");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		response.setContentType("application/xml;charset=UTF-8");

		System.out.println("CreateFeedPost called");

		try {
			out = response.getWriter();
		} catch (IOException e1) {
			System.err.println("Can not open response writer");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		if (dbClient == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("<xml><status>Error</status><description>Could not connect to CouchDB, check that the server is running and that the correct username/pass is set</description></xml>");
			return;
		}

		if (validator == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("<xml><status>Error</status><description>Could load xml validator</description></xml>");
			return;
		}

		// Store the request to a StringBuilder in order to reuse it
		StringBuilder reqStrBuilder = new StringBuilder();
		try {
			BufferedReader reader = request.getReader();
			char[] buf = new char[4 * 1024]; // 4 KB char buffer
			int len;
			while ((len = reader.read(buf, 0, buf.length)) != -1) {
				reqStrBuilder.append(buf, 0, len);
				// System.out.println("Total read thus far:"+reqStrBuilder.toString());
			}
		} catch (IOException e1) {
			System.err.println("Can not read input, received data:" + reqStrBuilder.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("<xml><status>Error</status><description>Can not read input</description></xml>");
			return;
		}

		String reqString = reqStrBuilder.toString();

		if (request.getContentType() != null && !request.getContentType().contains("application/xml")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("<xml><status>Error</status><description>Invalid Content-Type \"" + request.getContentType()
					+ "\" in HTTP Header, should be \"application/xml\"</description></xml>");
			return;
		}

		if (reqString.length() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("<xml><status>Error</status><description>Empty body on POST request</description></xml>");
			return;
		}

		try {
			// 4. Parse the document you want to check, after converting to InputStream
			Source source = new StreamSource(new ByteArrayInputStream(reqString.getBytes()));

			// 5. Check the document
			validator.validate(source);
			// out.write("<xml><status>OK</status><description>Xml is valid</description></xml>");
		} catch (SAXException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("<xml><status>Error</status><description>Xml is not valid because :");
			out.write(ex.getMessage() + "</description></xml>");
			return;
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("<xml><status>Error</status><description>Could not read document.</description></xml>");
			return;
		}

		// Convert XML to JSON and input into DB
		String JSON = Xml.convertToJson(reqString);
		if ("error".equals(JSON)) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("<xml><status>Error</status><description>Could not convert XML to JSON document.</description></xml>");
			return;
		}

		String feed_id;

		String id = Json.getSimpleTextKey(JSON, "Id");
		if (id == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("<xml><status>Error</status><description>Could not read \"id\" from json data " + JSON
					+ "</description></xml>");
			return;
		}
		System.out.println("Document ID from XML = " + id);

		feed_id = Common.cleanString(id);

		// Check existing feeds in EdgeNode and find an unused name
		// If there are multiple feeds with the same name, give a random uuid
		List<String> DBs = dbClient.context().getAllDbs();

		boolean existing = DBs.contains(feed_id);
		if (existing) {
			for (int i = 1; i < 10; i++) {
				String feed_id2 = feed_id + i;
				if (!DBs.contains(feed_id2)) {
					feed_id = feed_id2;
					existing = false;
					break;
				}
			}
			if (existing)
				feed_id = UUID.randomUUID().toString().replace("-", "");
		}

		// Replace the feed ID with the created one
		JSON = Json.replaceSimpleTextKey(JSON, "Id", feed_id);
		// Also add it into the special _id field for couchDB
		JSON = Json.addKeyValue(JSON, "\"_id\" : \"" + feed_id + "\"");

		// Add the document to feeds database
		Response resp = dbClient.saveJsonText(JSON);
		feed_id = resp.getId();
		System.out.println("Created document ID = " + feed_id);

		// Create a database with this feed ID
		dbClient.context().createDB(feed_id);

		// Access the new database to add design document for view
		String viewDoc = "{\n"
				+ "\t\"_id\": \"_design/get_data\",\n"
				+ "\t\"language\": \"javascript\",\n"
				+ "\t\"views\": {\n"
				+ "\t\t \"by_date\": {\n"
				+ "\t\t\t  \"map\": \"function(doc) {\\nif(doc.timestamp && doc.data) {\\nemit(doc.timestamp, doc.data);\\n}\\n}\"\n"
				+ "\t\t }\n" + "\t}\n" + "}\n";
		try {
			CouchDbClient dbClientFeed = new CouchDbClient(feed_id, false, "http", server, port, user, pass);
			resp = dbClientFeed.saveJsonText(viewDoc);
			System.out.println("Created view document with ID = " + resp.getId());
			dbClientFeed.shutdown();
			// Write the response along with the proper status code
			response.setStatus(HttpServletResponse.SC_CREATED);
			out.write("<xml><status>OK</status><feed_id>" + feed_id + "</feed_id></xml>");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.write("<xml><status>Error</status><description>Could not connect to CouchDB, check that the server is running and that the correct username/pass is set</description></xml>");
		}

	}

}
