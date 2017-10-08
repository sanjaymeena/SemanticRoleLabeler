/**
 * 
 */
package com.emotibot.srl.test.clustering;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.server.SRLParserHelper;
import com.emotibot.srl.test.frames.OpenWordnet;
import com.emotibot.srl.test.frames.SemanticFrameOptions;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.Limit;

import edu.stanford.nlp.io.EncodingPrintWriter.out;
import javafx.scene.input.DataFormat;
import se.lth.cs.srl.SemanticLabelerPipeLine;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

/**
 * @author Sanjay
 *
 */
public class DataAnalysis {

	private final static Logger log = LoggerFactory.getLogger(DataAnalysis.class);
	static SemanticLabelerPipeLine slp_pipeline;
	EmotibotTokenizer emotibotTokenzier;

	SRLOptions options = new SRLOptions();
	DataFormatConverter dfc = new DataFormatConverter();
	private SRLParserHelper srlhelper = new SRLParserHelper();

	static String vip_srlDataFile = "data2/vip/srl_data_vip.txt";
	private static final String[] VIP_INTENT_FILE_HEADER_MAPPING = { "sid", "sentence", "desc", "label", "val",
			"intent", "tokens" };

	private static final String[] EMOTIOCOUNT_FILE_HEADER_MAPPING = { "sentence", "freq", "other" };

	static CSVFormat vipCSVFileFormat = CSVFormat.DEFAULT.withHeader(VIP_INTENT_FILE_HEADER_MAPPING);
	static CSVFormat emoticountCSVFileFormat = CSVFormat.DEFAULT.withHeader(EMOTIOCOUNT_FILE_HEADER_MAPPING);

	
	
	String emotiCountOut = "data2/emoticount/out";
	
	
	long globalCounter;

	public DataAnalysis() {
		init();
	}

	void init() {
		options = new SRLOptions();
		options.model = 2;
		options.produceHITFormat = false;
		options.doPruner = false;
		options.doTree = false;

		// whether to use POS from NLP Package
		options.usePOSFromNLP = true;
		// whether to use dependency parser from package
		options.useDEPFromNLP = false;

		emotibotTokenzier = new EmotibotTokenizer();

		globalCounter = 0;

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DataAnalysis dataAnalysis = new DataAnalysis();
		// vipDataProcessing.test();
		// String fileName = "data2/vip/296752vip_Intent_tokens.csv";
		// vipDataProcessing.parseSRLandStore(fileName);
		// dataAnalysis.organizeSentencesByPredicate(vip_srlDataFile);

		String emotiCountDir = "data2/emoticount";
		dataAnalysis.analyzeEmotiCountData(emotiCountDir, true);
	}

	// CSV file header

	Multimap<String, Sentence> sentenceCluster = LinkedHashMultimap.create();
	Multimap<String, String> synsetCluster = LinkedHashMultimap.create();
	Multimap<String, String> verbTosynsetMap = LinkedHashMultimap.create();

	
	/**
	 * 
	 * @param file
	 * @param directory
	 * @throws Exception
	 */
	private void analyzeEmotiCountData(String file, boolean directory) throws Exception {
		Map<String,List<String>> sentenceData =parseCSVFile(file, directory);
		
		//store sentence data 
		parseSRLandStore(sentenceData,emotiCountOut);
	}

