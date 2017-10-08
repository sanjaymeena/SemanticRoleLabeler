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

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Sanjay
 *
 */
public class SemanticFrames {

	private volatile static SemanticFrames semanticFramesInstance;

	private Multimap<String, Frame> frame_to_frameDS_mapping;
	private Multimap<String, Frame> expanded_lexicalunits_to_FrameDS_Mapping;
	private Multimap<String, Frame> core_lexicalunits_to_FrameDS_Mapping;

	private Multimap<String, String> pos_to_word_map;

	private String framesDir = "resources/semantic_frame/chinese_frames/new_frames";
	private String framesDir2 = "resources/semantic_frame/chinese_frames/Frame_VIP";
	private boolean enhanceLexicalUnits = true;
	private String delimiter = "\t";

	private static String framesFilterFile = "resources/semantic_frame/chinese_frames/filtered/filtered_frames_ecommerce.txt";
	private static String domainSpecificFramesFile = "resources/semantic_frame/chinese_frames/filtered/domain_specific_frames.txt";
	private static String generalDomainFramesFile = "resources/semantic_frame/chinese_frames/filtered/general_domain_frames.txt";

	private Set<String> domainSpecificFrames = new LinkedHashSet<>();
	private Set<String> generalDomainFrames = new LinkedHashSet<>();
	private Set<String> filtered_frames = new LinkedHashSet<>();
	private List<String> mostCommonFrames = new ArrayList<>();

