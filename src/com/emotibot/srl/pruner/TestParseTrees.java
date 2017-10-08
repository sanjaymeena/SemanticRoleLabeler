/**
 * 
 */
package com.emotibot.srl.pruner;

import static com.emotibot.srl.pruner.Rules.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emotibot.srl.pruner.Constants.PRUNERRULES;
import com.emotibot.srl.test.TregexPatternFactory;
import com.emotibot.srl.utilities.AnalysisUtilities;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;
import edu.stanford.nlp.util.Pair;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

/**
 * @author Sanjay
 *
 */
public class TestParseTrees {
	private final static Logger log = LoggerFactory.getLogger(TestParseTrees.class);
	
	private String newline ="\n";
	static List<String> rules;
	EmotibotTokenizer emotibotTokenzier;
	static Multimap<PRUNERRULES, Rule> level_1_prunerRules;

	private void initializePruningRules() {
		level_1_prunerRules = LinkedListMultimap.create();

		Rule rule = new Rule();
		rule.setPrunerEnum(PRUNERRULES.IJ_with_sister_PU);
		rule.setRule(IJ_with_sister_PU);
		rule.setName(PRUNERRULES.IJ_with_sister_PU.toString());
		level_1_prunerRules.put(PRUNERRULES.IJ_with_sister_PU, rule);

		rule = new Rule();
		rule.setPrunerEnum(PRUNERRULES.IJ_with_sister_AD);
		rule.setRule(IJ_with_sister_AD);
		rule.setName(PRUNERRULES.IJ_with_sister_AD.toString());
		level_1_prunerRules.put(PRUNERRULES.IJ_with_sister_AD, rule);

		rule = new Rule();
		rule.setPrunerEnum(PRUNERRULES.IJ_remove_anywhere);
		rule.setRule(IJ_remove_anywhere);
		rule.setName(PRUNERRULES.IJ_remove_anywhere.toString());
		level_1_prunerRules.put(PRUNERRULES.IJ_remove_anywhere, rule);

		rule = new Rule();
		rule.setPrunerEnum(PRUNERRULES.phrase_with_removable_token_sister_PU);
		rule.setRule(phrase_with_removable_token_sister_PU);
		rule.setName(PRUNERRULES.phrase_with_removable_token_sister_PU.toString());
		level_1_prunerRules.put(PRUNERRULES.phrase_with_removable_token_sister_PU, rule);

		rule = new Rule();
		rule.setPrunerEnum(PRUNERRULES.tokens_remove_anywhere);
		rule.setRule(tokens_remove_anywhere);
		rule.setName(PRUNERRULES.tokens_remove_anywhere.toString());
		level_1_prunerRules.put(PRUNERRULES.tokens_remove_anywhere, rule);

	}

