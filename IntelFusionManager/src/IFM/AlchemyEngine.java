package IFM;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

public class AlchemyEngine implements AlchemyEngineInterface {

	private String observationsFile;
	private String learnedWeightsFile;
	private String resultsFile;
	private String path;

	public AlchemyEngine(String observationsFile, String learnedWeightsFile,
			String resultsFile, String path) {
		this.observationsFile = observationsFile;
		this.learnedWeightsFile = learnedWeightsFile;
		this.resultsFile = resultsFile;
		this.path = path;
	}

	public String infer(String observations) throws Exception {
		// writes observations to a file
		// executes alchemy infer, which outputs results to a file
		// reads result from a file and returns as a string

		writeObservationsToFile(observations);
		runAlchemyForInference("Notification"); 
		String result = readResultsFromFile();

		return result;
	}

	private String readResultsFromFile() throws FileNotFoundException {
		FileInputStream result = new FileInputStream(resultsFile);
		StringBuilder fileContent = new StringBuilder();
		Scanner reader = new Scanner(result);

		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			fileContent.append(line);
			fileContent.append("\n");
		}

		return fileContent.toString();
	}

	private void writeObservationsToFile(String observations)
			throws IOException {
		FileWriter fstream = new FileWriter(observationsFile);
		fstream.write(observations);
		fstream.close();
	}

	public void runAlchemyForInference(String Query) throws Exception {
		CommandLine cmdLine = new CommandLine(path + "infer");
		cmdLine.addArgument("-i");
		cmdLine.addArgument(learnedWeightsFile); // "src/alchemy/learnedpatterns.mln"
		cmdLine.addArgument("-e");
		cmdLine.addArgument(observationsFile);
		cmdLine.addArgument("-r");
		cmdLine.addArgument(resultsFile); // "src/alchemy/alert.results"
		cmdLine.addArgument("-q");
		cmdLine.addArgument(Query); // "alert"
		cmdLine.addArgument("-maxSteps");
		cmdLine.addArgument("1000");

		try {

			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
			Executor executor = new DefaultExecutor();
			executor.setExitValue(1);
			executor.setWatchdog(watchdog);
			executor.execute(cmdLine, resultHandler);
			resultHandler.waitFor();

		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void learn(String path, String factsForLearningFile,
			String rulesDefinitionFile, String learnedWeightsFile)
			throws Exception {
		CommandLine cmdLine = new CommandLine(path + "/learnwts");
		cmdLine.addArgument("-i");
		cmdLine.addArgument(factsForLearningFile);
		cmdLine.addArgument("-o");
		cmdLine.addArgument(rulesDefinitionFile);
		cmdLine.addArgument("-t");
		cmdLine.addArgument(learnedWeightsFile);
		cmdLine.addArgument("-g -multipleDatabases");
		
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		executor.execute(cmdLine, resultHandler);
		resultHandler.waitFor();
	}
}
