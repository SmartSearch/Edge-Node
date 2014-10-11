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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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

/**
 * Servlet implementation class ReplayFeed
 */
@WebServlet(name = "replayFeed", description = "Simulates a live feed", urlPatterns = { "/replayFeed" })
public class ReplayFeed extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// CouchDB access parameters
	private CouchDbClient feedsClient = null;
	private String server, user, pass;
	private int port;

	// HashMap<String, Timer> timerList = null;
	// HashMap<String, CouchDbClient> dbClientList = null;
	// HashMap<String, Integer> dbClientUsage = null;

	HashMap<String, ReplayTaskData> replayList = null;

	Calendar cal; // Used for printing dates

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReplayFeed() {
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
			System.out
					.println("Could not connect to CouchDB, check that the server is running and that the correct username/pass is set");
			System.out.println("Current configuration: server=" + server + ", port=" + port + ", user= " + user
					+ ", pass= " + pass);
			return;
		}

		// timerList = new HashMap<String, Timer>();
		// dbClientList = new HashMap<String, CouchDbClient>();
		// dbClientUsage = new HashMap<String, Integer>();

		replayList = new HashMap<String, ReplayTaskData>();

		cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

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
		response.setContentType("application/json;charset=UTF-8");

		PrintWriter out = response.getWriter();

		String action = request.getParameter("action");
		String feedname = null;
		if (action == null) {
			try {
				response.sendRedirect("replayFeed.html");
			} catch (IOException e1) {
				System.out.println("doGet IOException: Can not redirect");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.println("{\"error\":\"action parameter not specified\"}");
			}
			return;
		}

		// Read the feed name for the actions that need it
		if (action.equals("start") || action.equals("stop") || action.equals("delete")) {
			feedname = request.getParameter("feedname");
			if (feedname == null) {
				out.println("{\"error\":\"Start and stop actions require the parameter 'feedname'\"}");
				return;
			}
		}

		// Other parameters that are action-specific will be read from within the functions

		int res;
		if (action.equals("list"))
			res = listRunningFeeds(out);
		else if (action.equals("start"))
			res = startReplay(feedname, out, request);
		else if (action.equals("stop"))
			res = stopReplay(feedname, out);
		else if (action.equals("delete"))
			res = deleteReplay(feedname, out);
		else if (action.equals("delete_all"))
			res = deleteAllFeeds(out, request);
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

	int listRunningFeeds(PrintWriter out) {
		if (replayList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}
		int len;
		StringBuilder replayNames = new StringBuilder();
		for (String name : replayList.keySet()) {
			replayNames.append("\"" + name + "\",");
		}
		// Delete last comma character in name list
		if ((len = replayNames.length()) > 0)
			replayNames.deleteCharAt(len - 1);
		out.println("{\"list\":[" + replayNames.toString() + "]}");
		return 0;
	}

	int deleteAllFeeds(PrintWriter out, HttpServletRequest request) {
		if (replayList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}

		if (!"delete all replays".equals(request.getParameter("confirm"))) {
			out.println("{\"error\":\"To prevent accidental deletion, an additional parameter 'confirm=delete+all+replays' is required\"}");
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
			if (DBname.startsWith("replay_")) {
				doccount++;
				feedsClient.context().deleteDB(DBname, "delete database");
				// also remove from feeds list if existing
				String rev;
				if ((rev = feedsClient.getRevision(DBname)) != null) {
					feedsClient.remove(DBname, rev);
				}
			}
		}

		out.println("{\"status\": \"deleted " + doccount + " replays in " + (System.currentTimeMillis() - startTime)
				+ " ms\"}");
		return 0;
	}

	int deleteReplay(final String name, PrintWriter out) {
		if (replayList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}
		// First stop the replay, ignore if the replay is not running
		stopReplay(name, null);

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
			out.println("{\"error\":\"Deleted replay, but following error(s) occured: " + error.toString() + "\"}");
			return 1;
		} else {
			out.println("{\"status\":\"Deleted replay\"}");
			return 0;
		}
	}

	/*
	 * Returns 0 on success, 1 on bad input, -1 on error and prints description in out
	 */
	int startReplay(final String name, PrintWriter out, HttpServletRequest request) {
		if (replayList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}

		if (replayList.size() > 30) {
			out.println("{\"error\":\"Too many replays currently running (" + replayList.size()
					+ "), please stop some before retrying\"}");
			return 1;
		}

		String newname;
		// If the user provides a new name, prefix it with "replay_", else prefix the original name
		if (request.getParameter("newname") != null) {
			newname = "replay_" + Common.cleanString(request.getParameter("newname").toString());
		} else
			newname = "replay_" + name;

		// Do not start a new replay if already running
		if (replayList.containsKey(newname)) {
			out.println("{\"status\":\"Replay into '" + newname + "' already running\"}");
			return 0;
		}

		// Add a new entry on replayList to avoid starting more replays on consecutive requests
		ReplayTaskData data = new ReplayTaskData();
		replayList.put(newname, data);

		// Check if name and newname already exist the database
		List<String> DBs = feedsClient.context().getAllDbs();

		// If source feed name does not exist, or the feeds db has no description return error
		if (!DBs.contains(name) || !feedsClient.contains(name)) {
			replayList.remove(newname);
			out.println("{\"error\":\"Source feed does not exist or has no description in 'feeds' list\"}");
			return 1;
		}

		// Open a connection to the source and destination DBs
		try {
			data.srcClient = new CouchDbClient(name, false, "http", server, port, user, pass);
			// Create the new database if it does not exist
			data.dstClient = new CouchDbClient(newname, true, "http", server, port, user, pass);
		} catch (Exception e) {
			replayList.remove(newname);
			cancelReplay(data);
			if (data.srcClient == null) {
				// No connection could be made to source client
				out.println("{\"error\":\"Could not access DB '" + name + "' \"}");
			} else {
				// Connection to srcClient was successful, but couldn't connect to target
				out.println("{\"error\":\"Could not access DB '" + newname + "' \"}");
			}
			return -1;
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
				replayList.remove(newname);
				// release previously initialised clients
				cancelReplay(data);
				out.println("{\"error\":\"Could not add design document in DB '" + newname + "' \"}");
				return -1;
			}
		}

		// In feeds database copy the feed description from original feed to new feed
		// Get the revision of new feed description document, null means it does not exist
		String rev = feedsClient.getRevision(newname);
		try {
			// Get the original feed description
			JsonObject feedObj = feedsClient.find(JsonObject.class, name);

			// Change the document id to match new feed
			feedObj.addProperty("_id", newname);

			// If the document was not present in feeds before, remove _rev property and save
			if (rev == null) {
				feedObj.remove("_rev");
				feedsClient.save(feedObj);
			}
			// otherwise set _rev property to the revision and update the document
			else {
				feedObj.addProperty("_rev", rev);
				feedsClient.update(feedObj);
			}
		} catch (Exception e) {
			replayList.remove(newname);
			// release previously initialised clients
			cancelReplay(data);
			out.println("{\"error\":\"Could not copy feed description from '" + name + "' to '" + newname + "' \"}");
			return -1;
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
					out.println("{\"error\":\"Invalid start date '" + startDate + "'\"}");
					return 1;
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
					out.println("{\"error\":\"Invalid end date '" + endDate + "'\"}");
					return 1;
				}
			}
		}

		if (startMillis != null && endMillis != null && startMillis > endMillis) {
			out.println("{\"error\":\"End time is before start time\"}");
			return 1;
		}

		String repeatStr = request.getParameter("repeats");
		if (repeatStr != null) {
			try {
				data.repeats = Integer.parseInt(repeatStr);
			} catch (NumberFormatException e) {
				out.println("{\"error\":\"Invalid number of repeats '" + repeatStr + "'\"}");
				return 1;
			}
		} else {
			// only repeat once
			data.repeats = 1;
		}

		List<JsonObject> resList;

		// Get the first 2 documents, write the first one in the new feed DB and keep the other to
		// send in the next iteration
		if (startMillis != null && endMillis != null) {
			resList = data.srcClient.view("get_data/by_date").startKey(startMillis).endKey(endMillis).limit(2)
					.query(JsonObject.class);
		} else if (startMillis != null) {
			resList = data.srcClient.view("get_data/by_date").startKey(startMillis).limit(2).query(JsonObject.class);
		} else if (endMillis != null) {
			resList = data.srcClient.view("get_data/by_date").endKey(endMillis).limit(2).query(JsonObject.class);
		} else {
			resList = data.srcClient.view("get_data/by_date").limit(2).query(JsonObject.class);
		}

		if (resList == null || resList.size() != 2) {
			replayList.remove(newname);
			// release previously initialised clients
			cancelReplay(data);
			out.println("{\"error\":\"Feed '" + name + "' contains no data or only one measurement\"}");
			return 1;
		}

		data.count = 0;
		data.feedStart = resList.get(0).get("key").getAsLong();
		data.nextDocTime = resList.get(1).get("key").getAsLong();
		data.replayStart = System.currentTimeMillis();
		data.feedEnd = (endMillis == null ? Long.MAX_VALUE : endMillis);
		// Calculate the time difference from start to next document
		long diff = data.nextDocTime - data.feedStart;

		// Update time in the first document and save into DB
		data.dstClient.save(changeDocumentTime(resList.get(0), data.replayStart));

		// Update time in the second document and store for next run
		data.nextDoc = changeDocumentTime(resList.get(1), data.replayStart + diff);

		// Set the timer to save the next doc
		data.tm = new Timer("timer_" + newname);

		data.tm.schedule(new replayTimer(newname), diff);

		out.println("{\"status\":\"Started replay of '" + name + "' into '" + newname + "'\"}");
		return 0;
	}

	class replayTimer extends TimerTask {
		String name;

		replayTimer(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			ReplayTaskData data = replayList.get(name);
			List<JsonObject> resList = null;
			if (data == null)
				return;
			// Next document was already loaded before, save it
			try {
				data.dstClient.save(data.nextDoc);
			} catch (org.lightcouch.CouchDbException e) {
				System.err.println("CouchDB Exception saving");
			}

			if (data.nextDocTime + 1 < data.feedEnd) {
				try {
					// Get a new document
					// resList = data.srcClient.view("get_data/by_date").startKey(data.nextDocTime + 1).limit(1)
					// .query(JsonObject.class);
					resList = data.srcClient.view("get_data/by_date").startKey(data.nextDocTime + 1)
							.endKey(data.feedEnd).limit(1).query(JsonObject.class);
				} catch (Exception e) {
					System.err.println("CouchDB Exception reading");
				}
			}

			// No more data available
			if (resList == null || resList.size() != 1) {
				boolean restart = false;
				if (++data.count == data.repeats) {
					// We have done the required number of repetitions
					cancelReplay(data);
					replayList.remove(name);
				} else {
					try {
						resList = data.srcClient.view("get_data/by_date").startKey(data.feedStart).endKey(data.feedEnd)
								.limit(1).query(JsonObject.class);
					} catch (Exception e) {
						System.err.println("CouchDB Exception reading");
					}
					if (resList != null && resList.size() == 1) {
						// Restart a new loop
						restart = true;
						data.replayStart = System.currentTimeMillis();
					}
				}
				// System.out.println("Source feed for '" + name + "' contains no more data");
				if (!restart)
					return;
			}

			data.nextDoc = resList.get(0);
			data.nextDocTime = data.nextDoc.get("key").getAsLong();
			// Calculate the difference between start of feed and doc time
			long diff1 = data.nextDocTime - data.feedStart;
			// Check how much time has passed since start of replay
			long diff2 = System.currentTimeMillis() - data.replayStart;

			// Calculate how much time remains till next run
			long newTime = diff1 - diff2;
			if (newTime < 0)
				newTime = 0;

			// Update the new document time based on diff1
			data.nextDoc = changeDocumentTime(data.nextDoc, data.replayStart + diff1);

			try {
				// reschedule next timer according to actual time passed
				data.tm.schedule(new replayTimer(name), newTime);
			} catch (IllegalStateException e) {
				// We can get here if the timer was stopped in the meantime, suppress warning
			}

			// System.out.println("Running " + name + ", count = " + data.count++ + ", nextTime = " + data.nextDocTime);
		}
	}

	int stopReplay(String name, PrintWriter out) {
		if (replayList == null)
			return -1;

		if (!replayList.containsKey(name)) {
			if (out != null)
				out.println("{\"error\":\"Replay of '" + name + "' not running\"}");
			return 1;
		}

		ReplayTaskData data = replayList.get(name);
		cancelReplay(data);
		replayList.remove(name);
		if (out != null)
			out.println("{\"status\":\"Stopped replay of '" + name + "'\"}");
		return 0;
	}

	private void cancelReplay(ReplayTaskData data) {
		// Stop timer
		if (data.tm != null)
			data.tm.cancel();
		// Close CouchDB clients
		if (data.srcClient != null)
			data.srcClient.shutdown();
		if (data.dstClient != null)
			data.dstClient.shutdown();
	}

	// Stop all running timers
	private void cancelTimers() {
		for (String name : replayList.keySet()) {
			cancelReplay(replayList.get(name));
		}
		replayList.clear();
	}

	JsonObject changeDocumentTime(JsonObject Document, long newTimeMillis) {
		String xmlDateTime;
		boolean fixMillis = false;
		/*
		 * If the millis is divisible by 1000, printDateTime does not add a decimal printout. This workaround fixes this
		 * by adding a millisecond before printing
		 */
		if (newTimeMillis % 1000 == 0) {
			fixMillis = true;
			newTimeMillis++;
		}
		synchronized (this) {
			cal.setTimeInMillis(newTimeMillis);
			xmlDateTime = javax.xml.bind.DatatypeConverter.printDateTime(cal);
		}
		// Undo the change done previously and also fix the printout
		if (fixMillis) {
			xmlDateTime = xmlDateTime.replace(".001Z", ".000Z");
			newTimeMillis--;
		}
		JsonObject newDoc = new JsonObject();
		newDoc.addProperty("_id", xmlDateTime);
		newDoc.addProperty("timestamp", newTimeMillis);
		JsonObject data = Document.get("value").getAsJsonObject();
		// If "time" already exists, update it
		if (data.get("time") != null) {
			data.addProperty("time", xmlDateTime);
			newDoc.add("data", data);
		} else {
			// If not already existing, add "time" property at the beginning
			JsonObject data1 = new JsonObject();
			data1.addProperty("time", xmlDateTime);
			for (Entry<String, JsonElement> e : data.entrySet()) {
				data1.add(e.getKey(), e.getValue());
			}
			newDoc.add("data", data1);
		}
		return newDoc;
	}

	String getDocument(String name) {
		InputStream is = feedsClient.find(name);
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		// Store the document to a StringBuilder in order to convert to string
		StringBuilder docStrBuilder = new StringBuilder();
		try {
			char[] buf = new char[4 * 1024]; // 4 KB char buffer
			int len;
			while ((len = isr.read(buf, 0, buf.length)) != -1) {
				docStrBuilder.append(buf, 0, len);
				// System.out.println("Total read thus far:"+reqStrBuilder.toString());
			}
		} catch (IOException e1) {
			System.err.println("Can not read input, received data:" + docStrBuilder.toString());
			try {
				is.close();
			} catch (IOException e) {
			}
			return null;
		}
		// Close the input stream as requested by find()
		try {
			is.close();
		} catch (IOException e) {
		}
		return docStrBuilder.toString();
	}

	class ReplayTaskData {
		Timer tm;
		CouchDbClient srcClient, dstClient;
		JsonObject nextDoc;
		int count, repeats;
		long nextDocTime, replayStart, feedStart, feedEnd;
	}

}
