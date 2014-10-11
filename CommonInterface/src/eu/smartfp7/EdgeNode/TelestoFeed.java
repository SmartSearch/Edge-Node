package eu.smartfp7.EdgeNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
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
 * Servlet implementation class TelestoFeed
 */
@WebServlet(name = "telestoFeed", description = "Reads temperature and humidity from Telesto server and updates respective feed", urlPatterns = { "/telestoFeed" })
public class TelestoFeed extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// CouchDB access parameters
	private CouchDbClient feedsClient = null;
	private String server, user, pass;
	private int port;

	Map<String, TelestoTaskData> telestoList = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TelestoFeed() {
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

		telestoList = Collections.synchronizedMap(new HashMap<String, TelestoTaskData>());
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
		if (action == null) {
			try {
				response.sendRedirect("telestoFeed.html");
			} catch (IOException e1) {
				System.err.println("doGet IOException: Can not redirect");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.println("{\"error\":\"action parameter not specified\"}");
			}
			return;
		}

		int res = -1;
		String servername = null;
		if (action.equals("list"))
			res = listRunningTelesto(out);
		else {
			// Read the server name
			servername = request.getParameter("server");
			if (servername == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.println("{\"error\":\"server parameter not specified\"}");
				return;
			}
		}
		
		if (action.equals("start")) {
			res = startTelesto(servername, out, request);
		} else if (action.equals("stop")) {
			res = stopTelesto(servername, out);
		} else {
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

	int listRunningTelesto(PrintWriter out) {
		if (telestoList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}
		int len;
		StringBuilder names = new StringBuilder();
		for (String name : telestoList.keySet()) {
			names.append("\"" + name + "\",");
		}
		// Delete last comma character in name list
		if ((len = names.length()) > 0)
			names.deleteCharAt(len - 1);
		out.println("{\"names\":[" + names.toString() + "]}");
		return 0;
	}

	/*
	 * Returns 0 on success, 1 on bad input, -1 on error and prints description in out
	 */
	int startTelesto(String servername, PrintWriter out, HttpServletRequest request) {
		if (telestoList == null || feedsClient == null) {
			out.println("{\"error\":\"Server not correctly initialised\"}");
			return -1;
		}

		if (telestoList.size() > 30) {
			out.println("{\"error\":\"Too many telesto feeds currently running (" + telestoList.size()
					+ "), please stop some before retrying\"}");
			return 1;
		}

		String dbname = request.getParameter("feedname");
		// If the user provides a new name, clean and use it, else use the default
		if (dbname != null) {
			dbname = Common.cleanString(dbname);
		} else
			dbname = "temphumfeed";

		// Do not start a new telesto feed if already running
		if (telestoList.containsKey(servername)) {
			out.println("{\"status\":\"Telesto feed on '" + servername + "' already running\"}");
			return 0;
		}

		// Put a blank entry to prevent subsequent calls from initialising the same data
		telestoList.put(servername, null);
		TelestoTaskData data = initData(servername, dbname, out, request);
		if (data == null) {
			telestoList.remove(servername);
			return -1;
		}

		// Add a new entry on telestoList to avoid starting more threads on consecutive requests
		telestoList.put(servername, data);

		// In feeds database create the feed description if it doesn't exist for the new feed

		// Get the revision of new feed description document, null means it does not exist
		String rev = feedsClient.getRevision(dbname);
		if (rev == null) {
			// Get the original feed description
			JsonObject feedObj = new JsonObject();
			// Change the document id to match new feed
			feedObj.addProperty("_id", dbname);
			feedObj.addProperty("Feed", "test " + servername);
			try {
				feedsClient.save(feedObj);
			} catch (Exception e) {
				telestoList.remove(dbname);
				// release previously initialised clients
				cancelTelesto(data);
				out.println("{\"error\":\"Could not copy feed description from '" + servername + "' to '" + dbname
						+ "' \"}");
				return -1;
			}
		}

		// Set the timer to save the next doc
		data.tm = new Timer("timer_" + dbname);

		data.tm.schedule(new TelestoTimer(dbname), 0, data.interval);

		out.println("{\"status\":\"Started telesto feed for '" + servername + "' as '" + dbname + "'\"}");
		return 0;
	}

	TelestoTaskData initData(String servername, String dbname, PrintWriter out, HttpServletRequest request) {
		TelestoTaskData data = new TelestoTaskData();
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
		data.srcName = servername;
		data.interval = interval;
		// If we have consecutive errors for 60 minutes, stop
		// 60mins = 60*60*1000 = 3.6E6
		data.maxerrors = (int) (3.6E6 / data.interval) + 1;
		data.srcClient = new DefaultHttpClient();
		data.sinceHum = 0;
		data.sinceTemp = 0;
		if (data.srcClient == null) {
			// No connection could be made to source client
			out.println("{\"error\":\"Could not create HTTP client\"}");
			return null;
		}
		data.httpget = new HttpGet();
		// Open a connection to the destination DB
		try {
			// Create the new database if it does not exist
			data.dstClient = new CouchDbClient(dbname, true, "http", server, port, user, pass);
		} catch (Exception e) {
			telestoList.remove(dbname);
			cancelTelesto(data);
			// Couldn't connect to target db
			out.println("{\"error\":\"Could not access DB '" + dbname + "' \"}");
			return null;
		}

		// If the new db does not have design document for view, add it
		// TODO: copy from default
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
				telestoList.remove(dbname);
				// release previously initialised clients
				cancelTelesto(data);
				out.println("{\"error\":\"Could not add design document in DB '" + dbname + "' \"}");
				return null;
			}
		}

		data.cache = new LRUCache<String, String>(cacheSize);
		// Fill the cache with the latest data from CouchDB
		fillCache(data);

		return data;
	}

	void fillCache(TelestoTaskData data) {
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
		System.out.println("Cache for '" + data.srcName + "' loaded successfully with " + data.cache.size() + " documents");
	}

	class TelestoTimer extends TimerTask {
		String name;

		TelestoTimer(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			TelestoTaskData data = telestoList.get(name);
			// System.out.println("Running " + name + ", count = " + data.count++ + ", errorcount =" + data.errorcount);

			String resp;
			
			resp = checkServer(data, "Humidity", data.sinceHum);
			processResp(resp, data, "Humidity");
			resp = checkServer(data, "Temperature", data.sinceTemp);
			processResp(resp, data, "Temperature");
			return;
		}
		
		private void processResp(String resp, TelestoTaskData data, String type) {
			if (resp == null)
				return;
			String respJson = Xml.convertToJson(resp);
			if ("error".equals(respJson)) {
				System.err.println("Can not convert to JSON, data was: \n" + resp);
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

		private void sendData(long timestamp, JsonObject e, TelestoTaskData data) {
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

		String checkServer(TelestoTaskData data, String type, long sinceTime) {
			StringBuilder resultXML = null;
			try {
				data.httpget.setURI(new URI("http://" + data.srcName + "/SMART_EdgeNode/EdgeNode/DataFeeds/1/measurements/" + type + "/searchAfterDate?endDate=" + sinceTime));
			} catch (URISyntaxException e1) {
				return null;
			}

			try {
				// Execute HTTP request
				// System.out.println("executing request " + data.httpget.getURI());
				HttpResponse response = data.srcClient.execute(data.httpget);
				// System.out.println("----------------------------------------");
				// System.out.println(response.getStatusLine());
				// System.out.println("----------------------------------------");
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
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (resultXML == null || resultXML.length() == 0) {
				// After many consecutive errors, cancel the run
				if (data.errorcount++ > data.maxerrors)
					cancelTelesto(data);
				return null;
			} else {
				// reset the error counter
				data.errorcount = 0;
				return resultXML.toString();
			}
		}
	}

	int stopTelesto(String name, PrintWriter out) {
		if (telestoList == null)
			return -1;

		if (!telestoList.containsKey(name)) {
			if (out != null)
				out.println("{\"error\":\"Social feed with name '" + name + "' not running\"}");
			return 1;
		}

		TelestoTaskData data = telestoList.get(name);
		cancelTelesto(data);
		telestoList.remove(name);
		if (out != null)
			out.println("{\"status\":\"Stopped social feed with name '" + name + "'\"}");
		return 0;
	}

	private void cancelTelesto(TelestoTaskData data) {
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
		synchronized (telestoList) {
			for (String name : telestoList.keySet()) {
				cancelTelesto(telestoList.get(name));
			}
			telestoList.clear();
		}
	}
	
	class TelestoTaskData {
		Timer tm;
		HttpClient srcClient;
		String srcName;
		HttpGet httpget;
		CouchDbClient dstClient;
		LRUCache<String, String> cache;
		int count, errorcount, maxerrors;
		long interval;
		long sinceHum, sinceTemp;
	}

}
