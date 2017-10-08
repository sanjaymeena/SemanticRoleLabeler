/**
 * 
 */
package com.emotibot.srl.test.frames.conversations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

/**
 * @author Sanjay
 *
 */
public class CRFClassifierData {
	EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();

	/**
	 * 
	 * @param file
	 * @param outputFile
	 * @throws IOException
	 */
	public void createCRFClassifierData(String file, String outputFile) throws IOException {
		File f = new File(file);

		List<String> contents = FileUtils.readLines(f, "UTF-8");

		StringBuilder outputBuilder = new StringBuilder();
		for (String string : contents) {

			String input = string;
			if (!Strings.isNullOrEmpty(input)) {
				String[] tokens = emotibotTokenzier.tokenize(input);
				for (String string2 : tokens) {
					outputBuilder.append(string2 + Constants.delimiter + Constants.emptyNER);
					outputBuilder.append(Constants.newline);
				}
				// after every new sentence end, append new line
				outputBuilder.append(Constants.newline);
			}

		}

		System.out.println(outputBuilder.toString());
		File out = new File(outputFile);
		FileUtils.write(out, outputBuilder.toString(), "UTF-8");
	}

	/**
	 * 
	 * @param file
	 * @param outputFile
	 * @throws IOException
	 */
	public String createCRFClassifierData(List<String> contents) throws IOException {

		StringBuilder outputBuilder = new StringBuilder();
		for (String string : contents) {

			String input = string;
			if (!Strings.isNullOrEmpty(input)) {
				String[] tokens = emotibotTokenzier.tokenize(input);
				for (String string2 : tokens) {
					outputBuilder.append(string2 + Constants.delimiter + Constants.emptyNER);
					outputBuilder.append(Constants.newline);
				}
				// after every new sentence end, append new line
				outputBuilder.append(Constants.newline);
			}

		}

		// System.out.println(outputBuilder.toString());
		return outputBuilder.toString();
		// File out=new File(outputFile);
		// FileUtils.write(out, outputBuilder.toString(),"UTF-8");
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		CRFClassifierData crfData = new CRFClassifierData();

		// perform all the necessary steps
		crfData.performSteps();

	}

	private void performSteps() throws IOException {

		String file = "data/temp/frames_data/conversations/sentence_data/data_0329/cosmetic_ne_0329_color.txt";
		String outputFile = "data/temp/frames_data/conversations/sentence_data/data_0329/cosmetic_ne_0329_color_crf.txt";

		// create CRF Format training data
		createCRFClassifierData(file, outputFile);

		
	}

}
