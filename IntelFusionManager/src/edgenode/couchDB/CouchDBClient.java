package edgenode.couchDB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;

import IFM.AlchemyEngineInterface;

public class CouchDBClient implements Runnable {

	private AlchemyEngineInterface engine;
	private List<String> sourcesURL;
	String resultsServer;
	Long requestPeriod; // in milliseconds
	
	long lastTimeStamp=0;

	public CouchDBClient(AlchemyEngineInterface engine, List<String> sourcesURL,
			String resultsServer, Long requestPeriod) {
		this.engine = engine;
		this.sourcesURL = sourcesURL;
		this.resultsServer = resultsServer;
		this.requestPeriod = requestPeriod;
	}

	private String requestDataAllSources() {
		StringBuilder aggregatedData = new StringBuilder();
		for (String dataSource : sourcesURL) {
			try {
				Document dataFeed = requestDataSingleSource(dataSource);
//				String parsedFeed = XMLFeedParser.parseFromXMLToAlchemy(dataFeed);
				String parsedFeed = JSONFeedParser.parseFromJSONToAlchemy(dataFeed);
				aggregatedData.append(parsedFeed);
				lastTimeStamp = JSONFeedParser.parseTimeFromXML(dataFeed);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String observations = aggregatedData.toString();

		return observations;
	}

	private void recurrentInference() {
		String observations = requestDataAllSources();
		System.out.println("/--------    Observations      ---------/");
		System.out.println(observations);
		System.out.println("/---------------------------------------/");
		String alchemyResult;
		try {
			alchemyResult = engine.infer(observations);		
			System.out.println(alchemyResult);
			String line=null;
			BufferedReader br= new BufferedReader(new StringReader(alchemyResult));
			double[] arr = new double[2];
			int i=0;
			while( (line=br.readLine())!=null){
				arr[i]= Double.parseDouble(line.split(" ")[1]);
			}
			postInferenceResult(alchemyResult);
		} catch (Exception e) {
			// TODO send error message
			e.printStackTrace();
		}

	}

	private void postInferenceResult(String result) throws FileNotFoundException, ParserConfigurationException, FactoryConfigurationError, TransformerException {
//		String xmlResult = XMLFeedParser.parseFromAlchemyToXML(result);
		String xmlResult = JSONFeedParser.parseFromAlchemyToJSON(result, lastTimeStamp);

		System.out.println("/--------    Post Result      ---------/");
		System.out.println(xmlResult);
		System.out.println("/---------------------------------------/");
		
		HttpClient httpClient = new DefaultHttpClient();
		try {

			HttpPost request = new HttpPost(resultsServer);
			request.addHeader("content-type", "application/json");

			StringEntity params = new StringEntity(xmlResult);
			request.setEntity(params);

			HttpResponse response = httpClient.execute(request);

			System.out.println(response.toString());

			// handle response here...
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	private Document requestDataSingleSource(String dataSource)
			throws Exception {

		// -----------------
		// Get the XML file
		// ----------------
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		// http://dusk.ait.gr/SMARTEdgeNode/retrieveXML?feed=telestoarduinofeed&limit=100
		System.out.println("Retrieving data from "+ dataSource);
		Document doc = db.parse(new URL(dataSource).openStream());
		
		
		return doc;
	}

	public void run() {
		while (true) {
			recurrentInference();
			try {
				Thread.sleep(requestPeriod);
			} catch (InterruptedException e) {
			}
		}
	}

}
