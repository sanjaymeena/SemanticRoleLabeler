package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.emotibot.enlp.EWord;
import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.SegmentResult;
import com.emotibot.srl.format.TagConverter;
import com.google.common.base.Strings;

import mate.is2.data.SentenceData09;
import mate.is2.parser.Parser;
import mate.is2.util.OptionsSuper;

/**
 * This class make use of NLP word segmentation , NLP pos, SRL dependency Parser
 * to provide dependency Parsing results
 * 
 * @author Sanjay
 *
 */
public class SRLService {

	private static SRLService mateInstance;
	private static final String SRL_DEPENDENCY_TAGGER_MODEL = "resources/emotibot-srl/models/test/ltp_dependency_parser_test.mdl";
	private static final String posMappingFile = "resources/nlp/data/posmap/enlp2ltp.properties";
	private Parser dependencyParser;
	
	private TagConverter tc;

	/**
	 * 
	 */
	private void loadMateDependencyParser() {
		OptionsSuper options = new OptionsSuper();
		options.modelName = SRL_DEPENDENCY_TAGGER_MODEL;

		System.out.println("Loading srl-mate dependency parser information..");
		dependencyParser = new Parser(options);
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public synchronized static SRLService getInstance() throws IOException {
		if (mateInstance == null) {
			mateInstance = new SRLService();
			return mateInstance;
		} else {
			return mateInstance;
		}
	}

	public SRLService() throws IOException {
		//loadMateDependencyParser();
		tc = new TagConverter();
	}

	/**
	 * 
	 */
	private void testDependencyParser() {
		String f = "test/sentences.txt";
		File file = new File(f);

	}

	/**
	 * @throws Exception
	 * 
	 */
	public SentenceData09 getDependencyParse(String text) throws Exception {
		SentenceData09 instance = new SentenceData09();

		if (text == null) {
			instance = null;
		}

		else if (!text.equals("") && text.length() != 0) {
			instance = getNLPSegmentation(text);
			instance = dependencyParser.apply(instance);
		}

		return instance;

	}

	/**
	 * Get dependency Parser information from mate tools.
	 * 
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public SentenceData09 getDependencyParse(SentenceData09 instance) throws Exception {

		if (instance != null && instance.forms != null) {
			instance = dependencyParser.apply(instance);
		}

		return instance;

	}

	/**
	 * Given sentence, this function provides the Conll2009 format data with POS
	 * and Word segmentation information
	 * 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public SentenceData09 getNLPSegmentation(String text) throws Exception {
		return getNLPSegmentation(text, null);
	}
	
	public SentenceData09 getNLPSegmentation(String text, String fid) throws Exception {
		
		SentenceData09 instance = new SentenceData09();

		if (text == null) {
			instance = null;
		}

		else if (!text.equals("") && text.length() > 0) {
			SegmentResult segmentResult;
			if (fid == null) {
				segmentResult = NLPService.getWords(text);
			} else {
				segmentResult = NLPService.getWords(fid, text);
			}

			int textLength = segmentResult.getWordNameList().size();
			
			String[] forms = new String[textLength];
			String[] ppos = new String[textLength];

			for (int i = 0; i < segmentResult.wordList.size(); i++) {
				EWord eWord = segmentResult.wordList.get(i);
				String word = eWord.word;
				String pos = eWord.nature.toString();

				forms[i] = word;
				ppos[i] = pos;
			}

			instance.init(forms);

			// convert hanlp tagset to ltp tagset

			instance.ppos = tc.convertHanLPToLTP(ppos);
			instance.createSemantic(instance);
		}

		return instance;
		
	}
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// Interface 1
		SentenceData09 instance = SRLService.getInstance().getDependencyParse("");

		// cases;
		// 1. String is null
		// String text=null;
		// instance=SRLService.getInstance().getDependencyParse(text);
		// System.out.println(instance);
		//
		// // 2. String is empty
		// text="";
		// instance=SRLService.getInstance().getDependencyParse(text);
		// System.out.println(instance);
		//
		// // 3. String is non empty
		// text="\t\n\t";
		// instance=SRLService.getInstance().getDependencyParse(text);
		// System.out.println(instance);
		//
		//
		// // Interface 2
		// instance = SRLService.getInstance().getNLPSegmentation("");
		// instance = SRLService.getInstance().getDependencyParse(instance);
		// System.out.println(instance);

		// calling using code in nlp service
		instance = SRLService.getInstance().getNLPSegmentation("唯品国际商品为什么没有防伪溯源码", null);
		instance = NLPService.getDependencyParse(instance);
		System.out.println(instance);
		
	}

}
