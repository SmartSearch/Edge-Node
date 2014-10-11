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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.lightcouch.CouchDbClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.smartfp7.utils.LRUCache;
import eu.smartfp7.utils.Xml;

/**
 * Servlet implementation class SocialNetworkFeed
 */
@WebServlet(name = "socialFeed", description = "Searches social networks and updates respective feed", urlPatterns = { "/socialFeed" })
public class SocialNetworkFeed extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// CouchDB access parameters
	private CouchDbClient feedsClient = null;
	private String server, user, pass;
	private int port;

	Map<String, SocialTaskData> socialSearchList = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SocialNetworkFeed() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
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
			feedsClient = new CouchDbClient("feeds", true, "http", server, port, user, pass);
		} catch (Exception e) {
			System.err
					.println("Could not connect to CouchDB, check that the server is running and that the correct username/pass is set");
			System.err.println("Current configuration: server=" + server + ", port=" + port + ", user= " + user
					+ ", pass= " + pass);
			return;
		}

		socialSearchList = Collections.synchronizedMap(new HashMap<String, SocialTaskData>());
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// Stop all running timers
		cancelTimers();

		// Close feeds connection
		if (feedsClient != null) {
			feedsClient.shutdown();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json"))
			response.setContentType("application/json; charset=utf-8");
		else
			response.setContentType("text/plain; charset=utf-8");

		PrintWriter out = response.getWriter();

		String action = request.getParameter("action");
		String socialname = null;
		if (action == null) {
			try {
				response.sendRedirect("socialFeed.html");
			} catch (IOException e1) {
				System.err.println("doGet IOException: Can not redirect");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.println("{\"error\":\"action parameter not specified\"}");
			}
			return;
		}

		int res = -1;
		if (action.equals("start")) {
			// Read the keyword for the start action
			String keyword = request.getParameter("keyword");
			if (keyword == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.println("{\"error\":\"Start action requires the parameter 'keyword'\"}");
				return;
			}
			res = startSocial(keyword, out, request);
		} else if (action.equals("stop") || action.equals("delete")) {
			// Read the social network name for the other actions that need it
			socialname = request.getParameter("name");
			if (socialname == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.println("{\"error\":\"Stop and delete actions require the parameter 'name'\"}");
				return;
			}
			if (action.equals("stop"))
				res = stopSocial(socialname, out);
			else if (action.equals("delete"))
				res = deleteSocial(socialname, out);
		} else if (action.equals("list"))
			res = listRunningSocials(out);
		else if (action.equals("delete_all"))
			res = deleteAllSocials(out, request);
		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.println("{\"error\":\"Unknown action, should be one of: 'list', 'start', 'stop', 'delete', 'delete_all'\"}");
			return;
		}
		// error messages will be printed by the function
		if (res < 0) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (res > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// All below functions return 0 if ok -1 if server error 1 if bad request

	int listRunningSocials(PrintWriter out) {
		if (socialSearchList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}
		int len;
		StringBuilder socialNames = new StringBuilder();
		for (String name : socialSearchList.keySet()) {
			socialNames.append("\"" + name + "\",");
		}
		// Delete last comma character in name list
		if ((len = socialNames.length()) > 0)
			socialNames.deleteCharAt(len - 1);
		out.println("{\"names\":[" + socialNames.toString() + "]}");
		return 0;
	}

	int deleteAllSocials(PrintWriter out, HttpServletRequest request) {
		if (socialSearchList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}

		if (!"delete all socials".equals(request.getParameter("confirm"))) {
			out.println("{\"error\":\"To prevent accidental deletion, an additional parameter 'confirm=delete+all+socials' is required\"}");
			return 1;
		}

		cancelTimers();
		// Wait a bit for the timers to stop
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		// Delete the replay databases and remove them from feeds list
		long startTime = System.currentTimeMillis();
		int doccount = 0;
		for (String DBname : feedsClient.context().getAllDbs()) {
			if (DBname.startsWith("social_")) {
				doccount++;
				feedsClient.context().deleteDB(DBname, "delete database");
				// also remove from feeds list if existing
				String rev;
				if ((rev = feedsClient.getRevision(DBname)) != null) {
					feedsClient.remove(DBname, rev);
				}
			}
		}

		out.println("{\"status\": \"deleted " + doccount + " social feeds in "
				+ (System.currentTimeMillis() - startTime) + " ms\"}");
		return 0;
	}

	int deleteSocial(final String name, PrintWriter out) {
		if (socialSearchList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}
		// First stop the replay, ignore if the replay is not running
		stopSocial(name, null);

		StringBuffer error = new StringBuffer();

		// Delete the database
		if (feedsClient.context().getAllDbs().contains(name)) {
			feedsClient.context().deleteDB(name, "delete database");
		} else {
			error.append("feed database was not found");
		}

		// And remove from feeds list if existing
		String rev;
		if ((rev = feedsClient.getRevision(name)) != null) {
			feedsClient.remove(name, rev);
		} else {
			if (error.length() > 0) {
				error.append(", ");
			}
			error.append("feed was not present in feeds list");
		}
		if (error.length() > 0) {
			out.println("{\"error\":\"Deleted social feed, but following error(s) occured: " + error.toString() + "\"}");
			return 1;
		} else {
			out.println("{\"status\":\"Deleted social feed\"}");
			return 0;
		}
	}

	/*
	 * Returns 0 on success, 1 on bad input, -1 on error and prints description in out
	 */
	int startSocial(String keyword, PrintWriter out, HttpServletRequest request) {
		if (socialSearchList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}

		if (socialSearchList.size() > 30) {
			out.println("{\"error\":\"Too many social feeds currently running (" + socialSearchList.size()
					+ "), please stop some before retrying\"}");
			return 1;
		}

		String socialname;
		// If the user provides a new name, prefix it with "social_", else prefix the original name
		if (request.getParameter("name") != null) {
			socialname = "social_" + Common.cleanString(request.getParameter("name").toString());
		} else
			socialname = "social_" + Common.cleanString(keyword);

		// Do not start a new social feed if already running
		if (socialSearchList.containsKey(socialname)) {
			out.println("{\"status\":\"Social feed '" + socialname + "' already running\"}");
			return 0;
		}

		// Put a blank entry to prevent subsequent calls from initialising the same data
		socialSearchList.put(socialname, null);
		SocialTaskData data = initData(keyword, socialname, out, request);
		if (data == null) {
			socialSearchList.remove(socialname);
			return -1;
		}

		// Add a new entry on socialSearchList to avoid starting more social feeds on consecutive requests
		socialSearchList.put(socialname, data);

		// In feeds database create the feed description if it doesn't exist for the social feed

		// Get the revision of new feed description document, null means it does not exist
		String rev = feedsClient.getRevision(socialname);
		if (rev == null) {
			// Get the original feed description
			JsonObject feedObj = new JsonObject();
			// Change the document id to match new feed
			feedObj.addProperty("_id", socialname);
			feedObj.addProperty("Feed", "test " + keyword);
			try {
				feedsClient.save(feedObj);
			} catch (Exception e) {
				socialSearchList.remove(socialname);
				// release previously initialised clients
				cancelSocialSearch(data);
				out.println("{\"error\":\"Could not copy feed description from '" + keyword + "' to '" + socialname
						+ "' \"}");
				return -1;
			}
		}

		// Set the timer to save the next doc
		data.tm = new Timer("timer_" + socialname);

		data.tm.schedule(new SocialTimer(socialname), 0, data.interval);

		out.println("{\"status\":\"Started social feed for '" + keyword + "' as '" + socialname + "'\"}");
		return 0;
	}

	SocialTaskData initData(String keyword, String socialname, PrintWriter out, HttpServletRequest request) {
		SocialTaskData data = new SocialTaskData();
		// Default interval of 60 seconds
		long interval = 60000;
		if (request.getParameter("interval") != null) {
			try {
				long intrv2 = Long.parseLong(request.getParameter("interval"));
				if (intrv2 > 0)
					interval = intrv2 * 1000;
			} catch (Exception ignore) {
			}
		}

		int cacheSize = (int) (3 * interval / 1000);
		data.keyword = keyword;
		data.interval = interval;
		// If we have consecutive errors for 60 minutes, stop
		// 60mins = 60*60*1000 = 3.6E6
		data.maxerrors = (int) (3.6E6 / data.interval) + 1;
		data.srcClient = new DefaultHttpClient();
		if (data.srcClient == null) {
			// No connection could be made to source client
			out.println("{\"error\":\"Could not create HTTP client\"}");
			return null;
		}
		data.httpget = new HttpGet("http://telesto.zapto.org:81/SocialNetworkManager/Search/General/Posts/?term="
				+ keyword + "&pagesize=" + cacheSize);
		// Open a connection to the destination DB
		try {
			// Create the new database if it does not exist
			data.dstClient = new CouchDbClient(socialname, true, "http", server, port, user, pass);
		} catch (Exception e) {
			socialSearchList.remove(socialname);
			cancelSocialSearch(data);
			// Couldn't connect to target db
			out.println("{\"error\":\"Could not access DB '" + socialname + "' \"}");
			return null;
		}

		// If the new db does not have design document for view, add it
		if (!data.dstClient.contains("_design/get_data")) {
			String viewDoc = "{\n"
					+ "\t\"_id\": \"_design/get_data\",\n"
					+ "\t\"language\": \"javascript\",\n"
					+ "\t\"views\": {\n"
					+ "\t\t \"by_date\": {\n"
					+ "\t\t\t  \"map\": \"function(doc) {\\nif(doc.timestamp && doc.data) {\\nemit(doc.timestamp, doc.data);\\n}\\n}\"\n"
					+ "\t\t }\n" + "\t}\n" + "}\n";

			try {
				data.dstClient.saveJsonText(viewDoc);
			} catch (Exception e) {
				socialSearchList.remove(socialname);
				// release previously initialised clients
				cancelSocialSearch(data);
				out.println("{\"error\":\"Could not add design document in DB '" + socialname + "' \"}");
				return null;
			}
		}

		data.cache = new LRUCache<String, String>(cacheSize);
		// Fill the cache with the latest data from CouchDB
		fillCache(data);

		return data;
	}

	void fillCache(SocialTaskData data) {
		List<JsonObject> resList = null;
		String keyStr;
		try {
			//Get the most recent documents
			resList = data.dstClient.view("get_data/by_date").descending(true).limit(data.cache.getLimit())
					.query(JsonObject.class);
		} catch (Exception e) {
			return;
		}
		if (resList == null || resList.size() < 1) {
			return;
		}
		for (JsonObject o : resList) {
			//Store them in the cache
			try {
				keyStr = o.get("value").toString();
				data.cache.put(keyStr, null);
			} catch (Exception e) {
				continue;
			}
		}
		System.out.println("Cache for '" + data.keyword + "' loaded successfully with " + data.cache.size() + " documents");
	}

	class SocialTimer extends TimerTask {
		String name;

		SocialTimer(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			SocialTaskData data = socialSearchList.get(name);
			// System.out.println("Running " + name + ", count = " + data.count++ + ", errorcount =" + data.errorcount);
			String resp = checkServer(data);
			if (resp == null)
				return;
			String respJson = Xml.convertToJson(resp);
			if ("error".equals(respJson)) {
				System.err.println("Can not convert to JSON, server response was: \n" + resp);
				return;
			}
			JsonObject jsonObj = new JsonParser().parse(respJson).getAsJsonObject();
			JsonArray arr;
			try {
				arr = jsonObj.get("generalSearch").getAsJsonObject().get("Post").getAsJsonArray();
				String keyString, dateStr;
				JsonObject o2;
				long timestamp;
				for (JsonElement e : arr) {
					try {
						// System.out.println(e.toString());
						o2 = e.getAsJsonObject();
						dateStr = o2.get("CreationDate").getAsString();
						o2.remove("CreationDate");
						keyString = o2.toString();
						if (!data.cache.containsKey(keyString)) {
							timestamp = Common.string2millis(dateStr);
							data.cache.put(keyString, null);
							sendData(timestamp, o2, data);
						}
					} catch (Exception ignore) {
						continue;
					}
				}
			} catch (Exception ignore) {
				return;
			}
		}

		private void sendData(long timestamp, JsonObject e, SocialTaskData data) {
			JsonObject o = new JsonObject();
			String UTCdate = Common.millis2String(timestamp);
			int maxretries = 10;
			// Document should not exist in DB, if it exists increment the milliseconds by one
			while (data.dstClient.contains(UTCdate) && --maxretries > 0) {
				UTCdate = Common.millis2String(++timestamp);
			}
			if (maxretries == 0)
				return;
			// System.out.println(timestamp + ":" + UTCdate);
			o.addProperty("_id", UTCdate);
			o.addProperty("timestamp", timestamp);
			o.add("data", e);

			data.dstClient.save(o);
		}

		String checkServer(SocialTaskData data) {
			StringBuilder resultXML = null;
			try {
				// Execute HTTP request
				// System.out.println("executing request " + data.httpget.getURI());
				HttpResponse response = data.srcClient.execute(data.httpget);
				if (response.getStatusLine().getStatusCode()==javax.servlet.http.HttpServletResponse.SC_OK) {
					//Only continue if the call was successful
					
					// Get hold of the response entity
					HttpEntity entity = response.getEntity();
	
					// If the response does not enclose an entity, there is no need
					// to bother about connection release
					if (entity != null) {
						resultXML = new StringBuilder(2000);
						InputStream instream = entity.getContent();
						try {
							// Store the response
							BufferedReader rd = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
							String line = "";
							while ((line = rd.readLine()) != null) {
								resultXML.append(line);
							}
						} catch (IOException ex) {
							// In case of an IOException the connection will be released
							// back to the connection manager automatically
							ex.printStackTrace();
						} catch (RuntimeException ex) {
							// In case of an unexpected exception you may want to abort
							// the HTTP request in order to shut down the underlying
							// connection immediately.
							data.httpget.abort();
							ex.printStackTrace();
						} finally {
							// Closing the input stream will trigger connection release
							try {
								instream.close();
							} catch (Exception ignore) {
							}
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (resultXML == null || resultXML.length() == 0) {
				// After many consecutive errors, cancel the run
				if (data.errorcount++ > data.maxerrors)
					cancelSocialSearch(data);
				return null;
			} else {
				// reset the error counter
				data.errorcount = 0;
				return resultXML.toString();
			}
		}
	}

	int stopSocial(String name, PrintWriter out) {
		if (socialSearchList == null)
			return -1;

		if (!socialSearchList.containsKey(name)) {
			if (out != null)
				out.println("{\"error\":\"Social feed with name '" + name + "' not running\"}");
			return 1;
		}

		SocialTaskData data = socialSearchList.get(name);
		cancelSocialSearch(data);
		socialSearchList.remove(name);
		if (out != null)
			out.println("{\"status\":\"Stopped social feed with name '" + name + "'\"}");
		return 0;
	}

	private void cancelSocialSearch(SocialTaskData data) {
		if (data==null)
			return;
		// Stop timer
		if (data.tm != null)
			data.tm.cancel();
		// Close HTTP and CouchDB clients
		if (data.srcClient != null)
			data.srcClient.getConnectionManager().shutdown();
		if (data.dstClient != null)
			data.dstClient.shutdown();
	}

	// Stop all running timers
	private void cancelTimers() {
		synchronized (socialSearchList) {
			for (String name : socialSearchList.keySet()) {
				cancelSocialSearch(socialSearchList.get(name));
			}
			socialSearchList.clear();
		}
	}
	
	class SocialTaskData {
		Timer tm;
		HttpClient srcClient;
		HttpGet httpget;
		CouchDbClient dstClient;
		String keyword;
		LRUCache<String, String> cache;
		int count, errorcount, maxerrors;
		long interval;
	}

}
