/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.emotibot.enlp.EWord;
import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.SegmentResult;
import com.google.common.base.Strings;

import mate.is2.data.SentenceData09;
import mate.is2.lemmatizer.Lemmatizer;
import mate.is2.parser.Parser;
import mate.is2.tag.Tagger;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;

/**
 * Test toolkits for difference in pos and dependency for mate tools and
 * stanford
 * 
 * @author Sanjay
 *
 */
public class TestToolKits {

	// SRL preprocessor
	Preprocessor pp;

	/**
	 * @throws IOException
	 * 
	 */
	public void test() throws IOException {

		// load srl preprocessor 
		loadPreprocessor();
		
		
		//make sure to test run tokenizer to initialize it
		getTokens("他就好好珍惜他吧。");
		
		
		//initialize stanford dep by NLP team so that initialization is not considered during timging
		String sentence = "我喜欢你";
		String result = NLPService.getDependencyTree(NLPService.parse(sentence)).toString();
		
		
		
		StringBuilder sb=new StringBuilder();
		
		String data = "data/temp/random_sentences.txt";
		File f = new File(data);
		final List<String> lines = FileUtils.readLines(f, Charset.defaultCharset());
		long totalTime = 0;
		double tps = 0;

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		System.out.println("Running mate dep + Pos using SRL now..");
		for (String line : lines) {

			// process mate tools
			if(!Strings.isNullOrEmpty(line)){
				String[] tokens = getTokens(line);
				
				SentenceData09 res = pp.preprocess(tokens);
				//System.out.println(res.toString());
				
			}
			

			// process dependency parser models
		}

		stopWatch.stop();

		// time in milliseconds
		totalTime = stopWatch.getTime();

		double tps1 = ((double) totalTime) / (lines.size() * 1000);
		// double tps = ((double) lines.size()) / (totalTime * 1000);

		
		sb.append("Total sentences for testing: " + lines.size()+"\n");
		sb.append("Results for pos+dep from mate tools.This involves nlp tokenization +  (pos + dependency models created using mate tools : " +"\n");
		sb.append(
				"Total time taken for " + lines.size() + " sentences" + " : " + stopWatch.toString() + " "+"\n");
		sb.append("Total time taken per sentence : " + tps1 + " seconds"+"\n");

		
		
		
		
		System.out.println("Running stanford dep + Pos using NLP package now..");
		stopWatch = new StopWatch();
		stopWatch.start();

		for (String line : lines) {

			// process mate tools
			if(!Strings.isNullOrEmpty(line)){
				//String[] tokens = getTokens(line);
				
				//SentenceData09 res = pp.preprocess(tokens);
				//System.out.println(res.toString());
				 String res = NLPService.getDependencyTree(NLPService.parse(line)).toString();
				 //System.out.println(res);

			}
			

			// process dependency parser models
		}

		stopWatch.stop();

		// time in milliseconds
		totalTime = stopWatch.getTime();

		double tps2 = ((double) totalTime) / (lines.size() * 1000);
		// double tps = ((double) lines.size()) / (totalTime * 1000);

		
		sb.append("\n");
		sb.append("\n");
		
		
		sb.append("Results for pos+dep from NLP team using stanford dependencies. This is called through NLP package : " +"\n");
		sb.append(
				"Total time taken for " + lines.size() + " sentences" + " : " + stopWatch.toString() + " "+"\n");
		sb.append("Total time taken per sentence : " + tps2+ " seconds"+"\n");


		
		
		
		double diff = tps2/tps1;
		
		sb.append("\n" + "Result : "+"\n");
		sb.append("Mate tools is  : " + diff + " times faster than stanford based tools for pos+dep "+"\n");
	
		System.out.println(sb.toString());
	}

	/**
	 * 
	 */
	public void loadPreprocessor() {

		File mate_pos_model_file = new File("resources/emotibot-srl/models/test/ltp_pos_tagger.mdl");
		File mate_dep_model_file = new File("resources/emotibot-srl/models/test/ltp_dependency_parser_test.mdl");

		Tokenizer tokenizer = null;
		Lemmatizer lemmatizer = null;
		Tagger tagger = BohnetHelper.getTagger(mate_pos_model_file);
		mate.is2.mtag.Tagger mtagger = null;
		Parser parser = BohnetHelper.getParser(mate_dep_model_file);
		pp = new Preprocessor(tokenizer, lemmatizer, tagger, mtagger, parser);
	}

	public String[] getTokens(String text) {
		SegmentResult segmentResult = NLPService.getWords(text);
		ArrayList<String> tokensList = new ArrayList<String>();

		for (EWord w : segmentResult.wordList) {
			// sometimes there were empty strings.
			// String token = w.word.trim();
//			if (!token.equals("")) {
			tokensList.add(w.word);
//			}

		}
		String[] tokens = tokensList.toArray(new String[tokensList.size()]);
		// for (String string : tokens) {
		// System.out.print(string + " ");
		// }

		return tokens;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TestToolKits ttk = new TestToolKits();
		ttk.test();
	}

}
