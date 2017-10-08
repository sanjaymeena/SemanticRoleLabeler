/**
 * 
 */
package com.emotibot.srl;

import static com.emotibot.srl.server.Constants.CONLL_TRAINING_DATA_DIR;
import static com.emotibot.srl.server.Constants.FINAL_TRAINING_DATA_FILE;
import static com.emotibot.srl.server.Constants.HIT_TRAINING_DATA_DIR;
import static com.emotibot.srl.server.Constants.SENTENCES_DIR;
import static com.emotibot.srl.server.Constants.SRL_BAD_CASES_DIRECTORY;
import static com.emotibot.srl.server.Constants.SRL_MODEL_FILE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.format.DataFormatOptions;

import se.lth.cs.srl.Learn;
import se.lth.cs.srl.options.LearnOptions;
import se.lth.cs.srl.util.Util;

/**
 * @author Sanjay
 *
 *         This Class is kind of a wrapper to perform necessary steps before
 *         beginning the actual training process.
 *
 *         1) Convert from HIT to CONLL Format 2) Create one training file from
 *         multiple training files
 */
public class TrainSRL {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		try{
		TrainSRL tdp = new TrainSRL();

		tdp.trainSRLModel();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * 
	 * @throws Exception
	 */
	private void trainSRLModel() throws Exception {

		/**
		 * 1) Convert from HIT to CONLL Format 2) Create one training file from
		 * multiple training files
		 */
		//prepareTrainingData();

		// Train model. the parameters are defined in the function for now.
		trainModel();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void trainModel() throws IOException {

		// Train SRL Model based on the new data file.

		// create training data arguments

		String lang = "chi";
		// String training_file = "data/emotibot/conll_format_1000_cases.txt";
		String training_file = FINAL_TRAINING_DATA_FILE;
		String output_model_file = SRL_MODEL_FILE;
		String feature_dir = "-fdir resources/semantic_role_labeling/featuresets/chi";

		String parameters = lang + " " + training_file + " " + output_model_file + " " + feature_dir;
		String[] argumentList = parameters.split(" ");

		// LinkedList<String> argumentList = new LinkedList<String>();
		// argumentList.add(lang);
		// argumentList.add(training_file);
		// argumentList.add(output_model_file);
		// argumentList.add(feature_dir);
		// String[] vars = argumentList.toArray(new
		// String[argumentList.size()]);

		// int t = 10;

		long startTime = System.currentTimeMillis();

		Learn.learnOptions = new LearnOptions(argumentList);
		Learn.learn();
		System.out
				.println("Total time consumtion: " + Util.insertCommas(System.currentTimeMillis() - startTime) + "ms");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void trainModel(String lang, String training_file,String output_model_file,String feature_dir) throws IOException {

		// Train SRL Model based on the new data file.

		// create training data arguments

//		String lang = "chi";
//		// String training_file = "data/emotibot/conll_format_1000_cases.txt";
//		String training_file = FINAL_TRAINING_DATA_FILE;
//		String output_model_file = SRL_MODEL_FILE;
//		String feature_dir = "-fdir resources/semantic_role_labeling/featuresets/chi";

		String parameters = lang + " " + training_file + " " + output_model_file + " " + feature_dir;
		String[] argumentList = parameters.split(" ");

		// LinkedList<String> argumentList = new LinkedList<String>();
		// argumentList.add(lang);
		// argumentList.add(training_file);
		// argumentList.add(output_model_file);
		// argumentList.add(feature_dir);
		// String[] vars = argumentList.toArray(new
		// String[argumentList.size()]);

		// int t = 10;

		long startTime = System.currentTimeMillis();

		Learn.learnOptions = new LearnOptions(argumentList);
		Learn.learn();
		System.out
				.println("Total time consumtion: " + Util.insertCommas(System.currentTimeMillis() - startTime) + "ms");
	}
	
	

	/**
	 * Prepare training data in a single file . <br>
	 * Steps:
	 * <ul>
	 * <li>1. Convert from HIT to CONLL Format
	 * <li>2. Create single training data file by concatenating multiple files
	 * 
	 * @throws Exception
	 */
	public void prepareTrainingData() throws Exception {

		// TODO Auto-generated method stub
		// String inputDir = "data/emotibot/bad_cases_training_hit";
		// String outputDir = "data/emotibot/bad_cases_training_conll";

		// public static String
		// SRL_BAD_CASES_DIRECTORY="resources/emotibot-srl/srl_bad_cases";

		String rootDir = HIT_TRAINING_DATA_DIR;
		String inputDir = HIT_TRAINING_DATA_DIR;
		String sentencesDir=SENTENCES_DIR;
		String outputDir = CONLL_TRAINING_DATA_DIR;
		String goodCasesDir = CONLL_TRAINING_DATA_DIR;

		// convert directory recursively
		boolean isRecursive = true;

		DataFormatOptions options = new DataFormatOptions();
		options.rootDirectory = rootDir;
		options.inputDir = inputDir;
		options.outputDir = outputDir;
		
		options.sentencesDir=sentencesDir;
		
		options.goodCasesHITDir = goodCasesDir;
		options.isRecursive = true;
		options.onlyGoodCases = true;

		// Step 1 : Convert HIT to CONLL
		HITtoCONLLCoversion(options);

		// Step 2 : Concatenate all files to one file

		String outputfile = FINAL_TRAINING_DATA_FILE;
		concatenateCONLLFiles(outputDir, outputfile);

		// also put all the data in bad case file . The srl bad cases is same as
		// training data file
		outputfile = SRL_BAD_CASES_DIRECTORY + File.separator + "srl_bad_cases.txt";
		concatenateCONLLFiles(outputDir, outputfile);

	}

	/**
	 * This function will concatenate all files with conll sentences into a
	 * single file
	 * 
	 * @param outputDir
	 * @param outputfile
	 * @throws IOException
	 */
	public void concatenateCONLLFiles(String outputDir, String outputfile) throws IOException {
		// TODO Auto-generated method stub
		File rootDir = new File(outputDir);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		DataFormatConverter converter = new DataFormatConverter();
		ArrayList<CoNLLSentence> corpus = new ArrayList<CoNLLSentence>();

		// iterate through all the files
		for (File file : files) {
			ArrayList<CoNLLSentence> conLLSentenceList = converter.readCoNLLFormatCorpus(file, Format.CONLL,true);
			corpus.addAll(conLLSentenceList);
		}

		// append all files
		StringBuilder sb = new StringBuilder();
		for (CoNLLSentence coNLLSentence : corpus) {
			String str = coNLLSentence.getCoNLLSentence();
			sb.append(str + "\n");
		}

		System.out.println("writing to file : " + outputfile);
		File file = new File(outputfile);
		FileUtils.write(file, sb.toString());

	}

	/**
	 * 
	 * @throws Exception
	 */
	public void HITtoCONLLCoversion(DataFormatOptions options) throws Exception {
		DataFormatConverter converter = new DataFormatConverter();

		converter.convertHITtoCONLL2009(options);

	}

}
