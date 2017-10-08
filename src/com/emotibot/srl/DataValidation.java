/**
 * 
 */
package com.emotibot.srl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.format.DataFormatOptions;
import com.google.common.base.Strings;

/**
 * @author Sanjay
 *
 */
public class DataValidation {
	// please remember to put "/" at the end of the directory
	String rootDir = "data/corpus_validation/";
	String inputDir = "data/corpus_validation/input";
	String outputDir = "data/corpus_validation/output2";
	String sentencesDir = "data/corpus_validation/sentences";
	String final_hit_File = "data/corpus_validation/final/srl-emotibot-train_hit.txt";
	String final_conll_File = "data/corpus_validation/final/srl-emotibot-train_conll.txt";
	String final_srl_sentences_file = "data/corpus_validation/final/srl-sentences.txt";
	String goodCasesDirHIT = "data/corpus_validation/good_cases/hit";
	String goodCasesDirCONLL = "data/corpus_validation/good_cases/conll";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		DataValidation data_validation = new DataValidation();

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

		// TODO Auto-generated method stub
		// String inputDir = "data/emotibot/bad_cases_training_hit";
		// String outputDir = "data/emotibot/bad_cases_training_conll";

		// public static String
		// SRL_BAD_CASES_DIRECTORY="resources/emotibot-srl/srl_bad_cases";

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

		HITtoCONLLCoversion(data_format_options);

		// Step 2 : Concatenate all files to one file

		String outputfile = final_hit_File;

		// Concatenate all HIT files in good case folder
		concatenateCONLLFiles(goodCasesDirHIT, outputfile, onlyGoodCases, Format.HIT);

		// convert all good cases HIT files to CONNLL format
		data_format_options = new DataFormatOptions();

