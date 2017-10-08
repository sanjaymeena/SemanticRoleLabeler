package com.emotibot.srl.utilities;

import static com.emotibot.srl.server.Constants.chineseStanfordParseGrammarFile;
import static com.emotibot.srl.server.Constants.chineseStanfordParserMaxLength;
import static com.emotibot.srl.server.Constants.chineseStanfordParserServerPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.emotibot.enlp.EWord;
import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.SegmentResult;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

/**
 * Utility Class which provides many functions
 * 
 * @author Sanjay_Meena
 */
public class AnalysisUtilities {
	
	String encoding = "UTF-8";
	private LexicalizedParser chineseStanfordParser;
	private static AnalysisUtilities instance;
	private LabeledScoredTreeFactory tree_factory;
	private static EmotibotTokenizer tokenizer;

	private AnalysisUtilities() {

		tree_factory = new LabeledScoredTreeFactory();
		tokenizer = new EmotibotTokenizer();
	}
	
	
	 /**
	   * Convert from tree to string
	   * 
	   * @param tree
	   * @return string
	   */
	  public String treeToString(Tree tree) {
	    String sentence = null;
	    if (tree != null) {
	      ArrayList<String> sentenceArray = treeToArrayList(tree);

	      sentence = Sentence.listToString(sentenceArray).trim();

	    }
	    return sentence;

	  }
	  
	  /**
	   * Convert form tree to ArrayList
	   * 
	   * @param tree
	   * @return Tree
	   */
	  public ArrayList<String> treeToArrayList(Tree tree) {
	    ArrayList<Label> test = tree.yield();
	    ArrayList<String> arrayList = new ArrayList<String>();
	    for (Object element : test) {
	      Label label = (Label) element;
	      arrayList.add(label.value());

	    }

	    return arrayList;

	  }
	
	

	/**
	 * Tokenize chinese sentence
	 * 
	 * @param text
	 * @return
	 */
	public String[] tokenizer(String text) {
		return tokenizer.tokenize(text);

	}

	/**
	 * Return instance of this class
	 * 
	 * @return AnalysisUtilities
	 */
	public static AnalysisUtilities getInstance() {
		if (instance == null) {
			instance = new AnalysisUtilities();
		}
		return instance;
	}

