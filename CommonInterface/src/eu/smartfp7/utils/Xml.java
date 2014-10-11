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
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLOutputFactory;

public class Xml {

	public static String convertToJson(String xml_data) {
		return convertToJson(xml_data, new PrintWriter(System.err));
	}

	public static String convertToJson(String xml_data, PrintWriter out) {
		// Conversion based on https://github.com/beckchr/staxon/wiki/Converting-XML-to-JSON
		/*
		 * If we want to insert JSON array boundaries for multiple elements, we need to set the <code>autoArray</code>
		 * property. If our XML source was decorated with <code>&lt;?xml-multiple?&gt;</code> processing instructions,
		 * we'd set the <code>multiplePI</code> property instead.
		 */
		JsonXMLConfig config = new JsonXMLConfigBuilder().autoArray(true).prettyPrint(true).build();
		InputStream input = null;
		OutputStream output = null;
		String ret = "error";
		try {
			input = new ByteArrayInputStream(xml_data.getBytes("UTF-8"));
			output = new ByteArrayOutputStream();
			/*
			 * Create reader (XML).
			 */
			XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);

			/*
			 * Create writer (JSON).
			 */
			XMLEventWriter writer = new JsonXMLOutputFactory(config).createXMLEventWriter(output);

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
			out.print("<font color='red'>Unsupported encoding during conversion: XML to JSON.</font>");
		} catch (XMLStreamException se) {
			out.print("<font color='red'>XML Stream exception during conversion: XML to JSON.</font>");
			se.printStackTrace();
		} catch (FactoryConfigurationError fe) {
			out.print("<font color='red'>Factory configuration error during conversion: XML to JSON.</font>");
			fe.printStackTrace();
		} catch (TransformerFactoryConfigurationError tfe) {
			out.print("<font color='red'>Transformer factory configuration error during conversion: XML to JSON.</font>");
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

}
