package com.emotibot.srl.utilities;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.conll2009.parser.Parser;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.google.common.base.Strings;
import static com.emotibot.srl.format.Constants.*;

public class StatisticsGenerator {

	DataFormatConverter dfc;
	Parser conllFormatParser;
	Set<String> globalRelationSet;

	public StatisticsGenerator() throws IOException {
		dfc = new DataFormatConverter();
		globalRelationSet = new HashSet<String>();

		conllFormatParser = new Parser();

	}

	public void generateJSON(String dataFolder, String outputFile) throws IOException {

		// String dataFolder="data/corpus_validation/good_cases/hit";
		// String jsonFile1="data/corpus_validation/json/json_srl.txt";
		// String jsonFile2="python/data/json_srl.json";
		ArrayList<CoNLLSentence> data = readCoNLLFormatCorpus(dataFolder, Format.HIT);

		addInformationtoData(data);

		String sentenceDataJSON = createSentenceDataJson(data);

		String wordDataJSON = createWordDataJson(data);

		//
		// System.out.println("Json is created already");
		// System.out.println(json);

		FileUtils.write(new File(outputFile), sentenceDataJSON);

		System.out.println("Wrote to file " + outputFile);

		for (String rel : globalRelationSet) {
			System.out.println(rel);
		}

	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public String createWordDataJson(ArrayList<CoNLLSentence> data) {
		JsonBuilderFactory factory = Json.createBuilderFactory(new LinkedHashMap<String, String>());
		JsonArrayBuilder sentenceInfoBuilder = factory.createArrayBuilder();

		long sentenceCounter = 0;
		for (CoNLLSentence coNLLSentence : data) {

			sentenceCounter++;
			String sentence = coNLLSentence.getSentence();
			// String hit = coNLLSentence.getHITSentence();

			String file = coNLLSentence.getSource();
			String src = coNLLSentence.getSource();
			String[] tokens = src.split("/");
			if (tokens != null && tokens.length > 0) {
				String src1 = tokens[tokens.length - 2];
				src = src1;
			}

			ArrayList<ArrayList<String>> listOfLists = coNLLSentence.getDataList();
			ArrayList<String> posList = listOfLists.get(conllformat_pos_column_no );
			ArrayList<String> formList = listOfLists.get(conllformat_form_column_no );

			int sentenceTokenLength = formList.size();

			if (sentenceTokenLength > 0) {

				JsonObjectBuilder textBuilder = null;

				for (int i = 0; i < formList.size(); i++) {

					// textBuilder = Json.createObjectBuilder();

					textBuilder = factory.createObjectBuilder();
					String word = formList.get(i);
					String pos = posList.get(i);

					textBuilder.add("word", word);
					textBuilder.add("pos", pos);

					// add sentence id
					textBuilder.add("sentence_id", sentenceCounter);

					// add sentence
					textBuilder.add("sentence", sentence);

					// add src
					textBuilder.add("src", src);
					File f = new File(file);
					textBuilder.add("file", f.getName());

					sentenceInfoBuilder.add(textBuilder);
				}

			}

		}

		JsonObjectBuilder textBuilder1 = Json.createObjectBuilder();
		textBuilder1.add("words", sentenceInfoBuilder);
		JsonObject testNodeJsonObject = textBuilder1.build();

		Map<String, Boolean> config = new HashMap<>();
		config.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory jwf = Json.createWriterFactory(config);
		StringWriter sw = new StringWriter();
		try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
			jsonWriter.writeObject(testNodeJsonObject);
		}

		// System.out.println(sw.toString());

		return sw.toString();

	}

	/**
	 * Generate all stats
	 * 
	 * @param dataFolder
	 * @param dataJsonFile
	 * @param wordJsonFile
	 * @throws IOException
	 */
	public void generateDataStatsAll(String dataFolder, String dataJsonFile, String wordJsonFile, Format format)
			throws IOException {

		ArrayList<CoNLLSentence> data = readCoNLLFormatCorpus(dataFolder, format);

		generateDataStatsAll(data, dataJsonFile,wordJsonFile);

	}
	
	/**
	 * Generate all stats
	 * 
	 * @param dataFolder
	 * @param dataJsonFile
	 * @param wordJsonFile
	 * @throws IOException
	 */
	public void generateDataStatsAll(ArrayList<CoNLLSentence> data, String dataJsonFile, String wordJsonFile)
			throws IOException {

		

		// add data information
		addInformationtoData(data);

		String sentenceDataJSON = createSentenceDataJson(data);

		String wordDataJSON = createWordDataJson(data);

		//
		// System.out.println("Json is created already");
		// System.out.println(json);

		FileUtils.write(new File(dataJsonFile), sentenceDataJSON);

		FileUtils.write(new File(wordJsonFile), wordDataJSON);

		System.out.println("Wrote to file " + dataJsonFile);
		System.out.println("Wrote to file " + wordJsonFile);

		for (String rel : globalRelationSet) {
			System.out.println(rel);
		}

	}
	