	/**
	 * 
	 * @param file
	 * @param directory
	 * @throws Exception
	 */
	private Map<String,List<String>> parseCSVFile(String file, boolean directory) throws Exception {
		Collection<File> files = new ArrayList<>();

		File rootDir = new File(file);
		if (directory) {
			final String[] SUFFIX = { "csv" };
			files = FileUtils.listFiles(rootDir, SUFFIX, true);
		} else {
			files.add(rootDir);
		}
		
		Map<String,List<String>> sentenceData=new LinkedHashMap<String, List<String>>();

		Set<String> dataSet = new HashSet<>();

		for (File file2 : files) {
			FileReader fileReader = null;

			CSVParser csvFileParser = null;
																											
			// initialize FileReader object
			fileReader = new FileReader(file2);
			// initialize CSVParser object
			csvFileParser = new CSVParser(fileReader, emoticountCSVFileFormat);
			// Get a list of CSV file records
			List<CSVRecord> csvRecords = csvFileParser.getRecords();

			// Read the CSV file records starting from the second record to skip
			// the
			// header
			List<String> strings = new ArrayList<>();
			for (int i = 1; i < csvRecords.size(); i++) {
				CSVRecord record = csvRecords.get(i);

				String input = record.get(0);
				input=input.trim();
				if(!Strings.isNullOrEmpty(input)){
					strings.add(input);
				}
				

			}
			sentenceData.put(file2.getName(), strings);

		}

		
		
		for (String f : sentenceData.keySet()) {
			List<String> vals = sentenceData.get(f);
			
			System.out.println(f +  " total sentence : "+ vals.size());
			for (String string : vals) {
				System.out.println(string);
			}
		}
		
		
		System.out.println("data size " + dataSet.size());
		return sentenceData;

	}
	
	
	/**
	 * Parse SRL results for input  sentences and store them
	 * @param sentences
	 * @param outFile
	 * @throws Exception
	 */
	private void parseSRLandStore(Map<String,List<String>> sentenceData, String rootDir) throws Exception {
		
		StringBuilder sb = new StringBuilder();

		int counter = 0;
		
		
		File root = new File(rootDir);
		FileUtils.cleanDirectory(root);
		
		
		for (String file : sentenceData.keySet()) {
			
			List<String> sentences = sentenceData.get(file);
			for (String input : sentences) {

				if (!Strings.isNullOrEmpty(input)) {

					SRLJsonDataStructure srlJsonDS = getSRL(input);

					String conll = srlJsonDS.getConllSentence();
					sb.append(conll);
					sb.append("\n");
					sb.append("\n");
					// System.out.println(conll+"\n");
					counter++;
					System.out.println(counter + "  " + input);
				}

			}
			
			file=file.replace(".csv", ".txt");
			String outFile=rootDir+File.separator+file;
			FileUtils.writeStringToFile(new File(outFile), sb.toString(), "UTF-8");
			System.out.println("wrote to " + outFile);
		}
		

		


	}
	
	

	/**
	 * Parse SRL results for input  sentences and store them
	 * @param sentences
	 * @param outFile
	 * @throws Exception
	 */
	private void parseSRLandStore(List<String> sentences, String outFile) throws Exception {

		StringBuilder sb = new StringBuilder();

		int counter = 0;
		for (String input : sentences) {

			if (!Strings.isNullOrEmpty(input)) {

				SRLJsonDataStructure srlJsonDS = getSRL(input);

				String conll = srlJsonDS.getConllSentence();
				sb.append(conll);
				sb.append("\n");
				sb.append("\n");
				// System.out.println(conll+"\n");
				counter++;
				System.out.println(counter + "  " + input);
			}

		}

		FileUtils.writeStringToFile(new File(outFile), sb.toString(), "UTF-8");

		System.out.println("wrote to " + outFile);

	}

	/**
	 * Parse SRL results for input file sentences and store them
	 * 
	 * @throws Exception
	 */
	private void parseSRLandStore(String fileName) throws Exception {

		//

		// TODO Auto-generated method stub
		FileReader fileReader = null;

		CSVParser csvFileParser = null;

		// initialize FileReader object
		fileReader = new FileReader(fileName);
		// initialize CSVParser object
		csvFileParser = new CSVParser(fileReader, vipCSVFileFormat);
		// Get a list of CSV file records
		List<CSVRecord> csvRecords = csvFileParser.getRecords();

		// Read the CSV file records starting from the second record to skip the
		// header
		List<String> strings = new ArrayList<>();
		long counter = 0;
		long limit = 200000;
		for (int i = 1; i < csvRecords.size(); i++) {
			CSVRecord record = csvRecords.get(i);
			counter++;
			String input = record.get(1);
			strings.add(input);

			if (counter > limit)
				break;

		}
		StringBuilder sb = new StringBuilder();
		counter = 0;
		for (String input : strings) {

			if (!Strings.isNullOrEmpty(input)) {

				SRLJsonDataStructure srlJsonDS = getSRL(input);

				String conll = srlJsonDS.getConllSentence();
				sb.append(conll);
				sb.append("\n");
				sb.append("\n");
				// System.out.println(conll+"\n");
				counter++;
				System.out.println(counter + "  " + input);
			}

		}

		FileUtils.writeStringToFile(new File(vip_srlDataFile), sb.toString(), "UTF-8");

	}

