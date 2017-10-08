/**
 * 
 */
package com.emotibot.srl.format;

import static com.emotibot.srl.format.Constants.NEWLINE_PATTERN;
import static com.emotibot.srl.format.Constants.TAB_PATTERN;
import static com.emotibot.srl.format.Constants.VERB_SENSE_PATTERN;
import static com.emotibot.srl.format.Constants.conllformat_first_arg_column_no;
import static com.emotibot.srl.format.Constants.conllformat_form_column_no;
import static com.emotibot.srl.format.Constants.LEGAL_SRL_TAGS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.format.Constants.ErrorCases;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import se.lth.cs.srl.SemanticLabelerPipeLine;

/**
 * @author Sanjay
 *
 */
public class DataFormatConverter {

	private ArrayList<CoNLLSentence> conLLSentenceList;

	private Multimap<ErrorCases, CoNLLSentence> errorCasesMap;
	private Multimap<ErrorCases, CoNLLSentence> localErrorCasesMap;

	private Set<String> globalRelationSet;

	private DataWriter dataWriter;

	public DataFormatConverter() {

		errorCasesMap = LinkedListMultimap.create();
		localErrorCasesMap = LinkedListMultimap.create();

		// srlSocketClient = new SRLSocketClient();
		// wordlistChecker = new SRLWordListHelper();

		dataWriter = new DataWriter();

		globalRelationSet = new HashSet<String>();
	}

	/**
	 * Data format enum
	 */
	public enum Format {
		/**
		   * 
		   */
		CONLL(1),
		/**
		   * 
		   */
		HIT(2);
		public int id;

		Format(int id) {
			this.id = id;
		}

		/**
		 * @return id
		 */
		public int getValue() {
			return id;
		}
	}

	/**
	 * 
	 * @param datapath
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CoNLLSentence> createHITFormatData(String datapath, boolean verbose) throws Exception {
		conLLSentenceList = readTextCorpus(datapath);

		SRLOptions options = new SRLOptions();
		options.model = 2;

		System.out.println("Iterating through " + conLLSentenceList.size() + " for format creation..");

		// now iterate through all the sentences to create hit format
		int counter = 0;
		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			// String sentence = coNLLSentence.getSentence();
			String sentence = coNLLSentence.getProcessedSentence();
			// System.out.println(sentence);
			// SRL srl=SemanticLabelerPipeLine.getChineseInstance(options)
			// .performSemanticRoleLabelingForChinese(sentence,true);

			SRLJsonDataStructure result = SemanticLabelerPipeLine.getChineseInstance(options)
					.performSRLForChinese(sentence, options);

			String conllString = result.getConllSentence();

			coNLLSentence.setCoNLLSentence(conllString);

			String[] lines = (NEWLINE_PATTERN.split(conllString));

			coNLLSentence.setLines(lines);

			counter++;
			System.out.println("sentence:" + counter + " :  " + coNLLSentence.getSentence());
			convertCONLLtoHIT(coNLLSentence);

			if (verbose) {
				// System.out.println("sentence:" + counter + " : " +
				// coNLLSentence.getSentence());
				System.out.println(coNLLSentence.getHITSentence());

			}

			// System.out.println(srl.getOutput_srl_table_format());

		}

		return conLLSentenceList;
	}

	/**
	 * 
	 * @param datapath
	 * @param outputpath
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CoNLLSentence> createHITFormatData(String datapath, String outputpath) throws Exception {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		conLLSentenceList = readTextCorpus(datapath);

		SRLOptions options = new SRLOptions();
		options.model = 2;

		long counter = 0;
		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			counter++;
			// String sentence = coNLLSentence.getSentence();
			String sentence = coNLLSentence.getProcessedSentence();

			System.out.println("sentence:" + counter + " :  " + sentence);

			SRLJsonDataStructure result = SemanticLabelerPipeLine.getChineseInstance(options)
					.performSRLForChinese(sentence, options);

			String conllString = result.getConllSentence();

			// String result=sr.semanticParsingResultForChinese(sentence);

			coNLLSentence.setCoNLLSentence(conllString);

			String[] lines = (NEWLINE_PATTERN.split(conllString));

			coNLLSentence.setLines(lines);

			// System.out.println(srl.getOutput_srl_table_format());

		}

		// now iterate through all the sentences to create hit format
		System.out.println("Coverting sentences from CONLL to HIT Format..");
		counter = 0;
		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			counter++;
			// System.out.println("sentence:" + counter + " : " +
			// coNLLSentence.getSentence());

			convertCONLLtoHIT(coNLLSentence);

		}
		System.out.println("Done Coverting sentences from CONLL to HIT Format..");

		// write the output
		dataWriter.writeHITFormatData(conLLSentenceList, outputpath);

		stopWatch.stop();

		long totalTime = stopWatch.getTime();

		double tps = ((double) conLLSentenceList.size()) / (totalTime * 1000);

		System.out.println(
				"Total time taken for " + conLLSentenceList.size() + " sentences" + " : " + stopWatch.toString());
		System.out.println("Total time taken per sentence " + tps);

		return conLLSentenceList;
	}

	/**
	 * 
	 * @param datapath
	 * @param file_name
	 * @param outputpath
	 * @param sentenceCount
	 * @throws Exception
	 */
	public void createHITFormatData(String datapath, String file_name, String outputpath, int sentenceCount)
			throws Exception {

		boolean verbose = false;
		createHITFormatData(datapath, verbose);

		System.out.println("Going to write data");

		dataWriter.writeHITFormatData(conLLSentenceList, file_name, outputpath, sentenceCount);
	}

