/**
 * 
 */
package com.emotibot.srl.test.conll05;

import static com.emotibot.srl.format.Constants.TAB_PATTERN;
import static com.emotibot.srl.format.Constants.conllformat_first_arg_column_no;
import static com.emotibot.srl.format.Constants.conllformat_form_column_no;
import static com.emotibot.srl.format.Constants.conllformat_verb_column_no;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.server.SRLParserHelper;
import com.emotibot.srl.tmr.datastructure.SRL;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Transforms conll2009 data to CONLL2005 data. We are doing this for the
 * purpose of create training data for deep learning model based on paper :
 * "End to End learning of Semantic Role Labeling using Recurrent Neural Networks"
 * 
 * @author Sanjay
 *
 */
public class DataTransformer {
	DataFormatConverter dfc = new DataFormatConverter();

	SRLParserHelper srl_parser_helper;
	List<CoNLLSentence> notAddedSentences;
	List<CoNLLSentence> wrongSyntaxSentenceList;

	List<CoNLLSentence> globaladdedSentences;

	Set<String> wordDictSet;
	Set<String> verbDictSet;
	Set<String> labelSet;
	Set<String> oovWords;

	Set<String> trainDataPredicates;
	Set<String> testDataPredicates;

	String delimiter = "\t";
	String space = " ";
	String newline = "\n";

	String o = "O";
	String b = "B-";
	String i = "I-";

	String qq_dict_file = "data/deeplearning/emb/wordDict_QQ.txt";
	List<List<String>> sentenceTokenList;

	Set<String> qq_dict_set;

	/**
	 * 
	 */
	public DataTransformer() {

		initializeVariables();

		srl_parser_helper = new SRLParserHelper();

	}

	/**
	 * 
	 */
	private void organizeSRLData() {
		Corpus corpus = readAllSRLSentences();
		corpus.getTrainSRLSentences();
		ArrayList<CoNLLSentence> data = corpus.getTestSRLSentences();

		for (CoNLLSentence coNLLSentence : data) {
			SRLJsonDataStructure ds = getSRLforBadCase(coNLLSentence);
			int temp=10;

		}
	}

	private SRLJsonDataStructure getSRLforBadCase(CoNLLSentence coNLLSentence) {

		
		dfc.convertHITtoCONLL2009(coNLLSentence);
		String doc = coNLLSentence.getSentence();
		String conll = coNLLSentence.getCoNLLSentence();
		
		SRLOptions options = new SRLOptions();
		options.produceHITFormat=false;
		options.doPruner=false;
		options.doTree=false;
		
		SRLJsonDataStructure jsonDS = srl_parser_helper.createSRLJsonDataStructure(coNLLSentence, new SRLOptions());

		return jsonDS;
	}

	/**
	 * Create predicate identification model training and test data. The format
	 * is conll03 format.
	 * 
	 * @throws IOException
	 */
	public void createPredicateIdentificationModelData() throws IOException {
		Corpus allCorpus = readAllSRLSentences();

		ArrayList<CoNLLSentence> trainSRLSentences = allCorpus.getTrainSRLSentences();
		ArrayList<CoNLLSentence> testSRLSentences = allCorpus.getTestSRLSentences();
		ArrayList<CoNLLSentence> conll2009CorpusSentences = allCorpus.getConll2009CorpusSentences();

		StringBuilder trainDataBuilder = new StringBuilder();
		StringBuilder testDataBuilder = new StringBuilder();
		StringBuilder conll03EvalDataFormatBuilder = new StringBuilder();

		String trainFile = "data/deeplearning/predicate_identification/train.txt";
		String testFile = "data/deeplearning/predicate_identification/test.txt";
		String targetFile = "data/deeplearning/predicate_identification/target.txt";
		String evalFile = "data/deeplearning/predicate_identification/gold_test.txt";

		List<StringBuilder> trainDataBuilders = createPredicateIdentificationModelDataHelper(trainSRLSentences);
		trainDataBuilder = trainDataBuilders.get(0);

		List<StringBuilder> testDataBuilders = createPredicateIdentificationModelDataHelper(testSRLSentences);
		testDataBuilder = testDataBuilders.get(0);
		conll03EvalDataFormatBuilder = testDataBuilders.get(1);

		System.out.println(trainDataBuilder.toString());
		FileUtils.writeStringToFile(new File(trainFile), trainDataBuilder.toString(), "UTF-8");
		FileUtils.writeStringToFile(new File(testFile), testDataBuilder.toString(), "UTF-8");

		FileUtils.writeStringToFile(new File(evalFile), conll03EvalDataFormatBuilder.toString(), "UTF-8");

		// let write the target labels
		Set<String> targetLabelSet = new HashSet<>();
		targetLabelSet.add("O");
		targetLabelSet.add("B-V");
		targetLabelSet.add("I-V");

		StringBuilder targetBuilder = new StringBuilder();

		for (String string : targetLabelSet) {
			targetBuilder.append(string);
			targetBuilder.append(newline);
		}

		FileUtils.writeStringToFile(new File(targetFile), targetBuilder.toString(), "UTF-8");
	}

