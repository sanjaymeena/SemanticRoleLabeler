package com.emotibot.srl;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import com.emotibot.srl.format.DataFormatOptions;
import com.emotibot.srl.format.DataFormatConverter.Format;

public class DataValidationForBadCase {

	// please remember to put "/" at the end of the directory
	String rootDir = "data/corpus_validation/bad_cases_analysis";
	String inputDir = "data/corpus_validation/bad_cases_analysis/input";
	String outputDir = "data/corpus_validation/bad_cases_analysis/output2";
	String sentencesDir = "data/corpus_validation/bad_cases_analysis/sentences";
//	String final_hit_File = "data/corpus_validation/bad_cases_analysis/final/srl-emotibot-train_hit.txt";
//	String final_conll_File = "data/corpus_validation/bad_cases_analysis/final/srl-emotibot-train_conll.txt";
//	String final_srl_sentences_file = "data/corpus_validation/bad_cases_analysis/final/srl-sentences.txt";
	String goodCasesDirHIT = "data/corpus_validation/bad_cases_analysis/good_cases/hit";
	String goodCasesDirCONLL = "data/corpus_validation/bad_cases_analysis/good_cases/conll";
	String finalDirHIT = "data/corpus_validation/bad_cases_analysis/final/hit";
	String finalDirCONLL = "data/corpus_validation/bad_cases_analysis/final/conll";
	String finalDirSentence = "data/corpus_validation/bad_cases_analysis/final/sentence";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		DataValidationForBadCase data_validation = new DataValidationForBadCase();

		data_validation.prepareTrainingData();

	}

	/**
	 * Prepare training data in a single file . <br>
	 * Steps:
	 * <ul>
	 * <li>1. Convert from HIT to CONLL Format</li>
	 * <li>2. Create single training data file by concatenating multiple files
	 * </li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	public void prepareTrainingData() throws Exception {

		// use origin DataValidation code
		DataValidation dv = new DataValidation();
		
		// convert directory recursively
		boolean isRecursive = true;
		boolean onlyGoodCases = true;

		// Step 1 : Convert HIT to CONLL

		DataFormatOptions data_format_options = new DataFormatOptions();
		data_format_options.rootDirectory = rootDir;
		data_format_options.inputDir = inputDir;
		data_format_options.outputDir = outputDir;
		data_format_options.sentencesDir = sentencesDir;
		data_format_options.goodCasesHITDir = goodCasesDirHIT;
		data_format_options.goodCasesCONLLDir = goodCasesDirCONLL;

		data_format_options.isRecursive = isRecursive;
		data_format_options.onlyGoodCases = onlyGoodCases;

		dv.HITtoCONLLCoversion(data_format_options);

		// Step 2 : Concatenate all files to one file

		// Concatenate all HIT files in good case folder
		concatenateCONLLFilesToDir(goodCasesDirHIT, finalDirHIT, onlyGoodCases, Format.HIT);

		// convert all good cases HIT files to CONNLL format

		dv.HITtoCONLLCoversion(goodCasesDirHIT, goodCasesDirCONLL);
		
		concatenateCONLLFilesToDir(goodCasesDirCONLL, finalDirCONLL, onlyGoodCases, Format.CONLL);

		concatenateSentenceFilesToDir(goodCasesDirCONLL, finalDirSentence, onlyGoodCases, Format.CONLL);

	}
	
	/**
	 * Concatenate all sentences strings into one directory from the input files
	 * @param inputDir
	 * @param outputDir
	 * @param onlyGoodCases
	 * @param conll
	 * @throws IOException 
	 */
	private void concatenateSentenceFilesToDir(String inputDirPath, String outputDirPath, boolean onlyGoodCases, Format format) 
			throws IOException {
		
		DataValidation dv = new DataValidation();
		
		File[] files = new File(inputDirPath).listFiles();
		
		File outputDir = new File(outputDirPath);
		if (outputDir.exists()) {
			FileUtils.cleanDirectory(outputDir);
		} else {
			outputDir.mkdirs();
		}
		
		for (File subDir : files) {
			if (subDir.isDirectory()) {
				String outFileName = subDir.getName() + ".txt";
				String outFilePath = outputDir + File.separator + outFileName; 
				dv.concatenateSentenceFiles(subDir.getPath(), outFilePath, onlyGoodCases, format);
			}
		}
	}


	/**
	 * This function will concatenate all files with conll sentences into a
	 * directory. Good cases are read in the files which have keywords good,
	 * complete
	 * 
	 * @param goodCasesDir
	 * @param outputDir
	 * @throws IOException
	 */
	public void concatenateCONLLFilesToDir(String inputDirPath, String outputDirPath, boolean onlyGoodCases, Format format)
			throws IOException {
		
		DataValidation dv = new DataValidation();
		
		File[] files = new File(inputDirPath).listFiles();
		
		File outputDir = new File(outputDirPath);
		if (outputDir.exists()) {
			FileUtils.cleanDirectory(outputDir);
		} else {
			outputDir.mkdirs();
		}
		
		for (File subDir : files) {
			if (subDir.isDirectory()) {
				String outFileName = subDir.getName() + ".txt";
				String outFilePath = outputDir + File.separator + outFileName; 
				dv.concatenateCONLLFiles(subDir.getPath(), outFilePath, onlyGoodCases, format);
			}
		}
	}
	
}