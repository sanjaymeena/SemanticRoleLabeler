/**
 * 
 */
package com.emotibot.srl.test.frames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.Relation;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.datastructures.StatusCode;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import se.lth.cs.srl.SemanticLabelerPipeLine;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

/**
 * @author Sanjay
 *
 */
public class TestFrames {

	Multimap<String, String> file_to_Frame_Mapping = LinkedHashMultimap.create();
	Multimap<String, String> frame_to_file_Mapping = LinkedHashMultimap.create();
	Multimap<String, String> lexicalunits_to_Frame_Mapping = LinkedHashMultimap.create();

	Multimap<String, String> frame_to_lexical_units_mapping = LinkedHashMultimap.create();
	Multimap<String, Frame> frame_to_frameDS_mapping = LinkedHashMultimap.create();
	Multimap<String, Frame> expanded_lexicalunits_to_FrameDS_Mapping = LinkedHashMultimap.create();

	Multimap<String, Frame> core_lexicalunits_to_FrameDS_Mapping = LinkedHashMultimap.create();
	Multimap<String, String> pos_to_word_map = LinkedHashMultimap.create();

	static String framesInputDir = "resources/semantic_frame/chinese_frames/raw_frames";
	static String framesFilterFile = "resources/semantic_frame/chinese_frames/filtered/filtered_frames_ecommerce.txt";

	EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();
	Set<String> filtered_frames = new LinkedHashSet<>();

	Set<String> someSampleFrames = new LinkedHashSet<>();

	List<String> mostCommonFrames = new ArrayList<>();

	public TestFrames() {
		someSampleFrames.add("Delivery_送货");
		someSampleFrames.add("Discount_优惠");
		someSampleFrames.add("Freight_运费");
		someSampleFrames.add("Payment_付款");
		someSampleFrames.add("Refund_退款");
		someSampleFrames.add("Replacement_换货");
		someSampleFrames.add("Return_goods_退货");
		someSampleFrames.add("Shopping_购物");
		someSampleFrames.add("Size_尺码");
		someSampleFrames.add("Withdraw_提现");

		mostCommonFrames.add("State_状态");
		mostCommonFrames.add("Coming_to_be_形成");
		mostCommonFrames.add("Equating_等同");
		mostCommonFrames.add("Cause_to_present_使呈现");
		mostCommonFrames.add("Opinion_观点");
		mostCommonFrames.add("Possibility_可能发生的事");
		mostCommonFrames.add("Existence_存现");
		
		
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		TestFrames tf = new TestFrames();

		String newFramesDir = "resources/semantic_frame/chinese_frames/new_frames";

		// tf.convertFrameFilesNames(inputDir, newFramesDir);

		 boolean enhanceLexicalUnits = true;
		 tf.readFrameFiles(framesInputDir, enhanceLexicalUnits);
		// tf.readFrameFilters(framesFilterFile);

		// tf.testLexicalUnits();

		//tf.testLexicalUnits2();
	}

	/**
	 * This function selects the frames based on the give text file
	 * 
	 * @throws IOException
	 */
	private void readFrameFilters(String frameFilterFile) throws IOException {
		// TODO Auto-generated method stub
		File file = new File(frameFilterFile);

		List<String> contents = FileUtils.readLines(file, "UTF-8");

		for (String string : contents) {

			filtered_frames.add(string);

		}

	}

