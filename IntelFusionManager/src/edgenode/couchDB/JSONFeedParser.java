package edgenode.couchDB;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.jamesmurty.utils.XMLBuilder;

public class JSONFeedParser {

	public static String parseFromJSONToAlchemy(Document xmlFeed)
			throws Exception {
		// Parse XML to JSON

		DOMSource domSource = new DOMSource(xmlFeed);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		String xml = writer.toString();
		// System.out.println(writer.toString() );
		
		int endOfFirstLine = xml.indexOf('\n');
		int beginingOfLastLine = xml.lastIndexOf("</rdf>");
		xml = xml.substring(endOfFirstLine + 1, beginingOfLastLine - 1);
		
		JSONObject rootElement = XML.toJSONObject(xml);
		JSONObject measurementTree = rootElement.getJSONObject("measurement");
		String measurementTime = measurementTree.getString("time");
		JSONArray crowd = measurementTree.getJSONArray("crowd");
		
		StringBuilder observations = new StringBuilder();
		for(int i = 0; i < Math.min(crowd.length()-1,1); i++) {
			JSONObject crowdMeasurements = crowd.getJSONObject(i);
			Double densityValue = (Double) crowdMeasurements.get("density");
			System.out.print("Density:" + densityValue);
			String densityString = "";
			if(densityValue < 0.015)
				densityString = "Low";
			else
				densityString = "High";
			observations.append("CrowdDensityMeasurementValue(" + densityString + ")\n");
		}
		System.out.println(", time: "+ measurementTime);		
		return observations.toString();
	}

	public static long parseTimeFromXML(Document xmlFeed)
			throws Exception {
		// Parse XML to JSON

		DOMSource domSource = new DOMSource(xmlFeed);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		String xml = writer.toString();
		// System.out.println(writer.toString() );
		
		int endOfFirstLine = xml.indexOf('\n');
		int beginingOfLastLine = xml.lastIndexOf("</rdf>");
		xml = xml.substring(endOfFirstLine + 1, beginingOfLastLine - 1);
		
		JSONObject rootElement = XML.toJSONObject(xml);
		JSONObject measurementTree = rootElement.getJSONObject("measurement");
		return TimeConvert.string2millis(measurementTree.getString("time"));
	}

	public static String parseFromAlchemyToJSON(String alchemy, long timestamp) {
		JsonObject jsonResult = new JsonObject();

		String timeString = String.valueOf(timestamp);

		System.out.println("//---------- Alchemy Output ---------///");
		System.out.println(alchemy);
		System.out.println("//---------- -------------- ---------///");
		Scanner br = new Scanner(alchemy);

		while (br.hasNextLine()) {
			String sCurrentLine = br.nextLine();
			System.out.println(sCurrentLine);

			int pos1 = sCurrentLine.indexOf("(", 0);
			int pos2 = sCurrentLine.indexOf(",", 0);
			int pos3 = sCurrentLine.indexOf(")", 0);

			String Subject = sCurrentLine.substring(pos1 + 1, pos2)
					+ timeString;
			String Predicate = sCurrentLine.substring(0, pos1);
			String Value = sCurrentLine.substring(pos2 + 1, pos3);
			String Prob = sCurrentLine.substring(pos3 + 2, pos3 + 6);

			System.out.println("predicate: " + Predicate);
			System.out.println("subject: " + Subject);
			System.out.println("object: " + Value);
			System.out.println("Probability: " + Prob);
			System.out
					.println("------------------------------------------------------------------------------------");

			float prob = Float.parseFloat(Prob);
			if (prob > 0.0) {
				JsonObject reasonedValue = new JsonObject();
				reasonedValue.add(Predicate, new JsonPrimitive(Prob));
				jsonResult.add("_id",new JsonPrimitive(TimeConvert.millis2String(timestamp)));				
				jsonResult.add("timestamp",new JsonPrimitive(timestamp));				
				jsonResult.add("Probability", reasonedValue);
			}
		}

		return jsonResult.toString();
	}

	private static String buildRDF_JSON(Model RDFmodel) {

		// From http://www.w3.org/wiki/TriplesInJSON
		String queryString = "SELECT ?s ?p ?o  " + "WHERE { ?s ?p ?o }";

		Query q = QueryFactory.create(queryString);

		// Create a SPARQL-DL query execution for the given query and
		// ontology model
		QueryExecution qe = SparqlDLExecutionFactory.create(q, RDFmodel);

		// Print the query for better understanding
		System.out.println(q.toString());

		// We want to execute a SELECT query, do it, and return the result set
		ResultSet resultSet = qe.execSelect();

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(b, resultSet);
		String json = b.toString();

		System.out.println(json);

		return json;
	}

