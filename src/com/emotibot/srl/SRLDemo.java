package com.emotibot.srl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emotibot.srl.datastructures.Relation;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.pruner.TestParseTrees;
import com.emotibot.srl.test.frames.Frame;
import com.emotibot.srl.test.frames.OpenWordnet;
import com.emotibot.srl.test.frames.SemanticFrameOptions;
import com.emotibot.srl.test.frames.SemanticFrames;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import se.lth.cs.srl.SemanticLabelerPipeLine;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class SRLDemo {
	private final static Logger log = LoggerFactory.getLogger(SRLDemo.class);
	static SemanticLabelerPipeLine slp_pipeline;
	EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();

	private String readMultilineText() {
		String buf;
		String doc = null;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			System.err.println("\nInput Text:");

			while (true) {

				doc = "";
				buf = "";

				buf = br.readLine();
				if (buf == null) {
					break;
				}
				doc += buf;

				while (br.ready()) {
					buf = br.readLine();
					if (buf == null) {
						break;
					}
					if (buf.matches("^.*\\S.*$")) {
						doc += buf + " ";
					} else {
						doc += "\n";
					}
				}
				if (doc.length() == 0) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	@SuppressWarnings("resource")
	private String readLine() {
		Scanner scanner = null;
		String doc = "";

		scanner = new Scanner(System.in);
		System.err.println("\nInput Text:");
		doc = scanner.nextLine();

		return doc;
	}

	private void println(Object obj) {
		// TODO Auto-generated method stub
		System.out.println(obj);
	}

	private void printMenu() {

		int selection = 0;

		StringBuilder sb = new StringBuilder();
		sb.append(
				"======================================================================================================== \n");
		sb.append(
				"|                                  Chinese SRL                            							|\n");
		sb.append(
				"======================================================================================================== \n");

		sb.append(
				"                                   1. Run SRL (Table format)  Model 1                                          \n");
		sb.append(
				"                                   2. Run SRL (Json format)   Model 2                                          \n");
		sb.append(
				"                                   3. Run SRL (Json format) with VIP tokenizer Model 2                                          \n");
		sb.append(
				"                                   4. Run SRL (Json format) for SRLVIZ Model 2 (default tokenizer)                               \n");
		sb.append(
				"                                   5. Run SRL (Json format) Using Hybrid Preprocessor                               \n");
		sb.append(
				"                                   6. SRL integrated with Semantic Frames and Wordnet                               \n");
		sb.append(
				"                                   7. Test Semantic Frames API                          	     \n");
		sb.append("                                 8. quit                                                \n");

		println(sb);
		println("Select your choice..");

		String output = "";
		while (true) {

			Scanner scanner = null;
			String input = "";
			String result = "";
			try {
				scanner = new Scanner(System.in);
				while (!scanner.hasNextInt()) {
					println("Please select from the given choices");
					scanner.nextLine();
				}
				selection = scanner.nextInt();
				StringBuffer display = null;
				SRLOptions options = new SRLOptions();
				switch (selection) {

				case 1:
					input = readLine();

					options.model = 1;
					options.produceHITFormat = true;

					// please make sure to set this to false if want to load the
					// full SRL pipeline and not use NLP package
					options.usePOSFromNLP = true;
					options.useDEPFromNLP = false;

					System.out
							.println("The Json can be produce by manual case. HIT and CONLL data are created by Model");
					;

					if (!Strings.isNullOrEmpty(input)) {

						// result =
						// SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChineseJSON(input,
						// options);

						// Differance between getChineseInstance(options) and
						// getChineseInstance(options) is that latter will only
						// load SRL models and rest information is used from SRL
						// models kept in NLP package
						result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChineseJSON(input,
								options);

						System.out.println(result);

						// Differance between getChineseInstance(options) and
						// getChineseInstance(options) is that latter will only
						// load SRL models and rest information is used from SRL
						// models kept in NLP package
						result = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLForChinese(input, options).toString();
						result = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLForChinese(input, options).toString();

						System.out.println(result);

					} else
						System.out.println("empty input");

					break;

				case 2:
					input = readLine();

					options.model = 2;
					options.produceHITFormat = true;

					// whether to use POS from NLP Package
					options.usePOSFromNLP = true;
					// whether to use dependency parser from package
					options.useDEPFromNLP = false;

					System.out
							.println("The Json can be produce by manual case. HIT and CONLL data are created by Model");
					;

					if (!Strings.isNullOrEmpty(input)) {

						result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChineseJSON(input,
								options);

						System.out.println(result);

						result = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLForChinese(input, options).toString();

						System.out.println(result);

					} else
						System.out.println("empty input");

					break;

				case 3:
					input = readLine();
					
					options.model = 2;
					options.produceHITFormat = true;
					options.case_type = "vip";

					// whether to use POS from NLP Package
					options.usePOSFromNLP = true;
					options.useDEPFromNLP = false;

					System.out
							.println("The Json can be produce by manual case. HIT and CONLL data are created by Model");
					;

					if (!Strings.isNullOrEmpty(input)) {

						result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChineseJSON(input,
								options);

						System.out.println(result);

						result = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLForChinese(input, options).toString();

						System.out.println(result);

					} else
						System.out.println("empty input");

					break;

				case 4:
					input = readLine();

					options.model = 2;
					options.produceHITFormat = true;
					options.manualMode = true;

					// whether to use POS from NLP Package
					options.usePOSFromNLP = true;
					options.useDEPFromNLP = false;

					if (!Strings.isNullOrEmpty(input)) {

						result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChineseJSON(input,
								options);

						System.out.println(result);

						result = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLForChineseForSRLVIZ(input, options).toString();

						System.out.println(result);

					} else
						System.out.println("empty input");

					break;

				case 5:
					input = readLine();
					
					options.model = 2;
					options.produceHITFormat = true;
					options.manualMode = true;

					// whether to use POS from NLP Package
					// options.usePOSFromNLP = true;
					// options.useDEPFromNLP = false;

					if (!Strings.isNullOrEmpty(input)) {

						String[] tokens = new EmotibotTokenizer().tokenize(input);

						result = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLUsingHybridPreprocessor(tokens, options).toString();

						System.out.println(result);

					} else
						System.out.println("empty input");

					break;

				case 6:
				{
					input = readLine();

					options.model = 2;
					options.produceHITFormat = true;
					options.case_type = "vip";

					// whether to use POS from NLP Package
					options.usePOSFromNLP = true;
					options.useDEPFromNLP = false;

					SemanticFrameOptions sfOptions = new SemanticFrameOptions();
					sfOptions.useDomainSpecificFrame = true;
					sfOptions.useGeneralFrame = true;
					
					if (!Strings.isNullOrEmpty(input)) {

						SRLJsonDataStructure srlJsonDS = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLForChinese(input, options);


						testFrame(srlJsonDS, sfOptions);

					} else
						System.out.println("empty input");
					break;
				}
					
				case 7:
				{
					input = readLine();
					
					options.model = 2;
					options.produceHITFormat = true;
					options.case_type = "vip";

					// whether to use POS from NLP Package
					options.usePOSFromNLP = true;
					options.useDEPFromNLP = false;
					
					SemanticFrameOptions sfOptions = new SemanticFrameOptions();
					sfOptions.useDomainSpecificFrame = true;
					sfOptions.useGeneralFrame = false;

					if (!Strings.isNullOrEmpty(input)) {
						
						StringBuilder textBuilder = new StringBuilder();
							
						SRLJsonDataStructure srlJsonDS = SemanticLabelerPipeLine.getChineseInstance(options)
								.performSRLForChinese(input, options);
						
						sfOptions.useDomainSpecificFrame = true;
						sfOptions.useGeneralFrame = false;
						
						String root_idx = srlJsonDS.getRoot_index();
						Map<String, String> tokenMap = srlJsonDS.getTokenMap();
						Map<String, String> posMap = srlJsonDS.getPosMap();
						ArrayList<String> unitList = new ArrayList<String>();
						for (int i = 0 ; i < tokenMap.size() ; i++) {
							String idxStr = String.valueOf(i+1);
							String unit = tokenMap.get(idxStr) + "/" + posMap.get(idxStr);
							unitList.add(unit);
						}
						Multimap<String, Frame> frameMap = SemanticFrames.instance().getSemanticFrames(unitList, sfOptions);
						
						if (!Strings.isNullOrEmpty(root_idx)) {
							String root_unit = unitList.get(Integer.parseInt(root_idx) - 1);
							if (frameMap.get(root_unit) != null) {
								textBuilder.append("root_frames: " + frameMap.get(root_unit).toString() + "\n");
							} else {
								textBuilder.append("root_frames: " + "N/A" + "\n");
							}
						} else {
							textBuilder.append("root_frames: " + "N/A" + "\n");
						}
						
						if (frameMap.size() > 0) {
							textBuilder.append("all_frames: " + frameMap.toString() + "\n");
						} else {
							textBuilder.append("all_frames: " + "N/A" + "\n");
						}
						
						System.out.println(textBuilder.toString());

					} else
						System.out.println("empty input");
					break;
				}
					
					
				case 8:
					println("Bye");
					System.exit(0);
					break;

				default:
					println("Please select from the given choices");

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void testFrame(SRLJsonDataStructure srlJsonDS, SemanticFrameOptions sfOptions) {

		boolean useFrameFilter = true;

		String sentence = srlJsonDS.getSentence();
		Map<String, String> tokenMap = srlJsonDS.getTokenMap();
		Map<String, String> posMap = srlJsonDS.getPosMap();
		String root = srlJsonDS.getRoot_index();

		String t = tokenMap.get(root);
		String pos = posMap.get(root);

		String w = t + "/" + pos;

		Map<String, String> predRelationMap = srlJsonDS.getPredRelationMap();
		List<String> preds = new ArrayList<>();
		List<String> predsPOS = new ArrayList<>();
		for (String pred : predRelationMap.keySet()) {
			String temp = tokenMap.get(pred);
			String p = posMap.get(pred);
			preds.add(temp + "/" + p);
		}

		Set<String> predFrameList = new LinkedHashSet<>();
		for (String string : preds) {
			Multimap<String, Frame> frameMap = SemanticFrames.instance().getSemanticFrames(string, sfOptions);
			if (frameMap.size() > 0) {
				Collection<Frame> f = frameMap.get(string);
				for (Frame frame : f) {
					predFrameList.add(frame.getKey());
				}
			}
		}

		ArrayList<String> tokens = emotibotTokenzier.tokenizeandPOS(sentence, "", true);

		Multimap<String, Frame> frameMap1;
		Multimap<String, Frame> frameMap;

		frameMap1 = SemanticFrames.instance().getSemanticFrames(tokens, sfOptions);

		frameMap = SemanticFrames.instance().getSemanticFrames(w, sfOptions);

		boolean usePOS = true;
		List<List<String>> similarWords = OpenWordnet.instance().getSimilarWords(w, usePOS);

		boolean frameFound = false;
		boolean similarWordsFound = false;
		if (frameMap.size() > 0) {
			frameFound = true;
		}
		if (similarWords.size() > 0) {
			similarWordsFound = true;
		}

		List<String> simWords = new ArrayList<>();
		if (similarWordsFound) {
			simWords = similarWords.get(0);
			simWords.remove(w);
		}

		Set<String> framesList = new LinkedHashSet<>();
		if (frameFound) {
			Collection<Frame> f = frameMap.get(w);
			for (Frame frame : f) {
				framesList.add(frame.getKey());
			}
		}

		System.out.println("sentence : " + sentence);
		// System.out.println("tokens : " + tokens);

		System.out.println("root frame : " + framesList);
		System.out.println("root_verb : " + t);
		System.out.println("similar verbs to root verb :" + simWords);
		System.out.println("other probable frames:" + predFrameList);

		StringBuilder sb = new StringBuilder();

		System.out.println("all possible frames:" + frameMap1);
		Set<String> set = new LinkedHashSet<>();
		Multimap<String, Relation> srl_multimap = srlJsonDS.getSrl_multimap();
		for (String key : srl_multimap.keys()) {

			Collection<Relation> relations = srl_multimap.get(key);
			for (Relation relation : relations) {
				String rel = relation.getSrl_relation();
				String arg1 = relation.getArg1();
				String arg2 = relation.getArg2();

				String d = rel + " (" + arg1 + " , " + arg2 + ")" + "\n";
				set.add(d);

			}
		}
		for (String string : set) {
			sb.append(string);

		}
		System.out.println("-----------");
		System.out.println("SRL: ");
		System.out.println(sb);

		int temp = 10;
	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("resources/emotibot-srl/config/log4j.properties");
		// 江苏养猪场“地下藏毒万吨”将中国的土壤污染问题再次推上风头浪尖，最佳环境报道奖的“最佳影响力奖”李显峰报道。

		SRLDemo sRLDemo = new SRLDemo();

		String doc2 = "我在台北工作，每天下班都去小巨蛋。";
		sRLDemo.printMenu();

	}
}
