package edgenode;
import IFM.AlchemyEngine;
import IFM.XMLConfiguration;


public class EdgeNodeLearner {

	public static void main(String[] args) {
		String configXMLfieldPath = "src/IFM/configurationIFM.xml";

		XMLConfiguration configuration = XMLConfiguration
				.instance(configXMLfieldPath);
		
		String path = configuration.ALCHEMY_PATH; 
		String factsForLearningFile = configuration.ALCHEMY_FACTS_FOR_LEARNING;
		String rulesDefinitionFile = configuration.ALCHEMY_RULES_DEFINITION;
		String learnedWeightsFile = configuration.ALCHEMY_LEARNED_WEIGHTS;
		
		try {
			AlchemyEngine.learn(path, factsForLearningFile, rulesDefinitionFile, learnedWeightsFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
