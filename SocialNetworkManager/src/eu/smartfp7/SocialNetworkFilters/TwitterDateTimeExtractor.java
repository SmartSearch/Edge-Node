/* 
 * SMART FP7 - Search engine for MultimediA enviRonment generated contenT
 * Webpage: http://smartfp7.eu
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * The Original Code is Copyright (c) 2012-2013 of Telesto Technologies
 * All Rights Reserved
 *
 * Contributor(s):
 *  Xristos Smailis <smailisxristos@yahoo.com>
 *  Thanos Alexiou <thanos@telesto.gr>
 */
package eu.smartfp7.SocialNetworkFilters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import eu.smartfp7.FilterDataTypes.TwitterResult;
import eu.smartfp7.SocialNetworkManager.SocialNetworkFilter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TwitterDateTimeExtractor implements SocialNetworkFilter {

	static TwitterResult getData(URL address) throws Exception {
		URL page = address;
		StringBuffer text = new StringBuffer();
		HttpURLConnection conn = (HttpURLConnection) page.openConnection();
		conn.connect();
		InputStreamReader in = new InputStreamReader(
				(InputStream) conn.getContent());

		BufferedReader buff = new BufferedReader(in);
		/*
		 * String line = buff.readLine(); while (line != null) {
		 * text.append(line + "\n"); line = buff.readLine(); }
		 * 
		 * 
		 * System.out.println(text.toString());
		 */
		JAXBContext jaxbContext = JAXBContext.newInstance(TwitterResult.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		TwitterResult result = (TwitterResult) jaxbUnmarshaller.unmarshal(in);

		return result;
	}

	public String filter(String queryPar, String ContextURL) {

		TwitterResult TwitterData = null;
		try {
			System.out.println(new URL("http://localhost:"
					+ ContextURL.split(":")[2]
					+ "General/TwitterDriver/Posts/?term=" + queryPar
					+ "&pagesize=100"));

			TwitterData = getData(new URL("http://localhost:"
					+ ContextURL.split(":")[2]
					+ "General/TwitterDriver/Posts/?term=" + queryPar
					+ "&pagesize=100"));
			if (TwitterData == null)
				return null;
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		System.out.println(TwitterData.results.size());

		ArrayList<String> Dates = extractEventDate(TwitterData, "#SMART_demo");
		ArrayList<String> SessionTimes = extractEventSessionTimes(TwitterData,
				"#SMART_demo");
		System.out.println("length=" + SessionTimes.size());

		String ActivityURI = "http://SocialNetworkManager/activity";
		String hasDateStr = "http://SocialNetworkManager/Event/hasDate";
		String hasTemporalHintStr = "http://SocialNetworkManager/Event/hasTemporalHint";
		String isActiveStr = "http://SocialNetworkManager/Event/isActive";
		String hasNameStr = "http://SocialNetworkManager/Event/hasName";
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		// create the resource
		Resource activity = model.createResource(ActivityURI);
		Property hasDate = model.createProperty(hasDateStr);
		Property hasTemporalHint = model.createProperty(hasTemporalHintStr);
		Property isActive = model.createProperty(isActiveStr);
		Property hasName = model.createProperty(hasNameStr);
		for (int i = 0; i < SessionTimes.size(); i++) {
			activity.addProperty(hasTemporalHint, SessionTimes.get(i));
		}
		if (!Dates.isEmpty()) {
			for (int i = 0; i < SessionTimes.size(); i++) {
				activity.addProperty(hasTemporalHint, SessionTimes.get(i));
			}

			boolean isActiveVal = false;
			for (int i = 0; i < Dates.size(); i++) {
				activity.addProperty(hasDate, Dates.get(i));
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Calendar cal = Calendar.getInstance();
				try {
					if (cal.getTime().compareTo(dateFormat.parse(Dates.get(i))) == 0) {
						isActiveVal = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			activity.addProperty(isActive, isActiveVal + "");

			activity.addProperty(hasName, queryPar);

			StringWriter RDFoutput = new StringWriter();

			model.write(RDFoutput, "RDF/XML-ABBREV");
			return RDFoutput.toString();
		} else

			activity.addProperty(hasName, queryPar);

		StringWriter RDFoutput = new StringWriter();

		model.write(RDFoutput, "RDF/XML-ABBREV");
		return RDFoutput.toString();
	}

	static boolean existsInList(ArrayList<String> List, String Pattern) {
		for (int i = 0; i < List.size(); i++) {
			if (List.get(i).contentEquals(Pattern)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> extractEventSessionTimes(
			TwitterResult TwitterData, String EventHashTag) {

		ArrayList<String> result = new ArrayList();

		for (int i = 0; i < TwitterData.results.size(); i++) {
			// System.out.println(TwitterData.results.get(i).Message);
			// if (TwitterData.results.get(i).Message.contains(EventHashTag)) {
			String[] messageSplit = TwitterData.results.get(i).Message
					.toLowerCase().split(" ");
			for (int j = 0; j < messageSplit.length; j++) {
				if (messageSplit[j].contains(":")) {

					String[] timeSplit = messageSplit[j].split(":");

					if (timeSplit.length == 2) {

						try {
							String minutes = (String) timeSplit[1].subSequence(
									0, 2);
							Integer.parseInt(minutes);
							// System.out.println(minutes);
							String hour = timeSplit[0];
							Integer.parseInt(hour);
							// System.out.println(hour);

							String newTime = hour + ":" + minutes;
							System.out.println(newTime);
							result.add(newTime);
							/*
							 * if (existsInList(result, newTime) == false) {
							 * 
							 * 
							 * }
							 */
						} catch (Exception e) {
							// System.out.println("false");
						}
						/*
						 * String minutes = (String) timeSplit[1].subSequence(
						 * 0, 2);
						 */
						/*
						 * String hour = timeSplit[0];
						 * System.out.println("Session at: " + hour + ":" +
						 * minutes); System.out
						 * .println(TwitterData.results.get(i).Message); String
						 * newTime = hour + ":" + minutes; if
						 * (existsInList(result, newTime) == false) {
						 * result.add(newTime); }
						 */
					}
				}

			}

			// }
		}
		return result;

	}

	public static ArrayList<String> extractEventDate(TwitterResult TwitterData,
			String EventHashTag) {

		ArrayList<String> result = new ArrayList();
		for (int i = 0; i < TwitterData.results.size(); i++) {

			// System.out.println(TwitterData.results.get(i).Message);
			// if (TwitterData.results.get(i).Message.contains(EventHashTag)) {
			Date date = null;
			if (TwitterData.results.get(i).Message.contains("Now")) {
				date = new Date();
				System.out.println(TwitterData.results.get(i).Message);
				DateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
				System.out.println(dateFormat2.format(date));
				if (existsInList(result, dateFormat2.format(date)) == false) {
					result.add(dateFormat2.format(date));
				}
			}

			String[] messageSplit = TwitterData.results.get(i).Message
					.toLowerCase().split(" ");
			for (int j = 0; j < messageSplit.length - 1; j++) {

				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

				try {
					if (messageSplit[j].lastIndexOf("/") == 4) {

						if (messageSplit[j].contains(",")
								|| messageSplit[j].contains(".")
								|| messageSplit[j].contains(":")) {

							messageSplit[j] = messageSplit[j].replace(',', ' ');
							messageSplit[j].replace(".", "");
							messageSplit[j].replace(":", "");

						}
						date = dateFormat.parse(messageSplit[j]);
						System.out.println(TwitterData.results.get(i).Message);
						DateFormat dateFormat2 = new SimpleDateFormat(
								"dd-MM-yyyy");
						System.out.println(dateFormat2.format(date));
						if (existsInList(result, dateFormat2.format(date)) == false) {
							result.add(dateFormat2.format(date));
						}
						// }
					}
				} catch (ParseException e1) {

					String[] monthsFull = new DateFormatSymbols().getMonths();
					String[] monthsShort = new DateFormatSymbols()
							.getShortMonths();

					for (int m = 0; m < monthsFull.length; m++) {
						// System.out.println(months[l] + "------");
						int l = m + 1;
						String dateStr;
						if ((messageSplit[j].equalsIgnoreCase(monthsFull[m]) | messageSplit[j]
								.equalsIgnoreCase(monthsShort[m]))
								& !messageSplit[j].isEmpty()) {

							dateStr = j + "";

							if (j - 2 >= 0
									&& messageSplit[j - 2].contains("th")) {

								dateStr = messageSplit[j - 2].split("th")[0]
										+ '-' + l;
							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains(",") && isInteger(messageSplit[j + 1]
											.split(",")[0]))) {
								if (new Integer(
										messageSplit[j + 1].split(",")[0]) <= 31)
									dateStr = messageSplit[j + 1].split(",")[0]
											+ '-' + l;
							}

							if (j - 1 >= 0
									&& isInteger(messageSplit[j - 1])
									| (messageSplit[j - 1].contains(",") && isInteger(messageSplit[j - 1]
											.split(",")[0]))) {

								dateStr = messageSplit[j - 1].split(",")[0]
										+ '-' + l;
							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j - 1].contains(",") && isInteger(messageSplit[j + 1]
											.split(",")[0]))) {

								dateStr = messageSplit[j + 1].split(",")[0]
										+ '-' + l;
							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains(",") && isInteger(messageSplit[j + 1]
											.split(",")[0]))) {
								if (new Integer(
										messageSplit[j + 1].split(",")[0]) <= 31)
									dateStr = messageSplit[j + 1].split(",")[0]
											+ '-' + l;
							}
							if (j - 1 >= 0
									&& isInteger(messageSplit[j - 1])
									| (messageSplit[j - 1].contains(". ") && isInteger(messageSplit[j - 1]
											.split(". ")[0]))) {

								dateStr = messageSplit[j - 1].split(". ")[0]
										+ '-' + l;
							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains(". ") && isInteger(messageSplit[j + 1]
											.split(". ")[0]))) {
								if (new Integer(
										messageSplit[j + 1].split(". ")[0]) <= 31)
									dateStr = messageSplit[j + 1].split(". ")[0]
											+ '-' + l;
							}

							if (isInteger(messageSplit[j + 1])) {
								dateStr = messageSplit[j + 1] + '-' + l;
							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains(": ") && isInteger(messageSplit[j - 1]
											.split(": ")[0]))) {
								dateStr = messageSplit[j + 1].split(": ")[0]
										+ '-' + l;
							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains(":") && isInteger(messageSplit[j - 1]
											.split(": ")[0]))) {
								dateStr = messageSplit[j + 1].split(":")[0]
										+ '-' + l;
							}
							if (j - 1 >= 0
									&& (messageSplit[j - 1].contains("-")
											&& messageSplit[j - 1].split("-").length > 0 && isInteger(messageSplit[j - 1]
											.split("-")[0]))) {

								dateStr = messageSplit[j - 1].split("-")[0]
										+ '-' + l;
							}
							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains("-")
											&& messageSplit[j + 1].split("-").length > 0 && isInteger(messageSplit[j + 1]
											.split("-")[0]))) {

								dateStr = messageSplit[j + 1].split("-")[0]
										+ '-' + l;

							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains("-")
											&& messageSplit[j + 1].split("-").length > 0 && isInteger(messageSplit[j + 1]
											.split("-")[0]))) {
								try {
									if (new Integer(
											messageSplit[j + 1].split(",")[0]) <= 31)
										dateStr = messageSplit[j + 1]
												.split("-")[0] + '-' + l;
								} catch (Exception e) {
								}
							}

							if (isInteger(messageSplit[j + 1])
									| (messageSplit[j + 1].contains("-")
											&& messageSplit[j + 1].split("-").length > 0 && isInteger(messageSplit[j + 1]
											.split("-")[0]))) {
								try {
									if (new Integer(
											messageSplit[j + 1].split(".")[0]) <= 31)
										dateStr = messageSplit[j + 1]
												.split("-")[0] + '-' + l;
								} catch (Exception e) {
								}
							}

							if (j - 1 >= 0
									&& messageSplit[j - 1].contains("th")) {
								if (messageSplit[j - 1].split("th").length > 0)
									dateStr = messageSplit[j - 1].split("th")[0]
											+ '-' + l;
							}

							if (messageSplit[j + 1].contains("th")) {
								if (messageSplit[j + 1].split("th").length > 0)
									dateStr = messageSplit[j + 1].split("th")[0]
											+ '-' + l;
							}

							if (j - 1 >= 0
									&& messageSplit[j - 1].contains("th ")) {
								if (messageSplit[j - 1].split("th ").length > 0)
									dateStr = messageSplit[j - 1].split("th ")[0]
											+ '-' + l;
							}

							if (messageSplit[j + 1].contains("th ")) {
								if (messageSplit[j + 1].split("th ").length > 0)
									dateStr = messageSplit[j + 1].split("th ")[0]
											+ '-' + l;
							}
							if (j - 1 >= 0
									&& messageSplit[j - 1].contains("nd")
									&& !messageSplit[j - 1].contains("Sunday")) {
								if (messageSplit[j - 1].split("nd").length > 0)
									dateStr = messageSplit[j - 1].split("nd")[0]
											+ '-' + l;
							}
							if (messageSplit[j + 1].contains("nd")
									&& !messageSplit[j + 1].contains("Sunday")) {
								if (messageSplit[j + 1].split("nd").length > 0)
									dateStr = messageSplit[j + 1].split("nd")[0]
											+ '-' + l;
							}
							if (j - 1 >= 0
									&& messageSplit[j - 1].contains("st")) {
								if (messageSplit[j - 1].split("st").length > 0)
									dateStr = messageSplit[j - 1].split("st")[0]
											+ '-' + l;
							}
							if (messageSplit[j + 1].contains("st")) {
								if (messageSplit[j + 1].split("st").length > 0)
									dateStr = messageSplit[j + 1].split("st")[0]
											+ '-' + l;
							}

							if (j - 1 >= 0
									&& messageSplit[j - 1].contains("rd")) {
								if (messageSplit[j - 1].split("rd").length > 0)
									dateStr = messageSplit[j - 1].split("rd")[0]
											+ '-' + l;
							}

							if (messageSplit[j + 1].contains("rd")) {
								if (messageSplit[j + 1].split("rd").length > 0)
									dateStr = messageSplit[j + 1].split("rd")[0]
											+ '-' + l;
							}

							if (dateStr.lastIndexOf("-") <= 2) {

								SimpleDateFormat year = new SimpleDateFormat(
										"yyyy");
								dateStr += "-"
										+ year.format(TwitterData.results
												.get(i).CreationDate);
							}

							try {
								DateFormat dateFormat2 = new SimpleDateFormat(
										"dd-MM-yyyy");

								Date date2 = dateFormat2.parse(dateStr);

								System.out
										.println(TwitterData.results.get(i).Message);
								System.out.println(dateFormat.format(date2));

								if (existsInList(result,
										dateFormat.format(date2)) == false) {
									result.add(dateFormat.format(date2));
								}
							} catch (Exception e) {
								System.out
										.println(TwitterData.results.get(i).Message);

								System.out.println("Error:" + dateStr);

							}
							System.out.println();

						}
					}

					// }

					// Time extraction*****************************8

				}

			}
		}
		return result;
	}

	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean findSubEvent(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
