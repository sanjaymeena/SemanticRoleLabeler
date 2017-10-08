package com.emotibot.srl.pruner;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class SyntacticParserCMDLine {
	static String modelPath = "data/syntactictree/models/chinesePCFG_Emotibot.ser.gz";
	static String testFileWithTrees = "data/test/input";
	static String testFileWithTreesOutput = "data/test/output";
	
	EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();;
	
	String newline = "\n";
	private LabeledScoredTreeFactory tree_factory;
	static LexicalizedParser lp;

	public SyntacticParserCMDLine() throws ClassCastException, ClassNotFoundException, IOException {
		loadModel();
		tree_factory = new LabeledScoredTreeFactory();
		println("");

	}

	@SuppressWarnings("resource")
	private String readLine() {
		Scanner scanner = null;
		String doc = "";

		scanner = new Scanner(System.in);
		System.err.println("\nInput Text:");
		doc = scanner.nextLine();

		return doc;
	}

	private void println(Object obj) {
		// TODO Auto-generated method stub
		System.out.println(obj);
	}

	private void printMenu() {

		int selection = 0;

		StringBuilder sb = new StringBuilder();
		sb.append(
				"======================================================================================================== \n");
		sb.append(
				"|                                  Emotibot Syntactic Parser                            							|\n");
		sb.append(
				"======================================================================================================== \n");

		sb.append(
				"                                   1. Run Emotibot Syntactic Parser on input sentence                                       \n");
		sb.append(
				"                                   2. Run Emotibot Syntactic Parser on test file                               \n");
		sb.append("                                 3. quit                                                \n");

		println(sb);
		println("Select your choice..");

		while (true) {

			Scanner scanner = null;
			String input = "";
			try {
				scanner = new Scanner(System.in);
				while (!scanner.hasNextInt()) {
					println("Please select from the given choices");
					scanner.nextLine();
				}
				selection = scanner.nextInt();
				switch (selection) {

				case 1:
					input = readLine();

					if (!Strings.isNullOrEmpty(input)) {
						String[] tokens = emotibotTokenzier.tokenize(input);
						List<CoreLabel> rawWords = Sentence.toCoreLabelList(tokens);
						Tree parse = lp.apply(rawWords);
						System.out.println(parse);
					}

					break;

				case 2:

					testFromFiles(testFileWithTrees);

					break;

				case 3:
					println("Bye");
					System.exit(0);
					break;

				default:
					println("Please select from the given choices");

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private String testFromFile(File f) throws IOException {

		StringBuilder sb = new StringBuilder();
		List<String> lines = FileUtils.readLines(f, "UTF-8");
		for (String string : lines) {
			if (!Strings.isNullOrEmpty(string)) {
				Tree t = readTreeFromString(string);
				List<Tree> leaves = t.getLeaves();
				List<String> tokens = new ArrayList<>();
				for (Tree leaf : leaves) {
					tokens.add(leaf.toString());
				}
				String toks[] = tokens.toArray(new String[tokens.size()]);

				List<CoreLabel> rawWords = Sentence.toCoreLabelList(toks);
				Tree parse = lp.apply(rawWords);

				sb.append(parse.toString() + newline);

				if (parse.equals(t)) {
					System.out.println("correct : " + parse.toString());
				} else {

					System.out.println("incorrect : " + string + "   |   " + parse.toString());
				}

			}
		}

		return sb.toString();
	}

	private void testFromFiles(String directory) throws IOException {
		File rootDir = new File(directory);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		File outputDir = new File(testFileWithTreesOutput);
		// clean directory
		FileUtils.cleanDirectory(outputDir);

		// iterate through all the files in the given directory and read the

		for (File file : files) {
			String output = testFromFile(file);

			String temp = file.getName().replace(".txt", "");
			temp = temp + "_out.txt";
			String outputFile = testFileWithTreesOutput + File.separator + temp;
			File f = new File(outputFile);
			FileUtils.writeStringToFile(f, output, "UTF-8");
			System.out.println("wrote to  : " + f);

		}
	}

	private void loadModel() {

		lp = LexicalizedParser.loadModel(modelPath);

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

	public static void main(String[] args) throws Exception {

		SyntacticParserCMDLine sRLDemo = new SyntacticParserCMDLine();

		sRLDemo.printMenu();

	}
}
