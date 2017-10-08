/**
 * 
 */
package com.emotibot.srl;

import static com.emotibot.srl.format.Constants.NEWLINE_PATTERN;
import static com.emotibot.srl.format.Constants.TAB_PATTERN;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.google.common.base.Strings;

import mate.is2.data.SentenceData09;
import mate.is2.parser.Parser;
import mate.is2.tag.Tagger;

/**
 * This class has functions to train and test : The POS and POS tagger models
 * are based on the mate tools
 * <ul>
 * <li>1. Part of Speech Tagger</li>
 * <li>2. Dependency Parser Model</li>
 * </ul>
 * 
 * @author Sanjay
 *
 */
public class TrainTaggerModels {

	static String POS_TRAINING_FILE = "data/corpus_validation/training_data/pos_training_data.txt";	
	static String DEP_TRAINING_FILE = "data/corpus_validation/training_data/dep_training_data.txt";
	static String SRL_TRAINING_FILE = "data/corpus_validation/training_data/srl_training_data.txt";
	
	static String SRL_POS_TAGGER_MODEL = "resources/emotibot-srl/models/test/ltp_pos_tagger.mdl";
	static String SRL_DEPENDENCY_TAGGER_MODEL = "resources/emotibot-srl/models/test/ltp_dependency_parser_test.mdl";
	static String dep_out = "ltp_depenedency_parser_out.txt";
	static String pos_out = "ltp_pos_tagger_out.txt";

	public enum FILESENUM {

		GOLD_ALL,
		GOLD_DEP_SRL,
		GOLD_POS_SRL,
		GOLD_POS_DEP,
		GOLD_ONLY_SRL,
		GOLD_ONLY_DEP,
		GOLD_ONLY_POS,
		MAIN_DATA,
		LTP_DATA;

		@Override
		public String toString() {

			String string = "";
			if (this == MAIN_DATA) {
				string = "data/corpus_validation/final/srl-emotibot-train_hit.txt";
			} else if (this == LTP_DATA) {
				string = "data2/ltp/files/srl-sentences_preproc_from_ltp_goodcase.txt";
			} else if (this == GOLD_ALL) {
				string = "data/corpus_validation/bad_cases_analysis/final/hit/gold_all.txt";
			} else if (this == GOLD_ONLY_POS) {
				string = "data/corpus_validation/bad_cases_analysis/final/hit/gold_only_pos.txt";
			} else if (this == GOLD_ONLY_DEP) {
				string = "data/corpus_validation/bad_cases_analysis/final/hit/gold_only_dep.txt";
			} else if (this == GOLD_ONLY_SRL) {
				string = "data/corpus_validation/bad_cases_analysis/final/hit/gold_only_srl.txt";
			} else if (this == GOLD_POS_DEP) {
				string = "data/corpus_validation/bad_cases_analysis/final/hit/gold_pos_dep.txt";
			} else if (this == GOLD_POS_SRL) {
				string = "data/corpus_validation/bad_cases_analysis/final/hit/gold_pos_srl.txt";
			} else if (this == GOLD_DEP_SRL) {
				string = "data/corpus_validation/bad_cases_analysis/final/hit/gold_dep_srl.txt";
			}

			return string;
		}

		/**
		 * Enums related to Questions Transformation Rules
		 */
		public static EnumSet<FILESENUM> POS_FILES_ENUM_SET = EnumSet.of(

				GOLD_ALL, GOLD_POS_SRL, GOLD_POS_DEP, GOLD_ONLY_POS,
				// MAIN_DATA,
				LTP_DATA

		);

		public static EnumSet<FILESENUM> DEP_FILES_ENUM_SET = EnumSet.of(

				GOLD_ALL, GOLD_DEP_SRL, GOLD_POS_DEP, GOLD_ONLY_DEP, 
				LTP_DATA

		);

		public static EnumSet<FILESENUM> SRL_FILES_ENUM_SET = EnumSet.of(

				GOLD_ALL, GOLD_DEP_SRL, GOLD_POS_SRL, GOLD_ONLY_SRL, 
				MAIN_DATA

		);

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		TrainTaggerModels parser = new TrainTaggerModels();