	/**
	 * Generate Sentence Data for further analysis in files like python
	 * 
	 * @param dataFolder
	 * @param dataJsonFile
	 * @param wordJsonFile
	 * @throws IOException
	 */
	public void generateSentenceData(String dataFolder, String dataJsonFile, Format format) throws IOException {

		ArrayList<CoNLLSentence> data = readCoNLLFormatCorpus(dataFolder, format);
		generateSentenceData(data, dataJsonFile, format);
	}

	/**
	 * Generate Sentence Data for further analysis in files like python
	 * 
	 * @param dataFolder
	 * @param dataJsonFile
	 * @param wordJsonFile
	 * @throws IOException
	 */
	public void generateSentenceData(ArrayList<CoNLLSentence> data, String dataJsonFile, Format format)
			throws IOException {

		// add data information
		addInformationtoData(data);

		String sentenceDataJSON = createSentenceDataJson(data);
		//
		// System.out.println("Json is created already");
		// System.out.println(json);

		FileUtils.write(new File(dataJsonFile), sentenceDataJSON);

		System.out.println("Wrote to file " + dataJsonFile);
		for (String rel : globalRelationSet) {
			System.out.println(rel);
		}

	}

	/**
	 * 
	 * @param dataFolder
	 * @param dataJsonFile
	 * @param wordJsonFile
	 * @throws IOException
	 */
	public void generateWordData(String dataFolder, String wordJsonFile, Format format) throws IOException {

		ArrayList<CoNLLSentence> data = readCoNLLFormatCorpus(dataFolder, format);

		// add data information
		addInformationtoData(data);

		String wordDataJSON = createWordDataJson(data);

		//
		// System.out.println("Json is created already");
		// System.out.println(json);

		FileUtils.write(new File(wordJsonFile), wordDataJSON);

		System.out.println("Wrote to file " + wordJsonFile);

	}

	private void test() throws IOException {

		

//		String dataFolder = "data/editorial-q-q";
//		String jsonFile1 = "data/editorial-q-q/output/srl_test.json";
//		String jsonFile2 = "data/editorial-q-q/output/json_srl.json";
//		String jsonFile3 = "data/editorial-q-q/output/word_info_srl.json";

		 String dataFolder = "data/corpus_validation/good_cases/hit";
		 String jsonFile1 = "data/corpus_validation/json/srl_test.json";
		 String jsonFile2 = "python/data/json_srl.json";
		 String jsonFile3 = "python/data/word_info_srl.json";

		ArrayList<CoNLLSentence> data = readCoNLLFormatCorpus(dataFolder, Format.HIT);

		// add data information
		addInformationtoData(data);

		String sentenceDataJSON = createSentenceDataJson(data);

		String wordDataJSON = createWordDataJson(data);

		//
		// System.out.println("Json is created already");
		// System.out.println(json);

		FileUtils.write(new File(jsonFile1), sentenceDataJSON);
		FileUtils.write(new File(jsonFile2), sentenceDataJSON);

		FileUtils.write(new File(jsonFile3), wordDataJSON);

		System.out.println("Wrote to file " + jsonFile1);
		System.out.println("Wrote to file " + jsonFile2);
		System.out.println("Wrote to file " + jsonFile3);

		for (String rel : globalRelationSet) {
			System.out.println(rel);
		}

	}

	/**
	 * Add Information to sentence data.
	 * 
	 * @param data
	 */
	public void addInformationtoData(ArrayList<CoNLLSentence> data) {
		// TODO Auto-generated method stub
		for (CoNLLSentence coNLLSentence : data) {

			addRelationInformation(coNLLSentence);
			addPredicateInformation(coNLLSentence);
			addWordInformation(coNLLSentence);
			// System.out.println(coNLLSentence.getSentence());
		}
	}

	/**
	 * Add Word Information to the Data
	 * 
	 * @param coNLLSentence
	 */
	public void addWordInformation(CoNLLSentence coNLLSentence) {
		// TODO Auto-generated method stub

		// String conll = "";
		// List<String> predicateList = new ArrayList<String>();
		// int sentenceTokenLength = 0;
		//
		// conll = coNLLSentence.getCoNLLSentence();
		//
		// LinkedHashMap<String, String> linkedPOSMap = new
		// LinkedHashMap<String, String>();
		//
		// ArrayList<ArrayList<String>> listOfLists =
		// coNLLSentence.getDataList();
		// ArrayList<String> posList = listOfLists.get(pos_column_no);
		// ArrayList<String> formList = listOfLists.get(form_column_no);
		//
		// sentenceTokenLength = formList.size();
		//
		// if (sentenceTokenLength > 0) {
		// for (int i = 0; i < formList.size(); i++) {
		// String word=formList.get(i);
		// String
		// }
		//
		// }

	}