		HITtoCONLLCoversion(goodCasesDirHIT, goodCasesDirCONLL);
		concatenateCONLLFiles(goodCasesDirCONLL, final_conll_File, onlyGoodCases, Format.CONLL);

		
		concatenateSentenceFiles(goodCasesDirCONLL, final_srl_sentences_file, onlyGoodCases, Format.CONLL);
		// // also put all the data in bad case file . The srl bad cases is same
		// as
		// // training data file
		// outputfile = SRL_BAD_CASES_DIRECTORY + File.separator +
		// "srl_bad_cases.txt";
		// concatenateCONLLFiles(outputDir, outputfile);

	}
	
	/**
	 * Concatenate all sentences string from the input files
	 * @param goodCasesDirCONLL
	 * @param final_sentence_file
	 * @param onlyGoodCases
	 * @param conll
	 * @throws IOException 
	 */
	public void concatenateSentenceFiles(String inputDir, String outputFilePath, boolean onlyGoodCases,
			Format format) throws IOException {
		
				File rootDir = new File(inputDir);
				final String[] SUFFIX = { "txt" };
				Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

				DataFormatConverter converter = new DataFormatConverter();
				Set<CoNLLSentence> corpus = new LinkedHashSet<CoNLLSentence>();

				// iterate through all the files
				for (File file : files) {

					if (!onlyGoodCases) {
						ArrayList<CoNLLSentence> conLLSentenceList = converter.readCoNLLFormatCorpus(file, format,true);
						corpus.addAll(conLLSentenceList);
					}

					else {
						// since we we will only consider good cases files . we look for
						// keywords : "good" and "complete"
						String filename = file.getName();
						if (filename.contains("good") && filename.contains("complete")) {
							ArrayList<CoNLLSentence> conLLSentenceList = converter.readCoNLLFormatCorpus(file, format,true);
							corpus.addAll(conLLSentenceList);
						}
					}

				}

				// append all files
				StringBuilder sb = new StringBuilder();
				for (CoNLLSentence coNLLSentence : corpus) {
					String str="";
					switch (format) {
					case CONLL:
						str=coNLLSentence.getSentence();
						break;

					case HIT:
						str=coNLLSentence.getSentence();
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
	public void prepareTrainingData(DataFormatOptions data_format_options) throws Exception {

		
		// convert directory recursively
		boolean isRecursive = true;
		boolean onlyGoodCases = false;

		
		
		
		String rootDir = data_format_options.rootDirectory;
		String inputDir = data_format_options.inputDir;
		String outputDir = data_format_options.outputDir;
		String sentencesDir = data_format_options.sentencesDir;
		
		
		String final_hit_File = data_format_options.final_hit_File;
		String final_conll_File = data_format_options.final_conll_File;
		String goodCasesDirHIT = data_format_options.goodCasesHITDir;
		String goodCasesDirCONLL = data_format_options.goodCasesCONLLDir;
		
		isRecursive=data_format_options.isRecursive;
		onlyGoodCases=data_format_options.onlyGoodCases;

		HITtoCONLLCoversion(data_format_options);

		// Step 2 : Concatenate all files to one file

		String outputfile = final_hit_File;

		// Concatenate all HIT files in good case folder
		concatenateCONLLFiles(goodCasesDirHIT, outputfile, onlyGoodCases, Format.HIT);

		// convert all good cases HIT files to CONNLL format
		data_format_options = new DataFormatOptions();

		HITtoCONLLCoversion(goodCasesDirHIT, goodCasesDirCONLL);
		concatenateCONLLFiles(goodCasesDirCONLL, final_conll_File, onlyGoodCases, Format.CONLL);

		// // also put all the data in bad case file . The srl bad cases is same
		// as
		// // training data file
		// outputfile = SRL_BAD_CASES_DIRECTORY + File.separator +
		// "srl_bad_cases.txt";
		// concatenateCONLLFiles(outputDir, outputfile);

	}

	/**
	 * This function will concatenate all files with conll sentences into a
	 * single file. Good cases are read in the files which have keywords good,
	 * complete
	 * 
	 * @param outputDir
	 * @param outputfile
	 * @throws IOException
	 */
	public void concatenateCONLLFiles(String inputDir, String outputFilePath, boolean onlyGoodCases, Format format)
			throws IOException {
		// TODO Auto-generated method stub
		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		DataFormatConverter converter = new DataFormatConverter();
		Set<CoNLLSentence> corpus = new LinkedHashSet<CoNLLSentence>();

		// iterate through all the files
		for (File file : files) {

			if (!onlyGoodCases) {
				ArrayList<CoNLLSentence> conLLSentenceList = converter.readCoNLLFormatCorpus(file, format,true);
				corpus.addAll(conLLSentenceList);
			}

			else {
				// since we we will only consider good cases files . we look for
				// keywords : "good" and "complete"
				String filename = file.getName();
				if (filename.contains("good")) {
					ArrayList<CoNLLSentence> conLLSentenceList = converter.readCoNLLFormatCorpus(file, format,true);
					corpus.addAll(conLLSentenceList);
				}
			}

		}

		// append all files
		StringBuilder sb = new StringBuilder();
		for (CoNLLSentence coNLLSentence : corpus) {
			String str="";
			switch (format) {
			case CONLL:
				str=coNLLSentence.getCoNLLSentence();
				break;

			case HIT:
				str=coNLLSentence.getHITSentence();
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
	 * 
	 * @param dataFormatOptions
	 * @throws Exception
	 */
	public void HITtoCONLLCoversion(DataFormatOptions dataFormatOptions) throws Exception {

		// String rootDirectory=dataFormatOptions.rootDirectory;
		// String inputDir=dataFormatOptions.inputDir;
		// String outputDir=dataFormatOptions.outputDir;
		// boolean isRecursive=dataFormatOptions.isRecursive;
		// boolean onlyGoodCases=dataFormatOptions.onlyGoodCases;

		DataFormatConverter converter = new DataFormatConverter();
		converter.convertHITtoCONLL2009(dataFormatOptions);

	}

	/**
	 * 
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	public void HITtoCONLLCoversion(String input, String output) throws Exception {

		// String rootDirectory=dataFormatOptions.rootDirectory;
		// String inputDir=dataFormatOptions.inputDir;
		// String outputDir=dataFormatOptions.outputDir;
		// boolean isRecursive=dataFormatOptions.isRecursive;
		// boolean onlyGoodCases=dataFormatOptions.onlyGoodCases;

		DataFormatConverter converter = new DataFormatConverter();
		converter.convertHITtoCONLL2009(input, output);

	}
	
}
