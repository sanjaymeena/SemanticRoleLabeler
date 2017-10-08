/**
 * 
 */
package com.emotibot.srl.utilities;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import static com.emotibot.srl.format.Constants.*;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.format.DataWriter;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import static com.emotibot.srl.format.Constants.*;

/**
 * @author Sanjay
 *
 */
public class WordInfoGenerator {
	DataFormatConverter dfc;
	String pos_ambiguous_words = "pos_ambiguous_words";
	String dep_ambiguous_words = "dep_ambiguous_words";

	public WordInfoGenerator() {
		dfc = new DataFormatConverter();
	}

	public void extractDependencies() throws IOException {
		String dataFolder = "data/conll2009-chinese-srl/data";
		String outputFolder = "data/conll2009-chinese-srl/depinfo";

		Format format=Format.HIT;
		//Multimap<String, String> depToWordMap = LinkedListMultimap.create();

		ArrayList<CoNLLSentence> data = new ArrayList<CoNLLSentence>();
		Multimap<String, String> wordToDepMap = LinkedListMultimap.create();
		Multimap<String, String> depToWordMap = LinkedListMultimap.create();

		ArrayList<CoNLLSentence> badCases = new ArrayList<CoNLLSentence>();

		// read conll format data recursively
		data = dfc.readCoNLLFormatCorpus(dataFolder, format);

		// iterate over all sentences and add lists columnwise
		for (CoNLLSentence coNLLSentence : data) {
			ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

			if (!(coNLLSentence.getDataList().size() > 2)) {
				listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);
				coNLLSentence.setDataList(listOfLists);

				if (listOfLists.size() > 0) {
					ArrayList<String> form_list = listOfLists.get(conllformat_form_column_no);
					ArrayList<String> pos_list = listOfLists.get(conllformat_pdeprel_column_no);

					String firstToken = form_list.get(0);
					if (firstToken.trim().equals(",") || firstToken.trim().equals("，")) {
						badCases.add(coNLLSentence);
					}

					for (int i = 0; i < form_list.size(); i++) {

						String form = form_list.get(i);
						String pos = pos_list.get(i);

						boolean exists = wordToDepMap.get(form).contains(pos);

						if (!exists && !Strings.isNullOrEmpty(form) && !Strings.isNullOrEmpty(pos)) {
							wordToDepMap.put(form, pos);
						}

						exists = depToWordMap.get(pos).contains(form);
						if (!exists && !Strings.isNullOrEmpty(form) && !Strings.isNullOrEmpty(pos)) {
							depToWordMap.put(pos, form);
						}
					}

				}

			}

		}

		// Now we want to create ambiugous words , words which has more than one
		// POS detected by Tagger
		Set<String> ambiguous_words = new HashSet<String>();

		// test
		// Collection<String> t = posToWordMap.get("bl");

		for (String key : wordToDepMap.keySet()) {
			Collection<String> vals = wordToDepMap.get(key);
			if (vals.size() > 1) {
				ambiguous_words.add(key);
			}
			System.out.println(key + " : " + vals.toString());
		}

		for (String key : depToWordMap.keySet()) {
			Collection<String> vals = depToWordMap.get(key);
			// System.out.println(key + " : " + vals.toString());
			System.out.println(key);
		}

		// System.out.println(posToWordMap.keys());
		int temp = 0;

		// Let's empty the directory first so that there are no old files from
		// previous run
		if (!Strings.isNullOrEmpty(outputFolder)) {
			File outputDir = new File(outputFolder);
			FileUtils.cleanDirectory(outputDir);
		}

		for (String key : depToWordMap.keySet()) {
			Collection<String> vals = depToWordMap.get(key);

			StringBuilder sb = new StringBuilder();

			for (String string : vals) {

				// we don't want to include ambiguous words in the pos list. So
				// we check for it here.
				if (!ambiguous_words.contains(string)) {
					sb.append(string + "\n");
				}

			}

			// At this point, if the sb.ToString() is empty, it means that the
			// all the words in this pos list are ambiguous
			// and they will make their way in pos_ambiguous_words.txt

			String f = outputFolder + File.separator + key + ".txt";
			File file = new File(f);
			FileUtils.write(file, sb.toString(), "UTF-8");

		}

		String f = outputFolder + File.separator + dep_ambiguous_words + ".txt";
		File file = new File(f);

		StringBuilder sb1 = new StringBuilder();
		for (String string : ambiguous_words) {
			sb1.append(string + "\n");
		}
		FileUtils.write(file, sb1.toString(), "UTF-8");

		System.out.println("sentences beginning with comma : " + badCases.size());
		for (CoNLLSentence coNLLSentence : badCases) {
			System.out.println(coNLLSentence.getSentence());
		}