	/**
	 * Convert file name to Activity_paused_state_行为相对中止状态 this kind of format
	 * 
	 * @param inputDir
	 * @param newFramesDir
	 * @throws IOException
	 */
	private void convertFrameFilesNames(String inputDir, String newFramesDir) throws IOException {
		// TODO Auto-generated method stub
		String delimiter = "\t";

		List<File> files = new ArrayList<File>();

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

			List<String> contents = FileUtils.readLines(file, "UTF-8");

			int counter = 1;

			String frame = "";

			Frame sframe = new Frame();
			for (String string : contents) {

				String input = string;

				// information for frame , description
				if (counter == 1) {
					if (!Strings.isNullOrEmpty(input)) {
						String[] values = string.split(delimiter);

						// frame = values[1].trim();
						// frame = frame.trim();

						sframe.setFile(file.toString());
						sframe.setFrame_zh(values[0].trim());
						sframe.setFrame_en(values[1].trim());
						sframe.setDescription(values[2].trim());

					}

					else {
						System.err.println(file);
					}
				}
				counter++;
			}

			String label = sframe.getFrame_en() + "_" + sframe.getFrame_zh() + ".txt";
			String path = newFramesDir + File.separator + label;
			FileUtils.writeLines(new File(path), contents);
			System.out.println("wrote to : " + path);

		}

	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testLexicalUnits2() throws Exception {

		SRLOptions options = new SRLOptions();
		options.model = 2;
		options.produceHITFormat = true;
		// options.case_type = "vip";

		// whether to use POS from NLP Package
		options.usePOSFromNLP = true;
		options.useDEPFromNLP = false;

		String inputDir = "resources/semantic_frame/chinese_frames/qq_sentences/qq_sentences.txt";
		String inputDir2 = "data2/vipqq/data/sentences.txt";

		String outputFile = "data2/vipqq/out/sentences_lexical_units_out.txt";

		File file = new File(inputDir2);

		List<String> contents = FileUtils.readLines(file, "UTF-8");

		for (String string : contents) {

			testLexicalUnits2Helper(contents, options);

		}

	}

	/**
	 * 
	 * @param contents
	 * @param options
	 * @throws Exception
	 */
	private void testLexicalUnits2Helper(List<String> contents, SRLOptions options) throws Exception {
		List<String> no_lexical_unit_sent = new ArrayList<>();
		StringBuilder sampledOutput = new StringBuilder();
		SemanticFrameOptions sfoptions=new SemanticFrameOptions();
		
		

		for (String string : contents) {

			boolean found = false;
			SRLJsonDataStructure result1 = SemanticLabelerPipeLine.getChineseInstance(options)
					.performSRLForChinese(string, options);

			StatusCode status = result1.getStatus_code();
			String sentence = result1.getSentence();
			Map<String, String> tokenMap = result1.getTokenMap();
			Map<String, String> posMap = result1.getPosMap();
			Map<String, String> predRelationMap = result1.getPredRelationMap();

			/**
			 * We work with SRL output at multiple levels.
			 * <li>First we check for SRL output is present or not.</li>
			 * <li>If not, we still check if there exists a predicate relation
			 * map. If even that does not exist , we work with tokens</li>
			 * 
			 */

			// code for root verb
			String root = result1.getRoot_index();
			String rootToken = tokenMap.get(root);
			String rootPOS = posMap.get(root);
			String rootLexicalUnit = rootToken + "/" + rootPOS;

			// get root framemap
			Multimap<String, Frame> rootFrameMap;
			
			rootFrameMap = SemanticFrames.instance().getSemanticFrames(rootLexicalUnit, sfoptions);

			// get root similar words
			boolean usePOS = true;
			List<List<String>> similarWords = OpenWordnet.instance().getSimilarWords(rootLexicalUnit, usePOS);

			boolean frameFound = false;
			boolean similarWordsFound = false;
			if (rootFrameMap.size() > 0) {
				frameFound = true;
			}
			if (similarWords.size() > 0) {
				similarWordsFound = true;
			}

			List<String> simWords = new ArrayList<>();
			if (similarWordsFound) {
				simWords = similarWords.get(0);
				simWords.remove(rootLexicalUnit);
			}

			Set<String> rootFrameList = new LinkedHashSet<>();
			if (frameFound) {
				Collection<Frame> f = rootFrameMap.get(rootLexicalUnit);
				for (Frame frame : f) {
					rootFrameList.add(frame.getKey());
				}
			}

			// code for all predicates
			List<String> preds = new ArrayList<>();
			for (String pred : predRelationMap.keySet()) {
				String temp = tokenMap.get(pred);
				String p = posMap.get(pred);
				preds.add(temp + "/" + p);
			}

			Set<String> predFrameList = new LinkedHashSet<>();
			for (String string1 : preds) {
				Multimap<String, Frame> frameMapm = SemanticFrames.instance().getSemanticFrames(string,
						sfoptions);
				if (frameMapm.size() > 0) {
					Collection<Frame> f = frameMapm.get(string1);
					for (Frame frame : f) {
						predFrameList.add(frame.getKey());
					}
				}
			}

			ArrayList<String> sentenceTokens = emotibotTokenzier.tokenizeandPOS(sentence, "", true);

			Multimap<String, Frame> allTokenFrameMap;

			allTokenFrameMap = SemanticFrames.instance().getSemanticFrames(sentenceTokens, sfoptions);

			List<String> importantTokens = new ArrayList<>();
			for (String string2 : allTokenFrameMap.keySet()) {

//				boolean isVeryCommonFrame = false;
//				Collection<Frame> frames = allTokenFrameMap.get(string2);
//				for (Frame frame : frames) {
//					String key = frame.getKey();
//					if (mostCommonFrames.contains(key) && frames.size() < 3) {
//						isVeryCommonFrame = true;
//					}
//				}
//
//				if (!isVeryCommonFrame) {
//					int ran = string2.indexOf("/");
//					String w = string2.substring(0, ran);
//					importantTokens.add(w);
//				}
				int ran = string2.indexOf("/");
				String w = string2.substring(ran+1, string2.length());
				importantTokens.add(w);
			}

			System.out.println("sentence : " + sentence);
			// System.out.println("tokens : " + tokens);

			System.out.println("root frame : " + rootFrameList);
			System.out.println("root_verb : " + rootToken);
			System.out.println("similar verbs to root verb :" + simWords);
			System.out.println("other probable frames:" + predFrameList);
			System.out.println("token_sequence:" + importantTokens);

			StringBuilder sb = new StringBuilder();

			System.out.println("all possible frames:" + allTokenFrameMap);
			Set<String> set = new LinkedHashSet<>();
			Multimap<String, Relation> srl_multimap = result1.getSrl_multimap();
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
			for (String string2 : set) {
				sb.append(string2);

			}
			System.out.println("-----------");
			System.out.println("SRL: ");
			System.out.println(sb);

			int temp = 10;

		}
	}