	public TestParseTrees() {

		emotibotTokenzier = new EmotibotTokenizer();

		initializePruningRules();

		// rules = new ArrayList<String>();
		//
		// // Words to remove from any level
		// // We will start with simpler rules .
		//
		// /**
		// * Prune IJ $ PU at any level E.g. 嗯，我能不能把他换成那个什么套餐那个七块钱的那个什么套餐啊
		// *
		// */
		// rules.add("(IJ=prune_this $ PU=prune2_this)");
		// /**
		// * Prune IJ $+ AD E.g. 喔好的好的行那过几天过两天再查恩好
		// *
		// *
		// */
		// rules.add("(IJ=prune_this $+ AD=prune2_this)");
		//
		// /**
		// * Prune IJ at any level E.g. 恩...密码知道的，恩
		// *
		// */
		// rules.add("(ROOT << IJ=prune_this)");
		//
		// // Remove some common words
		// /**
		// * If the phrase before first PU has these tokens, press the phrase+
		// PU
		// *
		// */
		// rules.add("(NP|NN|VP|VV|VA|VE|NN|NNS|FLR=prune_this <<
		// 恩|对|ㄜ|疴|你好|您好|嗯 ) $+ PU=prune2_this");
		//
		//
		// /**
		// * Prune these tokens no matter where there positions is
		// *
		// */
		// rules.add("ROOT << 恩|阿|额|ㄟ|您好|你好|疴|就是|那么|欸||算了|摁=prune_this");

		// // Rules to prune NP
		// rules.add("(NP=prune_this < (PN < 自己) $- NP)");
		// rules.add("(NP=prune_this < (PN !< 你|我|他|她) !< (NN|NR|NT) $+ NP)");
		// rules.add("NP=prune_this < (NT < 那天)");
		// rules.add("NP< (PN=prune_this < 自己 $- PN|NN)");
		//
		// // rules in case when NP and PN occur adjacent and We want to keep
		// only
		// // PN
		// // rules.add("(NP < PN) $+ (NP=prune_this < NP ) |$- (NP=prune_this <
		// NP
		// // )");
		// rules.add("(NP < PN) $- (NP=prune_this < NN )");
		//
		// // remove ADJP|ADVP|ADJ which are modifying NP
		//
		// rules.add("(NP < NN) $ (ADJP|ADVP|ADJ=prune_this !$+ CP)");
		//
		// // Rules for DNP
		//
		// rules.add("DNP $+ (NP < NN) < (NP < (NP=prune_this < NN !$ QP)) <
		// DEG=prune2_this ");
		// rules.add("NP < (DNP=prune_this $+ NP [< ADJP < DEG]) !$- (VC < 是|不是)
		// ");
		//
		// // Rules for QP
		// rules.add("DNP $+ (NP < NN) < (NP < (QP=prune_this << (CD < 一) $+
		// NP)) < DEG ");
		// rules.add("DNP $+ (NP < NN) < (QP=prune_this << (CD < 一)) < DEG ");
		// rules.add("(NP < NN) $ (QP=prune_this !$+ CP)");
		//
		// // Rules for NR
		// rules.add("(NP < NR) $- (NP=prune_this !< NR) ");
		// rules.add("(NP < NR) $ (ADJP|ADVP|DNP|CP|DP=prune_this ?$+
		// (NP=prune2_this !< NR)) ");
		//
		// // Prune sentence particle
		// rules.add("SP=prune_this !< 吗 ");
		//
		// // Prune ADVP, modifier of verb phrase
		// rules.add("(VP < VV|VA|VE|VC ) $ (ADVP=prune_this !<<
		// 是否|不|怎么|没有|是不是|还没|不用)");
		//
		// // Prune VC and DEC= 的 when , DEC is the last member of the sentence
		// rules.add("(VP|VNV < VC=prune_this << (CP < (DEC=prune2_this < 的) !$+
		// NP))");
		//
		// // other patterns
		// // LCP containing CD and LC
		// rules.add("LCP=prune_this < (QP < (CD < 一)) < LC");

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		TestParseTrees testTrees = new TestParseTrees();
		
		String result = testTrees.pruneSentence("喔好的好的行那过几天过两天再查恩好");
		System.out.println(result);

		//	testTrees.test2();

	}

	/**
	 * 
	 * @throws IOException
	 */
	private void test2() throws IOException {
		String gold_file1 = "data/syntactictree/others/gold_data/sentences/Tree_Pruning_100_Sentence.txt";
		String gold_file2 = "data/syntactictree/others/gold_data/sentences/pruning_DNP_sentences.txt";
		String gold_file3 = "data/syntactictree/others/gold_data/sentences/pruning_NR.txt";
		String gold_file4 = "data/syntactictree/others/gold_data/sentences/pruning_QP_sentences.txt";

		String gold_file5 = "data/syntactictree/others/gold_data/tokenized/pruning_QP_sentences.txt";
		String gold_file6 = "data/syntactictree/others/gold_data/tokenized/Tree_Pruning_100_Sentence.txt";
		String gold_file7 = "data/syntactictree/others/gold_data/tokenized/prune_NN_PN_sentences.txt";

		String gold_file8 = "data/syntactictree/others/test_data/pruner_cm_interns.txt";
		// test2Helper(gold_file1, false);
		// test2Helper(gold_file5, true);
		// test2Helper(gold_file6, true);
		// test2Helper(gold_file7, true);

		test2Helper(gold_file1, false);

	}

