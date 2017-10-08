/**
 * 
 */
package com.emotibot.srl.test.sense;

import static com.emotibot.srl.format.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.format.DataWriter;
import com.google.common.base.Strings;

/**
 * @author Sanjay
 *
 */
public class TagVerbSenses {
	// please remember to put "/" at the end of the directory
	// String rootDir = "data/verbsense/output/";
	String inputDir = "data/corpus_validation/input";
	String outputDir = "data/verbsense/output";
	// String sentencesDir = "data/verbsense/sentences";
	// String final_hit_File =
	// "data/verbsense/final/srl-emotibot-train_hit.txt";
	// String final_conll_File =
	// "data/verbsense/final/srl-emotibot-train_conll.txt";
	// String final_srl_sentences_file =
	// "data/verbsense/final/srl-sentences.txt";
	// String goodCasesDirHIT = "data/corpus_validation/good_cases/hit";
	// String goodCasesDirCONLL = "data/corpus_validation/good_cases/conll";

	DataFormatConverter dfc;
	DataWriter dw;
	CPBPredicateInformation cpbInformation;
	Map<String, Predicate> predicateInformationMap;
	Map<String, String> predicateOutStrMap;

	// HashMap<String, Integer> map_predicate;
	// HashMap<String, Integer> map_more_than_1_sense;
	// HashMap<String, Integer> map_more_than_5_sense;
	// HashMap<String, Integer> map_not_in_CPB;
	int count_more_than_1_senses;
	int count_more_than_5_senses;
	int count_not_in_CPB;

	public TagVerbSenses() throws IOException {
		dfc = new DataFormatConverter();
		dw = new DataWriter();
		cpbInformation = new CPBPredicateInformation();
		predicateInformationMap = cpbInformation.getPredicateInformationMap();
		predicateOutStrMap = getPredicateOutStrMap();

		// count_more_than_1_senses = 0;
		// count_more_than_5_senses = 0;
		// count_not_in_CPB = 0;
		// map_predicate = new HashMap<String, Integer>();
		// map_more_than_1_sense = new HashMap<String, Integer>();
		// map_more_than_5_sense = new HashMap<String, Integer>();
		// map_not_in_CPB = new HashMap<String, Integer>();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		TagVerbSenses tvs = new TagVerbSenses();

		tvs.tagVerbSense();
		
	}

	public void tagVerbSense() throws Exception {

		// convert directory recursively
		boolean readDirectoryRecursively = true;

		if (!readDirectoryRecursively) {
			// convertHITtoCONLL2009(datapath);
		}

		else {

			// File sentencesDirectory;
			File outputDirectory = new File(outputDir);

			// Lets empty the output directory first.
			if (outputDirectory.exists()) {
				FileUtils.cleanDirectory(outputDirectory);
			} else {
				outputDirectory.mkdirs();
			}

			// sentencesDirectory = new File(sentencesDir);
			// if (sentencesDirectory.exists()) {
			// FileUtils.cleanDirectory(sentencesDirectory);
			// } else {
			// sentencesDirectory.mkdirs();
			// }

			File inputDirectory = new File(inputDir);
			final String[] SUFFIX = { "txt" };
			Collection<File> files = FileUtils.listFiles(inputDirectory, SUFFIX, true);

			// iterate through all the files
			for (File file : files) {
				String outputPath = file.getPath().replace(inputDir, outputDir);
				ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);
				tagVerbSense(sentenceList);
				dw.writeHITFormatData(sentenceList, outputPath);	
			}

			// System.out.printf("# of unique verbs in data: %d\n",
			// map_predicate.size());
			// int sum = 0;
			// for (int v : map_predicate.values()) {
			// sum += v;
			// }
			// System.out.printf("count of instances: %d\n", sum);
			//
			// System.out.printf("# of unique verbs (>1 senses): %d\n",
			// map_more_than_1_sense.size());
			// System.out.printf("count of instances (>1 senses): %d\n",
			// count_more_than_1_senses);
			//
			// System.out.printf("# of unique verbs (>5 senses): %d\n",
			// map_more_than_5_sense.size());
			// System.out.printf("count of instances (>5 senses): %d\n",
			// count_more_than_5_senses);
			//
			// System.out.printf("# of unique verbs not in CPB: %d\n",
			// map_not_in_CPB.size());
			// System.out.printf("count of instances not in CPB: %d\n",
			// count_not_in_CPB);

		}

	}

	public Map<String, String> getPredicateOutStrMap() throws IOException {

		HashMap<String, String> predicateOutStrMap = new HashMap<String, String>();

		for (Map.Entry<String, Predicate> entry : predicateInformationMap.entrySet()) {
			String predicate = entry.getKey();
			Predicate predicateInfo = entry.getValue();

			StringBuilder sb = new StringBuilder();
			sb.append(predicate);

			// sort senses
			// num of senses should < 100
			Stream<Map.Entry<Integer, Integer>> sortedSenseStream = predicateInfo.getSenseMap().entrySet().stream()
					.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
			sortedSenseStream.forEach(i -> sb.append(String.format(".%02d", i.getKey())));

			// System.out.println(sb.toString());
			predicateOutStrMap.put(predicate, sb.toString());
		}

		return predicateOutStrMap;
	}

	private void tagVerbSense(ArrayList<CoNLLSentence> sentenceList) {

		for (CoNLLSentence coNLLSentence : sentenceList) {
			tagVerbSense(coNLLSentence);
		}
	}

	private void tagVerbSense(CoNLLSentence coNLLSentence) {

		String sent = coNLLSentence.getHITSentence();
		boolean isValid = dfc.validateHITFormatData(coNLLSentence);

		String universal_empty = "_";

		if (isValid) {

			String[] lines = coNLLSentence.getLines();

			try {

				if (lines.length > 1) {

					// reads the data column wise
					ArrayList<ArrayList<String>> listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);

					if (listOfLists.size() > 0) {
						// int total_columns =
						// TAB_PATTERN.split(lines[0]).length;

						ArrayList<String> verb_column = listOfLists.get(conllformat_verb_column_no);

						for (int i = 0; i < verb_column.size(); i++) {
							String string = verb_column.get(i);

							if (!Strings.isNullOrEmpty(string) && !string.equals(universal_empty)) {
								// This string contains predicate with sense.
								// 要是.01

								String[] values = string.split("\\.");
								if (values != null && values.length == 2) {

									// predicate string
									String predicate = values[0];
									// String sense = values[1];

									String predicateOutStr = predicateOutStrMap.get(predicate);
									if (Strings.isNullOrEmpty(predicateOutStr)) {
										// set to default sense .01
										predicateOutStr = predicate + ".01";
									}
									// System.out.println(predicateOutStr);
									verb_column.set(i, predicateOutStr);
								}

							}

							else {
								// should not be here
							}
						}
						// System.out.println(verb_column);

					}

					// convert list of lists into string (HIT)
					String delimiter = "\t";
					StringBuilder sb = new StringBuilder();
					int num_rows = listOfLists.get(0).size();
					int num_columns = listOfLists.size();

					for (int i = 0; i < num_rows; i++) {
						String c = "";
						for (int j = 0; j < num_columns; j++) {
							c = listOfLists.get(j).get(i);
							if (j == num_columns - 1) {
								sb.append(c);
							} else {
								sb.append(c + delimiter);
							}
						}
						sb.append("\n");
					}

					coNLLSentence.setHITSentence(sb.toString());

				}
			} catch (ArrayIndexOutOfBoundsException error) {
				error.printStackTrace();
			}
		}

	}
}
