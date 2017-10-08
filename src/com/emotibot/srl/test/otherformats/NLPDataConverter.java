/**
 * 
 */
package com.emotibot.srl.test.otherformats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.google.common.base.Strings;

import se.lth.cs.srl.SemanticLabelerPipeLine;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

/**
 * @author Sanjay
 *
 */
public class NLPDataConverter {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		NLPDataConverter nlpdc = new NLPDataConverter();

//		String inputDir = "data/cases/vip/input/";
//		String outputDir = "data/cases/vip/output/tokens";
//		String case_type = "vip";
//		String case_type = "";
//		nlpdc.tokenizeSentences(inputDir, outputDir, case_type);
		
//		String inputDir1 = "data/cases/uni_test_set/correctsegment";
//		String outputDir1 = "data/cases/uni_test_set/output";
//		nlpdc.createSRLFromSentenceTokens(inputDir1,outputDir1);
		
//		String inputFilePath = "data/cases/vip/1130-唯品会-小i-待修复-有词性.txt";
//		String outputFilePath = "data/cases/vip/out.txt";
//		nlpdc.createSRLFromNLPFormat(inputFilePath, outputFilePath);
		
		String inputFilePath = "data/cases/nlp_data";
		String outputFilePath = "data/cases/word_lists";
		nlpdc.generateWordLists(inputFilePath, outputFilePath);
	}
	
	private void createSRLFromNLPFormat(String inputFilePath, String outputFilePath) throws Exception {
		
		String pattern_str = "(.+?)\\/([a-z,1-9]+)";
		Pattern pattern = Pattern.compile(pattern_str);
		
		File inputFile = new File(inputFilePath);
		
		List<String> lines = FileUtils.readLines(inputFile, "UTF-8");
		
		StringBuilder outputBuilder = new StringBuilder();
		for (String line : lines) {
			
			String[] vals = line.split("\t");
			// String sentence = vals[0];
			Matcher m = pattern.matcher(vals[1]);
			List<String> tokens = new ArrayList<String>();
			List<String> posArray = new ArrayList<String>();
			while (m.find()) {
				if (!Strings.isNullOrEmpty(m.group(1).trim())) {
					tokens.add(m.group(1).trim());
				} else {
					tokens.add(m.group(1).substring(1));
				}
				posArray.add(m.group(2));
				
			}
			
			SRLOptions options = new SRLOptions();
			options.manualMode = false;
			options.model = 2;
			options.produceHITFormat = true;
			options.doTree=false;
			
			options.usePOSFromNLP = true;
			options.useDEPFromNLP = false;
			if (tokens != null && tokens.size() > 0){
				System.out.println(tokens);
				SRLJsonDataStructure result = SemanticLabelerPipeLine.getChineseInstance(options).
						performSRLUsingHybridPreprocessor(tokens.toArray(new String[0]), posArray.toArray(new String[0]), options);
				
//				outputBuilder.append(result.getConllSentence());
				outputBuilder.append(result.getHitSentence() );
				//outputBuilder.append(result.toString() );
				outputBuilder.append("\n");
			}
		}

		File outputFile = new File(outputFilePath);			
		FileUtils.write(outputFile, outputBuilder.toString(), "UTF-8");
		
	}

	private void generateWordLists(String inputDirPath, String outputDirPath) throws IOException {
		
		String pattern_str = "(.+?)\\/([a-z,1-9]+)";
		Pattern pattern = Pattern.compile(pattern_str);
		
		List<File> files = new ArrayList<File>();
		File rootDir = new File(inputDirPath);
		final String[] SUFFIX = { "txt" };

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}
		
		File outputDir = new File(outputDirPath);
		if (outputDir.exists()) {
			FileUtils.cleanDirectory(outputDir);
		} else {
			outputDir.mkdirs();
		}
		
		Map<String, Set<String>> mapPosTokenSet = new HashMap<String, Set<String>>();
		
		for (File file : files) {
			System.out.println(file.getName());
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			for (String line : lines) {
				String[] vals = line.split("\t");

				Matcher m = pattern.matcher(vals[1]);
				String token;
				String pos;
				while (m.find()) {
					if (!Strings.isNullOrEmpty(m.group(1).trim())) {
						token = m.group(1).trim();
					} else {
						token = m.group(1).substring(1);
					}
					pos = m.group(2);
					
					
					if (mapPosTokenSet.get(pos) == null) {
						mapPosTokenSet.put(pos, new HashSet<String>());
					}
					mapPosTokenSet.get(pos).add(token);
				}
			}
		}
		
		for (String pos : mapPosTokenSet.keySet()) {

			File outputFile = new File(outputDir, String.format("%s.txt", pos));
			
			FileUtils.write(outputFile, String.join("\n", mapPosTokenSet.get(pos)));
		}
		
	}
	
	private void createSRLFromSentenceTokens(String inputDir, String outputDir) throws Exception {
		
		String split="\t" + " : ";
		StopWatch stopWatch = new StopWatch();
		int counter = 0;

		List<File> files = new ArrayList<File>();

		StringBuilder outputBuilder = new StringBuilder();
		SRLOptions options = new SRLOptions();
		options.manualMode = false;
		options.model = 2;
		options.produceHITFormat = true;
		options.doTree=false;
		
		options.usePOSFromNLP = true;
		options.useDEPFromNLP = false;
		
		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		System.out.println("Reading each file in directory :" + inputDir);
		// iterate through all the files
		for (File file : files) {
			outputBuilder = new StringBuilder();
			List<String> contents = FileUtils.readLines(file, "UTF-8");

			for (String string : contents) {

				SRLJsonDataStructure result = null;
				String input = string;
				String[] tokens=null;
				if (!Strings.isNullOrEmpty(input)) {

					String[] vals = input.split(split);
					if (vals!=null && vals.length>1){
						
						String t2=vals[1];
						if(!Strings.isNullOrEmpty(t2)){
							tokens = t2.trim().split(" ");
						}
					}
					
					if (tokens != null && tokens.length>0){
//						result = SemanticLabelerPipeLine.getChineseInstance(options)
//								.performSRL(tokens, options);
						result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLUsingHybridPreprocessor(tokens, options);
						counter++;
						// we do this as it takes time to load the srl model at
						// first instance
						if (counter == 1) {
							stopWatch.start();
						}

						// System.out.println(result);

						outputBuilder.append(result.getConllSentence() );
//						outputBuilder.append(result.getHitSentence() );
						//outputBuilder.append(result.toString() );
						outputBuilder.append("\n");
					}
				}
			}

			String newFilename= file.getName().replace(".txt", "_srl.txt");
			String outputFile = outputDir + File.separator + newFilename;
			File f = new File(outputFile);

			FileUtils.write(f, outputBuilder.toString(), "UTF-8");

		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void tokenizeSentences(String inputDir, String outputDir, String case_type) throws IOException {

		StopWatch stopWatch = new StopWatch();
		int counter = 0;

		List<File> files = new ArrayList<File>();

		StringBuilder outputBuilder = new StringBuilder();
		SRLOptions options = new SRLOptions();
		options.model = 2;
		options.produceHITFormat = true;

		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		EmotibotTokenizer tokenizer = new EmotibotTokenizer();
		
		System.out.println("Reading each file in directory :" + inputDir);
		// iterate through all the files
		for (File file : files) {
			outputBuilder = new StringBuilder();
			List<String> contents = FileUtils.readLines(file, "UTF-8");

			for (String string : contents) {

				String input = string;
				if (!Strings.isNullOrEmpty(input)) {

					String[] tokens = tokenizer.tokenize(input, case_type);

					counter++;
					// we do this as it takes time to load the srl model at
					// first instance
					if (counter == 1) {
						stopWatch.start();
					}

					// System.out.println(result);
					outputBuilder.append(string + "\t" + " : ");
					for (String string2 : tokens) {
						outputBuilder.append(string2 + " ");
					}
					outputBuilder.append("\n");
				}
			}

			String newFilename= file.getName().replace(".txt", "_tokens.txt");
			String outputFile = outputDir + File.separator + newFilename;
			File f = new File(outputFile);

			FileUtils.write(f, outputBuilder.toString(), "UTF-8");

		}
	}

}
