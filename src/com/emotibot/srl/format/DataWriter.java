/**
 * 
 */
package com.emotibot.srl.format;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.google.common.base.Strings;

/**
 * @author Sanjay
 *
 */
public class DataWriter {

	/**
	 * 
	 * This function creates the HIT format from input sentence corpus
	 * 
	 * @param datapath
	 * @throws Exception
	 */
	public void writeText(ArrayList<CoNLLSentence> conLLSentenceList, String outputpath) throws Exception {

		int counter = 0;
		// Lets produce the output now
		StringBuilder sb = new StringBuilder();
		List<String> emptySRLSentences = new ArrayList<String>();

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			counter++;
			// sb.append("sentence:" + counter + " : " +
			// coNLLSentence.getSentence() + "\n");

			String s = coNLLSentence.getSentence();
			if (!Strings.isNullOrEmpty(s)) {
				sb.append(s);
				sb.append("\n");
			}

			else {
				emptySRLSentences.add(coNLLSentence.getHITSentence());
			}

			// convertCONLLtoHIT(coNLLSentence);
		}

		String data = sb.toString();
		if (!Strings.isNullOrEmpty(data) && data.trim().length() > 0) {
			File file = new File(outputpath);

			String SUFFIX = "txt";
			File directory = new File(String.valueOf(outputpath));
			if (outputpath.contains(SUFFIX)) {
				File parentDir = directory.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			} else {

				if (!directory.exists()) {
					directory.mkdir();
				}

			}

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileUtils.write(file, sb.toString());
			System.out.println("Wrote to file " + file);

		}

	}

	/**
	 * 
	 * This function creates the HIT format from input sentence corpus
	 * 
	 * @param datapath
	 * @throws Exception
	 */
	public void writeSentences(ArrayList<CoNLLSentence> conLLSentenceList, String outputpath) throws Exception {

		int counter = 0;
		// Lets produce the output now
		StringBuilder sb = new StringBuilder();
		List<String> emptySRLSentences = new ArrayList<String>();

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			counter++;
			// sb.append("sentence:" + counter + " : " +
			// coNLLSentence.getSentence() + "\n");

			String s = coNLLSentence.getHITSentence();
			if (!Strings.isNullOrEmpty(s)) {
				sb.append(coNLLSentence.getHITSentence());
				sb.append("\n");
			}

			else {
				emptySRLSentences.add(coNLLSentence.getHITSentence());
			}

			// convertCONLLtoHIT(coNLLSentence);
		}

		String SUFFIX = "txt";
		File directory = new File(String.valueOf(outputpath));
		if (outputpath.contains(SUFFIX)) {
			File parentDir = directory.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdir();
			}
		} else {

			if (!directory.exists()) {
				directory.mkdir();
			}

		}

		String data = sb.toString();
		if (!Strings.isNullOrEmpty(data) && data.trim().length() > 0) {
			File file = new File(outputpath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileUtils.write(file, sb.toString());
			System.out.println("Wrote to file " + file);

		}

	}

	/**
	 * 
	 * This function creates the HIT format from input sentence corpus
	 * 
	 * @param datapath
	 * @throws Exception
	 */
	public void writeHITFormatData(ArrayList<CoNLLSentence> conLLSentenceList, String outputpath) throws Exception {

		int counter = 0;
		// Lets produce the output now
		StringBuilder sb = new StringBuilder();
		List<String> emptySRLSentences = new ArrayList<String>();

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			counter++;
			// sb.append("sentence:" + counter + " : " +
			// coNLLSentence.getSentence() + "\n");

			String s = coNLLSentence.getHITSentence();
			if (!Strings.isNullOrEmpty(s)) {
				sb.append(coNLLSentence.getHITSentence());
				sb.append("\n");
			}

			else {
				emptySRLSentences.add(coNLLSentence.getHITSentence());
			}

			// convertCONLLtoHIT(coNLLSentence);
		}

		String data = sb.toString();
		if (!Strings.isNullOrEmpty(data) && data.trim().length() > 0) {
			File file = new File(outputpath);

			String SUFFIX = "txt";
			File directory = new File(String.valueOf(outputpath));
			if (outputpath.contains(SUFFIX)) {
				File parentDir = directory.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			} else {

				if (!directory.exists()) {
					directory.mkdir();
				}

			}

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileUtils.write(file, sb.toString());
			System.out.println("Wrote to file " + file);

		}

	}

	/**
	 * This function creates the HIT format from input sentence corpus
	 * 
	 * @param conLLSentenceList
	 * @param file_name
	 *            : name of the file
	 * @param outputpath
	 *            : output directory
	 * @param sentence_count
	 *            : sentence per file
	 * @throws Exception
	 */
	public void writeHITFormatData(ArrayList<CoNLLSentence> conLLSentenceList, String file_name, String outputpath,
			int sentence_count) throws Exception {

		int fileCount = 0;
		int corpus_size = 0;

		corpus_size = conLLSentenceList.size();
		if (corpus_size % sentence_count == 0) {
			fileCount = corpus_size / sentence_count;
		} else {
			fileCount = corpus_size / sentence_count + 1;
		}

		if (fileCount == 0) {
			fileCount = 1;
		}
		int begin = 0;
		int end = 0;

		if (sentence_count > corpus_size) {
			end = conLLSentenceList.size();
		} else {
			end = sentence_count;
		}

		List<String> emptySRLSentences = new ArrayList<String>();

		for (int fileNumber = 1; fileNumber <= fileCount; fileNumber++) {

			String filename = outputpath + "/" + file_name + "_" + begin + "_" + end + ".txt";

			File file = new File(filename);

			StringBuilder sb = new StringBuilder();
			for (int i = begin ; i < end; i++) {
				
				CoNLLSentence coNLLSentence = conLLSentenceList.get(i);

				String s = coNLLSentence.getHITSentence();
				if (!Strings.isNullOrEmpty(s)) {
					sb.append(coNLLSentence.getHITSentence());
					sb.append("\n");
				}

				else {
					emptySRLSentences.add(coNLLSentence.getSentence());
				}
			}

			// write to file
			FileUtils.write(file, sb.toString());
			System.out.println("Wrote to file " + file);

			// update the begin and end index;
			begin = end;

			int temp_end = sentence_count + begin;
			if (temp_end > corpus_size) {
				end = corpus_size;
			} else {
				end = sentence_count + begin;
			}

		}

		System.out.println("Sentences with empty srl output: ");
		for (String string : emptySRLSentences) {
			System.out.println(string);
		}

	}

	/**
	 * This function breaks down large sentence corpus to smaller files
	 * 
	 * @param conLLSentenceList
	 * @param file_name
	 *            : name of the file
	 * @param outputpath
	 *            : output directory
	 * @param sentence_count
	 *            : sentence per file
	 * @throws Exception
	 */
	public void breakDownLargeSentenceCorpus(ArrayList<CoNLLSentence> conLLSentenceList, String file_name,
			String outputpath, int sentence_count) throws Exception {

		
		
		int fileCount = 0;
		int corpus_size = 0;

		corpus_size = conLLSentenceList.size();
		if (corpus_size % sentence_count == 0) {
			fileCount = corpus_size / sentence_count;
		} else {
			fileCount = corpus_size / sentence_count + 1;
		}

		if (fileCount == 0) {
			fileCount = 1;
		}
		int begin = 1;
		int end = 0;

		if (sentence_count > corpus_size) {
			end = conLLSentenceList.size();
		} else {
			end = sentence_count;
		}

		List<String> emptySRLSentences = new ArrayList<String>();

		for (int fileNumber = 1; fileNumber <= fileCount; fileNumber++) {

			String filename = outputpath + "/" + file_name + "_" + begin + "_" + end + ".txt";

			File file = new File(filename);

			StringBuilder sb = new StringBuilder();
			for (int i = begin - 1; i < end; i++) {
				// for (int i = 0; i < conLLSentenceList.size(); i++) {
				CoNLLSentence coNLLSentence = conLLSentenceList.get(i);

				String s = coNLLSentence.getSentence();
				if (!Strings.isNullOrEmpty(s)) {
					sb.append(s);
					sb.append("\n");
				}

				else {
					emptySRLSentences.add(coNLLSentence.getSentence());
				}
			}

			// write to file
			FileUtils.write(file, sb.toString());
			System.out.println("Wrote to file " + file);

			// update the begin and end index;
			begin = end;

			if (sentence_count < corpus_size) {

				int temp_end = sentence_count + begin;
				if (temp_end > corpus_size) {
					end = corpus_size;
				} else {
					end = sentence_count + begin;
				}

				// end=conLLSentenceList.size();
			} else {
				end = sentence_count;
			}

		}

		System.out.println("Sentences with empty srl output: ");
		for (String string : emptySRLSentences) {
			System.out.println(string);
		}

	}

}