	private List<StringBuilder> createPredicateIdentificationModelDataHelper(ArrayList<CoNLLSentence> data) {
		List<StringBuilder> builders = new ArrayList<>();

		StringBuilder dataBuilder = new StringBuilder();
		StringBuilder conllEvalDataBuilder = new StringBuilder();

		dataBuilder.append("-DOCSTART- -X- O O");
		dataBuilder.append(newline);
		dataBuilder.append(newline);

		for (CoNLLSentence coNLLSentence : data) {
			String[] lines = coNLLSentence.getLines();

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			if (lines.length > 1) {

				// this is the vertical column
				ArrayList<ArrayList<String>> listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);

				// build word string
				ArrayList<String> tokenList = listOfLists.get(conllformat_form_column_no);
				ArrayList<String> verbList = listOfLists.get(conllformat_verb_column_no);

				for (int i = 0; i < lines.length; i++) {
					StringBuilder rowBuilder = new StringBuilder();
					StringBuilder rowBuilder2 = new StringBuilder();
					String w = tokenList.get(i);

					String verb = verbList.get(i);
					String v = o;

					if (!verb.equals("_")) {

						v = b + "V";

					}

					rowBuilder.append(w + delimiter + "X" + delimiter + "X" + delimiter + v);
					rowBuilder2.append(w + delimiter + v);

					sb.append(rowBuilder);
					sb.append(newline);

					sb2.append(rowBuilder2);
					sb2.append(newline);

				}
				dataBuilder.append(sb.toString());
				dataBuilder.append(newline);

				conllEvalDataBuilder.append(sb2.toString());
				conllEvalDataBuilder.append(newline);

			}

			// System.out.println(sb.toString());
		}

		builders.add(dataBuilder);
		builders.add(conllEvalDataBuilder);

