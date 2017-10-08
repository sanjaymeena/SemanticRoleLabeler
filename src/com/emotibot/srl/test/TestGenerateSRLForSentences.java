/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.emotibot.srl.DataValidation;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatOptions;
import com.emotibot.srl.format.DataWriter;
import com.emotibot.srl.utilities.StatisticsGenerator;
import com.emotibot.srl.utilities.WordInfoGenerator;
import com.google.gson.stream.JsonReader;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import com.emotibot.srl.format.DataFormatConverter.Format;

/**
 * @author Sanjay
 *
 */
public class TestGenerateSRLForSentences {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		TestGenerateSRLForSentences tsr = new TestGenerateSRLForSentences();

		// Step 1 : generate srl for the input corpus. the file details are kept
		// inside
		// the function

		// tsr.generateSRLForRawSentencesCorpus();

		// Step 2 :
		// tsr.validateSRLData();

		// Step 3 : Generate word lists based on POS for this dataset
		//tsr.generateWordInfo();

		// Step 4 : Generate statistics for this dataset
		tsr.generateDataStatistics();

	}

	private void generateDataStatistics() throws IOException, ParseException {
		// TODO Auto-generated method stub
		// String dataFolder = "data/corpus_validation/good_cases/hit";

		DataFormatConverter dfc = new DataFormatConverter();
		StatisticsGenerator statsGen = new StatisticsGenerator();

		String dataFolder = "data/editorial-q-q/output/singlefile";
		String dataFolder1= "data/editorial-q-q/output/srl";
		String jsonFile1 = "data/editorial-q-q/output/wordinfo/srl_editorial_data.json";
		String jsonFile2 = "data/editorial-q-q/output/wordinfo/word_info_srl.json";
		String allRelationFile="data/editorial-q-q/output//other/all_srl_relations.txt";
		
		String sentencesOutputFolder="data/editorial-q-q/output/other/sentences/sentences";
		String wordsOutputFolder="data/editorial-q-q/output/other/words/words";

		Format format=Format.CONLL;
		
		
		statsGen.readRelationsSet(allRelationFile);
		
		File rootDir = new File(dataFolder1);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		// iterate through all the files
		for (File file : files) {
			
			String t1=file.getName().replace(".txt", "_sent.json");
			String t2=file.getName().replace(".txt", "_words.json");
			
			String outputFile1=sentencesOutputFolder+ file.separator + t1;
			String outputFile2=wordsOutputFolder+ file.separator +t2;
			
			ArrayList<CoNLLSentence> data = dfc.readCoNLLFormatCorpus(file, format,true);
			
			//statsGen.generateSentenceData(data, outputFile1,Format.CONLL);
			statsGen.generateDataStatsAll(data, outputFile1, outputFile2);
			
		}
		
		
		
		
		
		//statsGen.generateAllRelations(dataFolder, allRelationFile,Format.CONLL);
		
		
		//statsGen.generateDataStatsAll(dataFolder, jsonFile1, jsonFile2,Format.CONLL);
		//statsGen.generateWordData(dataFolder, jsonFile2,Format.CONLL);
		//statsGen.generateSentenceData(dataFolder, jsonFile1,Format.CONLL);
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void generateWordInfo() throws IOException {
		// TODO Auto-generated method stub
		String dataFolder = "data/editorial-q-q/output/singlefile/";
		Format format = Format.HIT;

		String outputFolder = "data/editorial-q-q/output/wordinfo/pos";

		WordInfoGenerator wig = new WordInfoGenerator();
		wig.generateWordRelatedInfo(dataFolder, format, outputFolder);
	}

	private void validateSRLData() throws Exception {
		// TODO Auto-generated method stub

		DataFormatOptions data_format_options = new DataFormatOptions();

		String rootDir = "data/editorial-q-q/output/validation_output";
		String inputDir = "data/editorial-q-q/output/srl";
		String outputDir = "data/editorial-q-q/output/validation_output/output";
		String sentencesDir = "data/editorial-q-q/output/validation_output/sentences";
		String final_hit_File = "data/editorial-q-q/output/validation_output/final/srl-emotibot-train_hit.txt";
		String final_conll_File = "data/editorial-q-q/output/validation_output/final/srl-emotibot-train_conll.txt";
		String goodCasesDirHIT = "data/editorial-q-q/output/validation_output/good_cases/hit";
		String goodCasesDirCONLL = "data/editorial-q-q/output/validation_output/good_cases/conll";
		boolean isRecursive = true;
		boolean onlyGoodCases = false;

		data_format_options.rootDirectory = rootDir;
		data_format_options.inputDir = inputDir;
		data_format_options.outputDir = outputDir;
		data_format_options.sentencesDir = sentencesDir;
		data_format_options.goodCasesHITDir = goodCasesDirHIT;
		data_format_options.goodCasesCONLLDir = goodCasesDirCONLL;

		data_format_options.isRecursive = isRecursive;
		data_format_options.onlyGoodCases = onlyGoodCases;

		data_format_options.final_conll_File = final_conll_File;
		data_format_options.final_hit_File = final_hit_File;

		DataValidation dataValidation = new DataValidation();
		dataValidation.prepareTrainingData(data_format_options);

	}

	/**
	 * @throws Exception
	 * 
	 */
	private void generateSRLForRawSentencesCorpus() throws Exception {
		// TODO Auto-generated method stub

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		String rootPath = "data/editorial-q-q/data/";
		String inputPath = rootPath + "orig/editorial_sentences.txt";
		String outputPath = rootPath + "batch";

		String srlOutput = "data/editorial-q-q/output/srl";

		int sentenceCount = 5000;
		String file_name = "editorial";
		String file_name_srl = "editorial_srl";

		DataFormatConverter dfc = new DataFormatConverter();
		DataWriter dataWriter = new DataWriter();

		// Step 1 : first read all the sentences

		ArrayList<CoNLLSentence> sentencesList = dfc.readTextCorpus(inputPath);
		long totalSentences = sentencesList.size();

		// Step 2 : this step will break down large sentence corpus into smaller
		// chunk of
		// files based on sentence count

		dataWriter.breakDownLargeSentenceCorpus(sentencesList, file_name, outputPath, sentenceCount);

		// Step 3 : Iterate over each smaller file and write srl results in hit
		// format

		File rootDir = new File(outputPath);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		// iterate through all the files
		for (File file : files) {

			String filename = file.getName();
			String srlFilename = filename.replace(file_name, file_name_srl);
			outputPath = srlOutput + File.separator + srlFilename;
			dfc.createHITFormatData(file.getAbsolutePath(), outputPath);

		}

		stopWatch.stop();
		long totalTime = stopWatch.getTime();

		double tps = ((double) totalTime) / (totalSentences * 1000);

		System.out.println("Total time taken for " + totalSentences + " sentences" + " : " + stopWatch.toString());
		System.out.println("Total time taken per sentence " + tps);
	}

}