	public void testLexicalUnits() throws IOException {
		String inputDir = "resources/semantic_frame/chinese_frames/qq_sentences/qq_sentences.txt";
		String inputDir2 = "data2/vipqq/data/sentences.txt";

		String outputFile = "data2/vipqq/out/sentences_lexical_units_out.txt";

		Map<String, Integer> frame_count_map = new LinkedHashMap<>();
		StringBuilder outputBuilder = new StringBuilder();

		EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();

		Set<String> expandedlexicalUnitSet = expanded_lexicalunits_to_FrameDS_Mapping.keySet();
		Set<String> corelexicalUnitSet = core_lexicalunits_to_FrameDS_Mapping.keySet();

		int maxFramesOutput = 3;
		// boolean foundInAllSemanticFrames = false;
		// boolean foundInfilteredSemanticFrames = false;

		File file = new File(inputDir2);

		int totalSentenes;
		List<String> contents = FileUtils.readLines(file, "UTF-8");
		totalSentenes = contents.size();

		List<String> no_lexical_unit_sent = new ArrayList<>();

		StringBuilder sampledOutput = new StringBuilder();

		for (String string : contents) {

			boolean found = false;
			Multimap<String, String> lu_to_found_Frame_Mapping = LinkedHashMultimap.create();
			// List<String> arr = new ArrayList<>();
			// Set<String> framesSet = new LinkedHashSet<>();

			ArrayList<String> tokens = emotibotTokenzier.tokenizeandPOS(string, "", true);
			for (String str : tokens) {
				Collection<Frame> sfList = null;

				// we check if core lexical units contain the input token or not
				if (corelexicalUnitSet.contains(str)) {
					sfList = core_lexicalunits_to_FrameDS_Mapping.get(str);
					found = true;
				}

				// if core lexical units do not contain input, we use the
				// expanded lexical units list
				else if (expandedlexicalUnitSet.contains(str)) {
					// arr.add(str);
					sfList = expanded_lexicalunits_to_FrameDS_Mapping.get(str);
					found = true;
				}

				if (!found) {
					no_lexical_unit_sent.add(string);
				}

				if (sfList != null && sfList.size() > 0) {
					// foundInAllSemanticFrames = true;

					for (Frame frame2 : sfList) {

						String key = frame2.getKey();

						// here we check if the given frame should be selected
						// or not
						if (filtered_frames.contains(key)) {
							// foundInfilteredSemanticFrames = true;
							lu_to_found_Frame_Mapping.put(str, key);

							String key1 = frame2.getKey();
							if (frame_count_map.containsKey(key1)) {
								int count = frame_count_map.get(key1);
								count += 1;
								frame_count_map.put(key1, count);
							} else {
								frame_count_map.put(key1, 1);
							}
						}

					}

				}

			}

			StringBuilder sb = new StringBuilder();
			sb.append(" lexical_units (LU): " + lu_to_found_Frame_Mapping.keySet() + " :: ");

			for (String string2 : lu_to_found_Frame_Mapping.keySet()) {

				Collection<String> vList = lu_to_found_Frame_Mapping.get(string2);

				List<String> outputFrameList = new ArrayList<>();
				List<String> listWithoutDuplicates = vList.parallelStream().distinct().collect(Collectors.toList());

				// lets look at sample frames
				for (String string3 : listWithoutDuplicates) {
					if (someSampleFrames.contains(string3)) {
						sampledOutput.append(string + " :" + listWithoutDuplicates + "\n");
					}
				}

				for (int i = 0; i < listWithoutDuplicates.size(); i++) {
					// here we are limiting the output of frames defined by
					// maxFramesOutput
					if (i < maxFramesOutput)
						outputFrameList.add(listWithoutDuplicates.get(i));
				}

				sb.append("LU" + string2 + "-> Frames " + outputFrameList + "  ;");
			}

			System.out.println(string + " :" + sb);
			outputBuilder.append(string + " :" + sb + "\n");
		}

		System.out.println("total sentences with no lexical unit: " + no_lexical_unit_sent.size());
		outputBuilder.append("total sentences with no lexical unit: " + no_lexical_unit_sent.size() + "\n");
		outputBuilder.append("total sentences : " + totalSentenes + "\n");

		FileUtils.write(new File(outputFile), outputBuilder.toString(), "UTF-8");

		for (String string2 : frame_count_map.keySet()) {
			int val = frame_count_map.get(string2);
			System.out.println(string2 + " : " + val);
		}

		System.out.println(sampledOutput);
	}

