package com.emotibot.srl.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.emotibot.srl.format.Constants.*;
import org.apache.commons.lang.StringUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.google.common.base.Strings;

public class DataReader {

	protected BufferedReader in;
	private File file;

	private ArrayList<CoNLLSentence> conLLSentenceList;

	public DataReader(File file) {
		conLLSentenceList = new ArrayList<CoNLLSentence>();
		this.file = file;
	}

	/**
	 * Read text corpus where each sentence is on one line.
	 * 
	 * @return
	 */
	public ArrayList<CoNLLSentence> readTextCorpus() {
		conLLSentenceList = new ArrayList<CoNLLSentence>();

		String delimiter = "~、，。?!";

		System.out.println("Opening reader for " + file + "...");
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			String str;

			// new sentence at every line.
			while ((str = in.readLine()) != null) {

				if (str.trim().equals("")) {
					continue;
				} else {

					// String[] strings = StringUtils.split(str, delimiter);
					// for (String string : strings) {
					// if (!Strings.isNullOrEmpty(string)) {
					// CoNLLSentence sentence = new CoNLLSentence();
					// sentence.setSentence(string);
					// conLLSentenceList.add(sentence);
					// }
					// }

					CoNLLSentence sentence = new CoNLLSentence();
					sentence.setSentence(str);
					conLLSentenceList.add(sentence);
					// break;
				}
			}

		} catch (IOException e) {
			System.out.println("Failed: " + e.toString());
			System.exit(1);
		}

		System.out.println("Finished reading  " + file + "...");
		return conLLSentenceList;
	}

	public ArrayList<CoNLLSentence> readData(Format format) {

		System.out.println("Opening reader for " + file + "...");
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			// in = new BufferedReader(new FileReader(file));
			readSentences(format);
		} catch (IOException e) {
			System.out.println("Failed: " + e.toString());
			System.exit(1);
		}

		return conLLSentenceList;
	}

	protected void readSentences(Format format) throws IOException {
		String str;

		StringBuilder senBuffer = new StringBuilder();
		ArrayList<String> list = new ArrayList<String>();
		while ((str = in.readLine()) != null) {

			if (!str.trim().equals("") && !str.trim().equals("\n")) {
				senBuffer.append(str).append("\n");
			} else {

				StringBuilder trimmedSentenceBuffer = new StringBuilder();
				if (!Strings.isNullOrEmpty(senBuffer.toString())) {
					String[] lines = (NEWLINE_PATTERN.split(senBuffer.toString()));
					for (String string : lines) {
						string = StringUtils.trim(string);
						trimmedSentenceBuffer.append(string).append("\n");
						list.add(string);
					}
	
					lines = list.toArray(new String[list.size()]);
					list = new ArrayList<String>();
	
					CoNLLSentence sentence = new CoNLLSentence();
	
					if (lines != null && lines.length > 0) {
						sentence.setLines(lines);
	
						sentence.setSource(file.getAbsolutePath());
	
						switch (format) {
						case CONLL:
							sentence.setCoNLLSentence(trimmedSentenceBuffer.toString());
							break;
	
						case HIT:
							sentence.setHITSentence(trimmedSentenceBuffer.toString());
							break;
	
						default:
							break;
						}
	
						conLLSentenceList.add(sentence);
	
						senBuffer = new StringBuilder();
						// break;
					}
				}
			}
		}
		
		if (senBuffer.length() != 0) {
			System.out.println("Illegal EOF of CoNLL Format" +  file.getPath());
			System.exit(1);
		}

	}

}