		return builders;

	}

	/**
	 * This function will create the predicate word list for all of SrL data.
	 * 
	 * @throws IOException
	 */
	public void createPredicateWordList() throws IOException {
		String t1 = "data/deeplearning/test/verbDict_SRL.txt";
		String t2 = "data/deeplearning/train/verbDict_SRL.txt";

		List<String> t11 = FileUtils.readLines(new File(t1));
		List<String> t12 = FileUtils.readLines(new File(t2));

		Set<String> predicateSet = new HashSet<>();

		for (String string : t11) {
			predicateSet.add(string);
		}

		for (String string : t12) {
			predicateSet.add(string);
		}

		StringBuilder sb = new StringBuilder();
		for (String string : predicateSet) {
			sb.append(string);
			sb.append(newline);
		}
		String rootDir = "data/deeplearning/train";
		String verbDictFile = rootDir + File.separator + "verbDict_SRL.txt";
		System.out.println(predicateSet.size());
		System.out.println("wrote to : " + verbDictFile);
		FileUtils.write(new File(verbDictFile), sb.toString(), "UTF-8");

	}

	private void initializeVariables() {

		notAddedSentences = new LinkedList<>();
		wrongSyntaxSentenceList = new LinkedList<>();

		qq_dict_set = new LinkedHashSet<>();

		dfc = new DataFormatConverter();

		wordDictSet = new LinkedHashSet<>();
		verbDictSet = new LinkedHashSet<>();
		oovWords = new LinkedHashSet<>();

		labelSet = new LinkedHashSet<>();

		sentenceTokenList = new LinkedList<>();

		globaladdedSentences = new LinkedList<>();
	}

	/**
	 * Read CONLL format sentences given input list of files
	 * 
	 * @return
	 */
	public ArrayList<CoNLLSentence> readSentences(List<File> files) {

		ArrayList<CoNLLSentence> totalData = new ArrayList<>();
		for (File file : files) {

			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);
			totalData.addAll(sentenceList);
		}

		return totalData;
	}

	/**
	 * This function will read all the srl tagged sentences
	 * 
	 * @return
	 */
	public Corpus readAllSRLSentences() {

		/**
		 * we read from following sources : 1. SRL Train Data 2. SRL Evaluation
		 * Data 3. CONLL2009 Data converted to our format
		 */

		ArrayList<File> files = new ArrayList<>();

		String filePath = "data/corpus_validation/final/srl-emotibot-train_hit.txt";

		String testDataDir = "data/corpus_validation/bad_cases_analysis/input";
		File rootDir1 = new File(testDataDir);
		final String[] SUFFIX = { "txt" };
		Collection<File> files_temp1 = FileUtils.listFiles(rootDir1, SUFFIX, true);
		files_temp1.add(new File(filePath));

		// read srl train sentences:
		ArrayList<File> trainfiles = new ArrayList<>();
		trainfiles.addAll(files_temp1);
		ArrayList<CoNLLSentence> trainSRLSentences = readSentences(trainfiles);

		// read srl test sentences
		String testDataDir1 = "evaluation/gold_data";
		File rootDir2 = new File(testDataDir1);
		Collection<File> files_temp2 = FileUtils.listFiles(rootDir2, SUFFIX, true);

		ArrayList<File> testfiles = new ArrayList<>();
		testfiles.addAll(files_temp2);
		ArrayList<CoNLLSentence> testSRLSentences = readSentences(testfiles);

		// read conll2009 sentences
		String conll2009Dir = "data/conll2009-chinese-srl/hit";
		File rootDir3 = new File(conll2009Dir);
		Collection<File> files_temp3 = FileUtils.listFiles(rootDir3, SUFFIX, true);

		ArrayList<File> conll2009Files = new ArrayList<>();
		conll2009Files.addAll(files_temp3);
		ArrayList<CoNLLSentence> conll2009CorpusSentences = readSentences(conll2009Files);

		Corpus corpus = new Corpus();
		corpus.setConll2009CorpusSentences(conll2009CorpusSentences);
		corpus.setTestSRLSentences(testSRLSentences);
		corpus.setTrainSRLSentences(trainSRLSentences);

		// this is the final concatenated data

		return corpus;

	}

	/**
	 * THis function creates the srl corpus for training word 2 vec.
	 * 
	 * @throws IOException
	 */
	public void createEmbeddingTrainingData() throws IOException {

		// these two are teh output files
		String outputFile = "data/deeplearning/w2v/srl_sentences.txt";
		String vocabFile = "data/deeplearning/w2v/vocab.txt";

		StringBuilder trainDataBuilder = new StringBuilder();
		StringBuilder testDataBuilder = new StringBuilder();
		StringBuilder vocabFileBuilder = new StringBuilder();

		Set<String> vocabSet = new LinkedHashSet<>();

		// read all srl data
		Corpus totalData = readAllSRLSentences();
		// ArrayList<CoNLLSentence>
		// trainSRLSentences=totalData.getTrainSRLSentences();
		// ArrayList<CoNLLSentence>
		// testSRLSentences=totalData.getTestSRLSentences();
		// ArrayList<CoNLLSentence>
		// conll2009CorpusSentences=totalData.getConll2009CorpusSentences();

		ArrayList<CoNLLSentence> finalData = totalData.getAllData();

		// create train data

		for (CoNLLSentence coNLLSentence : finalData) {
			List<String> tokens = coNLLSentence.getTokenList();
			StringBuilder localbuilder = new StringBuilder();
			for (String string : tokens) {
				localbuilder.append(string + space);
				vocabSet.add(string);
			}

			trainDataBuilder.append(localbuilder.toString().trim());
			trainDataBuilder.append(newline);

		}

		for (String word : vocabSet) {
			vocabFileBuilder.append(word);
			vocabFileBuilder.append(newline);

		}

		FileUtils.writeStringToFile(new File(outputFile), trainDataBuilder.toString(), "UTF-8");
		FileUtils.writeStringToFile(new File(vocabFile), vocabFileBuilder.toString(), "UTF-8");

		System.out.println(trainDataBuilder.toString());
		System.out.println("total sentences : " + finalData.size());
		System.out.println("total words : " + vocabSet.size());

	}

	/**
	 * This function created training data for srl by aggregating sentences from
	 * different folders.
	 * 
	 * @throws IOException
	 */
	public void createTrainDataForSRL() throws IOException {
		String filePath = "data/corpus_validation/final/srl-emotibot-train_hit.txt";

		String testDataDir = "data/corpus_validation/bad_cases_analysis/input";
		File rootDir1 = new File(testDataDir);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir1, SUFFIX, true);

		// lets add the main file to this list also
		files.add(new File(filePath));

		// iterate through all the files
		ArrayList<CoNLLSentence> totalData = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (File file : files) {

			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);
			System.out.println("sentence read : " + sentenceList.size());
			totalData.addAll(sentenceList);
		}

		// use this to select limited number of sentences for testing purpose.
		boolean useLimitedSentences = false;
		int noSentences = 500;
		boolean addOOVSentences = false;

		DeepData deepDS = transformData(totalData, useLimitedSentences, noSentences, addOOVSentences);

		String rootDir = "data/deeplearning/train";
		String verbDictFile = rootDir + File.separator + "verbDict_SRL.txt";
		String wordDictFile = rootDir + File.separator + "wordDict_SRL.txt";
		String targetDictFile = rootDir + File.separator + "targetDict.txt";
		String propFile = rootDir + File.separator + "prop_SRL.txt";
		String wordsFile = rootDir + File.separator + "words_SRL.txt";
		String tokenizedSentenceFile = rootDir + File.separator + "tokenized_sentences.txt";
		String conllSentenceFile = rootDir + File.separator + "conll_sentences.txt";

		// write data to files

		StringBuilder wordsFileBuilder = deepDS.getWordsFileBuilder();
		StringBuilder propositionFileBuilder = deepDS.getPropositionFileBuilder();

		StringBuilder wordDictBuilder = deepDS.getWordDictBuilder();
		StringBuilder verbDictBuilder = deepDS.getVerbDictBuilder();
		StringBuilder targetLabelDictBuilder = deepDS.getTargetLabelDictBuilder();

		// write wordDict
		FileUtils.writeStringToFile(new File(wordDictFile), wordDictBuilder.toString(), "UTF-8");
		FileUtils.writeStringToFile(new File(verbDictFile), verbDictBuilder.toString(), "UTF-8");

		FileUtils.writeStringToFile(new File(targetDictFile), targetLabelDictBuilder.toString(), "UTF-8");

		FileUtils.writeStringToFile(new File(wordsFile), wordsFileBuilder.toString(), "UTF-8");
		FileUtils.writeStringToFile(new File(propFile), propositionFileBuilder.toString(), "UTF-8");

		StringBuilder sentenceTokenDataBuilder = new StringBuilder();
		for (List<String> tokens : sentenceTokenList) {
			int count = 0;
			for (String string : tokens) {
				count++;
				sentenceTokenDataBuilder.append(string);
				if (count != tokens.size()) {
					sentenceTokenDataBuilder.append(" ");
				}

			}
			sentenceTokenDataBuilder.append(newline);
		}

		FileUtils.writeStringToFile(new File(tokenizedSentenceFile), sentenceTokenDataBuilder.toString(), "UTF-8");

		// lets add final train data sentences
		StringBuilder dataBuilder = new StringBuilder();
		List<String> dataString = generateOnlyArgumentCONLL(globaladdedSentences);
		for (String data : dataString) {
			dataBuilder.append(data);
			dataBuilder.append(newline);
		}
		FileUtils.writeStringToFile(new File(conllSentenceFile), dataBuilder.toString(), "UTF-8");

		System.out.println("-------------------------------");
		System.out.println("Use Limited Sentence parameter : " + useLimitedSentences);
		System.out.println("Total sentences read in data : " + totalData.size());
		int totalAddedSentences = deepDS.getPropositionFileData().size();
		System.out.println("Total sentences added :" + totalAddedSentences);
		System.out.println("Total sentences not added: " + (totalData.size() - totalAddedSentences));
		System.out.println("Total target labels added :" + deepDS.getTargetDictLabels().size());
		System.out.println("Total predicates in predicate dictionary :" + deepDS.getVerbDictSet().size());
		System.out.println("Total words in data : " + deepDS.getWordDictSet().size());

		System.out.println("-------------------------------");
		System.out.println("Stats for Data with problems..");

		System.out.println(
				"Total sentences with OOV words (not present in w2v dictionary) : " + notAddedSentences.size());
		System.out.println("Total sentences with syntax problems : " + wrongSyntaxSentenceList.size());
		System.out.println("Total OOV words (not present in w2v dictionary) : " + oovWords.size());
	}

	/**
	 * This function created test data for srl by aggregating sentences from
	 * different folders.
	 * 
	 * @throws IOException
	 */
	public void createTestDataForSRL() throws IOException {

		String rootDir = "data/deeplearning/test";

		String testDataDir = "evaluation/gold_data";
		File rootDir1 = new File(testDataDir);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir1, SUFFIX, true);

		// iterate through all the files
		ArrayList<CoNLLSentence> totalData = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (File file : files) {

			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);
			totalData.addAll(sentenceList);
		}
		for (CoNLLSentence coNLLSentence : totalData) {
			String conll = coNLLSentence.getHITSentence();
			sb.append(conll);
			sb.append(newline);
		}

		String testData = rootDir + File.separator + "srl/testData_srl_conll09.txt";
		File testDataFile = new File(testData);
		// write test data
		FileUtils.write(testDataFile, sb.toString());

		// use this to select limited number of sentenes for testing purpose.
		boolean useLimitedSentences = false;
		int noSentences = 100;
		boolean addOOVSentences = false;

		DeepData deepDS = transformData(totalData, useLimitedSentences, noSentences, addOOVSentences);

		String verbDictFile = rootDir + File.separator + "verbDict_SRL.txt";
		String wordDictFile = rootDir + File.separator + "wordDict_SRL.txt";
		String targetDictFile = rootDir + File.separator + "targetDict.txt";
		String propFile = rootDir + File.separator + "prop_SRL.txt";
		String wordsFile = rootDir + File.separator + "words_SRL.txt";
		String tokenizedSentenceFile = rootDir + File.separator + "tokenized_sentences.txt";
		String conllSentenceFile = rootDir + File.separator + "conll_sentences.txt";

		// write data to files

		StringBuilder wordsFileBuilder = deepDS.getWordsFileBuilder();
		StringBuilder propositionFileBuilder = deepDS.getPropositionFileBuilder();

		StringBuilder wordDictBuilder = deepDS.getWordDictBuilder();
		StringBuilder verbDictBuilder = deepDS.getVerbDictBuilder();
		StringBuilder targetLabelDictBuilder = deepDS.getTargetLabelDictBuilder();

		FileUtils.writeStringToFile(new File(wordDictFile), wordDictBuilder.toString(), "UTF-8");
		FileUtils.writeStringToFile(new File(verbDictFile), verbDictBuilder.toString(), "UTF-8");

		FileUtils.writeStringToFile(new File(targetDictFile), targetLabelDictBuilder.toString(), "UTF-8");

		FileUtils.writeStringToFile(new File(wordsFile), wordsFileBuilder.toString(), "UTF-8");
		FileUtils.writeStringToFile(new File(propFile), propositionFileBuilder.toString(), "UTF-8");

		StringBuilder sentenceTokenDataBuilder = new StringBuilder();
		for (List<String> tokens : sentenceTokenList) {
			int count = 0;
			for (String string : tokens) {
				count++;
				sentenceTokenDataBuilder.append(string);
				if (count != tokens.size()) {
					sentenceTokenDataBuilder.append(" ");
				}

			}
			sentenceTokenDataBuilder.append(newline);
		}

		FileUtils.writeStringToFile(new File(tokenizedSentenceFile), sentenceTokenDataBuilder.toString(), "UTF-8");

		// lets add final test data sentences
		StringBuilder dataBuilder = new StringBuilder();
		List<String> dataString = generateOnlyArgumentCONLL(globaladdedSentences);
		for (String data : dataString) {
			dataBuilder.append(data);
			dataBuilder.append(newline);
		}
		FileUtils.writeStringToFile(new File(conllSentenceFile), dataBuilder.toString(), "UTF-8");

		System.out.println("-------------------------------");
		System.out.println("Use Limited Sentence parameter : " + useLimitedSentences);
		System.out.println("Total sentences read in data : " + totalData.size());
		int totalAddedSentences = deepDS.getPropositionFileData().size();
		System.out.println("Total sentences added :" + totalAddedSentences);
		System.out.println("Total sentences not added: " + (totalData.size() - totalAddedSentences));
		System.out.println("Total target labels added :" + deepDS.getTargetDictLabels().size());
		System.out.println("Total predicates in predicate dictionary :" + deepDS.getVerbDictSet().size());
		System.out.println("Total words in data : " + deepDS.getWordDictSet().size());

		System.out.println("-------------------------------");
		System.out.println("Stats for Data with problems..");

		System.out.println(
				"Total sentences with OOV words (not present in w2v dictionary) : " + notAddedSentences.size());
		System.out.println("Total sentences with syntax problems : " + wrongSyntaxSentenceList.size());
		System.out.println("Total OOV words (not present in w2v dictionary) : " + oovWords.size());

	}

	/**
	 * Transform data based on parameters
	 * 
	 * @param sentenceList
	 * @param useLimitedSentences
	 * @param limitedSentenceNum
	 * @param addOOVSentences
	 * @return
	 * @throws IOException
	 */
	public DeepData transformData(ArrayList<CoNLLSentence> sentenceList, boolean useLimitedSentences,
			int limitedSentenceNum, boolean addOOVSentences) throws IOException {
		// we first call the init variable functions to reset
		initializeVariables();

		createQQDictSet();

		DeepData deepDS = new DeepData();

		List<CoNLLSentence> subSet;
		if (useLimitedSentences) {
			subSet = sentenceList.subList(0, limitedSentenceNum);

		} else {
			subSet = sentenceList;
		}

		// lets validate all the data
		List<CoNLLSentence> validSet = new LinkedList<>();
		List<CoNLLSentence> invalidSet = new LinkedList<>();
		for (CoNLLSentence coNLLSentence : subSet) {
			boolean valid = dfc.validateHITFormatData(coNLLSentence);
			if (valid) {
				validSet.add(coNLLSentence);

			} else {
				invalidSet.add(coNLLSentence);
			}
		}

		// get sentences without any syntax issues ..
		subSet = checkDataSyntax(validSet);
		int sentencesWithSyntaxProblems = sentenceList.size() - subSet.size();

		List<CoNLLSentence> addedSentences = new LinkedList<>();

		for (CoNLLSentence coNLLSentence : subSet) {

			List<String> data = getCONLL05FormatData(coNLLSentence, addOOVSentences);

			if (data.size() > 1) {
				String sentenceString = data.get(0);
				String propString = data.get(1);

				// add token sentence in conll format
				deepDS.getWordsFileData().add(sentenceString);
				// add proposition sentence in conll format
				deepDS.getPropositionFileData().add(propString);

				addedSentences.add(coNLLSentence);

			}

		}

		System.out.println("printing invalid sentences.. total : " + invalidSet.size());
		for (CoNLLSentence coNLLSentence : invalidSet) {
			System.err.println(coNLLSentence.getHITSentence());
		}

		System.out.println("total sentences : " + sentenceList.size());
		System.out.println("sentences  added  :  " + addedSentences.size());
		System.out.println("sentences not added  :  " + notAddedSentences.size());
		System.out.println("sentences with syntax issue : " + sentencesWithSyntaxProblems);

		// lets create data dicts
		Set<String> targetDictLabels = createPredictedLabelsSet();

		deepDS.setTargetDictLabels(targetDictLabels);
		deepDS.setWordDictSet(wordDictSet);
		deepDS.setVerbDictSet(verbDictSet);

		deepDS.runBuilders();

		globaladdedSentences.addAll(addedSentences);

		return deepDS;
	}

	/**
	 * Transform data
	 * 
	 * @throws IOException
	 */
	public DeepData transformData(String filePath, boolean useLimitedSentences, int limitedSentenceNum,
			boolean addOOVSentences) throws IOException {

		// read the file
		File file = new File(filePath);

		ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);

		return transformData(sentenceList, useLimitedSentences, limitedSentenceNum, addOOVSentences);

	}

	private void createQQDictSet() throws IOException {
		// TODO Auto-generated method stub
		List<String> qq_dict_lines = FileUtils.readLines(new File(qq_dict_file));

		qq_dict_set = new HashSet<>();

		for (String string : qq_dict_lines) {
			qq_dict_set.add(string);
		}
	}

	/**
	 * Check Data Syntax
	 * 
	 * @param subSet
	 * @return
	 */
	private List<CoNLLSentence> checkDataSyntax(List<CoNLLSentence> subSet) {

		List<CoNLLSentence> correctSyntaxSentenceList = new ArrayList<>();

		int wrongSyntax = 0;

		for (CoNLLSentence coNLLSentence : subSet) {

			boolean correct = checkDataSyntax(coNLLSentence);

			if (correct) {
				correctSyntaxSentenceList.add(coNLLSentence);
			} else {
				wrongSyntaxSentenceList.add(coNLLSentence);
				wrongSyntax++;
			}

		}

		return correctSyntaxSentenceList;

	}

	private List<String> generateOnlyArgumentCONLL(List<CoNLLSentence> CoNLLSentenceList) {
		List<String> results = new LinkedList<>();

		for (CoNLLSentence sentence : CoNLLSentenceList) {
			String res = generateOnlyArgumentCONLLHelper(sentence);
			results.add(res);
		}

		return results;
	}

	private String generateOnlyArgumentCONLLHelper(CoNLLSentence coNLLSentence) {
		// TODO Auto-generated method stub
		// this is horizontal row present in conll2009 string
		String[] lines = coNLLSentence.getLines();
		int first_argument_column = conllformat_first_arg_column_no;
		StringBuilder sb = new StringBuilder();
		if (lines.length > 1) {

			// total number of columns
			int total_columns = TAB_PATTERN.split(lines[0]).length;

			// this is the vertical column
			ArrayList<ArrayList<String>> listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);

			// build word string
			ArrayList<String> tokenList = listOfLists.get(conllformat_form_column_no);

			for (int i = 0; i < lines.length; i++) {
				StringBuilder rowBuilder = new StringBuilder();
				String w = tokenList.get(i);
				rowBuilder.append(w);
				for (int j = first_argument_column; j < total_columns; j++) {
					String c = listOfLists.get(j).get(i);
					rowBuilder.append(delimiter + c);

				}
				sb.append(rowBuilder);
				sb.append(newline);

			}

		}

		System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * 
	 * @param coNLLSentence
	 * @return
	 */
	private boolean checkDataSyntax(CoNLLSentence coNLLSentence) {

		boolean isCorrect = true;

		String leftP = "(";
		String rightP = ")";

		int leftParenthesis = 0;
		int rightParenthesis = 0;

		int first_argument_column = conllformat_first_arg_column_no;

		// this is horizontal row present in conll2009 string
		String[] lines = coNLLSentence.getLines();

		if (lines.length > 1) {

			// total number of columns
			int total_columns = TAB_PATTERN.split(lines[0]).length;

			// this is the vertical column
			ArrayList<ArrayList<String>> listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);

			for (int i = 0; i < lines.length; i++) {

				for (int j = first_argument_column; j < total_columns; j++) {
					String c = listOfLists.get(j).get(i);

					c = c.toUpperCase();
					if (c.contains(leftP)) {
						leftParenthesis++;
					}
					if (c.contains(rightP)) {
						rightParenthesis++;
					}

				}

			}
			if (leftParenthesis != rightParenthesis) {
				System.err.println("mismatch in  parenthesis ");
				System.err.println(coNLLSentence.getHITSentence());
				isCorrect = false;
			}

		} else {
			isCorrect = false;
		}
		return isCorrect;
	}

	/**
	 * Creates a predicted label set
	 * 
	 * @return
	 */
	private Set<String> createPredictedLabelsSet() {
		Set<String> uniqueLabels = new HashSet<>();
		Set<String> targetDict = new LinkedHashSet<>();
		for (String label : labelSet) {
			label = label.replace("(", "");
			label = label.replace("*", "");
			label = label.replace(")", "");

			uniqueLabels.add(label);
			// System.out.println(label);

		}

		targetDict.add(o);
		for (String label : uniqueLabels) {

			if (!Strings.isNullOrEmpty(label)) {
				String str1 = b + label;
				String str2 = i + label;

				targetDict.add(str1);
				targetDict.add(str2);
			}

		}
		System.out.println("Total srl labels: " + uniqueLabels.size());
		System.out.println("Total target labels in BIO format: " + targetDict.size());
		for (String label : targetDict) {
			System.out.println(label);
		}

		return targetDict;
	}

	/**
	 * Create CONLL05 format data
	 * 
	 * @param coNLLSentence
	 * @param addOOVSentences
	 * @return
	 */
	private List<String> getCONLL05FormatData(CoNLLSentence coNLLSentence, boolean addOOVSentences) {

		List<String> output = new LinkedList<>();

		int predicate_column_no = conllformat_verb_column_no;

		String hitString = coNLLSentence.getHITSentence();

		StringBuilder wBuilder = new StringBuilder();
		StringBuilder propBuilder = new StringBuilder();

		// this is horizontal row present in conll2009 string
		String[] lines = coNLLSentence.getLines();

		if (lines.length > 1) {

			// total number of columns
			int total_columns = TAB_PATTERN.split(lines[0]).length;

			// this is the vertical column
			ArrayList<ArrayList<String>> listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);

			// build word string
			ArrayList<String> tokenList = listOfLists.get(conllformat_form_column_no);

			// adding these to main token list
			sentenceTokenList.add(tokenList);

			boolean noqqEntry = false;
			for (String string : tokenList) {

				if (!qq_dict_set.contains(string)) {
					noqqEntry = true;
					System.out.println(tokenList + " not in qq");
					oovWords.add(string);
				}

			}

			// if the option is not add sentences with OOV words, we set
			// addOOVSentences to the detected value based from qq_dict_set.

			boolean addSentence = addOOVSentences;
			if (!addOOVSentences) {
				addSentence = noqqEntry;
			}

			if (!addSentence) {

				for (String string : tokenList) {

					// System.out.println(string);
					if (Strings.isNullOrEmpty(string) || string.equals(" ")) {

						System.out.println("token is emty");
					} else {

					}

					wBuilder.append(string);
					wBuilder.append(newline);
					// also add the word to wordDictSet file
					wordDictSet.add(string);

				}
				wBuilder.append(newline);

				// add the output string
				output.add(wBuilder.toString());

				for (int i = 0; i < lines.length; i++) {

					for (int j = predicate_column_no; j < total_columns; j++) {
						String c = listOfLists.get(j).get(i);

						c = c.toUpperCase();

						if (j == predicate_column_no) {
							String[] vals = c.split("\\.");
							c = vals[0];
							if (!Strings.isNullOrEmpty(c)) {
								verbDictSet.add(c);
							}
						}
						//
						if (j >= conllformat_first_arg_column_no) {
							if (c.equals("A1*")) {
								System.out.println(coNLLSentence.getHITSentence());
							}
							c = fixHumanTypos(c);
							labelSet.add(c);
						}

						if (j == total_columns - 1) {
							propBuilder.append(c);
						} else {
							propBuilder.append(c + delimiter);
						}

					}
					propBuilder.append(newline);

				}

				// // we will add all args
				// for (int i = conllformat_first_arg_column_no; i <
				// listOfLists.size(); i++) {
				//
				// ArrayList<String> column = listOfLists.get(i);
				//
				// for (int j = 0; j < column.size(); j++) {
				//
				// String string = column.get(j);
				// // System.out.println(string);
				// string = fixHumanTypos(string);
				// labelSet.add(string);
				// }
				// }

				// now append a new line at the end of the sentence
				propBuilder.append(newline);
				// add the prop string
				output.add(propBuilder.toString());
			}

			else {
				notAddedSentences.add(coNLLSentence);

			}
		}
		// System.out.println(sb.toString());
		return output;
	}

	/**
	 * Checks missing words between given two dictionaries
	 * 
	 * @throws IOException
	 */
	private void checkMissingWords(String wordsFile) throws IOException {

		List<String> qq_dict_lines = FileUtils.readLines(new File(qq_dict_file));
		List<String> srl_dict_lines = FileUtils.readLines(new File(wordsFile));

		qq_dict_set = new HashSet<>();
		Set<String> srl_dict_set = new HashSet<>();

		for (String string : qq_dict_lines) {
			qq_dict_set.add(string);
		}

		for (String string : srl_dict_lines) {
			srl_dict_set.add(string);
		}

		// long counter=0;
		// //lets check for missing words now in large qq dict file
		// for (String string : srl_dict_set) {
		// if(!qq_dict_file.contains(string)){
		// System.out.println(string);
		// counter++;
		// }
		// }
		// System.out.println("total " + counter + " missing words");

	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public String fixHumanTypos(String string) {
		String new_s = string;
		new_s = new_s.toUpperCase();

		new_s = new_s.replace("）", ")");
		new_s = new_s.replace("（", "(");
		new_s = new_s.replace("((", "(");
		new_s = new_s.replace("))", ")");

		new_s = new_s.replace("*_", "*");
		new_s = new_s.replace("**", "*");
		new_s = new_s.replace(")*", ")");
		new_s = new_s.replace("*)*", "*)");
		new_s = new_s.replace(")*)", "*)");

		new_s = new_s.replace("(C-ARG*)", "*");
		new_s = new_s.replace("(C-ARG*", "*");
		new_s = new_s.replace("(C-A1*)", "*");

		new_s = new_s.replace("(MDO*)", "(MOD*)");
		new_s = new_s.replace("(NOD*)", "(MOD*)");
		new_s = new_s.replace("(C-A0*)", "(A0*)");
		new_s = new_s.replace("(TEP*)", "(TMP*)");
		new_s = new_s.replace("(ATP)*", "(ATP*)");

		new_s = new_s.replace("(ADC*)", "(ADV*)");
		new_s = new_s.replace("(AVD*)", "(ADV*)");

		new_s = new_s.replace("(NRG*)", "(NEG*)");
		new_s = new_s.replace("(NRG*)", "(NEG*)");
		new_s = new_s.replace("(ENG*)", "(NEG*)");

		new_s = new_s.replace("(ATZ*", "(ATA*");
		new_s = new_s.replace("(ATO*)", "(ATP*)");
		new_s = new_s.replace("(RAG*)", "*");

		new_s = new_s.replace("(TGT*)", "*");

		// new_s = new_s.replace("A", "A0");

		return new_s;

	}

	/**
	 * 
	 * @param dataFolder
	 * @throws IOException
	 */
	public void constructCONLLFromBIO(String dataFile) throws IOException {
		File file = new File(dataFile);
		List<String> lines = FileUtils.readLines(file);

		Multimap<String, String> sentenceCollection = HashMultimap.create();

		for (String string : lines) {
			String[] values = string.split("\t");
			List<String> tokens = Arrays.asList(values[0].split(" "));
			StringBuilder sb = new StringBuilder();
			for (String string2 : tokens) {
				sb.append(string2);
			}

			sentenceCollection.put(sb.toString(), string);
		}

		for (String string : sentenceCollection.keySet()) {
			Collection<String> values = sentenceCollection.get(string);
			for (String string2 : values) {
				System.out.println(string2);
			}
			System.out.println("");
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DataTransformer dts = new DataTransformer();

		// String f = "data/deeplearning/deep_format/train.txt";
		// dts.constructCONLLFromBIO(f);

//		dts.createTrainDataForSRL();
//		dts.createTestDataForSRL();
		// dts.createPredicateWordList();

		// dts.createEmbeddingTrainingData();

		// dts.createPredicateIdentificationModelData();
		
		dts.organizeSRLData();
	}

}