		// Steps :
		// 1. Train POS Tagger
		// 2. Train Dependency Tagger
		// 3. Change POS and Dependency in SRL
		
//		parser.concatenateTrainingData(FILESENUM.POS_FILES_ENUM_SET, POS_TRAINING_FILE, Format.HIT);
//		String[] args1 = { "-train", POS_TRAINING_FILE, "-model", SRL_POS_TAGGER_MODEL, "-test", POS_TRAINING_FILE,
//		 		"-out", pos_out };
//		parser.trainPOSTagger(args1);

		boolean convertToConll = false;
		
		
//		parser.prepareTrainingFile(FILESENUM.DEP_FILES_ENUM_SET, DEP_TRAINING_FILE, convertToConll);
//		String[] args2 = { "-train", DEP_TRAINING_FILE, "-model", SRL_DEPENDENCY_TAGGER_MODEL, "-test",
//				DEP_TRAINING_FILE, "-out", dep_out };
//		parser.trainDependencyParser(args2);
//		parser.initializeModels();
		
		
		convertToConll = true;
		//HybridPreprocessor.resetCounter();
		parser.prepareTrainingFile(FILESENUM.SRL_FILES_ENUM_SET, SRL_TRAINING_FILE, convertToConll);
		//HybridPreprocessor.printCounter();
	}
	
	/**
	 * Function to initialize the trained POS and Dependency Parser Model
	 */
	public void initializeModels() {
		HybridPreprocessor.getHybridPreprocessor().initializeModels();
	}

