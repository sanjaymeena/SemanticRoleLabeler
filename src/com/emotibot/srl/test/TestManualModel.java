/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.server.SRLParserHelper;
import com.emotibot.srl.tmr.datastructure.SRL;

/**
 * @author Sanjay
 *
 */
public class TestManualModel {
	HashMap<String, CoNLLSentence> badcase_map = new HashMap<String, CoNLLSentence>();
	SRLParserHelper srl_parser_helper = new SRLParserHelper();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TestManualModel tm = new TestManualModel();

		tm.test();
		// tm.test_with_file();
	}

	private String getSRLforBadCase(CoNLLSentence coNLLSentence) {

		String doc = coNLLSentence.getSentence();
		String conll = coNLLSentence.getCoNLLSentence();

		SRL srl = srl_parser_helper.createSRLOutput(doc, conll);
		System.out.println(srl.toString());

		// String json = helper.createSRLJsonOutput(doc, conll);
		// System.out.println(json);

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean checkBadCase(String sentence) {
		boolean badCase = false;

		if (badcase_map.containsKey(sentence)) {
			badCase = true;
		}

		return badCase;
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void test() throws IOException {

		
		
		//recurse over all files in the directory
//		String path1 = "data/manualmode/hit";
//		test(path1,Format.HIT,true);
		
		String path1 = "data/manualmode/hit/manual_mode_cases.txt";
		test(path1,Format.HIT,false);
		
		//load only one given file. set recursive variable to false;
//		String path2 = "data/manualmode/test_conll.txt";
//		test(path2,Format.CONLL,false);
		

	}

	/**
	 * 
	 * @param path 
	 * @param recursive whether to recurse across directories
	 * @throws IOException
	 */
	private void test(String path,Format format , boolean recursive) throws IOException {

		
		// TODO Auto-generated method stub
		SRLParserHelper helper = new SRLParserHelper();
		// String path = "ST-Chinese-trial.txt";

		ArrayList<CoNLLSentence> conLLSentenceList = new ArrayList<CoNLLSentence>();
		Collection<File> files=new ArrayList<>();
		
		
		File rootDir = new File(path);
		if(recursive){
			final String[] SUFFIX = { "txt" };
			files = FileUtils.listFiles(rootDir, SUFFIX, true);
		}
		else{
			files.add(rootDir);
		}
	

		DataFormatConverter dfc = new DataFormatConverter();

		// iterate through all the files and create the list of bad cases
		for (File file : files) {
			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, format, true);
			conLLSentenceList.addAll(sentenceList);
		}

		switch (format) {
		case HIT:
			for (CoNLLSentence coNLLSentence : conLLSentenceList) {
				dfc.convertHITtoCONLL2009(coNLLSentence);
			}

			break;
		case CONLL:

			break;

		default:
			break;
		}

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {
			dfc.generateSentenceFromCoNLLData(coNLLSentence);
		}

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			String doc = coNLLSentence.getSentence();
			String conll = coNLLSentence.getCoNLLSentence();

			SRL srl = helper.createSRLOutput(doc, conll);
			System.out.println(srl.toString());

			// String json = helper.createSRLJsonOutput(doc, conll);
			// System.out.println(json);

			// SRLParserHelper
			// System.out.println(srl.getOutput_srl_table_format());

		}

	}

}