	/**
	 * Convert HIT format data files to CONLL Format data files. Can do for
	 * files in a directory recursively as well.
	 * 
	 * @param datapath
	 * @param outputpath
	 * @param readDirectoryRecursively
	 *            if recursively, make sure to give proper directory path
	 *            instead of a file path
	 * @throws Exception
	 */
	public void convertHITtoCONLL2009(DataFormatOptions dataFormatOptions) throws Exception {

		String rootDirectory = dataFormatOptions.rootDirectory;
		String datapath = dataFormatOptions.inputDir;
		String goodCasesDir = dataFormatOptions.goodCasesHITDir;
		String outputpath = dataFormatOptions.outputDir;
		String sentencesDir = dataFormatOptions.sentencesDir;

		boolean readDirectoryRecursively = dataFormatOptions.isRecursive;
		boolean onlyGoodCases = dataFormatOptions.onlyGoodCases;

		long totalSentencesCounter = 0;
		long goodCasesCounter = 0;
		long wrongCasesCounter = 0;
		long badCasesCounter = 0;
		long noNLPTaggingCasesCounter = 0;
		long legacyTaggingCasesCounter = 0;

		if (!readDirectoryRecursively) {
			convertHITtoCONLL2009(datapath);
		}

		else {

			File sentencesDirectory;
			File goodCasesDirectory;
			File outputDir;

			// Lets empty the output directory first.
			outputDir = new File(outputpath);
			if (outputDir.exists()) {
				FileUtils.cleanDirectory(outputDir);
			} else {
				outputDir.mkdirs();
			}

			sentencesDirectory = new File(sentencesDir);
			if (sentencesDirectory.exists()) {
				FileUtils.cleanDirectory(sentencesDirectory);
			} else {
				sentencesDirectory.mkdirs();
			}

			goodCasesDirectory = new File(goodCasesDir);
			if (goodCasesDirectory.exists()) {
				FileUtils.cleanDirectory(goodCasesDirectory);
			} else {
				goodCasesDirectory.mkdirs();
			}

			File rootDir = new File(datapath);
			final String[] SUFFIX = { "txt" };
			Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

			// iterate through all the files
			for (File file : files) {

				if (onlyGoodCases) {

				}

				localErrorCasesMap = LinkedListMultimap.create();

				String parentDir = file.getParent();
				String path = parentDir.replace(datapath, "");
				String newOutputFile = outputpath + File.separator + path + File.separator + file.getName();

				// Convert this file to CONLL Format
				convertHITtoCONLL2009(file.getAbsolutePath());

				totalSentencesCounter += conLLSentenceList.size();

				String f = file.getName().split(".txt")[0];

				String bad_case_file = outputpath + File.separator + path + File.separator + f + "-" + "bad_cases.txt";
				String bad_case_text_file = sentencesDir + File.separator + path + File.separator + f + "-"
						+ "bad_cases.txt";

				// this file goes with all the other cases
				String good_case_file1 = outputpath + File.separator + path + File.separator + f + "-"
						+ "good_cases.txt";
				String good_case_text_file = sentencesDir + File.separator + path + File.separator + f + "-"
						+ "good_cases.txt";

				// this file goes to output dir : good_cases
				String good_case_file2 = goodCasesDir + File.separator + path + File.separator + f + "-"
						+ "good_cases.txt";

				String wrong_nlp_tagging_case_file = outputpath + File.separator + path + File.separator + f + "-"
						+ "wrong_nlp_tagging_cases.txt";
				String wrong_nlp_tagging_text_file = sentencesDir + File.separator + path + File.separator + f + "-"
						+ "wrong_nlp_tagging_cases.txt";

				String no_wrong_nlp_tagging_case_file = outputpath + File.separator + path + File.separator + f + "-"
						+ "no_nlp_tagging_cases.txt";
				String no_wrong_nlp_tagging_text_file = sentencesDir + File.separator + path + File.separator + f + "-"
						+ "wrong_nlp_tagging_cases.txt";

				String legacy_tagging_case_file = outputpath + File.separator + path + File.separator + f + "-"
						+ "legacy_tagging_cases.txt";
				String legacy_tagging_text_file = sentencesDir + File.separator + path + File.separator + f + "-"
						+ "legacy_tagging_cases.txt";

				ArrayList<CoNLLSentence> badCaseList = new ArrayList<CoNLLSentence>(
						localErrorCasesMap.get(ErrorCases.HUMAN));
				ArrayList<CoNLLSentence> goodCaseList = new ArrayList<CoNLLSentence>(
						localErrorCasesMap.get(ErrorCases.GOOD));
				ArrayList<CoNLLSentence> wrongTaggingCaseList = new ArrayList<CoNLLSentence>(
						localErrorCasesMap.get(ErrorCases.NLP_ERRORS));
				ArrayList<CoNLLSentence> noTaggingCaseList = new ArrayList<CoNLLSentence>(
						localErrorCasesMap.get(ErrorCases.NO_NLP_TAGS));
				ArrayList<CoNLLSentence> legacyTagsList = new ArrayList<CoNLLSentence>(
						localErrorCasesMap.get(ErrorCases.LEGACY_TAGS));

				errorCasesMap.putAll(localErrorCasesMap);

				// Write output to various files

				// Since we are done, we will write error sentences that were
				// not parsed
				// because of some mistakes

				if (badCaseList.size() > 0) {
					dataWriter.writeHITFormatData(badCaseList, bad_case_file);
					dataWriter.writeText(badCaseList, bad_case_text_file);
					badCasesCounter += badCaseList.size();
				}

				if (goodCaseList.size() > 0) {
					dataWriter.writeHITFormatData(goodCaseList, good_case_file1);

					dataWriter.writeHITFormatData(goodCaseList, good_case_file2);
					dataWriter.writeText(goodCaseList, good_case_text_file);
					goodCasesCounter += goodCaseList.size();
				}

				if (wrongTaggingCaseList.size() > 0) {
					dataWriter.writeHITFormatData(wrongTaggingCaseList, wrong_nlp_tagging_case_file);
					dataWriter.writeText(wrongTaggingCaseList, wrong_nlp_tagging_text_file);
					wrongCasesCounter += wrongTaggingCaseList.size();
				}

				if (noTaggingCaseList.size() > 0) {
					dataWriter.writeHITFormatData(noTaggingCaseList, no_wrong_nlp_tagging_case_file);
					dataWriter.writeText(noTaggingCaseList, no_wrong_nlp_tagging_text_file);
					noNLPTaggingCasesCounter += noTaggingCaseList.size();
				}

				if (legacyTagsList.size() > 0) {
					dataWriter.writeHITFormatData(legacyTagsList, legacy_tagging_case_file);
					dataWriter.writeText(legacyTagsList, legacy_tagging_text_file);
					legacyTaggingCasesCounter += legacyTagsList.size();
				}

			}

			System.out.println("total srl sentences : " + totalSentencesCounter);
			System.out.println("total good cases : " + goodCasesCounter);
			System.out.println("total bad cases : " + badCasesCounter);
			System.out.println("total parsing error cases (dependency, pos etc.): " + wrongCasesCounter);
			System.out.println("total cases with no nlp tags: " + noNLPTaggingCasesCounter);
			System.out.println("total cases with legacy issues: " + legacyTaggingCasesCounter);

		}

	}