	private static String buildXML_JSON(Model RDFmodel)
			throws ParserConfigurationException, FactoryConfigurationError,
			TransformerException, FileNotFoundException {

		// An example of Json
		// {
		// "_id": "2013-25-01T15:00:55.861Z",
		// "_rev": "1-4983d5be99f64ca767c57c18c8dc881a",
		// "timestamp": 1359126055861,
		// "data": {
		// "time": "2012-07-26T10:44:00.334Z",
		// "notification": {
		// "@ID": " ID1359126055861",
		// "level": "high"
		// }
		// }
		// }

		// Previous version of JSON/XML

		/*
		 * long timestamp = System.currentTimeMillis(); // // Convert the Unix
		 * time into AIT's XML date // DateFormat formatter = new
		 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Calendar cal =
		 * Calendar.getInstance(); // cal.setTimeInMillis(timestamp); // String
		 * last3digits = String.valueOf(timestamp); // int length =
		 * last3digits.length(); // int index = length-3; // last3digits =
		 * last3digits.substring(index); // String Xmltime =
		 * formatter.format(cal.getTime()) + "." +last3digits + "Z"; // // //
		 * String json = new String(); // json = json.concat("{ "); // json =
		 * json.concat("\"_id\": \""+ Xmltime + "\"," ); // json =
		 * json.concat("\"timestamp\": "); // json =
		 * json.concat(String.valueOf(timestamp)); // json = json.concat(", ");
		 * // json = json.concat("\"data\":  {"); // json =
		 * json.concat("\"time\" :"); // json = json.concat(" \"" + timestamp +
		 * "\", "); //resultTime.item(0).getTextContent() // // // list the
		 * statements in the Model // StmtIterator iter =
		 * RDFmodel.listStatements(); // // // print out the predicate, subject
		 * and object of each statement // while (iter.hasNext()) { // //
		 * Statement stmt = iter.nextStatement(); // // json =
		 * json.concat("\"notification\" : {"); // json =
		 * json.concat("\"@ID\" : " + " \"" + stmt.getSubject().toString() +
		 * "\", "); // json = json.concat("\"level\" : " + "\"" +
		 * stmt.getObject().toString() + "\""); // json = json.concat(" } "); //
		 * }
		 */

		long timestamp = System.currentTimeMillis();
		// Convert the Unix time into AIT's XML date
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		String last3digits = String.valueOf(timestamp);
		int length = last3digits.length();
		int index = length - 3;
		last3digits = last3digits.substring(index);
		String Xmltime = formatter.format(cal.getTime()) + "." + last3digits
				+ "Z";

		String xml = new String();
		StmtIterator iter = RDFmodel.listStatements();
		Statement stmt = iter.nextStatement();

		// stmt.getSubject().toString()

		XMLBuilder xml1 = XMLBuilder.create("Feed").e("Id")
				.t("reasoning_feed_imperial").up()

				.e("Type").t("Virtual").up()

				.e("Title").t("reasoning_feed_imperial").up()

				.e("Description")
				.t("Notification regarding the type of an event").up()

				.e("Outputs").e("Output").e("Name").t("event").up()
				.e("ProducedBy").t("alchemy_reasoner").up().e("Type")
				.t("double").up().e("HasConfidence").t("true").up().up().up();
		// .a("language", "Java")
		// .a("scm","SVN")
		// .e("Location")
		// .a("type", "URL")
		// .t("http://code.google.com/p/java-xmlbuilder/")
		// .up()
		// .up()
		// .e("JetS3t")
		// .a("language", "Java")
		// .a("scm","CVS")
		// .e("Location")
		// .a("type", "URL")
		// .t("http://jets3t.s3.amazonaws.com/index.html");

		// xml = xml.concat(" } }");

		// xml= xml.concat(" } }");

		System.out.println(xml1);

		Properties outputProperties = new Properties();

		PrintWriter writer = new PrintWriter(new FileOutputStream(
				"projects.xml"));
		xml1.toWriter(writer, outputProperties);
		outputProperties.put(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
		// PrintWriter writer = new PrintWriter(new
		// FileOutputStream("projects.xml"));
		xml = xml1.asString(outputProperties);

		return xml;
	}

}