	/**
	 * Run batch SRL on Directory
	 * 
	 * @throws Exception
	 */
	public Multimap<String, Frame> readFrameFiles(String inputDir, boolean enhanceLexicalUnits) throws Exception {

		String delimiter = "\t";

		Set<String> posInFrames = new HashSet<>();
		List<Frame> frames = new ArrayList<>();

		Multimap<String, String> frameElements_to_Frame = LinkedHashMultimap.create();
		List<File> files = new ArrayList<File>();

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
		int fileCounter = 0;
		for (File file : files) {

			List<String> contents = FileUtils.readLines(file, "UTF-8");

			int counter = 1;

			List<String> frame_elements_rows = new ArrayList<>();
			Multimap<String, FrameElement> frameElementMultiMap = LinkedHashMultimap.create();
			List<LexicalUnit> LUnitsList = new ArrayList<>();

			Frame sframe = new Frame();
			for (String string : contents) {

				String input = string;

				// information for frame , description
				if (counter == 1) {
					if (!Strings.isNullOrEmpty(input)) {
						String[] values = string.split(delimiter);

						sframe.setFile(file.toString());
						sframe.setFrame_zh(values[0].trim());
						sframe.setFrame_en(values[1].trim());
						sframe.setDescription(values[2].trim());
						String key = values[1].trim() + "_" + values[0].trim();
						sframe.setKey(key);

					}

					else {
						System.err.println(file);
					}
				}
				// information for frame lexical units
				if (counter == 2) {

					if (!Strings.isNullOrEmpty(input)) {
						String[] values = string.split(delimiter);
						sframe.setLexical_units(Arrays.asList(values));

						for (String string2 : values) {
							String[] vals = string2.split("/");
							LexicalUnit lu = new LexicalUnit(vals[0].trim(), vals[1].trim());
							LUnitsList.add(lu);

							posInFrames.add(vals[1].trim());
							pos_to_word_map.put(vals[1].trim(), vals[0].trim());
						}

					}
				}
				// information for frame elements

				else if (counter > 2) {
					if (!Strings.isNullOrEmpty(input)) {

						String[] values = string.split(delimiter);
						if (values.length > 2) {
							frameElements_to_Frame.put(values[2].trim(), sframe.getFrame_en());
							frame_elements_rows.add(input);

							String label_zh = values[1];
							String label_en = values[2];

							String abbrv = values[3];
							boolean isOptional = Boolean.parseBoolean(values[4]);

							String desc = "";
							if (values.length > 5 && !Strings.isNullOrEmpty(values[5])) {
								desc = values[5];
							}

							String raw = input;

							FrameElement fe = new FrameElement(label_zh, label_en, abbrv, isOptional, desc, raw);
							frameElementMultiMap.put(label_en, fe);
							int t = 10;

						}

					}
				}

				counter++;

			}

			fileCounter++;

			sframe.setFrame_element_rows(frame_elements_rows);
			sframe.setFrameElementMultiMap(frameElementMultiMap);

			frames.add(sframe);
			// add frame to multimap

			String key = sframe.getFrame_en() + "_" + sframe.getFrame_zh();
			frame_to_frameDS_mapping.put(key, sframe);

		}

		// enhance frame lexical units from wordnet

		organizeFramesData(enhanceLexicalUnits);
		// pos tags info

		// print information
		Set<String> frameKeys = frame_to_frameDS_mapping.keySet();
		System.out.println("Semantic Frames  :  ");

		for (String string : frameKeys) {
			// Collection<Frame> f = frame_to_frameDS_mapping.get(string);
			// for (Frame frame : f) {
			// String en_label = frame.getFrame_en();
			// String zh_label = frame.getFrame_zh();
			// System.out.println(en_label + "_" + zh_label);
			// }
			System.out.println(string);

		}
		System.out.println("POS in Semantic Frames  :  " + posInFrames.toString());

		// System.out.println("POS ..");
		// for (String string : pos_to_word_map.keySet()) {
		// System.out.println(string + " : " + pos_to_word_map.get(string));
		// }

		
		//print out frame elements
		System.out.println("Printing Frames  :  " );
		System.out.println(" Frames elements size   :  "  + frameElements_to_Frame.keySet().size());
		for (String string : frameElements_to_Frame.keySet()) {
			Collection<String> frame = frameElements_to_Frame.get(string);
			
			System.out.println(string + " : " + frame);

		}
		
		return expanded_lexicalunits_to_FrameDS_Mapping;
	}