	/**
	 * Reads conll Format corpus recursively from the directory
	 * 
	 * @param dataFolder
	 * @return
	 */
	public ArrayList<CoNLLSentence> readCoNLLFormatCorpus(String dataFolder, Format format) {

		ArrayList<CoNLLSentence> data = new ArrayList<CoNLLSentence>();

		data = dfc.readCoNLLFormatCorpus(dataFolder, format);

		return data;
	}

	/**
	 * Add predicate information
	 * 
	 * @param coNLLSentence
	 */
	private void addPredicateInformation(CoNLLSentence coNLLSentence) {

		String conll = "";
		List<String> predicateList = new ArrayList<String>();
		int sentenceTokenLength = 0;

		conll = coNLLSentence.getCoNLLSentence();

		if (Strings.isNullOrEmpty(conll)) {
			// String hit=coNLLSentence.getHITSentence();
			dfc.convertHITtoCONLL2009(coNLLSentence);
			conll = coNLLSentence.getCoNLLSentence();
		}

		// Sentence sent = conllFormatParser.parse(conll);
		// sent.processPredArguments();

		// for (Predicate pred : sent.getPredicates()) {
		//
		// IWord iWord = pred.getPredWord();
		// Word2009 w = (Word2009) iWord;
		// String root = w.getDeprel();
		// String pos = w.getPos();
		//
		//
		// // it is a verb
		// if (pos.contains("V")) {
		// String p=w.getForm();
		// predicateList.add(p);
		// //int t=10;
		//
		// }
		// }

		ArrayList<ArrayList<String>> listOfLists = coNLLSentence.getDataList();
		ArrayList<String> verbList = listOfLists.get(conllformat_verb_column_no - 1);
		ArrayList<String> formList = listOfLists.get(conllformat_first_arg_column_no - 1);

		sentenceTokenLength = formList.size();

		int counter = 0;
		for (String string : verbList) {
			if (!string.equals("_")) {
				String verb = formList.get(counter);
				predicateList.add(verb);
			}
			counter++;
		}

		if (predicateList.size() > 1) {
			coNLLSentence.setHasMultiplePredicates(true);
		}
		coNLLSentence.setPredicateList(predicateList);
		coNLLSentence.setSentenceTokenLength(sentenceTokenLength);
	}

	/**
	 * Add relation argument information
	 * 
	 * @param coNLLSentence
	 */
	private void addRelationInformation(CoNLLSentence coNLLSentence) {

		Map<String, Integer> relationMap = new HashMap<String, Integer>();
		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

		if (!(coNLLSentence.getDataList().size() > 2)) {
			listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);
			coNLLSentence.setDataList(listOfLists);
		}

		if (listOfLists.size() > 0) {

			for (int i = conllformat_first_arg_column_no; i < listOfLists.size(); i++) {

				ArrayList<String> column = listOfLists.get(i);

				for (int j = 0; j < column.size(); j++) {

					String string = column.get(j).trim();
					String new_s = fixHumanTypos(string);

					if (!Strings.isNullOrEmpty(new_s)) {
						relationMap.put(new_s, 1);
						coNLLSentence.setRelationMap(relationMap);
						if (!globalRelationSet.contains(new_s)) {
							globalRelationSet.add(new_s);
						}
					}

				}
			}
		}
		// int t = 10;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public String fixHumanTypos(String string) {
		String new_s = string;

		if (string.contains("VA")) {
			int t = 10;
		}
		new_s = new_s.replace("(VA", "(v");
		new_s = new_s.replace("(VH", "(v");
		new_s = new_s.replace("(V*", "v*");
		new_s = new_s.replaceAll("[()*v]", "").replace("and", "").trim();
		new_s = new_s.replaceAll("）", "");
		new_s = new_s.replaceAll("（", "");

		if (!Strings.isNullOrEmpty(new_s)) {

			new_s = new_s.toUpperCase();

			new_s = new_s.replace("NRG", "NEG");
			new_s = new_s.replace("ENG", "NEG");

			new_s = new_s.replace("EXY", "EXT");

			new_s = new_s.replace("NOD", "MOD");
			new_s = new_s.replace("AVD", "MOD");
			new_s = new_s.replace("MD", "MOD");
			new_s = new_s.replace("MDO", "MOD");
			new_s = new_s.replace("MODO", "MOD");

			new_s = new_s.replace("DIS+V", "DIS");
			new_s = new_s.replace("DIS+", "DIS");

			new_s = new_s.replace("ADV&", "ADV");
			new_s = new_s.replace("ADJ", "ADV");

			new_s = new_s.replace("TEM", "TMP");
			new_s = new_s.replace("TEP", "TMP");

			new_s = new_s.replace("C-A1", "A1");
			new_s = new_s.replace("C-A0", "A0");
			new_s = new_s.replace("C-AT", "AT");
			new_s = new_s.replace("C-V", "");

			new_s = new_s.replace("LOV", "LOC");

			// new_s = new_s.replace("A", "A0");

			new_s = new_s.replace("C-AFT", "AFT");
		}