	/**
	 * @throws IOException
	 * 
	 */
	public void test3() throws IOException {

		String inputDir = "data/syntactictree/others/gold_data/sentences";
		String outputDir = "data/syntactictree/others/gold_data/tokenized";

		List<File> files = new ArrayList<File>();

		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		log.debug("Reading each file in directory :" + inputDir);

		// iterate through all the files
		for (File file : files) {
			List<String> contents = FileUtils.readLines(file, "UTF-8");
			StringBuilder outputBuilder = new StringBuilder();

			for (String string : contents) {

				String input = string;
				if (!Strings.isNullOrEmpty(input)) {
					String[] vals = string.split("\t");
					String key = vals[0];
					String[] tokens = emotibotTokenzier.tokenize(key);

					for (int i = 0; i < tokens.length; i++) {
						if (i != tokens.length - 1) {
							outputBuilder.append(tokens[i] + " ");
						} else {
							outputBuilder.append(tokens[i]);

						}

					}

					outputBuilder.append("\t");

					for (int i = 1; i < vals.length; i++) {
						if (i != vals.length - 1) {
							outputBuilder.append(vals[i] + "\t");
						} else {
							outputBuilder.append(vals[i]);

						}

					}

					outputBuilder.append("\n");

				}
			}

			String outputFile = outputDir + File.separator + file.getName();
			File outputF = new File(outputFile);
			FileUtils.writeStringToFile(outputF, outputBuilder.toString(), "UTF-8");

		}
	}

	/**
	 * 
	 * @param origSentence
	 */
	public String pruneSentence(String origSentence) {
		String[] tokens = emotibotTokenzier.tokenize(origSentence);
		Tree tree = AnalysisUtilities.getInstance().parseChineseSentence(origSentence, false).parse;
		Tree copy = tree.deepCopy();

		String orig = AnalysisUtilities.getInstance().treeToString(copy);
		pruneTree(copy);
		String output = AnalysisUtilities.getInstance().treeToString(copy);
		output = output.replace(" ", "");

		// StringBuilder sb
		log.debug("Original sentence: " + origSentence);
		log.debug("Tree: " + tree);

		System.out.print("Tokens :  ");
		for (int i = 0; i < tokens.length; i++) {
			if (i != tokens.length - 1) {
				System.out.print(tokens[i] + " ");
			} else {
				System.out.print(tokens[i]);

			}

		}

		
		log.debug("pruned: " + output);
		log.debug("---------------------------");
		return output;

	}

	private void test2Helper(String file, boolean isTokenizedInput) throws IOException {

		if (isTokenizedInput) {
			System.err.println("The input containes tokenized sentences");
		}

		File f1 = new File(file);
		List<String> contents = FileUtils.readLines(f1, "UTF-8");

		Multimap<String, String> dataMap = LinkedListMultimap.create();
		Map<String, String> dataMap2 = new LinkedHashMap<String, String>();

		int totalCounter = 0;
		int correctCounter = 0;
		int badCounter = 0;
		for (String string : contents) {

			if (!Strings.isNullOrEmpty(string)) {
				String[] vals = string.split("\t");

				String key = vals[0];

				dataMap2.put(key, string);
				for (int i = 1; i < vals.length; i++) {
					dataMap.put(key, vals[i]);
				}

			}

		}

		List<String> dnp_list = new ArrayList<String>();
		List<String> tree_analysis_list = new ArrayList<>();

		for (String key : dataMap.keySet()) {

			if (!Strings.isNullOrEmpty(key)) {

				StringBuilder analysisBuilder = new StringBuilder();
				Collection<String> vals = dataMap.get(key);
				String origSentence = key;
				Collection<String> goldSentenceList = vals;

				boolean isCorrect = false;

				String[] tokens = null;
				if (!isTokenizedInput) {

					origSentence = removeSomeMarkers(origSentence);
					tokens = emotibotTokenzier.tokenize(origSentence);
				} else {
					tokens = key.split(" ");
				}

				Tree tree = AnalysisUtilities.getInstance().parseChineseSentence(tokens, false).parse;
				Tree copy = tree.deepCopy();
				String orig = AnalysisUtilities.getInstance().treeToString(copy);

				// calling modify Tree function
				List<String> rulesUsed = pruneTree2(copy);

				String output = AnalysisUtilities.getInstance().treeToString(copy);
				output = output.replace(" ", "");

				StringBuilder sb = new StringBuilder();

				analysisBuilder.append(origSentence + "\t");
				for (String gold : goldSentenceList) {
					analysisBuilder.append(gold + "\t");
				}
				analysisBuilder.append(tree.toString());
				tree_analysis_list.add(analysisBuilder.toString());

				int counter = 0;
				sb.append("[ ");
				for (String s : goldSentenceList) {
					counter++;
					if (counter == goldSentenceList.size()) {
						sb.append("g" + counter + ": " + s);
					} else {
						sb.append("g" + counter + ": " + s + " , ");
					}

				}
				sb.append(" ]");

				totalCounter++;
				if (goldSentenceList.contains(output)) {
					correctCounter++;
					isCorrect = true;
				} else {
					badCounter++;
				}

				log.info(totalCounter + ": original sentence: " + key.replace(" ", "")
						+ " ||  gold_answers : " + sb.toString() );
				
				log.info("Tree: " + tree);
				log.info("Rules used in order: " + rulesUsed.toString());

				
				StringBuilder sb1=new StringBuilder();
				for (int i = 0; i < tokens.length; i++) {
					if (i != tokens.length - 1) {
						sb1.append(tokens[i] + " ");
					} else {
						sb1.append(tokens[i]);

					}

				}log.info("Tokens :" + " " +sb1.toString());

				//log.info("");
				log.info("pruned: " + output + " || Correct: " + isCorrect);
				log.info("---------------------------");

				// here we can decide which sentences to print for special cases
				if (tree.toString().contains("(NP (CP (IP")) {
					String row = dataMap2.get(key);
					dnp_list.add(row);
				}

			}

		}

		log.info("total sentences : " + totalCounter);
		log.info("correct prunes : " + correctCounter);
		log.info("incorrect prunes : " + badCounter);

		double acc = (((double) correctCounter) * 100) / totalCounter;
		log.info("accuracy % : " + acc);

		// log.debug("Count of selected pattern sentences : " +
		// dnp_list.size());
		// for (String string : dnp_list) {
		// log.debug(string);
		// }

		// contents = FileUtils.readLines(f2, "UTF-8");
		// for (String string : contents) {
		// if (!Strings.isNullOrEmpty(string)) {
		// String[] vals = string.split("\t");
		// String key=vals[0];
		// dataMap2.put(key, string);
		//
		//
		// }
		// }
		//
		//
		// log.debug("Count: " + dataMap.size());
		// for (String string : dataMap2.values()) {
		// log.debug(string);
		// }

		// output some output for anlaysis
		// StringBuilder sb=new StringBuilder();
		// for (String string : tree_analysis_list) {
		// sb.append(string + "\n");
		// }
		//
		// String analysisFile =
		// "data/syntactictree/others/tree_analysis_100_sentences.txt";
		// FileUtils.writeStringToFile(new File(analysisFile), sb.toString(),
		// "UTF-8");

	}