	/**
	 * Enhance lexical units
	 */
	private void organizeFramesData(boolean enhanceLexicalUnits) {

		Set<String> totalLexicalUnits = new HashSet<>();

		Set<String> noMatchsWord = new HashSet<>();
		for (String key : frame_to_frameDS_mapping.keySet()) {

			Collection<Frame> frames = frame_to_frameDS_mapping.get(key);
			for (Frame frame : frames) {

				List<String> lexical_units = frame.getLexical_units();

				for (String string : lexical_units) {
					totalLexicalUnits.add(string);
					expanded_lexicalunits_to_FrameDS_Mapping.put(string, frame);
					core_lexicalunits_to_FrameDS_Mapping.put(string, frame);
					// String[] vals = string.split("/");
					// List<List<String>> sims =
					// OpenWordnet.instance().getSimilarWords(vals[0].trim(),
					// false);

					string = string.trim();
					if (enhanceLexicalUnits) {
						List<List<String>> sims = OpenWordnet.instance().getSimilarWords(string, true);
						if (sims.size() < 1) {

							// noMatchsWord.add(vals[0].trim());
							noMatchsWord.add(string);
						} else {

							Set<String> other_similar_token_set = new HashSet<>();
							for (List<String> list : sims) {

								for (String token : list) {
									token = token.trim();
									if (!token.equals(string)) {
										other_similar_token_set.add(token);
									}
								}
							}

							for (String string2 : other_similar_token_set) {
								expanded_lexicalunits_to_FrameDS_Mapping.put(string2, frame);
							}

						}

					}

				}
			}
		}
		System.out.println("Total semantic Frames; " + frame_to_frameDS_mapping.keySet().size());

		System.out.println("Total lexical units in semantic Frame; " + totalLexicalUnits.size());
		int total_matches = totalLexicalUnits.size() - noMatchsWord.size();
		System.out.println("total lexical units with some match from chinese wordnet: " + total_matches);
		System.out.println("total lexical units with NO match from chinese wordnet: " + noMatchsWord.size());
		System.out.println("total lexical units after word meaning expansion: "
				+ expanded_lexicalunits_to_FrameDS_Mapping.keySet().size());

	}

