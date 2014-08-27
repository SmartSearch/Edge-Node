package IFM;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

	
public class XMLConfiguration {

	public String ALCHEMY_PATH;
	public String ALCHEMY_DB;
	public String ALCHEMY_PATTERNS;
	public String ALCHEMY_RESULTS;
	
	public String ALCHEMY_LEARNED_WEIGHTS;
	public String ALCHEMY_FACTS_FOR_LEARNING;
	public String ALCHEMY_RULES_DEFINITION;

	public String NAME_SPACE;
	public String NAME_SPACE_VALUE;
	
	public String URLGet_CouchDB;
	public String URLPost_CouchDB;
	
	public Long REQUEST_PERIOD;
     

	private static XMLConfiguration INSTANCE = null;
	public static synchronized XMLConfiguration instance(String path) {
		if (INSTANCE == null) {
			INSTANCE = new XMLConfiguration(path);
		}
		return INSTANCE;
	}

	
	public XMLConfiguration(String path) {
		// TODO Auto-generated constructor stub
			
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			
			System.out.println("Read the file configurationIMF.xml");
			Document doc = db.parse(path);

			Element rootElement = doc.getDocumentElement();
			rootElement.normalize();
			
			//<?xml version="1.0"?>
			//<configuration>
			//	<alchemyPath>src/alchemy</alchemyPath>
			//	<alchemyDBPath>src/alchemy/mln.db</alchemyDBPath>
			//	<urlGetCouchDB>http://dusk.ait.gr/SMARTEdgeNode/retrieveXML?feed=aitathens</urlGetCouchDB>
			//	<limit>1</limit>
			//	<urlPostCouchDB>http://dusk.ait.gr/couchdb/aitathens</urlPostCouchDB>
			//</configuration>
			       
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
		
			setALCHEMY_PATH(      (String) xpath.compile("//configuration/alchemyPath/text()").evaluate(doc, XPathConstants.STRING));
			setALCHEMY_DB(        (String) xpath.compile("//configuration/alchemyDBPath/text()").evaluate(doc, XPathConstants.STRING));
			setALCHEMY_PATTERNS(  (String) xpath.compile("//configuration/alchemyMlnPatternsPath/text()").evaluate(doc, XPathConstants.STRING) );
			setALCHEMY_RESULTS(   (String) xpath.compile("//configuration/alchemyMlnResultsPath/text()").evaluate(doc, XPathConstants.STRING) );
			
			setALCHEMY_FACTS_FOR_LEARNING(      (String) xpath.compile("//configuration/alchemyFactsForLearning/text()").evaluate(doc, XPathConstants.STRING));
			setALCHEMY_LEARNED_WEIGHTS(      (String) xpath.compile("//configuration/alchemyLearnedWeights/text()").evaluate(doc, XPathConstants.STRING));
			setALCHEMY_RULES_DEFINITION(      (String) xpath.compile("//configuration/alchemyRulesDefiniton/text()").evaluate(doc, XPathConstants.STRING));
			
			setNAME_SPACE(        (String) xpath.compile("//configuration/nameSpace/prefix/text()").evaluate(doc, XPathConstants.STRING) );
			setNAME_SPACE_VALUE(  (String) xpath.compile("//configuration/nameSpace/value/text()").evaluate(doc, XPathConstants.STRING) );
			setURLGet_CouchDB(    (String) xpath.compile("//configuration/urlGetCouchDB/text()").evaluate(doc, XPathConstants.STRING) + "&limit=1"); 
			setURLPost_CouchDB(   (String) xpath.compile("//configuration/urlPostCouchDB/text()").evaluate(doc, XPathConstants.STRING)); 
			setRequestPeriod(     (Double) xpath.compile("//configuration/requestPeriod/text()").evaluate(doc, XPathConstants.NUMBER));
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	//--------------------------------------
	public String getALCHEMY_PATH() {
		return ALCHEMY_PATH;
	}

	public void setALCHEMY_PATH(String aLCHEMY_PATH) {
		ALCHEMY_PATH = aLCHEMY_PATH;
	}	
	
	public void setALCHEMY_LEARNED_WEIGHTS(String ALCHEMY_LEARNED_WEIGHTS) {
		this.ALCHEMY_LEARNED_WEIGHTS = ALCHEMY_LEARNED_WEIGHTS;
	}
	
	public void setALCHEMY_RULES_DEFINITION(String ALCHEMY_RULES_DEFINITION) {
		this.ALCHEMY_RULES_DEFINITION = ALCHEMY_RULES_DEFINITION;
	}
	
	public void setALCHEMY_FACTS_FOR_LEARNING(String ALCHEMY_FACTS_FOR_LEARNING) {
		this.ALCHEMY_FACTS_FOR_LEARNING = ALCHEMY_FACTS_FOR_LEARNING;
	}
	
	//-------------------------------------
	public String getALCHEMY_DB() {
		return ALCHEMY_DB;
	}

	public void setALCHEMY_DB(String alchemy_DB) {
		ALCHEMY_DB = alchemy_DB;
	}
	
	//---------------------------------------
	public String getALCHEMY_PATTERNS() {
		return ALCHEMY_PATTERNS;
	}

	public void setALCHEMY_PATTERNS(String aLCHEMY_PATTERNS) {
		ALCHEMY_PATTERNS = aLCHEMY_PATTERNS;
	}
	
	//--------------------------------------
	public String getALCHEMY_RESULTS() {
		return ALCHEMY_RESULTS;
	}


	public void setALCHEMY_RESULTS(String aLCHEMY_RESULTS) {
		ALCHEMY_RESULTS = aLCHEMY_RESULTS;
	}
	
	public String getNAME_SPACE() {
		return NAME_SPACE;
	}


	public void setNAME_SPACE(String nAME_SPACE) {
		NAME_SPACE = nAME_SPACE;
	}
	
	public String getNAME_SPACE_VALUE() {
		return NAME_SPACE_VALUE;
	}


	public void setNAME_SPACE_VALUE(String nAME_SPACE_VALUE) {
		NAME_SPACE_VALUE = nAME_SPACE_VALUE;
	}
	
	//---------------------------------------
	public String getURLGet_CouchDB() {
		return URLGet_CouchDB;
	}

	public void setURLGet_CouchDB(String uRLGet_CouchDB) {
		URLGet_CouchDB = uRLGet_CouchDB;
	}

	//----------------------------------------
	public String getURLPost_CouchDB() {
		return URLPost_CouchDB;
	}


	public void setURLPost_CouchDB(String uRLPost_CouchDB) {
		URLPost_CouchDB = uRLPost_CouchDB;
	}
	
	public void setRequestPeriod(Double period) {
		REQUEST_PERIOD = period.longValue();
	}
	
	
	
	
}
