package se.lth.cs.srl;

import static com.emotibot.srl.server.Constants.CHINESE_DEPENDENCY_PARSER;
import static com.emotibot.srl.server.Constants.CHINESE_LEMMATIZER;
import static com.emotibot.srl.server.Constants.CHINESE_SRL_MODEL_1;
import static com.emotibot.srl.server.Constants.CHINESE_SRL_MODEL_2;
import static com.emotibot.srl.server.Constants.POS_TAGGER_TEST;
import static com.emotibot.srl.server.Constants.SRL_BAD_CASES_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.HybridPreprocessor;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.server.SRLParserHelper;
import com.google.common.base.Strings;

import mate.is2.data.SentenceData09;
import mate.is2.lemmatizer.Lemmatizer;
import mate.is2.parser.Parser;
import mate.is2.tag.Tagger;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.http.whatswrongglue.WhatsWrongHelper;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.languages.Language.L;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.pipeline.Pipeline;
import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.pipeline.Step;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.SimpleChineseLemmatizer;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;

public class SemanticLabelerPipeLine {

	private SRLParserHelper srlhelper = new SRLParserHelper();
	private volatile static SemanticLabelerPipeLine englishInstance;
	private volatile static SemanticLabelerPipeLine chineseInstance;
	private CompletePipeline englishPipeline;
	private CompletePipeline chinesePipleLine;

	HashMap<String, CoNLLSentence> badcases_srl_map = new HashMap<String, CoNLLSentence>();

	public SemanticLabelerPipeLine() throws ZipException, ClassNotFoundException, IOException {

		CompletePipelineCMDLineOptions options = createOptionsForEnglish();
		englishPipeline = getCompletePipeline(options, L.eng);

	}

	/**
	 * 
	 * @param l
	 * @param options2
	 *            Load only SRL models and not the pre processor models
	 * @throws ZipException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public SemanticLabelerPipeLine(L l, SRLOptions srlOptions)
			throws ZipException, ClassNotFoundException, IOException {
		switch (l) {
		case eng:
			CompletePipelineCMDLineOptions options = createOptionsForEnglish();
			englishPipeline = getCompletePipeline(options, L.eng);
			break;

		case chi:
			loadBadCasesSRL();
			CompletePipelineCMDLineOptions options1 = createOptionsForChinese();

			if (srlOptions != null && (srlOptions.useDEPFromNLP || srlOptions.usePOSFromNLP)) {

				chinesePipleLine = getPartialPipeline(srlOptions, options1, L.chi);
			} else {
				chinesePipleLine = getCompletePipeline(options1, L.chi);
			}

			break;
		default:
			break;
		}

	}

	/**
	 * This function will load bad cases for SRL . We will use this function
	 * when we want to provide results using manually annotated SRL rather than
	 * using trained model. Current implementation is only
	 */
	private void loadBadCasesSRL() throws IOException {
		// TODO Auto-generated method stub

		System.out.println("Loading bad cases from directory: " + SRL_BAD_CASES_DIRECTORY);
		DataFormatConverter dfc;
		dfc = new DataFormatConverter();
		ArrayList<CoNLLSentence> conLLSentenceList = new ArrayList<CoNLLSentence>();

		String path = SRL_BAD_CASES_DIRECTORY;
		File rootDir = new File(path);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		// iterate through all the files and create the list of bad cases
		for (File file : files) {
			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.CONLL, true);
			conLLSentenceList.addAll(sentenceList);
		}

		// generate sentence from CONLL data format. we essentially append
		// the tokens together
		for (CoNLLSentence coNLLSentence : conLLSentenceList) {
			dfc.generateSentenceFromCoNLLData(coNLLSentence);
		}

		// iterate through the conll sentence list and add to the bad cases file
		// map.
		// Too many logs!
		int count = 30;
		System.out.printf("Show only the first %d sentences for manual mode\n", count);
		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			String doc = coNLLSentence.getSentence();

