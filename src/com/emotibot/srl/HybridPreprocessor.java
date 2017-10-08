package com.emotibot.srl;

import java.util.Arrays;

import com.emotibot.enlp.EWord;
import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.SegmentResult;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.TagConverter;

import mate.is2.data.SentenceData09;
import mate.is2.parser.Parser;
import mate.is2.tag.Tagger;
import mate.is2.util.OptionsSuper;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class HybridPreprocessor {

	private volatile static HybridPreprocessor hybridPreprocessorInstance;
	
	static String SRL_POS_TAGGER_MODEL = "resources/emotibot-srl/models/test/ltp_pos_tagger.mdl";
	static String SRL_DEPENDENCY_TAGGER_MODEL = "resources/emotibot-srl/models/test/ltp_dependency_parser_test.mdl";
	
	protected Tagger posTagger;
	protected Parser dependencyParser;
	protected TagConverter tc;
	protected DataFormatConverter dfc;
	
	static int count_NLP_getWord_tagger = 0;
	static int count_NLP_getNature_tagger = 0;
	static int count_SRL_tagger = 0;
	
	public static void main(String[] args) {
		
	}
	
	public static void resetCounter() {
		count_NLP_getWord_tagger = 0;
		count_NLP_getNature_tagger = 0;
		count_SRL_tagger = 0;
	}
	
	public static void printCounter() {
		System.out.println("count_NLP_getWord_tagger: " + count_NLP_getWord_tagger);
		System.out.println("count_NLP_getNature_tagger: " + count_NLP_getNature_tagger);
		System.out.println("count_SRL_tagger: " + count_SRL_tagger);
	}
	
	public synchronized static HybridPreprocessor getHybridPreprocessor() {
		if (hybridPreprocessorInstance == null) {
			hybridPreprocessorInstance = new HybridPreprocessor();
		}
		return hybridPreprocessorInstance;
	}
	
	
	public HybridPreprocessor() {
		System.out.println("Initialize Hybrid Preprocessor");
		initializeModels();
		
		dfc = new DataFormatConverter();
	}

	public void initializeModels() {
		try {
			tc = new TagConverter();
		} catch (Exception e) {
			e.printStackTrace();
		}

		OptionsSuper options = new OptionsSuper();

		posTagger = new Tagger();
		options.modelName = SRL_POS_TAGGER_MODEL;
		posTagger.readModel(options);
		

		options = new OptionsSuper();
		options.modelName = SRL_DEPENDENCY_TAGGER_MODEL;

		dependencyParser = new Parser(options);

	}
	
	public SentenceData09 preprocess(String[] forms) {
		SentenceData09 instance = new SentenceData09();
		instance.init(forms);

		// Fill lemma column for SRL prediction 
		instance.plemmas = instance.forms;
		
		instance = tagPOS(instance);
		instance = tagDEP(instance);

		return instance;
	}
	
	public SentenceData09 preprocess(String[] forms, String[] posArray) {
		SentenceData09 instance = new SentenceData09();
		instance.init(forms);

		// Fill lemma column for SRL prediction 
		instance.plemmas = instance.forms;
		
		instance = tagPOS(instance, posArray);
		instance = tagDEP(instance);

		return instance;
	}

	private boolean checkSegmentationConsistency(String[] tokens) {
		String[] new_tokens = new EmotibotTokenizer().tokenize(String.join("", tokens));
		if (Arrays.deepEquals(tokens, new_tokens)) {
			return true;
		} else {
			return false;
		}
	}
	
	private SentenceData09 tagPOS(SentenceData09 instance, String[] posArray) {
		try {
			instance.ppos = tc.convertHanLPToLTP(posArray);
			instance.createSemantic(instance);
		} catch (Exception e) {
			e.printStackTrace();
			instance = posTagger.apply(instance);
			count_SRL_tagger ++;
			// System.out.println("NLP's pos tagger is wrong. Try ours.");
		}
		return instance;
	}

	private SentenceData09 tagPOS(SentenceData09 instance) {
		
		String appid = "";
		
		SegmentResult segmentResult = NLPService.getWords(String.join("", instance.forms));
		int textLength = segmentResult.getWordNameList().size();
		String[] pTokens = new String[textLength];
		String[] ppos = new String[textLength];
		
		for (int i = 0; i < segmentResult.wordList.size(); i++) {
			EWord eWord = segmentResult.wordList.get(i);
			pTokens[i] = eWord.word;
			String pos = eWord.nature.toString();
			ppos[i] = pos;
		}
		
		try {
			// Same tokenization. Use NLP's pos tagger.
			if (Arrays.deepEquals(instance.forms, pTokens)) {
				instance.ppos = tc.convertHanLPToLTP(ppos);
				count_NLP_getWord_tagger ++;
			// Different tokenization
			} else {
				String[] pposNature = NLPService.getNature(appid, instance.forms);
				instance.ppos = tc.convertHanLPToLTP(pposNature);
				count_NLP_getNature_tagger ++;
			}
			instance.createSemantic(instance);

		} catch (Exception e) {

			e.printStackTrace();
			String[][] feats = instance.feats;
			instance = posTagger.apply(instance);
			instance.feats = feats;
			count_SRL_tagger ++;
			// System.out.println("NLP's pos tagger is wrong. Try ours.");
		}

		
		return instance;
	}
	
	/**
	 * 
	 * @param instance
	 *            the instance to process
	 * @return the same object as it was passed, but with some arrays filled out
	 */
	private SentenceData09 tagDEP(SentenceData09 instance) {


		instance.pfeats = new String[instance.forms.length];
		instance.ofeats = new String[instance.forms.length];
		Arrays.fill(instance.pfeats, "_");
		Arrays.fill(instance.ofeats, "_");

		// if tokens length is greater than 150, just return dummy
		int token_length = instance.feats.length;
		boolean too_long = false;
		if (token_length > 150) {
			too_long = true;
		}
		
		if (dependencyParser != null && !too_long) {
			synchronized (dependencyParser) {

				instance = dependencyParser.apply(instance);

			}
		} else { // If there is no parser, we have to recreate the sentence
			// object so the root dummy gets thrown out (as in the
			// parser)
			instance = new SentenceData09(instance);
		}
		return instance;
	}
}