		Set<String> unique = new HashSet<String>(wordToDepMap.keySet());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println("Printing word count per POS Category...");
		for (String key : depToWordMap.keySet()) {
			Collection<String> vals = depToWordMap.get(key);

			Set<String> unique1 = new HashSet<String>(vals);
			// System.out.println(key + " : " + vals.toString());
			System.out.println(key + "  : " + unique1.size());
		}
		System.out.println("Total dep tag types : " + depToWordMap.keySet().size());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println("Total ambiguous words/(words with multiple dep) :  " + ambiguous_words.size());
		System.out.println("Total sentences in corpus:  " + data.size());

	}

	// This program will read the folders mentioned in the args list. It is
	// assumed that each file name is a pos category. for e.g "nn"
	// The output is going to be concated files in the give output folder
	public void concatPOSFiles(List<String> args, String outputFolder) throws IOException {

		Multimap<String, String> posToWordMap = LinkedListMultimap.create();

		final String[] SUFFIX = { "txt" };
		List<File> files = new ArrayList<File>();

		for (String folder : args) {

			File dir = new File(folder);
			if (dir.isDirectory()) {
				Collection<File> temp = FileUtils.listFiles(dir, SUFFIX, true);
				files = new ArrayList<File>(temp);
			} else {
				files.add(dir);
			}

			// iterate through all the files
			for (File file : files) {

				String key = file.getName().replace(".txt", "");

				List<String> lines = FileUtils.readLines(file, ENCODING);
				for (String string : lines) {

					String word = string.trim();
					boolean exists = posToWordMap.get(key).contains(word);

					if (!exists && !Strings.isNullOrEmpty(word) && !Strings.isNullOrEmpty(key)) {
						posToWordMap.put(key, word);
					}

				}

			}

		}

		// Let's empty the directory first so that there are no old files from
		// previous run
		if (!Strings.isNullOrEmpty(outputFolder)) {
			File outputDir = new File(outputFolder);
			FileUtils.cleanDirectory(outputDir);
		}

		for (String key : posToWordMap.keySet()) {
			Collection<String> vals = posToWordMap.get(key);

			StringBuilder sb = new StringBuilder();

			for (String string : vals) {

				// we don't want to include ambiguous words in the pos list. So
				// we check for it here.

				sb.append(string + "\n");

			}

			String f = outputFolder + File.separator + key + ".txt";
			File file = new File(f);
			FileUtils.write(file, sb.toString(), "UTF-8");
			System.out.println("wrote output to " + file.toString());
		}

		Set<String> unique = new HashSet<String>(posToWordMap.values());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println("Printing word count per POS Category...");
		for (String key : posToWordMap.keySet()) {
			Collection<String> vals = posToWordMap.get(key);

			Set<String> unique1 = new HashSet<String>(vals);
			// System.out.println(key + " : " + vals.toString());
			System.out.println(key + "  : " + unique1.size());
		}
		System.out.println("Total pos tag types : " + posToWordMap.keySet().size());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println(
				"Total ambiguous words/(words with multiple pos) :  " + posToWordMap.get(pos_ambiguous_words).size());

	}

	/**
	 * This file will generate POS files from data folders. It is assumed that
	 * the data folder is CONLL2009.
	 * 
	 * @throws IOException
	 */
	public void generatePOSFilesFromData(String inputFolder, String outputFolder) throws IOException {

		Format format = Format.HIT;

		generateWordRelatedInfo(inputFolder, format, outputFolder);
	}

	/**
	 * Generates POS and word information
	 * 
	 * @throws IOException
	 * 
	 */
	public void generateWordRelatedInfo(String dataFolder, Format format, String outputFolder) throws IOException {
		// String dataFolder = "data/conll2009-chinese-srl/hit/";

		ArrayList<CoNLLSentence> data = new ArrayList<CoNLLSentence>();
		Multimap<String, String> wordToPOSMap = LinkedListMultimap.create();
		Multimap<String, String> posToWordMap = LinkedListMultimap.create();

		ArrayList<CoNLLSentence> badCases = new ArrayList<CoNLLSentence>();

		// read conll format data recursively
		data = dfc.readCoNLLFormatCorpus(dataFolder, format);

		// iterate over all sentences and add lists columnwise
		for (CoNLLSentence coNLLSentence : data) {
			ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

			if (!(coNLLSentence.getDataList().size() > 2)) {
				listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);
				coNLLSentence.setDataList(listOfLists);

				if (listOfLists.size() > 0) {
					ArrayList<String> form_list = listOfLists.get(conllformat_form_column_no);
					ArrayList<String> pos_list = listOfLists.get(conllformat_pos_column_no);

					String firstToken = form_list.get(0);
					if (firstToken.trim().equals(",") || firstToken.trim().equals("，")) {
						badCases.add(coNLLSentence);
					}

					for (int i = 0; i < form_list.size(); i++) {

						String form = form_list.get(i);
						String pos = pos_list.get(i);

						boolean exists = wordToPOSMap.get(form).contains(pos);

						if (!exists && !Strings.isNullOrEmpty(form) && !Strings.isNullOrEmpty(pos)) {
							wordToPOSMap.put(form, pos);
						}

						exists = posToWordMap.get(pos).contains(form);
						if (!exists && !Strings.isNullOrEmpty(form) && !Strings.isNullOrEmpty(pos)) {
							posToWordMap.put(pos, form);
						}
					}

				}

			}

		}

		// Now we want to create ambiugous words , words which has more than one
		// POS detected by Tagger
		Set<String> ambiguous_words = new HashSet<String>();

		// test
		// Collection<String> t = posToWordMap.get("bl");

		for (String key : wordToPOSMap.keySet()) {
			Collection<String> vals = wordToPOSMap.get(key);
			if (vals.size() > 1) {
				ambiguous_words.add(key);
			}
			System.out.println(key + " : " + vals.toString());
		}

		for (String key : posToWordMap.keySet()) {
			Collection<String> vals = posToWordMap.get(key);
			// System.out.println(key + " : " + vals.toString());
			System.out.println(key);
		}

		// System.out.println(posToWordMap.keys());
		int temp = 0;

		// Let's empty the directory first so that there are no old files from
		// previous run
		if (!Strings.isNullOrEmpty(outputFolder)) {
			File outputDir = new File(outputFolder);
			FileUtils.cleanDirectory(outputDir);
		}

		for (String key : posToWordMap.keySet()) {
			Collection<String> vals = posToWordMap.get(key);

			StringBuilder sb = new StringBuilder();

			for (String string : vals) {

				// we don't want to include ambiguous words in the pos list. So
				// we check for it here.
				if (!ambiguous_words.contains(string)) {
					sb.append(string + "\n");
				}

			}

			// At this point, if the sb.ToString() is empty, it means that the
			// all the words in this pos list are ambiguous
			// and they will make their way in pos_ambiguous_words.txt

			String f = outputFolder + File.separator + key + ".txt";
			File file = new File(f);
			FileUtils.write(file, sb.toString(), "UTF-8");

		}

		String f = outputFolder + File.separator + pos_ambiguous_words + ".txt";
		File file = new File(f);

		StringBuilder sb1 = new StringBuilder();
		for (String string : ambiguous_words) {
			sb1.append(string + "\n");
		}
		FileUtils.write(file, sb1.toString(), "UTF-8");

		System.out.println("sentences beginning with comma : " + badCases.size());
		for (CoNLLSentence coNLLSentence : badCases) {
			System.out.println(coNLLSentence.getSentence());
		}

		Set<String> unique = new HashSet<String>(wordToPOSMap.keySet());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println("Printing word count per POS Category...");
		for (String key : posToWordMap.keySet()) {
			Collection<String> vals = posToWordMap.get(key);

			Set<String> unique1 = new HashSet<String>(vals);
			// System.out.println(key + " : " + vals.toString());
			System.out.println(key + "  : " + unique1.size());
		}
		System.out.println("Total pos tag types : " + posToWordMap.keySet().size());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println("Total ambiguous words/(words with multiple pos) :  " + ambiguous_words.size());
		System.out.println("Total sentences in corpus:  " + data.size());
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		WordInfoGenerator wfig = new WordInfoGenerator();

		// This file will generate POS files from data folders. It is assumed
		// that the data folder is conll.

		// String dataFolder =
		// "resources/emotibot-srl/srl_training_data/singlefile/";
		// String outputFolder = "data/wordinfo/emotibot/pos";

		// String dataFolder = "data/wordinfo/nlp/data";
		// String outputFolder = "data/wordinfo/nlp/pos";

		String dataFolder = "data/conll2009-chinese-srl/hit";
		String outputFolder = "data/conll2009-chinese-srl/wordinfo/pos";
		String sentencesFolder = "data/conll2009-chinese-srl/sentences";

		// wfig.generatePOSFilesFromData(dataFolder,outputFolder);
		//wfig.writeSentences(dataFolder, sentencesFolder);
		
		wfig.extractDependencies();
		
		
		

		// List<String> args1 = new ArrayList<>();
		// args1.add("data/wordinfo/concatlists/input/hanlp_pos");
		// args1.add("data/wordinfo/concatlists/input/nlp_team_pos");
		//
		// String outputFolder1= "data/wordinfo/concatlists/output/posList";
		// wfig.concatPOSFiles(args1, outputFolder1);
	}

	/**
	 * This function will write sentences to file
	 * 
	 * @param dataFolder
	 * @param sentencesFolder
	 * @throws IOException
	 */
	private void writeSentences(String dataFolder, String sentencesFolder) throws IOException {
		// TODO Auto-generated method stub
		Format format = Format.HIT;
		DataWriter dataWriter = new DataWriter();

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

		// empty the output folder
		if (!Strings.isNullOrEmpty(sentencesFolder)) {
			File sentencesFolderDir = new File(sentencesFolder);
			FileUtils.cleanDirectory(sentencesFolderDir);
		}

		// iterate through all the files
		for (File file : files) {

			StringBuilder sb = new StringBuilder();
			ArrayList<CoNLLSentence> conLLSentenceList = dfc.readCoNLLFormatCorpus(file, format,true);
			for (CoNLLSentence coNLLSentence : conLLSentenceList) {
				sb.append(coNLLSentence.getSentence());
				sb.append("\n");
			}

			String f1 = sentencesFolder + File.separator + file.getName();
			File f = new File(f1);
			FileUtils.writeStringToFile(f, sb.toString(), "UTF-8");
		}
	}

}