	/**
	 * Given the input CONLL2009 format data, this function will replace columns
	 * based on two flags
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void predictNotGoldColumn(String inputFilePath, String outputFilePath, boolean flag_predict_pos,
			boolean flag_predict_dep) throws IOException {

		File file = new File(inputFilePath);

		StringBuilder sb_pred_msg = new StringBuilder();
		sb_pred_msg.append("predict ");
		sb_pred_msg.append(flag_predict_pos ? "pos " : "");
		sb_pred_msg.append((flag_predict_pos & flag_predict_dep) ? "and " : "");
		sb_pred_msg.append(flag_predict_dep ? "dep " : "");
		sb_pred_msg.append("for " + file.getName());
		System.out.println(sb_pred_msg);

		StringBuilder sb = new StringBuilder();
		DataFormatConverter dfc = new DataFormatConverter();

		List<CoNLLSentence> sentences = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);

		// start stopwatch
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		System.out.println(
				"Iterating over " + sentences.size() + " sentences for modfiying POS and Dependency Information..");
		for (CoNLLSentence coNLLSentence : sentences) {
			List<String> tokens = coNLLSentence.getTokenList();
			String[] array = tokens.toArray(new String[0]);

			SentenceData09 output = HybridPreprocessor.getHybridPreprocessor().preprocess(array);

			CoNLLSentence tempSentence = new CoNLLSentence();
			String[] lines1 = (NEWLINE_PATTERN.split(output.toString()));
			tempSentence.setLines(lines1);

			ArrayList<ArrayList<String>> dataLines1 = dfc.readCONLLDataColumnwise(coNLLSentence);
			ArrayList<ArrayList<String>> dataLines2 = dfc.readCONLLDataColumnwise(tempSentence);

			List<Integer> columnsToBeChanged = new ArrayList<Integer>();

			// columns to change are 1) POS 2) Dep
			// POS : 4,5
			// DEP index : 8,9
			// Dep Relation : 10,11

			// columnsToBeChanged
			if (flag_predict_pos) {
				columnsToBeChanged.add(4);
				columnsToBeChanged.add(5);
			}
			if (flag_predict_dep) {
				columnsToBeChanged.add(8);
				columnsToBeChanged.add(9);
				columnsToBeChanged.add(10);
				columnsToBeChanged.add(11);
			}

			ArrayList<ArrayList<String>> dataLines3 = new ArrayList<ArrayList<String>>();

			for (int i = 0; i < dataLines1.size(); i++) {

				if (columnsToBeChanged.contains(i)) {
					dataLines3.add(i, dataLines2.get(i));

				} else {
					dataLines3.add(i, dataLines1.get(i));
				}

			}

			// lets set the gold label tags as well if they are not present.
			// we will set these for : 1) POS 2) Dependency Tags

			// we set the following columns :
			// POS : 4 DEP : 8 , DEPREL : 10

			if (dataLines3.get(4).get(0).equals("_")) {
				dataLines3.set(4, dataLines3.get(5));
			}
			if (dataLines3.get(8).get(0).equals("0")) {
				dataLines3.set(8, dataLines3.get(9));
			}
			if (dataLines3.get(10).get(0).equals("_")) {
				dataLines3.set(10, dataLines3.get(11));
			}

			String delimiter = "\t";
			StringBuilder sb1 = new StringBuilder();
			String[] lines = coNLLSentence.getLines();
			int total_columns = TAB_PATTERN.split(lines[0]).length;
			for (int i = 0; i < lines.length; i++) {
				String c = "";
				for (int j = 0; j < total_columns; j++) {
					c = dataLines3.get(j).get(i);
					if (j == total_columns - 1) {
						sb1.append(c);
					} else {
						sb1.append(c + delimiter);
					}
				}
				sb1.append("\n");
			}

			sb.append(sb1);
			sb.append("\n");

		}

		sb.append("\n");

		stopWatch.stop();

		long totalTime2 = stopWatch.getTime();
		double tps1 = ((double) totalTime2) / (sentences.size() * 1000);
		System.out.println(
				"Total time taken for " + sentences.size() + " sentences" + " : " + stopWatch.toString() + " seconds");
		System.out.println("Total time taken per sentence : " + tps1 + " seconds");

		System.out.println("Writing training data where model predicts not gold columns : " + outputFilePath);
		FileUtils.writeStringToFile(new File(outputFilePath), sb.toString(), "UTF-8");
	}

	/**
	 * Train dependency parser based on arguments.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void trainDependencyParser(String[] args) throws Exception {

		Parser dependencyParser = new Parser();
		try {
			// train dependency parser with given args
			dependencyParser.train(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Train POS tagger based on arguments
	 * 
	 * @param args
	 */
	public void trainPOSTagger(String[] args) {

		Tagger posTagger = new Tagger();
		try {
			posTagger.train(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Concatenate all files. Call this before calling training
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void concatenateTrainingData(EnumSet<FILESENUM> fileEnums, String outputFilePath, Format format) throws Exception {

		ArrayList<String> inputFilePaths = new ArrayList<String>();
		for (FILESENUM fileEnum : fileEnums) {
			inputFilePaths.add(fileEnum.toString());
		}
		concatenateTrainingData(inputFilePaths, outputFilePath, format);
	}
	
	public void concatenateTrainingData(String inputDirPath, String outputFilePath, Format format) throws Exception {
		
		File inputDir = new File(inputDirPath);
		final String[] SUFFIX = { "txt" };
		ArrayList<String> inputFilePaths = new ArrayList<String>();

		if (inputDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(inputDir, SUFFIX, true);
			for (File file : temp) {
				inputFilePaths.add(file.getAbsolutePath());
			}
		} else {
			inputFilePaths.add(inputDir.getAbsolutePath());
		}
		
		concatenateTrainingData(inputFilePaths, outputFilePath, format);

	}

	public void concatenateTrainingData(ArrayList<String> inputFilePaths, String outputFilePath, Format format) throws Exception {

		DataFormatConverter converter = new DataFormatConverter();
		Set<CoNLLSentence> corpus = new LinkedHashSet<CoNLLSentence>();

		// iterate through all the files
		for (String path : inputFilePaths) {
			File file = new File(path);
			if (file.exists()) {
				ArrayList<CoNLLSentence> conLLSentenceList = converter.readCoNLLFormatCorpus(file, format, true);
				corpus.addAll(conLLSentenceList);
			}
		}

		// append all files
		StringBuilder sb = new StringBuilder();
		for (CoNLLSentence coNLLSentence : corpus) {
			String str = "";
			switch (format) {
			case CONLL:
				str = coNLLSentence.getCoNLLSentence();
				break;

			case HIT:
				str = coNLLSentence.getHITSentence();
				break;

			default:
				break;
			}

			if (!Strings.isNullOrEmpty(str) && str.length() > 1) {
				sb.append(str + "\n");
			}

		}

		File file = new File(outputFilePath);
		FileUtils.write(file, sb.toString());
		System.out.println("wrote to file : " + outputFilePath);

	}

	/**
	 * Deal with different gold file to generate single file for training SRL
	 * and DEP. For not gold columns, we have to fill it by model.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void prepareTrainingFile(EnumSet<FILESENUM> fileEnums, String finalTraingingFilePath, boolean convertToConll) throws Exception {

		

		String tempDirPath = "data/corpus_validation/temp";
		String tempHITDirPath = "data/corpus_validation/temp/hit";
		String tempCoNLLDirPath = "data/corpus_validation/temp/conll";
		File tempDir = new File(tempDirPath);
		if (tempDir.exists()) {
			FileUtils.cleanDirectory(tempDir);
		} else {
			tempDir.mkdirs();
		}
		new File(tempHITDirPath).mkdirs();
		new File(tempCoNLLDirPath).mkdirs();

		for (FILESENUM fileEnum : fileEnums) {
			File file = new File(fileEnum.toString());
			if (file.exists()) {
				String convertedFilePath = tempHITDirPath + File.separator + file.getName();
				
				System.out.println("prepare file " + file.getName());
				switch (fileEnum) {

				// for now, we have pos and srl of our 20k sentences fixed
				// for SRL training
				case MAIN_DATA:
					predictNotGoldColumn(file.getPath(), convertedFilePath, false, true);
					break;

				// for DEP training
				case LTP_DATA:
					FileUtils.copyFile(file, new File(convertedFilePath));
					break;

				// for DEP and SRL training
				case GOLD_ALL:
					FileUtils.copyFile(file, new File(convertedFilePath));
					break;

				case GOLD_ONLY_POS:
					System.out.println("Should not come here. Do you specify correct files?");
					break;

				// for DEP training
				case GOLD_ONLY_DEP:
					predictNotGoldColumn(file.getPath(), convertedFilePath, true, false);
					break;

				// for SRL training
				case GOLD_ONLY_SRL:
					predictNotGoldColumn(file.getPath(), convertedFilePath, true, true);
					break;

				// for DEP training
				case GOLD_POS_DEP:
					predictNotGoldColumn(file.getPath(), convertedFilePath, false, false);
					break;

				// for SRL training
				case GOLD_POS_SRL:
					predictNotGoldColumn(file.getPath(), convertedFilePath, false, true);
					break;

				// for DEP and SRL training
				case GOLD_DEP_SRL:
					predictNotGoldColumn(file.getPath(), convertedFilePath, true, false);
					break;

				default:
					System.out.println("[Default] Should not come here. Do you specify correct files?");
					break;
				}
			}
		}

		if (convertToConll) {
			DataFormatConverter dfc = new DataFormatConverter();
			System.out.println(String.format("Convert HIT folder:%s to CoNLL folder:%s", tempHITDirPath, tempCoNLLDirPath));
			dfc.convertHITtoCONLL2009(tempHITDirPath, tempCoNLLDirPath);
			
			// concatenate all files
			concatenateTrainingData(tempCoNLLDirPath, finalTraingingFilePath, Format.CONLL);
			
		} else {
			concatenateTrainingData(tempHITDirPath, finalTraingingFilePath, Format.CONLL);
		}
		
		if (tempDir.exists()) {
			FileUtils.cleanDirectory(tempDir);
		}
	}

}