	/**
	 * @throws IOException
	 * 
	 */
	public SemanticFrames() throws IOException {

		initializeMultiMaps();

		try {
			// read frame files

			List<String> inputDirs = new ArrayList<>();
			inputDirs.add(framesDir);
			inputDirs.add(framesDir2);

			readFrameFiles(inputDirs, enhanceLexicalUnits);

			// read frame filter files. These are the the list of frames which
			// we want to select from the list of all possible fram
			readFrameFilters();

			readDomainSpecificFrames();
			readGeneralDomainFrames();

			mostCommonFrames.add("State_状态");
			mostCommonFrames.add("Coming_to_be_形成");
			mostCommonFrames.add("Equating_等同");
			mostCommonFrames.add("Cause_to_present_使呈现");
			mostCommonFrames.add("Opinion_观点");
			mostCommonFrames.add("Possibility_可能发生的事");
			mostCommonFrames.add("Existence_存现");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void readGeneralDomainFrames() throws IOException {
		// TODO Auto-generated method stub

		File file = new File(generalDomainFramesFile);

		List<String> contents = FileUtils.readLines(file, "UTF-8");

		for (String string : contents) {

			generalDomainFrames.add(string);

		}
	}

	private void readDomainSpecificFrames() throws IOException {
		// TODO Auto-generated method stub

		File file = new File(domainSpecificFramesFile);

		List<String> contents = FileUtils.readLines(file, "UTF-8");

		for (String string : contents) {

			domainSpecificFrames.add(string);

		}
	}

	/**
	 * 
	 * @return
	 */
	public synchronized static SemanticFrames instance() {
		if (semanticFramesInstance == null) {
			try {

				synchronized (OpenWordnet.class) {
					if (semanticFramesInstance == null) {
						System.out.println("Creating new instance for Semantic Frames..");
						semanticFramesInstance = new SemanticFrames();
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return semanticFramesInstance;

	}

	/**
	 * 
	 */
	private void initializeMultiMaps() {
		// TODO Auto-generated method stub

		frame_to_frameDS_mapping = LinkedHashMultimap.create();
		expanded_lexicalunits_to_FrameDS_Mapping = LinkedHashMultimap.create();
		core_lexicalunits_to_FrameDS_Mapping = LinkedHashMultimap.create();
		pos_to_word_map = LinkedHashMultimap.create();

		filtered_frames = new LinkedHashSet<>();
	}

	/**
	 * This function selects the frames based on the give text file
	 * 
	 * @throws IOException
	 */
	private void readFrameFilters() throws IOException {
		// TODO Auto-generated method stub
		File file = new File(framesFilterFile);

		List<String> contents = FileUtils.readLines(file, "UTF-8");

		for (String string : contents) {

			filtered_frames.add(string);

		}

	}

	/**
	 * Read semantic frame files
	 * 
	 * @param inputDir
	 * @param enhanceLexicalUnits
	 *            whether to use wordnet to enhance coverage of lexical units or
	 *            not
	 * @return
	 * @throws Exception
	 */
	public Multimap<String, Frame> readFrameFiles(List<String> inputDir, boolean enhanceLexicalUnits) throws Exception {

		List<File> allFiles = new ArrayList<File>();

		for (String directory : inputDir) {

			List<File> files = new ArrayList<File>();
			File rootDir = new File(directory);
			final String[] SUFFIX = { "txt" };

			if (rootDir.isDirectory()) {
				Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
				files = new ArrayList<File>(temp);
				allFiles.addAll(files);
			} else {
				allFiles.add(rootDir);

			}
		}

		Set<String> posInFrames = new HashSet<>();
		List<Frame> frames = new ArrayList<>();

		Multimap<String, String> frameElements_to_Frame = LinkedHashMultimap.create();

		// iterate through all the files

		for (File file : allFiles) {

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

						}

					}
				}

				counter++;

			}

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
		// System.out.println("Semantic Frames  :  ");
		System.out.println(String.format("Read %d Semantic Frames", frameKeys.size()));

		// for (String string : frameKeys) {
			// Collection<Frame> f = frame_to_frameDS_mapping.get(string);
			// for (Frame frame : f) {
			// String en_label = frame.getFrame_en();
			// String zh_label = frame.getFrame_zh();
			// System.out.println(en_label + "_" + zh_label);
			// }
			// System.out.println(string);

		// }
		System.out.println("POS in Semantic Frames  :  " + posInFrames.toString());

		// System.out.println("POS ..");
		// for (String string : pos_to_word_map.keySet()) {
		// System.out.println(string + " : " + pos_to_word_map.get(string));
		// }

		return expanded_lexicalunits_to_FrameDS_Mapping;

	}

	/**
	 * Read semantic frame file
	 * 
	 * @param inputDir
	 * @param enhanceLexicalUnits
	 *            whether to use wordnet to enhance coverage of lexical units or
	 *            not
	 * @return
	 * @throws Exception
	 */
	public Multimap<String, Frame> readFrameFiles(String inputDir, boolean enhanceLexicalUnits) throws Exception {

		List<String> inputDirs = new ArrayList<>();
		inputDirs.add(inputDir);

		return readFrameFiles(inputDirs, enhanceLexicalUnits);
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
	 * Get Semantic Frame Map
	 * 
	 * @param input
	 * @param useFrameFilter
	 *            whether to only use the frames defined in filtered frame file
	 * @return
	 */
	public Multimap<String, Frame> getSemanticFrames(String input, SemanticFrameOptions sfOptions) {
		
		boolean useFrameFilter;
		boolean useGeneralDomainFrames=sfOptions.isUseGeneralFrame();
		boolean useDomainSpecificFrames=sfOptions.isUseDomainSpecificFrame();
		
		Set<String> expandedlexicalUnitSet = expanded_lexicalunits_to_FrameDS_Mapping.keySet();
		Set<String> corelexicalUnitSet = core_lexicalunits_to_FrameDS_Mapping.keySet();

		// boolean foundInAllSemanticFrames = false;
		// boolean foundInfilteredSemanticFrames = false;
		Multimap<String, Frame> lu_to_found_Frame_Mapping = LinkedHashMultimap.create();
		Map<String, Integer> frame_count_map = new LinkedHashMap<>();

		Collection<Frame> sfList = null;

		// we check if core lexical units contain the input token or not
		if (corelexicalUnitSet.contains(input)) {
			sfList = core_lexicalunits_to_FrameDS_Mapping.get(input);
		}

		// if core lexical units do not contain input, we use the expanded
		// lexical units list
		else if (expandedlexicalUnitSet.contains(input)) {
			// arr.add(str);
			sfList = expanded_lexicalunits_to_FrameDS_Mapping.get(input);
		}

		if (sfList != null && sfList.size() > 0) {
			// foundInAllSemanticFrames = true;

			for (Frame frame2 : sfList) {

				String key = frame2.getKey();

				// here we check if the given frame should be selected
				// or not
				boolean addFrame = false;
				
				
				if((useDomainSpecificFrames && domainSpecificFrames.contains(key))||(useGeneralDomainFrames && generalDomainFrames.contains(key))){
					// foundInfilteredSemanticFrames = true;
					addFrame = true;
				} 
//				else  if(useGeneralDomainFrames && generalDomainFrames.contains(key)){
//					addFrame = true;
//				}

				if (addFrame) {
					lu_to_found_Frame_Mapping.put(input, frame2);

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

		return lu_to_found_Frame_Mapping;
	}

	/**
	 * Get semantic Frame map
	 * 
	 * @param tokens
	 * @param useFrameFilter
	 *            whether to only use the frames defined in filtered frame file
	 * @return
	 */
	public Multimap<String, Frame> getSemanticFrames(ArrayList<String> tokens, SemanticFrameOptions options) {

		Multimap<String, Frame> lu_to_found_Frame_Mapping = LinkedHashMultimap.create();
		for (String string : tokens) {

			Multimap<String, Frame> tempMap = getSemanticFrames(string, options);
			lu_to_found_Frame_Mapping.putAll(tempMap);
		}
		return lu_to_found_Frame_Mapping;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SemanticFrameOptions options=new SemanticFrameOptions();
		Multimap<String, Frame> out = SemanticFrames.instance().getSemanticFrames("退货/v", options);
		System.out.println(out);

	}

}
