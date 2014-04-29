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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This class contains commonly used functions
 * 
 * @author Nikolaos Katsarakis nkat@ait.edu.gr
 * 
 */
public class Common {

	static Calendar cal=new GregorianCalendar(TimeZone.getTimeZone("UTC"));

	/**
	 * Returns a cleaned string by first setrieving the part of input string after the latest ":" and cleaning it up so
	 * that it only contains latin chars a-z, numbers 0-9 and underscores "_"
	 * 
	 * @param inputStr
	 *            The String to clean up
	 * @return Clean String
	 */
	public static String cleanString(String inputStr) {
		// Get the last part of the string
		String[] array = inputStr.toLowerCase().split(":");
		String id2 = array[array.length - 1].trim();
		// Remove whitespace
		id2 = id2.replaceAll("\\s", "");

		// Replace any non latin alphanum chars with underscores
		id2 = id2.replaceAll("[^a-z0-9_]", "_");

		return id2;
	}

	/**
	 * Converts given string to milliseconds from Unix Epoch
	 * 
	 * @param lexicalXSDDateTime
	 *            - A string containing lexical representation of xsd:datetime
	 * @return Milliseconds since 1970-01-01
	 */
	public static long string2millis(String lexicalXSDDateTime) {
		long res;
		try {
			res = javax.xml.bind.DatatypeConverter.parseDateTime(lexicalXSDDateTime).getTimeInMillis();
		} catch (IllegalArgumentException e) {
			return -1;
		}
		return res;
	}

	public static String millis2String(long millis) {
		int fixMillis = 0;
		String xmlDateTime;
		/*
		 * If the millis is divisible by 1000, printDateTime does not add a decimal printout. This workaround fixes this
		 * by adding a millisecond before printing
		 */
		if (millis % 1000 == 0)
			fixMillis = 1;
		synchronized (cal) {
			cal.setTimeInMillis(millis+fixMillis);
			xmlDateTime = javax.xml.bind.DatatypeConverter.printDateTime(cal);
		}
		// If fixMillis was set to 1, fix the printout
		if (fixMillis==1)
			xmlDateTime = xmlDateTime.replace(".001Z", ".000Z");
		return xmlDateTime;
	}
}