	/**
	 * Run batch SRL on Directory
	 * 
	 * @throws Exception
	 */
	public void readFrameFiles(String inputDir) throws Exception {

		String delimiter = "\t";

		List<Frame> frames = new ArrayList<>();

		Multimap<String, String> frameElements_to_Frame = LinkedHashMultimap.create();

		// File outputF = new File(outputDir);

		List<File> files = new ArrayList<File>();

		StringBuilder outputBuilder = new StringBuilder();

		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		System.out.println("Reading each file in directory :" + inputDir);
		long totalSentenes = 0;
		// iterate through all the files

		int fileCounter = 1;
		for (File file : files) {
			outputBuilder = new StringBuilder();
			List<String> contents = FileUtils.readLines(file, "UTF-8");
			totalSentenes += contents.size();
			int counter = 1;

			String frame = "";
			String description = "";
			List<String> frame_elements_rows = new ArrayList<>();
			Frame sframe = new Frame();
			for (String string : contents) {

				if (fileCounter > 595) {
					// System.out.println("");
				}
				String input = string;

				// information for frame , description
				if (counter == 1) {
					if (!Strings.isNullOrEmpty(input)) {
						String[] values = string.split(delimiter);

						frame = values[1].trim();
						frame = frame.trim();

						sframe.setFile(file.toString());
						sframe.setFrame_zh(values[0].trim());
						sframe.setFrame_en(values[1].trim());
						sframe.setDescription(values[2].trim());

						sframe.setLexical_units(Arrays.asList(values));

						file_to_Frame_Mapping.put(file.getName(), frame);
						frame_to_file_Mapping.put(frame, file.getName());
						// System.out.println(file.getName() + " : " + frame );
					}

					else {
						System.err.println(file);
					}
				}
				// information for frame lexical units
				if (counter == 2) {

					if (!Strings.isNullOrEmpty(input)) {
						String[] values = string.split(delimiter);

						for (String string2 : values) {
							lexicalunits_to_Frame_Mapping.put(string2, frame);

							frame_to_lexical_units_mapping.put(frame, string2);

						}
					}
				}
				// information for frame elements

				else if (counter > 2) {
					if (!Strings.isNullOrEmpty(input)) {

						String[] values = string.split(delimiter);
						if (values.length > 2) {
							frameElements_to_Frame.put(values[2].trim(), sframe.getFrame_en());
							frame_elements_rows.add(input);
						}

					}
				}

				counter++;

			}
			fileCounter++;
			frames.add(sframe);
			// add frame to multimap
			frame_to_frameDS_mapping.put(sframe.getFrame_en(), sframe);
			// String outputFile = outputDir + File.separator + file.getName();
			// String outputFile = outputDir + File.separator +
			// file.getPath().replace(inputDir, "");
			// File f = new File(outputFile);
			//
			// FileUtils.write(f, outputBuilder.toString(), "UTF-8");

		}

		int temp = 0;
		// for (String string : lexicalunits_to_Frame_Mapping.keySet()) {
		// System.out.println(string + " : " +
		// lexicalunits_to_Frame_Mapping.get(string));
		// }
		//
		//
		//
		//
		for (String string : frame_to_lexical_units_mapping.keySet()) {
			System.out.println(string + " : " + frame_to_lexical_units_mapping.get(string));
		}
		// System.out.println("Total frame files : " +
		// frame_to_lexical_units_mapping.keySet().size());

		// int c = 1;
		// for (String string : frame_to_lexical_units_mapping.keySet()) {
		// System.out.println(c + " :" +string );
		// c++;
		// }
		// for (String string : file_to_Frame_Mapping.keySet()) {
		// System.out.println(string + " : " +
		// file_to_Frame_Mapping.get(string));
		// }
		// for (String string : frame_to_file_Mapping.keySet()) {
		// Collection<String> values = frame_to_file_Mapping.get(string);
		// if (values.size()>1)
		// System.out.println(string + " : " + values);
		// }
		//
		// for (Frame f : frames) {
		// System.out.println(
		// c + " : " + f.getFrame_zh() + "_" + f.getFrame_en() + " description:
		// " + f.getDescription());
		// c++;
		// }
		//
		// for (String string : frameElements_to_Frame.keySet()) {
		// Collection<String> values = frameElements_to_Frame.get(string);
		// if (values.size() >= 1){
		// //System.out.println(string);
		// System.out.println(string + " : " + values);
		// }
		//
		// }

		System.out.println("Total Frames : " + frames.size());
		System.out.println("Total Frames elements : " + frameElements_to_Frame.keySet().size());
		System.out.println("Total lexical units  : " + lexicalunits_to_Frame_Mapping.keySet().size());
	}

}