	private String removeSomeMarkers(String origSentence) {
		String newSentence = "";
		newSentence = origSentence.replace("...", "");

		newSentence = newSentence.replace("..", "");
		return newSentence;
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void parseTreeOnTokenizedText(String file) throws IOException {

		File f1 = new File(file);

		List<String> contents = FileUtils.readLines(f1, "UTF-8");
		for (String string : contents) {

			if (!Strings.isNullOrEmpty(string)) {
				String[] vals = string.split(" ");
				if (vals != null && vals.length > 0) {
					Tree tree = parseTreeOnTokenizedText(vals);
					Tree copy = tree.deepCopy();

					// older version
					// pruneTree(copy);

					// newer version
					genericPrune2(copy);

					for (String tok : vals) {
						System.out.print(tok + " ");
					}
					log.debug("");
					log.debug(tree.toString());

					String output = AnalysisUtilities.getInstance().treeToString(copy);
					output = output.replace(" ", "");
					log.debug("pruned : " + output);
					log.debug("------------------------");
				}

			}

		}
	}

	private Tree parseTreeOnTokenizedText(String[] vals) {
		Tree tree = null;
		if (vals != null) {
			tree = AnalysisUtilities.getInstance().parseChineseSentence(vals, false).parse;
		}

		return tree;
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void test() throws IOException {
		// TODO Auto-generated method stub

		String good_file = "data/syntactictree/others/good.txt";
		String bad_file = "data/syntactictree/others/bad.txt";
		File f = new File(good_file);

		Tree dummy = AnalysisUtilities.getInstance().parseChineseSentence("d", false).parse;
		log.debug("---------------------------");
		System.err.println("Good results after removing non required information");
		log.debug("---------------------------");
		log.debug("---------------------------");

		List<String> contents = FileUtils.readLines(f, "UTF-8");
		for (String string : contents) {

			String input = string;
			if (!Strings.isNullOrEmpty(input)) {

				Tree tree = AnalysisUtilities.getInstance().parseChineseSentence(input, false).parse;
				String orig = AnalysisUtilities.getInstance().treeToString(tree);
				pruneTree(tree);
				String output = AnalysisUtilities.getInstance().treeToString(tree);
				log.debug("original: " + orig);
				log.debug("pruned: " + output);
				log.debug("---------------------------");
			}
		}

		log.debug("---------------------------");
		System.err.println("Bad results because of wrong tokenization/wrong parse tree");
		log.debug("---------------------------");
		log.debug("---------------------------");

		f = new File(bad_file);

		contents = FileUtils.readLines(f, "UTF-8");
		for (String string : contents) {

			String input = string;
			if (!Strings.isNullOrEmpty(input)) {

				Tree tree = AnalysisUtilities.getInstance().parseChineseSentence(input, false).parse;
				String orig = AnalysisUtilities.getInstance().treeToString(tree);
				pruneTree(tree);
				String output = AnalysisUtilities.getInstance().treeToString(tree);
				log.debug("original: " + orig);
				log.debug("pruned: " + output);
				log.debug("--------------");
			}
		}

	}

	/**
	 * 
	 * @param tree
	 */
	public Tree pruneTree(Tree tree) {
		// TODO Auto-generated method stub
		// genericPrune(tree);
		genericPrune2(tree);
		return tree;
	}

	/**
	 * 
	 * @param tree
	 */
	public List<String> pruneTree2(Tree tree) {

		// genericPrune(tree);

		return genericPrune2(tree);
	}

	/**
	 * Given a tree, prune it.
	 * 
	 * @param parent
	 */
	private List<String> genericPrune2(Tree parent) {

		List<String> rulesUsed = new ArrayList<>();

		String tregex1;
		// String tregex2;

		TregexPattern matchPattern1;
		TregexMatcher matcher1;

		TsurgeonPattern p;
		List<TsurgeonPattern> ps;
		ArrayList<Pair<TregexPattern, TsurgeonPattern>> ops;

		Set<PRUNERRULES> keySet = level_1_prunerRules.keySet();
		for (PRUNERRULES pruner_rule : keySet) {
			Collection<Rule> vals = level_1_prunerRules.get(pruner_rule);
			for (Rule ruleDS : vals) {
				String rule = ruleDS.getRule();
				String ruleName=ruleDS.getName();

				ops = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
				ps = new ArrayList<TsurgeonPattern>();

				matchPattern1 = TregexPatternFactory.getPattern(rule);
				matcher1 = matchPattern1.matcher(parent);

				if (matcher1.find()) {

					rulesUsed.add(ruleName);

					if (matcher1.getNode("prune_this") != null) {

						ps.add(Tsurgeon.parseOperation("prune prune_this"));
					}
					if (matcher1.getNode("prune2_this") != null) {

						ps.add(Tsurgeon.parseOperation("prune prune2_this"));
					}

					p = Tsurgeon.collectOperations(ps);
					ops.add(new Pair<TregexPattern, TsurgeonPattern>(matchPattern1, p));
					try {
						Tsurgeon.processPatternsOnTree(ops, parent);
					} catch (Exception exception) {
						exception.printStackTrace();
					}

				}

			}
		}

		return rulesUsed;
	}

	/**
	 * Given a tree, prune it.
	 * 
	 * @param parent
	 */
	private Tree genericPrune(Tree parent) {

		String tregex1;
		// String tregex2;

		TregexPattern matchPattern1;
		TregexMatcher matcher1;

		TsurgeonPattern p;
		List<TsurgeonPattern> ps;
		ArrayList<Pair<TregexPattern, TsurgeonPattern>> ops;

		for (String rule : rules) {

			ops = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
			ps = new ArrayList<TsurgeonPattern>();

			matchPattern1 = TregexPatternFactory.getPattern(rule);
			matcher1 = matchPattern1.matcher(parent);

			if (matcher1.find()) {

				if (matcher1.getNode("prune_this") != null) {

					ps.add(Tsurgeon.parseOperation("prune prune_this"));
				}
				if (matcher1.getNode("prune2_this") != null) {

					ps.add(Tsurgeon.parseOperation("prune prune2_this"));
				}

				p = Tsurgeon.collectOperations(ps);
				ops.add(new Pair<TregexPattern, TsurgeonPattern>(matchPattern1, p));
				try {
					Tsurgeon.processPatternsOnTree(ops, parent);
				} catch (Exception exception) {
					exception.printStackTrace();
				}

			}

		}
		return parent;
	}

}