	/**
	 * 
	 * @param datapath
	 * @throws IOException
	 */
	public void convertHITtoCONLL2009(String datapath) throws IOException {

		File file = new File(datapath);
		readCoNLLFormatCorpus(file, Format.HIT, true);

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			convertHITtoCONLL2009(coNLLSentence);

		}
	}

	/**
	 * Convert HIT to CONLL and write to output file.
	 * 
	 * @param datapath
	 * @param outputpath
	 * @throws IOException
	 */
	public void convertHITtoCONLL2009(String datapath, String outputpath) throws IOException {

		// clean the directory first

		File outputDir = new File(outputpath);
		if (!Strings.isNullOrEmpty(outputpath)) {
			outputDir = new File(outputpath);
			if (outputDir.exists()) {
				FileUtils.cleanDirectory(outputDir);
			} else {
				outputDir.mkdirs();
			}
		}

		File rootDir = new File(datapath);
		final String[] SUFFIX = { "txt" };
		List<File> files = new ArrayList<File>();

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		// iterate through all the files
		for (File file : files) {

			readCoNLLFormatCorpus(file, Format.HIT, true);

			for (CoNLLSentence coNLLSentence : conLLSentenceList) {

				convertHITtoCONLL2009(coNLLSentence);

			}

			// Lets produce the output now
			StringBuilder sb = new StringBuilder();
			for (CoNLLSentence coNLLSentence : conLLSentenceList) {

				// counter++;
				// sb.append("sentence:" + counter + " : " +
				// coNLLSentence.getSentence() + "\n");
				String coNLL = coNLLSentence.getCoNLLSentence();
				if (coNLL != null) {
					sb.append(coNLLSentence.getCoNLLSentence());
					sb.append("\n");
				}

				// convertCONLLtoHIT(coNLLSentence);
			}

			// String parentDir = file.getParentFile().getName();
			String parentDir = file.getParent().replace(datapath, "");
			String path = outputpath + File.separator + parentDir + File.separator + file.getName();
			File f = new File(path);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileUtils.write(f, sb.toString());
			System.out.println("Wrote to file " + file);

		}

	}

	/**
	 * Convert CONLL to HIT format data
	 * 
	 * @param datapath
	 * @return
	 */
	public ArrayList<CoNLLSentence> convertCONLLtoHIT(File datapath) {
		readCoNLLFormatCorpus(datapath, Format.CONLL, true);

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			// convertHITtoCONLL2009(coNLLSentence);
			convertCONLLtoHIT(coNLLSentence);
		}

		return conLLSentenceList;
	}

	/**
	 * This function converts CONLL sentence string to HIT sentence String
	 * 
	 * @param conllString
	 * @return
	 */
	public String convertCONLLtoHIT(String conllString) {
		String[] lines = null;
		if (!Strings.isNullOrEmpty(conllString)) {
			lines = (NEWLINE_PATTERN.split(conllString));
		}
		CoNLLSentence temp = new CoNLLSentence();
		temp.setCoNLLSentence(conllString);
		temp.setLines(lines);

		convertCONLLtoHIT(temp);

		return temp.getHITSentence();

	}

	/**
	 * This function converts CONLL TO HIT Format
	 * 
	 * @param coNLLSentence
	 */
	public String convertCONLLtoHIT(CoNLLSentence coNLLSentence) {
		String[] lines = coNLLSentence.getLines();

		if (lines.length < 1) {
			lines = (NEWLINE_PATTERN.split(coNLLSentence.getCoNLLSentence()));
		}

		String universal = "*";
		String universal_replace = "_";

		// take index column
		int index_column = 0;
		// the column number of the token
		int token_column_no = 1;
		// the column number of the verb column
		int verb_column_no = 13;
		// the first argument column number
		int first_arg_column_no = 14;
		// dependency column
		int dependency_column = 9;

		StringBuilder sb = new StringBuilder();

		boolean bad_case = false;

		if (lines.length >= 1 && lines[0].length() > 0) {
			ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

			/**
			 * 
			 */

			int total_columns = TAB_PATTERN.split(lines[0]).length;

			if (total_columns > verb_column_no) {

				for (int i = 0; i < total_columns; i++) {
					listOfLists.add(new ArrayList<String>());
				}

				for (int k = 0; k < lines.length; k++) {
					String line = lines[k];
					String[] cols = TAB_PATTERN.split(line);

					int col_length = cols.length;

					if (col_length != total_columns) {
						System.out.println("Columns number does not match! ");
						localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
						coNLLSentence.setHITSentence("");
						bad_case = true;
						return "";
					} else {

						for (int index = 0; index <= col_length - 1; index++) {

							String var = cols[index];
							ArrayList<String> l = listOfLists.get(index);
							l.add(var);
						}

					}

				}
			}

			// get index of all verb columns
			int verb_indicate_col = 12;
			int temp = 0;
			ArrayList<Integer> vindexlist = new ArrayList<>();
			for (String str : listOfLists.get(verb_indicate_col)) {

				if (str.equals("Y")) {
					vindexlist.add(temp);
				}
				temp++;
			}

			ArrayList<String> dependency_list = listOfLists.get(dependency_column);
			ArrayList<String> index_list = listOfLists.get(index_column);
			ArrayList<Integer> already_added_verb_list = new ArrayList<>();

			// sanity check
			String num = dependency_list.get(0);
			boolean cleanData = true;

			if (!StringUtils.isNumeric(num)) {
				cleanData = false;
				return sb.toString();
			}

			for (int i = first_arg_column_no; i < listOfLists.size(); i++) {

				// vertical column containing
				ArrayList<String> column = listOfLists.get(i);

				int index = 0;
				int begin_index = -1;
				int end_index = -1;

				ArrayList<String> new_column = new ArrayList<String>();
				new_column = column;
				for (int j = 0; j < column.size(); j++) {

					String string = column.get(j);

					if (!string.equals("_")) {

						end_index = index;

						int arg_index = Integer.parseInt(index_list.get(end_index));

						int start_iter = 0;
						if (end_index != 0) {
							start_iter = end_index - 1;
						}

						boolean atleast_one_match = false;
						for (int k = start_iter; k >= 0; k--) {

							int dependency_index = Integer.parseInt(dependency_list.get(k));
							int current_index = Integer.parseInt(index_list.get(k));

							if (dependency_index == arg_index) {
								begin_index = current_index - 1;
								atleast_one_match = true;
								continue;
							} else {

								if (!atleast_one_match) {
									begin_index = end_index;
								}
								// begin_index=current_index;
								break;
							}

						}

						String newbegin_String = "";
						String newend_String = "";

						// the columns are at only index
						if (begin_index == end_index) {
							newbegin_String = "(" + string + universal + ")";
							new_column.set(begin_index, newbegin_String);
						} else {
							newbegin_String = "(" + string + universal;
							newend_String = "*)";

							new_column.set(begin_index, newbegin_String);
							new_column.set(end_index, newend_String);
						}

						// begin_index and and end_index are known. String is
						// known

					}
					index++;

				}

				// since every column in args will contain only one verb we need
				// to
				// replace only once.
				boolean replace_verb = true;

				// since we finished looping . lets make final changes to the
				// columns
				for (int t = 0; t < new_column.size(); t++) {
					String str = new_column.get(t);
					if (str.equals(universal_replace)) {
						new_column.set(t, universal);
					}

					if (vindexlist.contains(t) && replace_verb && !already_added_verb_list.contains(t)) {
						String str1 = "(v" + universal + ")";
						new_column.set(t, str1);
						replace_verb = false;
						already_added_verb_list.add(t);
					}
				}
				// finally set this new column to original column
				column = new_column;

			}

			// System.out.println("total arg columns: " + listOfLists.size());

			// lets create new table string
			String delimiter = "\t";

			for (int i = 0; i < lines.length; i++) {

				for (int j = 0; j < total_columns; j++) {
					String c = listOfLists.get(j).get(i);

					if (j == total_columns - 1) {
						sb.append(c);
					} else {
						sb.append(c + delimiter);
					}

				}
				sb.append("\n");

			}

			coNLLSentence.setHITSentence(sb.toString());

		}

		else {
			System.out.println("the srl table for input : " + coNLLSentence.getSentence() + " is empty");
		}

		return sb.toString();
	}

	/**
	 * Replace the predicted columns values with empty strings. We want to do
	 * this sometimes for testing the results with new models
	 * 
	 * @param coNLLSentenceList
	 * @param predictedColumnList
	 */
	public void removePredictedColumnsFromCONLLSentence(List<CoNLLSentence> coNLLSentenceList,
			List<Integer> predictedColumnList) {

		for (CoNLLSentence coNLLSentence : coNLLSentenceList) {
			removePredictedColumnsFromCONLLSentence(coNLLSentence, predictedColumnList);
		}
	}

	/**
	 * Replace the predicted columns values with empty strings. We want to do
	 * this sometimes for testing the results with new models
	 * 
	 * @param coNLLSentence
	 * @param predictedColumnList
	 */
	public void removePredictedColumnsFromCONLLSentence(CoNLLSentence coNLLSentence,
			List<Integer> predictedColumnList) {
		String[] lines = coNLLSentence.getLines();
		if (lines != null && lines.length > 1) {

			// reads the data column wise
			ArrayList<ArrayList<String>> listOfLists = readCONLLDataColumnwise(coNLLSentence);
			if (listOfLists.size() > 0) {
				int total_columns = TAB_PATTERN.split(lines[0]).length;

				// update the column list , replacing the predicted columns
				// lists
				for (int column_index : predictedColumnList) {

					List<String> list = listOfLists.get(column_index);
					ArrayList<String> newList = new ArrayList<String>();
					for (String string : list) {
						newList.add("_");
					}
					listOfLists.set(column_index, newList);
				}

				// create the new conll string from updated column list
				String delimiter = "\t";
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < lines.length; i++) {

					for (int j = 0; j < total_columns; j++) {
						String c = "";
						int array_size = listOfLists.get(j).size();
						if (array_size < lines.length) {
							System.err.println(
									"Expected size :" + lines.length + "real size: " + listOfLists.get(j).size());
							c = "_";
						} else {
							c = listOfLists.get(j).get(i);
						}

						if (j == total_columns - 1) {
							sb.append(c);
						} else {
							sb.append(c + delimiter);
						}

					}
					sb.append("\n");
				}

				coNLLSentence.setCoNLLSentence(sb.toString());

			}

		}
	}

	/**
	 * This is an important function to convert from HIT to CONLL format. It
	 * takes care of many issues which may happen because of wrong tagging . for
	 * e.g. multiple head issue.
	 * 
	 * @param coNLLSentence
	 */
	public void convertHITtoCONLL2009(CoNLLSentence coNLLSentence) {

		boolean isValid = validateHITFormatData(coNLLSentence);

		if (isValid) {

			String[] lines = coNLLSentence.getLines();

			try {

				if (lines.length > 1) {

					// reads the data column wise
					ArrayList<ArrayList<String>> listOfLists = readCONLLDataColumnwise(coNLLSentence);

					if (listOfLists.size() > 0) {
						int total_columns = TAB_PATTERN.split(lines[0]).length;

						ArrayList<String> dependency_column = listOfLists.get(Constants.conllformat_phead_column_no);

						Map<String, String> headFinderMap = new LinkedHashMap<String, String>();

						// System.out.println("total arg columns: " +
						// listOfLists.size());

						String universal_replace = "_";

						for (int i = conllformat_first_arg_column_no; i < listOfLists.size(); i++) {

							Map<Integer, String> map = new LinkedHashMap<>();
							ArrayList<String> column = listOfLists.get(i);

							int index = 0;
							int begin_index = -1;
							int end_index = -1;

							boolean multiTokenArg_begin = false;
							boolean multiTokenArg_end = false;

							for (int j = 0; j < column.size(); j++) {

								String string = column.get(j);
								index++;
								// System.out.println(string);

								if (string.contains("(") && string.contains(")")
										&& !string.toLowerCase().contains("(v*")) {
									// String new_s = string.replaceAll("[()*]",
									// "").replace("and", "");
									String new_s = fixHumanTypos(string);

									map.put(index, new_s);
									multiTokenArg_begin = false;
								}

								else if (string.toLowerCase().equals("(v*") || string.toLowerCase().equals("(v*)")
										|| string.toLowerCase().equals("(va*)")
										|| string.toLowerCase().equals("(v*）")) {
									map.put(index, universal_replace);
									multiTokenArg_begin = false;
								}

								// this is where the many tokens range begin.
								else if (string.contains("(") && !string.contains(")")) {
									// String new_s = string.replaceAll("[()*]",
									// "").replace("and", "");
									String new_s = fixHumanTypos(string);
									begin_index = index;

									map.put(begin_index, new_s);

									String b = Integer.toString(begin_index);
									headFinderMap.put(b, dependency_column.get(begin_index - 1));
									multiTokenArg_begin = true;
								}

								// this is where the many tokens range end. Here
								// we update the map to reflect new position of
								// the argument for CONLL format.
								else if (string.contains(")") && !string.toLowerCase().contains("(v*")) {

									int final_end_index = 0;
									int final_begin_index = 0;

									end_index = index;
									String argument = map.get(begin_index);
									if (argument == null) {
										map.put(end_index, universal_replace);
									} else {

										if (multiTokenArg_begin && !multiTokenArg_end) {
											String b = Integer.toString(index);
											headFinderMap.put(b, dependency_column.get(index - 1));
											multiTokenArg_end = true;

											// multi argument helper function.
											int[] final_index_list = multiArgumentHelper(map, headFinderMap,
													begin_index, end_index);
											final_begin_index = final_index_list[0];
											final_end_index = final_index_list[1];

											// this is a workaround to remove
											// the argument which was put in the
											// begin_index based on hit format.
											if (!map.get(begin_index).equals(universal_replace)) {
												map.put(begin_index, universal_replace);
											}

											// put information into the map
											map.put(final_end_index, argument);
											map.put(final_begin_index, universal_replace);

											// reset variables for the next
											// argument
											headFinderMap = new LinkedHashMap<>();
											multiTokenArg_end = false;
											multiTokenArg_begin = false;
										}

									}

								}

								else if (string.contains("*") && !string.contains("(")
										&& !string.toLowerCase().contains("(v*") && !string.contains(")")) {
									map.put(index, universal_replace);
									if (multiTokenArg_begin && !multiTokenArg_end) {
										String b = Integer.toString(index);
										headFinderMap.put(b, dependency_column.get(index - 1));

									}

								} else {

								}

							}

							ArrayList<String> newString = new ArrayList<String>(map.values());
							listOfLists.set(i, newString);
						}

						String delimiter = "\t";
						StringBuilder sb = new StringBuilder();

						for (int i = 0; i < lines.length; i++) {

							for (int j = 0; j < total_columns; j++) {
								String c = "";
								int array_size = listOfLists.get(j).size();
								if (array_size < lines.length) {
									System.err.println("Expected size :" + lines.length + "real size: "
											+ listOfLists.get(j).size());

									System.out.println(coNLLSentence.getHITSentence());

									c = universal_replace;
								} else {
									c = listOfLists.get(j).get(i);
								}

								if (j == total_columns - 1) {
									sb.append(c);
								} else {
									sb.append(c + delimiter);
								}

							}
							sb.append("\n");
						}

						// System.out.println(sb.toString());
						coNLLSentence.setCoNLLSentence(sb.toString());

					}
				}
			}

			catch (ArrayIndexOutOfBoundsException error) {
				error.printStackTrace();
			}

			localErrorCasesMap.get(ErrorCases.GOOD).add(coNLLSentence);
		}

		// the validator has failed. we will add it to bad case

	}

	/**
	 * This function helps to deal with multiple arguments. Its a helper
	 * function to convertHITtoCONLL2009
	 * 
	 * @param map
	 * @param headFinderMap
	 * @param index
	 * @param begin_index
	 * @param end_index
	 * @return
	 */
	private int[] multiArgumentHelper(Map<Integer, String> map, Map<String, String> headFinderMap, int begin_index,
			int end_index) {

		// we have reached the end of
		// argument now.

		int final_end_index = 0;
		int final_begin_index = 0;

		List<String> newArgIndexList = getNewArgPositionIndex(headFinderMap);
		int updatedIntIndex = 0;
		if (newArgIndexList.size() == 1) {

			// there is only head

			String updatedIndex = newArgIndexList.get(0);

			updatedIntIndex = Integer.parseInt(updatedIndex);

			if (updatedIntIndex != end_index) {
				final_end_index = updatedIntIndex;
				final_begin_index = end_index;
				// end_index=updated_end_index;

			}

			else {
				final_end_index = end_index;
				final_begin_index = begin_index;

			}

		}
		// there are two possible heads
		else {
			List<Integer> myList = new ArrayList<>(newArgIndexList.size());
			for (int z = 0; z < newArgIndexList.size(); z++) {
				myList.add(Integer.parseInt(newArgIndexList.get(z)));

			}

			// essentially its the last token. If last token is also in the
			// newArgArrayList, lets take
			// the
			// if(myList.contains(sentenceLength)){
			// Collections.sort(myList);
			//
			// }
			// We will take the only take the
			// first value for the solution.
			updatedIntIndex = myList.get(0);

			final_end_index = updatedIntIndex;
			final_begin_index = end_index;

		}

		return new int[] { final_begin_index, final_end_index };

	}

	/**
	 * This function will return us the new index position of the multiple token
	 * argument
	 * 
	 * 
	 * @param headFinderMap
	 * @return
	 */
	private List<String> getNewArgPositionIndex(Map<String, String> headFinderMap) {

		/**
		 * We reach here only when both the variables are true
		 */

		List<String> tokenList = new ArrayList<>();
		List<String> depList = new ArrayList<>();

		Set<String> keys = headFinderMap.keySet();
		Collection<String> values = headFinderMap.values();

		tokenList.addAll(keys);
		depList.addAll(values);

		List<String> tokenTemp = new ArrayList<>();
		for (int k = 0; k < depList.size(); k++) {

			String depIndex = depList.get(k);
			String tokenIndex = tokenList.get(k);
			if (!tokenList.contains(depIndex)) {
				tokenTemp.add(tokenIndex);

			}

		}

		return tokenTemp;

	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public String fixHumanTypos(String string) {
		String new_s = string;

		new_s = new_s.replace("(VA", "(v");
		new_s = new_s.replace("(VH", "(v");
		new_s = new_s.replace("(V*", "v*");
		new_s = new_s.replaceAll("[()*v]", "").replace("and", "").trim();
		new_s = new_s.replaceAll("）", "");
		new_s = new_s.replaceAll("（", "");

		if (!Strings.isNullOrEmpty(new_s)) {

			new_s = new_s.toUpperCase();

			new_s = new_s.replace("A0,", "A0");

			new_s = new_s.replace("NRG", "NEG");
			new_s = new_s.replace("ENG", "NEG");

			new_s = new_s.replace("EXY", "EXT");

			new_s = new_s.replace("NOD", "MOD");
			new_s = new_s.replace("AVD", "MOD");
			new_s = new_s.replace("MD", "MOD");
			new_s = new_s.replace("KOD", "MOD");
			new_s = new_s.replace("MDO", "MOD");
			new_s = new_s.replace("MODO", "MOD");

			new_s = new_s.replace("DIS+V", "DIS");
			new_s = new_s.replace("DIS+", "DIS");

			new_s = new_s.replace("ADV&", "ADV");
			new_s = new_s.replace("ADJ", "ADV");

			new_s = new_s.replace("TEM", "TMP");
			new_s = new_s.replace("TEP", "TMP");
			new_s = new_s.replace("TMP0", "TMP");

			new_s = new_s.replace("C-A1", "A1");
			new_s = new_s.replace("C-A0", "A0");
			new_s = new_s.replace("C-AT", "AT");
			new_s = new_s.replace("C-V", "_");

			new_s = new_s.replace("LOV", "LOC");

			// new_s = new_s.replace("A", "A0");

			new_s = new_s.replace("C-AFT", "AFT");

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

		
		}

		return new_s;
	}

	/**
	 * Add relation argument information
	 * 
	 * @param coNLLSentence
	 */
	public void addRelationInformation(CoNLLSentence coNLLSentence) {

		Map<String, Integer> relationMap = new HashMap<String, Integer>();
		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

		if (!(coNLLSentence.getDataList().size() > 2)) {
			listOfLists = readCONLLDataColumnwise(coNLLSentence);
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
	 * @param coNLLSentence
	 */
	public void generateSentenceFromCoNLLData(CoNLLSentence coNLLSentence) {
		ArrayList<ArrayList<String>> listOfLists = readCONLLDataColumnwise(coNLLSentence);
		StringBuilder sb = new StringBuilder();

		ArrayList<String> tokens = new ArrayList<>();
		if (listOfLists != null && listOfLists.size() > 1) {
			tokens = listOfLists.get(1);

			for (String string : tokens) {
				sb.append(string);
			}
		}

		coNLLSentence.setTokenList(tokens);
		coNLLSentence.setSentence(sb.toString());
		// System.out.println(sb.toString());
	}

	/**
	 * Reads the CONLL Format data columnwise
	 * 
	 * @param coNLLSentence
	 * @return
	 */
	public ArrayList<ArrayList<String>> readCONLLDataColumnwise(CoNLLSentence coNLLSentence) {

		String[] lines = coNLLSentence.getLines();
		boolean readError = false;

		int verb_column_no = 13;

		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

		int total_columns = TAB_PATTERN.split(lines[0]).length;

		if (total_columns > verb_column_no) {

			for (int i = 0; i < total_columns; i++) {
				listOfLists.add(new ArrayList<String>());
			}

			for (int k = 0; k < lines.length; k++) {
				String line = lines[k];
				String[] cols = TAB_PATTERN.split(line);

				for (int index = 0; index <= cols.length - 1; index++) {

					try {
						listOfLists.get(index).add(cols[index]);
					} catch (java.lang.IndexOutOfBoundsException e) {
						readError = true;

						e.printStackTrace();
						// badCaseList.add(coNLLSentence);
					}
				}

			}
		}

		if (readError) {
			System.err.println(coNLLSentence.getCoNLLSentence());
		}

		return listOfLists;
	}

	/**
	 * 
	 * @param coNLLSentence
	 * @return
	 */
	public boolean validateHITFormatData(CoNLLSentence coNLLSentence) {

		boolean isValid = true;
		// String sent = coNLLSentence.getHITSentence();
		String[] lines = coNLLSentence.getLines();

		// this verb column number is index
		int verb_column_no = 13;
		int verb_check_column = 12;
		int alternate_verb_check_column = 10;
		int token_index_column = 0;
		int dependency_index_column = 8;

		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

		int total_columns = TAB_PATTERN.split(lines[0]).length;

		// 1 check if number of lines is greater than 1
		if (lines.length <= 1) {
			isValid = false;
			localErrorCasesMap.get(ErrorCases.NLP_ERRORS).add(coNLLSentence);
			return isValid;
		}

		// 2. total columns should be greater than verb column number
		if (total_columns <= verb_column_no + 1) {

			isValid = false;
			localErrorCasesMap.get(ErrorCases.NLP_ERRORS).add(coNLLSentence);
			return isValid;
		}

		// 3. create the list of lists and check if there are any errors during
		// creation. sometimes there is an index out of bound exception.
		if (total_columns > verb_column_no) {

			for (int i = 0; i < total_columns; i++) {
				listOfLists.add(new ArrayList<String>());
			}

			for (int k = 0; k < lines.length; k++) {
				String line = lines[k];
				String[] cols = TAB_PATTERN.split(line);

				for (int index = 0; index <= cols.length - 1; index++) {

					try {
						listOfLists.get(index).add(cols[index]);
					} catch (java.lang.IndexOutOfBoundsException e) {
						e.printStackTrace();
						System.err.println(coNLLSentence.getHITSentence());
						isValid = false;
						localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
						return isValid;
					}
				}

			}

		}

		// // this is a function which generates string for sentence, many not
		// be a
		// // right place but for test purpose for now
		// if (listOfLists.size() > 1) {
		// ArrayList<String> tokens = listOfLists.get(0);
		// StringBuilder sb = new StringBuilder();
		// for (String string : tokens) {
		// sb.append(string);
		// }
		//
		// coNLLSentence.setSentence(sb.toString());
		// }

		// Check if the mandatory number of columns are present or not
		// 1 一般 一般 一般 AD AD 3 3 ADV ADV _ _ (ADV*)
		// 1 一般 一般 一般 AD AD 3 3 _ _ ADV ADV _ _ (ADV*)

		// check for missing columns
		boolean hasVerb = false;
		if (listOfLists.size() > 0) {
			int length = listOfLists.size();

			if (length > verb_check_column + 1) {
				hasVerb = checkForPresenceofVerb(listOfLists, verb_check_column);
				if (!hasVerb) {

					//// Check if the mandatory number of columns are present or
					//// not
					// 1 一般 一般 一般 AD AD 3 3 ADV ADV _ _ (ADV*)
					// 1 一般 一般 一般 AD AD 3 3 _ _ ADV ADV _ _ (ADV*)
					hasVerb = checkForPresenceofVerb(listOfLists, alternate_verb_check_column);
					if (!hasVerb) {
						// means that we need to add two columns
						isValid = false;
						System.err.println("no verb present");
						System.err.println(coNLLSentence.getHITSentence());
						localErrorCasesMap.get(ErrorCases.NO_NLP_TAGS).add(coNLLSentence);
						return isValid;
					} else {

					}

				}
			}

			else {

			}

		}

		// 4. check if the (v*) tag is present or not in the argument columns

		// 5. check for presence of stars
		// also check if there are some arguments and not only (v*)
		if (listOfLists.size() > 0) {

			// System.out.println("total arg columns: " + listOfLists.size());
			int first_arg_column_no = 14;

			for (int i = first_arg_column_no; i < listOfLists.size(); i++) {
				boolean hasVerbTag = false;
				boolean hasNonVerbATag = false;

				ArrayList<String> column = listOfLists.get(i);

				for (int j = 0; j < column.size(); j++) {

					String string = column.get(j);
					string = string.toUpperCase();
					// check for presence of stars
					if (!string.contains("*")) {
						isValid = false;
						System.err.println("asterisk problem");
						System.err.println(coNLLSentence.getHITSentence());
						localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
						return isValid;

					}

					if (string.equals("(V*)")) {
						hasVerbTag = true;
					}

					if (string.contains("(") && !(string.equals("(V*)"))) {
						hasNonVerbATag = true;
					}

				}

				if (!hasVerbTag) {
					System.err.println("no (v*) in argument columns");
					System.err.println(coNLLSentence.getHITSentence());
					isValid = false;
					localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
					return isValid;
				}
				if (!hasNonVerbATag) {
					System.err.println("no argument in argument columns other than (v*)");
					System.err.println(coNLLSentence.getHITSentence());
					isValid = false;
					localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
					return isValid;
				}

			}

		}

		// 6. Check for index of out of bound error in dependency
		if (listOfLists.size() > 0) {
			ArrayList<String> token_index_list = listOfLists.get(token_index_column);
			ArrayList<String> dependency_index_list = listOfLists.get(dependency_index_column);

			ArrayList<Integer> token_index_list_1 = new ArrayList<Integer>();
			ArrayList<Integer> dependency_index_list_1 = new ArrayList<Integer>();

			try {
				for (String s : token_index_list)
					token_index_list_1.add(Integer.valueOf(s));
				for (String s : dependency_index_list)
					dependency_index_list_1.add(Integer.valueOf(s));

				Integer obj1 = Collections.max(token_index_list_1);
				Integer obj2 = Collections.max(dependency_index_list_1);

				// boolean
				// same_list=listEqualsNoOrder(token_index_list,dependency_index_list);
				if (obj2 > obj1) {
					isValid = false;
					System.err.println("the dependency column has higher index number than token index number");
					System.err.println(coNLLSentence.getHITSentence());
					localErrorCasesMap.get(ErrorCases.NLP_ERRORS).add(coNLLSentence);
					return isValid;
				}
				
				for (int i = 0; i < token_index_list.size(); i++) {
					 int token_index=Integer.parseInt(token_index_list.get(i));
					
					String dep_token = dependency_index_list.get(i);
					int dep_token_index = Integer.parseInt(dep_token);
					if(token_index==dep_token_index){
						isValid = false;
						System.err.println("the dependency column index and token index are same which is not right.  " + token_index + " = " +dep_token_index);
						System.err.println(coNLLSentence.getHITSentence());
						localErrorCasesMap.get(ErrorCases.NLP_ERRORS).add(coNLLSentence);
						return isValid;
					}
				}
				
				

			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				isValid = false;
				localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
				return isValid;
			}
		}

		// 7 . check if all the rows have equal number of tokens
		if (listOfLists.size() > 0) {

			int no_of_columns = lines[0].length();

			for (int k = 0; k < lines.length; k++) {
				String line = lines[k];
				String[] cols = TAB_PATTERN.split(line);

				int col_nos = cols.length;

				if (k == 0) {
					no_of_columns = col_nos;
				} else {
					if (no_of_columns != col_nos) {
						System.err.println(
								"error : wrong no of tokens in columns" + "\n" + coNLLSentence.getHITSentence());
						isValid = false;
						localErrorCasesMap.get(ErrorCases.NLP_ERRORS).add(coNLLSentence);
						return isValid;
					}
				}

			}

		}

		// 8 . check if none of the entries is empty or some known issues
		// entry is : (* , (v*
		if (listOfLists.size() > 0) {
			boolean badCase = false;
			for (int k = 0; k < lines.length; k++) {
				String line = lines[k];
				String[] cols = TAB_PATTERN.split(line);

				for (String string : cols) {
					string = string.trim();

					if (string.equals("*(") || string.equals("(*") || string.equals("(*)")) {
						badCase = true;
					} else if (string.toLowerCase().equals("(v*") || string.toLowerCase().equals("(vh*)")
							|| string.toLowerCase().equals("(va*)")) {
						badCase = true;
					}

					if (badCase) {
						System.err.println("error : empty token in the row" + "\n" + coNLLSentence.getHITSentence());
						isValid = false;
						localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
						return isValid;

					}

				}

			}

		}

		// 9. This is a legacy bug because of wrong tokenization where the first
		// token was completely missing.

		if (listOfLists.size() > 0) {
			boolean badCase = false;

			ArrayList<String> form_list = listOfLists.get(conllformat_form_column_no);

			String firstToken = form_list.get(0);
			if (firstToken.trim().equals(",") || firstToken.trim().equals("，")) {
				badCase = true;
			}

			if (badCase) {
				System.err.println(
						"error : first token is comma . probably legacy bug" + "\n" + coNLLSentence.getHITSentence());
				isValid = false;
				localErrorCasesMap.get(ErrorCases.LEGACY_TAGS).add(coNLLSentence);
				return isValid;

			}

		}

		// 10. The no of argument columns should be equal to the number of verbs
		ArrayList<String> verb_check_column_list = listOfLists.get(verb_check_column);
		int verb_counter = 0;
		boolean badCase = false;
		int no_arg_columns = 0;
		for (String string2 : verb_check_column_list) {
			if (string2.equals("Y")) {
				verb_counter++;
			}
		}
		// no of arg columns
		no_arg_columns = listOfLists.size() - verb_column_no - 1;
		if (no_arg_columns != verb_counter) {
			badCase = true;
		}
		if (badCase) {
			System.err.println("error : number of verbs and number of arguments are not equal" + "\n"
					+ coNLLSentence.getHITSentence());
			isValid = false;
			localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
			return isValid;

		}

		// 11. check verb_check column and verb column
		ArrayList<String> verb_column_list = listOfLists.get(verb_column_no);
		for (int i = 0; i < lines.length; i++) {
			String verb_check_str = verb_check_column_list.get(i);
			String verb_str = verb_column_list.get(i);
			if (verb_check_str.equals("_")) {
				if (!verb_str.equals("_")) {
					badCase = true;
				}
			} else if (verb_check_str.equals("Y")) {
				Matcher m = VERB_SENSE_PATTERN.matcher(verb_str);
				if (!m.find()) {
					badCase = true;
				}
			} else {
				badCase = true;
			}
			if (badCase) {
				System.err.println("error : illegal value in verb check column or verb column" + "\n"
						+ coNLLSentence.getHITSentence());
				isValid = false;
				localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
				return isValid;
			}
		}
		
		
		
		// 12 . Checks for mismatch in parenthesis in argument columns
		int first_argument_column = conllformat_first_arg_column_no;
		String leftP = "(";
		String rightP = ")";
		int leftParenthesis = 0;
		int rightParenthesis = 0;
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
			
			badCase = true;
		}
		if (badCase) {
			System.err.println("mismatch in  parenthesis " + leftParenthesis + " " + rightParenthesis + "\n"
					+ coNLLSentence.getHITSentence());
			isValid = false;
			localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
			return isValid;
		}
		
		// 13. check if all tags are in whitelist
		int first_arg_column_no = 14;

		for (int i = first_arg_column_no; i < listOfLists.size(); i++) {

			ArrayList<String> column = listOfLists.get(i);

			for (int j = 0; j < column.size(); j++) {

				String tag = getTag(column.get(j));
				if (!Strings.isNullOrEmpty(tag)) {
					boolean isTagLegal = LEGAL_SRL_TAGS.contains(tag);
					if (!isTagLegal) {
						System.err.println("Invalid tag found: " + tag);
						System.err.println(coNLLSentence.getHITSentence());
						isValid = false;
						localErrorCasesMap.get(ErrorCases.HUMAN).add(coNLLSentence);
						return isValid;
					}
				}
			}
		}
		

		return isValid;
	}
	
	private static String getTag(String str) {
		int idx_start = 0;
		int idx_end = str.length();
		
		if (str.startsWith("("))
			idx_start = 1;
		
		if (str.endsWith("*)"))
			idx_end -= 2;
		if (str.endsWith("*"))
			idx_end -= 1;
		
		return str.substring(idx_start, idx_end);
	}

	/**
	 * Check for equality of Lists without word order in consideration
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static <T> boolean listEqualsNoOrder(List<T> l1, List<T> l2) {
		final Set<T> s1 = new HashSet<>(l1);
		final Set<T> s2 = new HashSet<>(l2);

		return s1.equals(s2);
	}

	/**
	 * 
	 * @param listOfLists
	 * @param conllformat_verb_column_no
	 */
	private boolean checkForPresenceofVerb(ArrayList<ArrayList<String>> listOfLists, int verb_check_column) {
		boolean hasVerb = false;

		if (listOfLists.size() > 0) {
			int length = listOfLists.size();

			if (length > verb_check_column + 1) {
				ArrayList<String> list = listOfLists.get(verb_check_column);

				for (String string : list) {
					if (string.toLowerCase().contains("y") && !string.contains("*")) {
						hasVerb = true;
						break;
					}
				}
			}
		}

		return hasVerb;

	}

	/**
	 * Read Corpus from the given file path
	 * 
	 * @param datapath
	 * @return
	 */
	public ArrayList<CoNLLSentence> readTextCorpus(String datapath) {

		File file = new File(datapath);
		DataReader dataReader = new DataReader(file);

		ArrayList<CoNLLSentence> sentenceList = dataReader.readTextCorpus();

		for (CoNLLSentence coNLLSentence : sentenceList) {

			// String str=getTokenizedString(coNLLSentence.getSentence());
			coNLLSentence.preprocessSentence();
		}

		conLLSentenceList = sentenceList;
		return sentenceList;
	}

	/**
	 * Read Corpus from the given file path
	 * 
	 * @param datapath
	 * @return
	 */
	public ArrayList<CoNLLSentence> readCoNLLFormatCorpus(String datapath) {

		File file = new File(datapath);

		return readCoNLLFormatCorpus(file, Format.CONLL, true);

	}

	/**
	 * Reads conll Format corpus recursively from the directory
	 * 
	 * @param dataFolder
	 * @return
	 */
	public ArrayList<CoNLLSentence> readCoNLLFormatCorpus(String dataFolder, Format format) {

		ArrayList<CoNLLSentence> data = new ArrayList<CoNLLSentence>();
		File rootDir = new File(dataFolder);
		final String[] SUFFIX = { "txt" };
		List<File> files = new ArrayList<File>();

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		// iterate through all the files
		for (File file : files) {

			ArrayList<CoNLLSentence> conLLSentenceList = readCoNLLFormatCorpus(file, format, true);
			data.addAll(conLLSentenceList);
		}

		return data;
	}

	/**
	 * Read Corpus from the given file path
	 * 
	 * @param datapath
	 * @return
	 */
	public ArrayList<CoNLLSentence> readCoNLLFormatCorpus(File file, Format format, boolean generateSentence) {

		DataReader dataReader = new DataReader(file);

		// read sentences
		ArrayList<CoNLLSentence> sentenceList = dataReader.readData(format);

		if (generateSentence) {

			// add sentences text ;
			for (CoNLLSentence coNLLSentence : sentenceList) {
				generateSentenceFromCoNLLData(coNLLSentence);
			}
		}

		conLLSentenceList = sentenceList;
		return conLLSentenceList;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// String path = "100conll.txt";
		String output = "";
		String input = "";

		DataFormatConverter fc = new DataFormatConverter();

		// path = "ST-Chinese-trial.txt";
		// output="data/emotibot/conll_format.txt";
		// fc.convertCONLLtoHIT(path,output);

		// input = "data/emotibot/HIT_format.txt";
		// output = "data/emotibot/conll_format_1000_cases.txt";
		// fc.convertHITtoCONLL2009(input, output);

		// fc.testHITtoCONLLCoversion();

		// input = "data/emotibot/srl_corpus.txt";
		// // output="data/emotibot/hit_format_output_250.txt";
		// output = "data/emotibot/test";
		//

		//
		// int sentenceCount = 250;
		// // String file_name="srl";
		// String file_name = "srl_bad_cases";
		// fc.createHITFormatData(input, file_name, output, sentenceCount);

		fc.testHITFormatCreation();
		// fc.testHITtoCONLLCoversion();
	}

	private void testHITFormatCreation() throws Exception {

		// String input = "data/emotibot/bad_cases/srl_bad_cases.txt";
		// String output = "data/emotibot/bad_cases_training_data";
		// int sentenceCount = 250;
		// String file_name = "srl_bad_cases";

		// String input = "data/emotibot/sentences_1500.txt";
		// String output = "data/emotibot/";
		// int sentenceCount = 1500;
		// String file_name = "sentences_hit";

		String input = "data/bad_case_generation_5types.txt";
		String output = "data/";
		int sentenceCount = 300;
		String file_name = "bad_case_generation_5types_hit";

		createHITFormatData(input, file_name, output, sentenceCount);
	}

	private void testHITtoCONLLCoversion() throws Exception {
		// String input = "data/emotibot/bad_cases/srl_bad_cases_hit.txt";
		// String output = "data/emotibot/bad_cases/srl_bad_cases_conll.txt";
		// convertHITtoCONLL2009(input, output, false);

		String rootDirectory = "data/emotibot/";
		String inputDir = "data/emotibot/bad_cases_training_hit";
		String outputDir = "data/emotibot/bad_cases_training_conll";
		String goodCasesDir = "data/emotibot/good_cases";

		DataFormatOptions data_format_options = new DataFormatOptions();
		data_format_options.rootDirectory = rootDirectory;
		data_format_options.inputDir = inputDir;
		data_format_options.outputDir = outputDir;
		data_format_options.goodCasesHITDir = goodCasesDir;

		data_format_options.isRecursive = true;
		data_format_options.onlyGoodCases = true;

		// convert directory recursively
		convertHITtoCONLL2009(data_format_options);
	}

	private void convertCONLLtoSentences() {

	}

}