	/**
	 * Organize the sentences by predicate
	 * 
	 * @throws Exception
	 */
	private void organizeSentencesByPredicate(String dataFile) throws Exception {

		List<Sentence> sentences = new ArrayList<>();
		ArrayList<CoNLLSentence> conllSenteces = dfc.readCoNLLFormatCorpus(new File(dataFile), Format.CONLL, true);
		System.out.println("Size of sentences in data : " + conllSenteces.size());
		int limit = 200000;

		List<CoNLLSentence> samples = conllSenteces.subList(0, limit);

		/**
		 * Create the sentecne datastructure containing synset and verb
		 * information
		 */
		for (CoNLLSentence conllSentence : samples) {

			conllSentence.setProcessedSentence(conllSentence.getSentence());
			SRLJsonDataStructure srlJsonDS = srlhelper.createSRLJsonDataStructure(conllSentence, options);

			Sentence sent = addWordnetSynInformation(srlJsonDS);
			sentences.add(sent);

		}

		/**
		 * Create the sentence cluster
		 */
		createSentenceCluster(sentences);

	}

	/**
	 * This is test code for expirements. Please keep it for a while .
	 * 
	 * @throws Exception
	 */
	private void test() throws Exception {

		List<Sentence> sentences = new ArrayList<>();

		//
		String fileName = "data2/vip/296752vip_Intent_tokens.csv";
		String outfileName = "data2/vip/out.csv";

		// TODO Auto-generated method stub
		FileReader fileReader = null;

		CSVParser csvFileParser = null;

		// initialize FileReader object
		fileReader = new FileReader(fileName);

		// initialize CSVParser object
		csvFileParser = new CSVParser(fileReader, vipCSVFileFormat);

		// Get a list of CSV file records
		List<CSVRecord> csvRecords = csvFileParser.getRecords();

		// Read the CSV file records starting from the second record to skip the
		// header
		List<String> strings = new ArrayList<>();
		long counter = 0;
		long limit = 2000;
		for (int i = 1; i < csvRecords.size(); i++) {
			CSVRecord record = csvRecords.get(i);
			counter++;
			String input = record.get(1);
			strings.add(input);

			if (counter > limit)
				break;

		}

		for (String input : strings) {
			SRLJsonDataStructure srlJsonDS = getSRL(input);
			Sentence sent = addWordnetSynInformation(srlJsonDS);
			sentences.add(sent);

		}

		createSentenceCluster(sentences);

		String NEW_LINE_SEPARATOR = "\n";

		// CSV file header
		Object[] FILE_HEADER = { "sentence", "predicate" };
		FileWriter fileWriter = null;

		CSVPrinter csvFilePrinter = null;

		// Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

		StringBuilder builder = new StringBuilder();

		System.out.println(" Total verbs: " + sentenceCluster.keySet().size());
		for (String predicate : sentenceCluster.keySet()) {
			StringBuilder sb = new StringBuilder();
			Collection<Sentence> vals = sentenceCluster.get(predicate);
			sb.append("pred : " + predicate + " total sentences : " + vals.size() + "\n");
			sb.append("\t");
			for (Sentence sentence : vals) {
				sb.append(sentence + ", ");

				builder.append(sentence + "\t" + predicate + "\n");
			}
			System.out.println(sb.toString());

		}

		try {

			// initialize FileWriter object
			fileWriter = new FileWriter(outfileName);

			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			// Create CSV file header
			csvFilePrinter.printRecord(FILE_HEADER);

			for (String predicate : sentenceCluster.keySet()) {
				Collection<Sentence> vals = sentenceCluster.get(predicate);
				for (Sentence sentence : vals) {
					List<String> record = new ArrayList<>();
					record.add(sentence.getSentence());
					record.add(predicate);
					csvFilePrinter.printRecord(record);
				}

			}

			System.out.println("CSV file was created successfully !!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}

		// FileUtils.writeStringToFile(new File(outfileName),
		// builder.toString(), "UTF-8");

		csvFileParser.close();

	}

	/**
	 * Function to create sentence cluster
	 * 
	 * @param sentences
	 */
	private void createSentenceCluster(List<Sentence> sentences) {

		// Lets create sentence clusters based on verb predicate
		for (Sentence sent : sentences) {
			/**
			 * Create sentence cluster
			 */
			updateSentenceCluster2(sent);

		}

		analyzeSynsetCluster();

		for (String key : synsetCluster.keySet()) {
			Collection<String> vals = synsetCluster.get(key);

			StringBuilder sb1 = new StringBuilder();
			for (String string : vals) {
				Collection<Sentence> sents = sentenceCluster.get(string);

				sb1.append(sents.size() + " ");
			}
			System.out.println(" synset: " + key + "  values : " + vals + " sizes of sentences : " + sb1.toString());

		}
		System.out.println(" total clusters: " + synsetCluster.size());
		System.out.println(" total sentences without cluseter for root verb: " + globalCounter);

	}

	private void analyzeSynsetCluster() {
		// TODO Auto-generated method stub

		List<String> synsets = new ArrayList<>();
		for (String key : synsetCluster.keySet()) {
			Collection<String> vals = synsetCluster.get(key);

			if (vals.size() < 2) {
				synsets.add(key);
			}

		}

		Map<String, String> localSynsetCluster = new HashMap<>();
		for (String key : synsets) {
			Collection<String> vals = synsetCluster.get(key);

			StringBuilder sb1 = new StringBuilder();
			for (String verb : vals) {
				Collection<Sentence> sents = sentenceCluster.get(verb);
				Collection<String> verbSynsets = verbTosynsetMap.get(verb);

				for (String synset : verbSynsets) {

					Collection<String> verbsInSynset = synsetCluster.get(synset);

					localSynsetCluster.put(verb, synset);
				}

				sb1.append(sents.size() + " ");
			}
			System.out.println(" synset: " + key + "  values : " + vals + " sizes of sentences : " + sb1.toString());

		}

		System.out.println("Synsets with one element : " + synsets.size());
		System.out.println("reduced Synsets with one element : " + localSynsetCluster.size());
		int temp = 10;
	}

	/**
	 * This function updates various maps that may represent sentence cluster
	 * information
	 * 
	 * @param sent
	 */
	private void updateSentenceCluster2(Sentence sent) {
		// TODO Auto-generated method stub
		String rootVerb = sent.getRootVerb();
		Map<String, List<String>> map = sent.getPredSynsetMap();

		List<String> root_verb_synsets = map.get(rootVerb);
		// add the root verb and sentence to the sentence cluster multimap
		sentenceCluster.put(rootVerb, sent);

		/**
		 * No synsets could be found for the root verb
		 */
		if (root_verb_synsets == null || root_verb_synsets.size() == 0) {
			globalCounter++;
		} else {

			// put all verbs here
			for (String key : root_verb_synsets) {

				synsetCluster.put(key, rootVerb);
				verbTosynsetMap.put(rootVerb, key);

			}

		}

		// if (synsetCluster.containsKey(rootVerb)) {
		// synsetCluster.get(rootVerb).add(sent);
		// }
		//
		// else {
		// synsetCluster.put(rootVerb, sent);
		// }
		//
		// int temp = 10;
	}

	SRLJsonDataStructure getSRL(String input) throws Exception {
		SRLJsonDataStructure srlJsonDS = null;
		if (!Strings.isNullOrEmpty(input)) {
			srlJsonDS = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChinese(input, options);

		}
		return srlJsonDS;

	}

	private Sentence addWordnetSynInformation(SRLJsonDataStructure srlJsonDS) throws Exception {

		Map<String, List<String>> predicateSynsetMap = new HashMap<>();
		Sentence sent = new Sentence();

		String sentence = srlJsonDS.getSentence();
		Map<String, String> tokenMap = srlJsonDS.getTokenMap();
		Map<String, String> posMap = srlJsonDS.getPosMap();
		String root = srlJsonDS.getRoot_index();

		String t = tokenMap.get(root);
		String pos = posMap.get(root);

		String w = t + "/" + pos;

		Map<String, String> predRelationMap = srlJsonDS.getPredRelationMap();

		List<String> preds = new LinkedList<>();
		List<String> predsPOS = new LinkedList<>();

		String rootVerb = "";
		for (String pred : predRelationMap.keySet()) {

			String value = predRelationMap.get(pred);
			String temp = tokenMap.get(pred);

			if (value.equals("HED")) {
				rootVerb = temp;
			}

			String p = posMap.get(pred);
			preds.add(temp + "/" + p);
		}

		System.out.println(sentence);
		for (String predicate : preds) {
			boolean isRoot = false;
			String[] vals = predicate.split("/");
			if (vals[0].equals(rootVerb)) {
				rootVerb = predicate;
				isRoot = true;
			}

			List<List<String>> output = OpenWordnet.instance().getSimilarWords(predicate, true);
			List<String> synsetList = OpenWordnet.instance().getSynsetList(predicate, true);

			predicateSynsetMap.put(predicate, synsetList);

			System.out.println("predicate:  " + predicate + ", isRoot : " + isRoot + " , synsetlist: " + synsetList
					+ "  , similar words " + output);
		}

		sent.setPredSynsetMap(predicateSynsetMap);
		sent.setRootVerb(rootVerb);
		sent.setSentence(sentence);
		sent.setPredicates(preds);

		System.out.println("---------");

		return sent;
	}

}