			// put sentences into map
			if (!Strings.isNullOrEmpty(doc)) {
				if (count > 0) {
					System.out.println(doc);
					count--;
				}
				badcases_srl_map.put(doc, coNLLSentence);
			}

		}
		System.out.printf("Load %d sentences for manual mode\n", badcases_srl_map.size());

	}

	public synchronized static SemanticLabelerPipeLine getEnglishInstance() {
		if (englishInstance == null) {
			try {
				englishInstance = new SemanticLabelerPipeLine(L.eng, null);
				return englishInstance;
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return englishInstance;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public synchronized static SemanticLabelerPipeLine getChineseInstance() {
		if (chineseInstance == null) {
			try {

				synchronized (SemanticLabelerPipeLine.class) {
					if (chineseInstance == null) {
						System.out.println("Creating new instance for wordnet..");
						chineseInstance = new SemanticLabelerPipeLine(L.chi, null);

					}
				}

			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return chineseInstance;

	}

	/**
	 * 
	 * @return
	 */
	public synchronized static SemanticLabelerPipeLine getChineseInstance(SRLOptions options) {

		if (!options.useDEPFromNLP && !options.usePOSFromNLP) {
			chineseInstance = getChineseInstance();
		}

		// here we are going to use the preprocessor which srl has put in nlp
		// package. So we only need to load SRL model
		else {
			if (chineseInstance == null) {
				try {

					synchronized (SemanticLabelerPipeLine.class) {
						if (chineseInstance == null) {
							System.out.println(
									"Creating new instance for chinese SRL its modules shared in nlp package..");
							chineseInstance = new SemanticLabelerPipeLine(L.chi, options);

						}
					}

				} catch (ZipException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return chineseInstance;

	}

	/**
	 * Returns the image encoded in the form of a string.
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static String getDependencyParserImageString(Sentence s) throws IOException {

		String imageString = WhatsWrongHelper.getDependencyParserImageString(s);
		return imageString;
	}

	/**
	 * The output is the sentence datastructure after parsing
	 * 
	 * @param inputSentence
	 * @return
	 * @throws Exception
	 */
	public Sentence parseSRLForChinese(String inputSentence, SRLOptions options) throws Exception {
		Sentence s = chinesePipleLine.parse(inputSentence, options.model);
		return s;
	}

	/**
	 * 
	 * 
	 * @param inputSentence
	 * @return
	 * @throws Exception
	 */
	public SRLJsonDataStructure performSRLForChinese(String inputSentence, SRLOptions options) throws Exception {
		Sentence s = chinesePipleLine.parse(inputSentence, options);

		CoNLLSentence conllSentence = new CoNLLSentence();
		conllSentence.setProcessedSentence(inputSentence);

		conllSentence.setCoNLLSentence(s.toString());
		SRLJsonDataStructure json_ds = srlhelper.createSRLJsonDataStructure(conllSentence, options);
		// set sentence datastructure
		json_ds.setSentenceDS(s);

		// System.out.println(result);
		return json_ds;
	}

	/**
	 * 
	 * 
	 * @param inputSentence
	 * @return
	 * @throws Exception
	 *             Author: LCR This is combination of performSRLForChineseJSON
	 *             and performSemanticRoleLabelingForChinese1 A workaround to
	 *             provide manual mode for visualization tool
	 */
	public SRLJsonDataStructure performSRLForChineseForSRLVIZ(String inputSentence, SRLOptions options)
			throws Exception {

		CoNLLSentence conllSentence = new CoNLLSentence();
		conllSentence = performSRLForChineseJSONHelper(inputSentence, options);

		SRLJsonDataStructure json_ds = srlhelper.createSRLJsonDataStructure(conllSentence, options);

		// set sentence datastructure
		Sentence s = Sentence.newSentence(conllSentence.getCoNLLSentence().split("\n"));
		json_ds.setSentenceDS(s);

		return json_ds;
	}

	/**
	 * Perform semantic role labeling for chinese json
	 * 
	 * @param inputSentence
	 * @return
	 * @throws Exception
	 */
	public String performSemanticRoleLabelingForChineseJSON(String inputSentence) throws Exception {

		String json = "";
		CoNLLSentence conllSentence = new CoNLLSentence();
		conllSentence.setProcessedSentence(inputSentence);

		boolean badcase = checkBadCase(inputSentence);
		if (badcase) {
			conllSentence.setBadCase(badcase);
			String conll = getSRLforBadCase(inputSentence);
			conllSentence.setCoNLLSentence(conll);

		} else {
			Sentence s = chinesePipleLine.parse(inputSentence);
			conllSentence.setBadCase(false);
			conllSentence.setCoNLLSentence(s.toString());

		}

		json = srlhelper.createSRLJsonOutput(conllSentence, null);

		return json;
	}

	/**
	 * Function to parse SRL and provide JSON output based on the SRL options
	 * 
	 * @param inputSentence
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public String performSRLForChineseJSON(List<String> inputSentences, SRLOptions options) throws Exception {

		// boolean segmentSentence = options.segmentSentence;
		// boolean doNER = options.doNER;

		String json = "";

		if (inputSentences != null && inputSentences.size() > 0) {
			List<CoNLLSentence> sentenceList = new ArrayList<CoNLLSentence>();
			for (String string : inputSentences) {

				if (!Strings.isNullOrEmpty(string)) {
					string = string.trim();
					CoNLLSentence c = performSRLForChineseJSONHelper(string, options);
					sentenceList.add(c);
				}

			}

			json = srlhelper.createSRLJsonOutput(sentenceList, options);
		}

		return json;
	}

	/**
	 * Function to parse SRL and provide JSON output based on the SRL options
	 * 
	 * @param inputSentence
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public String performSRLForChineseJSON(String inputSentence, SRLOptions options) throws Exception {

		// boolean segmentSentence = options.segmentSentence;
		// boolean doNER = options.doNER;

		String json = "";

		CoNLLSentence conllSentence = new CoNLLSentence();

		conllSentence = performSRLForChineseJSONHelper(inputSentence, options);
		json = srlhelper.createSRLJsonOutput(conllSentence, options);

		return json;
	}

	/**
	 * Function to parse SRL and provide JSON output based on the SRL options.
	 * In this case, sentence is a array of tokens
	 * 
	 * @param tokenList
	 *            : Sentence as tokens
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public String performSRLForChineseJSON(String[] tokenList, SRLOptions options) throws Exception {

		// boolean segmentSentence = options.segmentSentence;
		// boolean doNER = options.doNER;

		String json = "";

		CoNLLSentence conllSentence = new CoNLLSentence();

		conllSentence = performSRLForChineseJSONHelper(tokenList, options);
		json = srlhelper.createSRLJsonOutput(conllSentence, options);

		return json;
	}

	/**
	 * 
	 * @param tokenList
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public SRLJsonDataStructure performSRL(String[] tokenList, SRLOptions options) throws Exception {

		CoNLLSentence coNLLSentence = performSRLForChineseJSONHelper(tokenList, options);
		SRLJsonDataStructure output = srlhelper.createSRLJsonDataStructure(coNLLSentence, options);
		return output;
	}

	// Workaround
	// No manual mode
	public SRLJsonDataStructure performSRLUsingHybridPreprocessor(String[] tokens, SRLOptions options)
			throws Exception {

		CoNLLSentence conllSentence = new CoNLLSentence();

		String inputSentence = "";
		for (String string : tokens) {
			inputSentence = inputSentence + string;
		}
		conllSentence.setProcessedSentence(inputSentence);

		int model_no = 2;
		SentenceData09 sent = HybridPreprocessor.getHybridPreprocessor().preprocess(tokens);
		Sentence s = chinesePipleLine.parseWithCompleteSentenceData09(sent, model_no);
		conllSentence.setCoNLLSentence(s.toString());

		SRLJsonDataStructure output = srlhelper.createSRLJsonDataStructure(conllSentence, options);
		return output;
	}

	// Workaround
	// No manual mode
	public SRLJsonDataStructure performSRLUsingHybridPreprocessor(String[] tokens, String[] posArray,
			SRLOptions options) throws Exception {

		CoNLLSentence conllSentence = new CoNLLSentence();

		String inputSentence = "";
		for (String string : tokens) {
			inputSentence = inputSentence + string;
		}
		conllSentence.setProcessedSentence(inputSentence);

		int model_no = 2;
		SentenceData09 sent = HybridPreprocessor.getHybridPreprocessor().preprocess(tokens, posArray);
		Sentence s = chinesePipleLine.parseWithCompleteSentenceData09(sent, model_no);
		conllSentence.setCoNLLSentence(s.toString());

		SRLJsonDataStructure output = srlhelper.createSRLJsonDataStructure(conllSentence, options);
		return output;
	}

	/**
	 * This helper function takes word tokens of a sentence as input and perform
	 * SRL
	 * 
	 * @param tokenList
	 * @param options
	 * @return
	 * @throws Exception
	 */
	private CoNLLSentence performSRLForChineseJSONHelper(String[] tokenList, SRLOptions options) throws Exception {
		// TODO Auto-generated method stub
		boolean manualMode = options.manualMode;
		int model_no = options.model;
		if (model_no != 1 && model_no != 2) {
			model_no = 1;
		}

		String inputSentence = "";
		for (String string : tokenList) {
			inputSentence = inputSentence + string;
		}
		CoNLLSentence conllSentence = new CoNLLSentence();
		conllSentence.setProcessedSentence(inputSentence);

		// If manual mode is true then we check for bad case first
		if (manualMode) {
			boolean badcase = checkBadCase(inputSentence);
			if (badcase) {

				String conll = getSRLforBadCase(inputSentence);

				conllSentence.setBadCase(badcase);
				conllSentence.setCoNLLSentence(conll);

			} else {

				// Sentence s = chinesePipleLine.parse(inputSentence);
				Sentence s = chinesePipleLine.parse(tokenList, model_no);
				conllSentence.setCoNLLSentence(s.toString());

			}

		}

		else {
			// Sentence s = chinesePipleLine.parse(inputSentence);
			Sentence s = chinesePipleLine.parse(tokenList, model_no);
			conllSentence.setCoNLLSentence(s.toString());

		}
		return conllSentence;
	}

	/**
	 * Helper function for SRL which returs CONLLSentence with the parse
	 * information
	 * 
	 * @param inputSentence
	 * @param options
	 * @return
	 * @throws Exception
	 */
	private CoNLLSentence performSRLForChineseJSONHelper(String inputSentence, SRLOptions options) throws Exception {
		// TODO Auto-generated method stub

		boolean manualMode = options.manualMode;
		int model_no = options.model;
		if (model_no != 1 && model_no != 2) {
			model_no = 1;
		}

		CoNLLSentence conllSentence = new CoNLLSentence();
		conllSentence.setProcessedSentence(inputSentence);

		// If manual mode is true then we check for bad case first
		if (manualMode) {
			boolean badcase = checkBadCase(inputSentence);
			if (badcase) {

				String conll = getSRLforBadCase(inputSentence);

				conllSentence.setBadCase(badcase);
				conllSentence.setCoNLLSentence(conll);

			} else {

				// Sentence s = chinesePipleLine.parse(inputSentence);
				Sentence s = chinesePipleLine.parse(inputSentence, options);
				conllSentence.setCoNLLSentence(s.toString());

			}

		}

		else {
			// Sentence s = chinesePipleLine.parse(inputSentence);
			Sentence s = chinesePipleLine.parse(inputSentence, options);
			conllSentence.setCoNLLSentence(s.toString());

		}
		return conllSentence;

	}

	/**
	 * 
	 * @param coNLLSentence
	 * @return
	 */
	private String getSRLforBadCase(String sentence) {

		CoNLLSentence coNLLSentence = badcases_srl_map.get(sentence);
		if (coNLLSentence != null) {

			String conll = coNLLSentence.getCoNLLSentence();
			return conll;

		}

		// String json = helper.createSRLJsonOutput(doc, conll);
		// System.out.println(json);

		return null;
	}

	/**
	 * Function to check if bad case SRL is present in the map or not.
	 * 
	 * @return
	 */
	public boolean checkBadCase(String sentence) {
		boolean badCase = false;

		if (badcases_srl_map.containsKey(sentence)) {
			badCase = true;
		}

		return badCase;
	}

	/**
	 * Create all the options for Chinese SRL
	 * 
	 * @return
	 */
	private CompletePipelineCMDLineOptions createOptionsForChinese() {

		L langauge = L.chi;
		boolean tokenize = true;
		boolean reranker = false;
		boolean skipPI = false;

		File input = new File("orignal-sentences.txt");
		File output = new File("output.txt");

		File lemmatizer = new File(CHINESE_LEMMATIZER);
		File parser = new File(CHINESE_DEPENDENCY_PARSER);
		// File tagger = new File(POS_TAGGER);
		File tagger = new File(POS_TAGGER_TEST);
		File srl1 = new File(CHINESE_SRL_MODEL_1);
		File srl2 = new File(CHINESE_SRL_MODEL_2);

		// File tokenizer = new File(
		// "resources/semantic_role_labeling/models/chinese");

		// File srl = new
		// File("resources/semantic_role_labeling/models/chinese/chinese-test-srl.mdl");
		//

		// TODO Auto-generated method stub
		CompletePipelineCMDLineOptions options = new CompletePipelineCMDLineOptions();
		options.acBeam = 4;
		options.aiBeam = 4;
		options.alfa = 1.0;
		options.desegment = false;
		options.input = input;

		options.l = langauge;
		options.loadPreprocessorWithTokenizer = tokenize;

		options.lemmatizer = lemmatizer;
		options.parser = parser;
		options.tagger = tagger;
		options.srl1 = srl1;
		options.srl2 = srl2;

		// we make it null to use default tokenizer
		options.tokenizer = null;

		options.reranker = reranker;
		options.skipPI = skipPI;

		return options;

	}

	/**
	 * Create SRL Options for CHinese based on argument map. This function is
	 * mostly for evaluation purpose
	 * 
	 * @param args
	 * @return
	 */
	public CompletePipelineCMDLineOptions createOptionsForChinese(Map<String, String> args) {

		L langauge = L.chi;
		boolean tokenize = true;
		boolean reranker = false;
		boolean skipPI = false;

		File input = new File("orignal-sentences.txt");
		File output = new File("output.txt");

		File lemmatizer = new File(CHINESE_LEMMATIZER);
		File parser = new File(CHINESE_DEPENDENCY_PARSER);
		File tagger = new File(POS_TAGGER_TEST);
		// File tagger = new File(POS_TAGGER);
		File srl1 = new File(args.get("srl1"));
		File srl2 = new File(args.get("srl2"));

		// File tokenizer = new File(
		// "resources/semantic_role_labeling/models/chinese");

		// File srl = new
		// File("resources/semantic_role_labeling/models/chinese/chinese-test-srl.mdl");
		//

		// TODO Auto-generated method stub
		CompletePipelineCMDLineOptions options = new CompletePipelineCMDLineOptions();
		options.acBeam = 4;
		options.aiBeam = 4;
		options.alfa = 1.0;
		options.desegment = false;
		options.input = input;

		options.l = langauge;
		options.loadPreprocessorWithTokenizer = tokenize;

		options.lemmatizer = lemmatizer;
		options.parser = parser;
		options.tagger = tagger;
		options.srl1 = srl1;
		options.srl2 = srl2;

		// we make it null to use default tokenizer
		options.tokenizer = null;

		options.reranker = reranker;
		options.skipPI = skipPI;

		return options;

	}

	private CompletePipelineCMDLineOptions createOptionsForEnglish() {

		L langauge = L.eng;
		boolean tokenize = true;
		boolean reranker = false;
		boolean skipPI = false;

		File input = new File("orignal-sentences.txt");
		File output = new File("output.txt");

		File lemmatizer = new File("resources/semantic_role_labeling/models/english/lemma_en.model");
		File parser = new File("resources/semantic_role_labeling/models/english/parser_en.model");

		File tagger = new File("resources/semantic_role_labeling/models/english/postagger_en.model");
		File tokenizer = new File("resources/semantic_role_labeling/models/english/en-token.bin");
		File srl = new File("resources/semantic_role_labeling/models/english/srl-eng.model");

		// TODO Auto-generated method stub
		CompletePipelineCMDLineOptions options = new CompletePipelineCMDLineOptions();
		options.acBeam = 4;
		options.aiBeam = 4;
		options.alfa = 1.0;
		options.desegment = false;
		options.input = input;

		options.l = langauge;
		options.loadPreprocessorWithTokenizer = tokenize;

		options.lemmatizer = lemmatizer;
		options.parser = parser;
		options.tagger = tagger;
		options.srl1 = srl;
		options.tokenizer = tokenizer;

		options.reranker = reranker;
		options.skipPI = skipPI;

		return options;

	}

	/**
	 * Function to load complete SRL pipeline for given options and language
	 * 
	 * @param options
	 * @param lang
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public CompletePipeline getCompletePipeline(FullPipelineOptions options, L lang)
			throws ZipException, IOException, ClassNotFoundException {

		Language.setLanguage(lang);
		Preprocessor pp = Language.getLanguage().getPreprocessor(options);

		return getPipeLineHelper(options, pp);
	}

	/**
	 * Helper function to load SRL model with given Preprocessor and pipeline
	 * options
	 * 
	 * @param options
	 * @param pp
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private CompletePipeline getPipeLineHelper(FullPipelineOptions options, Preprocessor pp)
			throws ZipException, IOException, ClassNotFoundException {

		Parse.parseOptions = options.getParseOptions();
		// SemanticRoleLabeler srl1;
		SemanticRoleLabeler srl2;

		if (options.reranker) {
			// srl1 = new Reranker(Parse.parseOptions);
			srl2 = new Reranker(Parse.parseOptions);
		} else {
			// ZipFile zipFile1 = new ZipFile(Parse.parseOptions.modelFile1);
			ZipFile zipFile2 = new ZipFile(Parse.parseOptions.modelFile2);
			if (Parse.parseOptions.skipPI) {
				// srl1 = Pipeline.fromZipFile(zipFile1, new Step[] { Step.pd,
				// Step.ai, Step.ac });
				srl2 = Pipeline.fromZipFile(zipFile2, new Step[] { Step.pd, Step.ai, Step.ac });

			} else {
				// srl1 = Pipeline.fromZipFile(zipFile1);
				srl2 = Pipeline.fromZipFile(zipFile2);
			}
			// zipFile1.close();
			zipFile2.close();
		}
		// CompletePipeline pipeline = new CompletePipeline(pp, srl1);
		CompletePipeline pipeline = new CompletePipeline(pp, srl2);
		return pipeline;
	}

	/**
	 * This function only loads the SRL models for processing. It is assumed
	 * that tokenization, pos, dependency are already taken care elsewhere
	 * 
	 * @param srlOptions
	 * 
	 * @param options
	 * @param lang
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public CompletePipeline getPartialPipeline(SRLOptions srlOptions, FullPipelineOptions options, L lang)
			throws ZipException, IOException, ClassNotFoundException {
		Language.setLanguage(lang);
		// We set the preprocesor to null;
		Lemmatizer lemmatizer = new SimpleChineseLemmatizer();

		Preprocessor pp = null;
		Tagger tagger = null;
		mate.is2.mtag.Tagger mtagger = null;
		Parser parser = null;
		Tokenizer tokenizer = null;

		// check if we need to use local pos tagger
		if (!srlOptions.usePOSFromNLP) {
			System.out.println("Loading SRL POS tagger locally..");
			tagger = options.tagger == null ? null : BohnetHelper.getTagger(options.tagger);

		}
		// check if we need to use local dependency parser
		if (!srlOptions.useDEPFromNLP) {
			System.out.println("Loading SRL Dependency Parser locally..");
			parser = options.parser == null ? null : BohnetHelper.getParser(options.parser);

		}
		pp = new Preprocessor(tokenizer, lemmatizer, tagger, mtagger, parser);

		return getPipeLineHelper(options, pp);
	}

}