	/**
	 * Read tree from a string
	 * 
	 * @param parseStr
	 *            input tree in form a string
	 * @return tree
	 */
	public Tree readTreeFromString(String parseStr) {
		// read in the input into a Tree data structure
		TreeReader treeReader = new PennTreeReader(new StringReader(parseStr), tree_factory);
		Tree inputTree = null;
		try {
			inputTree = treeReader.readTree();
			treeReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputTree;
	}

	/**
	 * Parse a chinese sentence using client server model
	 * 
	 * @param sentence
	 * @return
	 */
	public ParseResult parseChineseSentence(String[] tokens, boolean useSockerServer) {

		Tree parse = null;
		double parseScore = Double.MIN_VALUE;

		//System.err.println("parsing syntax tree locally:" + tokens);

		// if socket server not available, then use a local parser object
		if (chineseStanfordParser == null) {

			try {
				Options op = new Options();
				int maxLength = new Integer(chineseStanfordParserMaxLength).intValue();
				String[] options = { "-maxLength", Integer.toString(maxLength), "-outputFormat", "oneline" };

				op.setOptions(options);

				chineseStanfordParser = LexicalizedParser.loadModel(chineseStanfordParseGrammarFile, op);

				/**
				 * Not applicable in the new version of Stanford.
				 */
				// parser.setMaxLength();
				// parser.setOptionFlags("maxLength",
				// Integer.toString(maxLength),"-outputFormat", "oneline");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {

			List<HasWord> sent = Sentence.toWordList(tokens);
			// System.err.println("hasWord:"+sent);

			parse = chineseStanfordParser.parse(sent);

			// System.err.println(parse.toString());
			if (parse != null) {

				// remove all the parent annotations (this is a hacky way to do
				// it)
				String ps = parse.toString().replaceAll("\\[[^\\]]+/[^\\]]+\\]", "");
				// System.out.println("Hello...... " + ps);
				parse = AnalysisUtilities.getInstance().readTreeFromString(ps);

				parseScore = 0.0;
				return new ParseResult(true, parse, parseScore);
			}
		} catch (Exception e) {
		}

		parse = readTreeFromString("(ROOT (. .))");
		parseScore = -99999.0;
		return new ParseResult(false, parse, parseScore);
	}

	/**
	 * Parse a chinese sentence using client server model
	 * 
	 * @param sentence
	 * @return
	 */
	public ParseResult parseChineseSentence(String sentence, boolean useSockerServer) {
		String result = "";
		PrintWriter pw;
		BufferedReader br;
		String line;
		Tree parse = null;
		double parseScore = Double.MIN_VALUE;

		if (useSockerServer) {
			// Tree parse = null;
			// double parseScore = Double.MIN_VALUE;
			//
			// System.err.println(sentence);
			// see if a parser socket server is available
			int port = new Integer(chineseStanfordParserServerPort);
			String host = "127.0.0.1";
			Socket client;

			try {
				client = new Socket(host, port);

				pw = new PrintWriter(client.getOutputStream());
				br = new BufferedReader(new InputStreamReader(client.getInputStream()));
				pw.println(sentence);
				pw.flush(); // flush to complete the transmission

				/**
				 * 1)Removed the ready method. It was giving issues 2)Removed
				 * the else condition and parseScore method
				 */
				while ((line = br.readLine()) != null) {
					line = line.replaceAll("\n", "");
					line = line.replaceAll("\\s+", " ");
					result += line + " ";

				}

				br.close();
				pw.close();
				client.close();

				if (parse == null) {
					parse = readTreeFromString("(ROOT (. .))");
					parseScore = -99999.0;
				}

				parse = readTreeFromString(result);
				return new ParseResult(true, parse, parseScore);

			} catch (Exception ex) {

				System.err.println("Could not connect to parser server.");
				// ex.printStackTrace();
			}

		}
		//System.err.println("parsing syntax tree locally:" + sentence);

		// if socket server not available, then use a local parser object
		if (chineseStanfordParser == null) {

			try {
				Options op = new Options();
				int maxLength = new Integer(chineseStanfordParserMaxLength).intValue();
				String[] options = { "-maxLength", Integer.toString(maxLength), "-outputFormat", "oneline" };

				op.setOptions(options);

				chineseStanfordParser = LexicalizedParser.loadModel(chineseStanfordParseGrammarFile, op);

				/**
				 * Not applicable in the new version of Stanford.
				 */
				// parser.setMaxLength();
				// parser.setOptionFlags("maxLength",
				// Integer.toString(maxLength),"-outputFormat", "oneline");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {

			String[] traditional_word_array = tokenizer.tokenize(sentence);

			List<HasWord> sent = Sentence.toWordList(traditional_word_array);
			// System.err.println("hasWord:"+sent);

			parse = chineseStanfordParser.parse(sent);

			// System.err.println(parse.toString());
			if (parse != null) {

				// remove all the parent annotations (this is a hacky way to do
				// it)
				String ps = parse.toString().replaceAll("\\[[^\\]]+/[^\\]]+\\]", "");
				// System.out.println("Hello...... " + ps);
				parse = AnalysisUtilities.getInstance().readTreeFromString(ps);

				parseScore = 0.0;
				return new ParseResult(true, parse, parseScore);
			}
		} catch (Exception e) {
		}

		parse = readTreeFromString("(ROOT (. .))");
		parseScore = -99999.0;
		return new ParseResult(false, parse, parseScore);
	}

	
	public Tree lemmatizeTree(Tree temp1) {
		// TODO Auto-generated method stub
		return null;
	}
}
