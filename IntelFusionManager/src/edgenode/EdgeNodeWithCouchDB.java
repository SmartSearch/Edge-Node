package edgenode;

import java.util.LinkedList;
import java.util.List;

import edgenode.couchDB.CouchDBClient;
import IFM.AlchemyEngine;
import IFM.AlchemyEngineInterface;
import IFM.XMLConfiguration;

public class EdgeNodeWithCouchDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Testing the IMF
		testIFM();

	}

	private static void testIFM() {
		String configXMLfieldPath = "src/IFM/configurationIFM.xml";

		XMLConfiguration configuration = XMLConfiguration
				.instance(configXMLfieldPath);

		AlchemyEngineInterface alchemy = new AlchemyEngine(
				configuration.ALCHEMY_DB, configuration.ALCHEMY_PATTERNS,
				configuration.ALCHEMY_RESULTS, configuration.ALCHEMY_PATH);

		List<String> listOfServers = new LinkedList<String>();
		listOfServers.add(configuration.URLGet_CouchDB);

		CouchDBClient client = new CouchDBClient(alchemy, listOfServers,
				configuration.URLPost_CouchDB, configuration.REQUEST_PERIOD);

		client.run();
	}
}
