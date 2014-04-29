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
 
package eu.smartfp7.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

public class Json {

	public static String convertToXml(String json_data) {
		return convertToXml(json_data, new PrintWriter(System.err));
	}

	public static String convertToXml(String json_data, PrintWriter out) {
		// Conversion based on https://github.com/beckchr/staxon/wiki/Converting-XML-to-JSON
		/*
		 * If the <code>multiplePI</code> property is set to <code>true</code>, the StAXON reader will generate
		 * <code>&lt;xml-multiple&gt;</code> processing instructions which would be copied to the XML output. These can
		 * be used by StAXON when converting back to JSON to trigger array starts. Set to <code>false</code> if you
		 * don't need to go back to JSON.
		 */
		JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).build();
		InputStream input = null;
		OutputStream output = null;
		String ret = "error";
		try {
			input = new ByteArrayInputStream(json_data.getBytes("UTF-8"));
			output = new ByteArrayOutputStream();
			/*
			 * Create reader (JSON).
			 */
			XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);

			/*
			 * Create writer (XML).
			 */
			XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
			writer = new PrettyXMLEventWriter(writer); // format output

			/*
			 * Copy events from reader to writer.
			 */
			writer.add(reader);

			/*
			 * Close reader/writer.
			 */
			reader.close();
			writer.close();
			ret = output.toString();
		} catch (UnsupportedEncodingException ue) {
			out.print("<font color='red'>Unsupported encoding during conversion: JSON to XML.</font>");
		} catch (XMLStreamException se) {
			out.print("<font color='red'>XML Stream exception during conversion: JSON to XML.</font>");
			se.printStackTrace();
		} catch (FactoryConfigurationError fe) {
			out.print("<font color='red'>Factory configuration error during conversion: JSON to XML.</font>");
			fe.printStackTrace();
		} catch (TransformerFactoryConfigurationError tfe) {
			out.print("<font color='red'>Transformer factory configuration error during conversion: JSON to XML.</font>");
			tfe.printStackTrace();
		} finally {
			/*
			 * As per StAX specification, XMLEventReader/Writer.close() doesn't close the underlying stream.
			 */
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
				}
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
				}
		}
		return ret;
	}

	/**Get the string value of a simple text key surrounded by quotes.
	 * More advanced key searches should be done using a json library.
	 * @param json_data The String to be searched
	 * @param key The key String
	 * @return The retrieved value or null if not found
	 */
	public static String getSimpleTextKey(String json_data, String key) {
		int searchStart;
		int beginIndex = json_data.indexOf("\"" + key + "\"");
		if (beginIndex < 0)
			return null;
		searchStart = beginIndex + key.length() + 2;
		int separatorIndex = json_data.substring(searchStart).indexOf(':');
		if (separatorIndex < 0)
			return null;
		searchStart = searchStart + separatorIndex + 1;
		int dataStart = json_data.substring(searchStart).indexOf("\"");
		if (dataStart < 0)
			return null;
		searchStart = searchStart + dataStart + 1;
		int dataEnd = json_data.substring(searchStart).indexOf("\"");
		if (dataEnd < 0)
			return null;
		return json_data.substring(searchStart, searchStart + dataEnd);
	}

	/**Replace the string value of a simple text key surrounded by quotes.
	 * More advanced replacements should be done using a json library.
	 * @param json_data The String to be searched
	 * @param key The key String
	 * @param replacement The replacement String
	 * @return The source string if key not found or an error happens, the updated string if successful
	 */
	public static String replaceSimpleTextKey(String json_data, String key, String replacement) {
		int searchStart;
		int beginIndex = json_data.indexOf("\"" + key + "\"");
		if (beginIndex < 0)
			return json_data;
		searchStart = beginIndex + key.length() + 2;
		int separatorIndex = json_data.substring(searchStart).indexOf(':');
		if (separatorIndex < 0)
			return json_data;
		searchStart = searchStart + separatorIndex + 1;
		int dataStart = json_data.substring(searchStart).indexOf("\"");
		if (dataStart < 0)
			return json_data;
		searchStart = searchStart + dataStart + 1;
		int dataEnd = json_data.substring(searchStart).indexOf("\"");
		if (dataEnd < 0)
			return json_data;
		return json_data.substring(0,searchStart) + replacement + json_data.substring(searchStart + dataEnd,json_data.length());
	}
	
	/**Adds the specified key-value pair to the beginning of json String
	 * More advanced replacements should be done using a json library.
	 * @param json_data The source String
	 * @param keyvalue A String containing the key-value pair as <b>"key" : "value"</b>, no check is done for correct form
	 * @return The source string if an error happens, the updated string if successful
	 */
	public static String addKeyValue(String json_data, String keyvalue) {
		if (json_data.charAt(0) != '{')
			return json_data;
		return "{\n\t" + keyvalue + "," + json_data.substring(1);
	}
}
