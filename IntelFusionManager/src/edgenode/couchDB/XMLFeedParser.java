package edgenode.couchDB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.jamesmurty.utils.XMLBuilder;

public class XMLFeedParser {

	public static String parseFromXMLToAlchemy(Document xmlFeed)
			throws Exception {
		// Parse XML to Jena
		System.out.println("before parseFromXMLToAlchemy");

		Model model = null;
		model = ModelFactory.createDefaultModel();

		Element rootElement = xmlFeed.getDocumentElement();
		rootElement.normalize();

		// <measurement>
		// <time>2013-02-28T15:18:56.644Z</time>
		// <Thermometer ID="temperature_sensor">
		// <temperature>27</temperature>
		// </Thermometer>
		// <Humidity ID="humidity_sensor">
		// <humidity>31</humidity>
		// </Humidity>
		// </measurement>

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		// XPathExpression xpathTime =
		// xpath.compile("//measurement/time/text()");

		// ------------------
		// Build RDF triples
		// ------------------
		String Subject = "AITroom"; // Should be the ID of the ROI (Region
									// of Interest))
		String Predicate = "CrowdDensityMeasurementValue";
		String Value = (String) xpath.compile(
		// "//measurement/Thermometer/temperature/text()").evaluate(
		// "//measurement/Humidity/humidity/text()").evaluate(
				"//measurement/crowd/density/text()").evaluate(

		xmlFeed, XPathConstants.STRING);

		Resource SubjectLit = model.createResource(Subject);
		Resource ValueLit = model.createResource(Value);
		Property PropertyLit = model.createProperty(Predicate);

		// TO FINISH

		// Can also create statements directly . . .
		Statement statement = model.createStatement(SubjectLit, PropertyLit,
				ValueLit);

		// but remember to add the created statement to the model
		model.add(statement);

		// ---- Write Model to MLN File Format

		// list the statements in the Model
		StmtIterator iter = model.listStatements();
		StringBuilder parsedModel = new StringBuilder();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {

			Statement stmt = iter.nextStatement(); // get next statement
			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object

			// String rdfstring = new String(predicate.toString() + "("
			// + subject.toString() + "," + object.toString() + ")");

			// If the measure is lower than a threshold put crowdDensity as low
			// else high
			double threshold = 0.2;
			// double objectInt=0.001;
			String objectString = object.toString();
			double objectDouble = Double.parseDouble(objectString);
			
			
			if (objectDouble > threshold) {
				objectString = "High";
			}
			else{
				objectString="Low";
			}
			

			String rdfstring = new String(predicate.toString() + "("
					+ objectString + ")");

			parsedModel.append(rdfstring);
			parsedModel.append("\n");
			System.out.println("before break");
			break;
//			System.out.println("after break");

		}

		return parsedModel.toString();
	}

	public static String parseFromAlchemyToXML(String alchemy) {
		 Model model = ModelFactory.createDefaultModel();

		long timestamp = System.currentTimeMillis();
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
			if (prob > 0.65) {

				Resource SubjectLit = model.createResource(Subject);
				Resource ValueLit = model.createResource(Value);
				Property PropertyLit = model.createProperty("rdf:type");

				// Can also create statements directly . . .
				Statement statement = model.createStatement(SubjectLit,
						PropertyLit, ValueLit);
				// but remember to add the created statement to the model
				model.add(statement);

				// For the probability measure
				Resource SubjectLitPr = model.createResource(Subject);
				Resource ValueLitPr = model.createResource(Prob);
				Property PropertyLitPr = model
						.createProperty("smart:fp7:ProbabilityMeasurementValue");

				// Can also create statements directly . . .
				Statement statementPr = model.createStatement(SubjectLitPr,
						PropertyLitPr, ValueLitPr);

				model.add(statementPr);

			}
		}

		String rdfResult = buildRDF_JSON(model);

		return rdfResult;
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