		return new_s;
	}

	/**
	 * Create json output for sentence data information
	 * 
	 * @param data
	 * @return
	 */
	public String createSentenceDataJson(ArrayList<CoNLLSentence> data) {

		JsonArrayBuilder sentenceInfoBuilder = Json.createArrayBuilder();
		long sentenceCounter = 0;
		for (CoNLLSentence coNLLSentence : data) {

			sentenceCounter++;
			String sentence = coNLLSentence.getSentence();
			// String hit = coNLLSentence.getHITSentence();

			augmentRelationInfo(coNLLSentence);

			JsonObjectBuilder textBuilder = Json.createObjectBuilder();

			// add sentence text
			textBuilder.add("sentence", sentence);

			// add sentence id
			textBuilder.add("id", sentenceCounter);

			String file = coNLLSentence.getSource();
			String src = coNLLSentence.getSource();
			String[] tokens = src.split("/");
			if (tokens != null && tokens.length > 0) {
				String src1 = tokens[tokens.length - 2];
				src = src1;
			}

			// src
			textBuilder.add("src", src);
			// add actual filename

			File f = new File(file);
			textBuilder.add("file", f.getName());

			// add relation map
			Map<String, Integer> map = coNLLSentence.getRelationMap();
			for (String keys : map.keySet()) {
				Integer val = map.get(keys);
				textBuilder.add(keys, val);
			}

			// add predicate information
			JsonArrayBuilder verbInfoBuilder = Json.createArrayBuilder();
			// JsonObjectBuilder verbInfoBuilder = Json.createObjectBuilder();

			List<String> predList = coNLLSentence.getPredicateList();
			boolean hasMultiplePreds = coNLLSentence.isHasMultiplePredicates();
			int multiplePredsVal = hasMultiplePreds ? 1 : 0;
			int tokensLength = coNLLSentence.getSentenceTokenLength();

			for (String verb : predList) {
				verbInfoBuilder.add(verb);
			}
			textBuilder.add("predicates", verbInfoBuilder);
			textBuilder.add("multiplePredicates", multiplePredsVal);
			textBuilder.add("tokenLength", tokensLength);

			sentenceInfoBuilder.add(textBuilder);
		}

		JsonObjectBuilder textBuilder = Json.createObjectBuilder();
		textBuilder.add("sentences", sentenceInfoBuilder);
		JsonObject testNodeJsonObject = textBuilder.build();

		Map<String, Boolean> config = new HashMap<>();
		config.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory jwf = Json.createWriterFactory(config);
		StringWriter sw = new StringWriter();
		try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
			jsonWriter.writeObject(testNodeJsonObject);
		}

		// System.out.println(sw.toString());

		return sw.toString();
	}

	private void augmentRelationInfo(CoNLLSentence coNLLSentence) {
		Map<String, Integer> map = coNLLSentence.getRelationMap();
		for (String string : globalRelationSet) {
			if (!map.containsKey(string)) {
				map.put(string, 0);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		StatisticsGenerator smg = new StatisticsGenerator();
		smg.test();

	}

	/**
	 * 
	 * @param dataFolder
	 * @param allRelationFile
	 * @param format
	 * @throws IOException 
	 */
	public void generateAllRelations(String dataFolder, String allRelationFile, Format format) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<CoNLLSentence> data = readCoNLLFormatCorpus(dataFolder, format);

		for (CoNLLSentence coNLLSentence : data) {

			addRelationInformation(coNLLSentence);

			// System.out.println(coNLLSentence.getSentence());
		}

		StringBuilder sb = new StringBuilder();
		for (String rel : globalRelationSet) {
			sb.append(rel + "\n");
		}
		
		FileUtils.write(new File(allRelationFile), sb.toString());
		
		System.out.println("Wrote to file " + allRelationFile);

	}

	public void readRelationsSet(String allRelationFile) throws IOException {
		// TODO Auto-generated method stub
		
		File file=new File(allRelationFile);
		List<String> contents = FileUtils.readLines(file, "UTF-8");
		
		globalRelationSet=new HashSet<String>();
		for (String string : contents) {
			//
			if(!Strings.isNullOrEmpty(string)){
				globalRelationSet.add(string);
			}
		}
		
	}

}
