package edgenode.fileDB;

import IFM.AlchemyEngine;

public class FileDBClient {
	

	private String factsForLearningFile, rulesDefinitionFile, learnedWeightsFile;
	private AlchemyEngine engine;
	
	
	
	
	public FileDBClient(String factsFile, String rulesFile,
			String learnedWeightsFile, AlchemyEngine engine) {
		this.factsForLearningFile = factsFile;
		this.rulesDefinitionFile = rulesFile;
		this.learnedWeightsFile = learnedWeightsFile;
		this.engine = engine;
	}


	public void run() {
		// Read the observations from the inputFile
		String observations = "";
		
		try {
			String alchemyResult  = engine.infer(observations);
			
			// write alchemyResult to outputFile
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
