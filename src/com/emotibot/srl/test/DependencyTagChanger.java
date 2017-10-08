package com.emotibot.srl.test;

import static com.emotibot.srl.format.Constants.NEWLINE_PATTERN;
import static com.emotibot.srl.format.Constants.TAB_PATTERN;
import static com.emotibot.srl.format.Constants.conllformat_deprel_column_no;
import static com.emotibot.srl.format.Constants.conllformat_head_column_no;
import static com.emotibot.srl.format.Constants.conllformat_pdeprel_column_no;
import static com.emotibot.srl.format.Constants.conllformat_phead_column_no;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.emotibot.enlp.NLPService;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.format.TagConverter;
import com.emotibot.srl.utilities.AnalysisUtilities;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure.Extras;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.international.pennchinese.ChineseGrammaticalStructure;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import edu.stanford.nlp.util.Filters;
import edu.stanford.nlp.util.StringUtils;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class DependencyTagChanger {
	EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();
	TagConverter tagConverter;
	String delimiter = "\t";
	String newline = "\n";

	DataFormatConverter dfc;
	String[] stanfordDependencyTaggerArguments;
	boolean conllx;
	boolean basic;
	boolean nonCollapsed;
	boolean collapsed;
	boolean keepPunct;

	// boolean parseTree;

	Properties stanfordDepProperties;

	public DependencyTagChanger() throws IOException {

		List<String> argList = new ArrayList<>();
		argList.add("-conllx");
		argList.add("-collapsed");
		argList.add("-basic");

		stanfordDependencyTaggerArguments = argList.toArray(new String[argList.size()]);
		stanfordDepProperties = StringUtils.argsToProperties(stanfordDependencyTaggerArguments);

		// String treeFileName = stanfordDepProperties.getProperty("treeFile");
		// String treeDirname = stanfordDepProperties.getProperty("treeDir");
		// String sentFileName = stanfordDepProperties.getProperty("sentFile");
		conllx = stanfordDepProperties.getProperty("conllx") != null;
		basic = stanfordDepProperties.getProperty("basic") != null;
		nonCollapsed = stanfordDepProperties.getProperty("nonCollapsed") != null;
		collapsed = stanfordDepProperties.getProperty("collapsed") != null;

		// parseTree = stanfordDepProperties.getProperty("parseTree") !=
		// null;
		keepPunct = stanfordDepProperties.getProperty("keepPunct") != null;

		dfc = new DataFormatConverter();
		
		tagConverter=new TagConverter();
		
	}

	public static void main(String args[]) throws Exception {
		DependencyTagChanger gcf = new DependencyTagChanger();

		// change the dependency tags in a file
		// gcf.modifyDependencyTagsinData("data/manualmode/hit/manual_mode_cases.txt",
		// "data/dependecyparser/text.txt");

		// change dep tags in recursively in directory
		// String inputDir="data/corpus_validation/good_cases/hit";
		// String outputDir="data/dependecyparser";
		// gcf.modifyDependencyTagsinDataOnDirectory(inputDir, outputDir);

		// tranform ctb syntactic parse tree data to srl conll format. This data
		// then can be used for dependency parsing
		String inputDir = "data/datasets/ctb8";
		String outputDir = "data/datasets/ctb-srl/ctb";
		String outputDir2 = "data/datasets/ctb-srl/lengthbased";
		String outputDir3 = "data/datasets/ctb-srl/final";
		gcf.tranformCTBtoCONLLFOrmat(inputDir, outputDir, outputDir2,outputDir3);
	}

	/**
	 * This function will tranform CTB dataset to SRL conll format. We do this
	 * to create more training data for SRL dependency parser
	 * 
	 * @param outputDir2
	 * @param outputDir3 
	 * @throws Exception 
	 */
	public void tranformCTBtoCONLLFOrmat(String inputDir, String outputDirPath, String outputDirPath2, String outputDirPath3) throws Exception {
		StopWatch stopWatch = new StopWatch();
		int counter = 0;

		// keys
		// token length < 8 : small
		// token length > 8 and < 15 : mid
		// token length > 15 and < 25 : long
		// token length > 25 : very long
		String small = "small";
		String mid = "mid";
		String longs = "long";
		String vlong = "vlong";

		Multimap<String, String> sentenceCONLLMultiMap = LinkedHashMultimap.create();
		Multimap<String, String> sentenceMultiMap2 = LinkedHashMultimap.create();

		List<File> files = new ArrayList<File>();

		StringBuilder outputBuilder = new StringBuilder();

		// lets empty the output directory first
		File outputDir = new File(outputDirPath);	
		File outputDir2 = new File(outputDirPath2);		
		File outputDir3 = new File(outputDirPath3);		
		
		//create or empty output directories
		createOrEmptyDirectory(outputDir);
		createOrEmptyDirectory(outputDir2);
		createOrEmptyDirectory(outputDir3);

		

		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		System.out.println("Reading each file in directory :" + inputDir);
		long totalSentences = 0;
		List<String> allsentenceCONLLList = new ArrayList<>();
		List<String> allSentenceList = new ArrayList<>();
		// iterate through all the files
		for (File file : files) {
			outputBuilder = new StringBuilder();

			List<String> lines = FileUtils.readLines(file, "UTF-8");

			List<String> sentenceList = new ArrayList<>();
			for (String string : lines) {
				if (!Strings.isNullOrEmpty(string)) {
					Tree tree = AnalysisUtilities.getInstance().readTreeFromString(string);
					
					List<Tree> leaves = tree.getLeaves();
					StringBuilder sb=new  StringBuilder();
					for (Tree t : leaves) {
						String token = AnalysisUtilities.getInstance().treeToString(t);
						sb.append(token);
					}
					String sentence=sb.toString();

					int sentenceLength = tree.getLeaves().size();
					String conllDepOutput = generateStanfordDepCOnllFormat2(tree);
					String transformedOutput = transformToCONLL2009(tree, conllDepOutput);
					
					
					sentenceList.add(transformedOutput);
					
					
					allsentenceCONLLList.add(transformedOutput);
					allSentenceList.add(sentence);
					// 1: token length < 8 : small
					// 2: token length > 8 and < 15 : mid
					// 3: token length > 15 and < 25 : long
					// 4: token length > 25 : very long

					int caseType = 0;
					if (sentenceLength <= 8) {
						caseType = 1;
					} else if (sentenceLength > 8 && sentenceLength <15) {
						caseType = 2;
					} else if (sentenceLength >= 15 && sentenceLength < 25) {
						caseType = 3;
					} else if (sentenceLength >= 25) {
						caseType = 4;
					} else {
						System.out.println("case not considered");
					}

					totalSentences++;
					counter++;

					switch (caseType) {
					case 1:
						sentenceCONLLMultiMap.put(small, transformedOutput);
						sentenceMultiMap2.put(small, sentence);
						break;
					case 2:
						sentenceCONLLMultiMap.put(mid, transformedOutput);
						sentenceMultiMap2.put(mid, sentence);
						break;
					case 3:
						sentenceCONLLMultiMap.put(longs, transformedOutput);
						sentenceMultiMap2.put(longs, sentence);
						break;	
					case 4:
						sentenceCONLLMultiMap.put(vlong, transformedOutput);
						sentenceMultiMap2.put(vlong, sentence);
						break;

					default:
						break;
					}

					// we do this as it takes time to load the srl model at
					// first instance
					if (counter == 1) {
						stopWatch.start();
					}
				}
			}

			for (String conllOutput : sentenceList) {
				outputBuilder.append(conllOutput + newline);
				outputBuilder.append(newline);
			}

			// String outputFile = outputDir + File.separator + file.getName();
			String outputFile = outputDir + File.separator + file.getPath().replace(inputDir, "");
			File f = new File(outputFile);

			System.out.println(outputBuilder.toString());
			FileUtils.write(f, outputBuilder.toString(), "UTF-8");
			System.out.println("Wrote new Data to : " + f.toString());
		}

		stopWatch.stop();

		// time in milli seconds
		long totalTime = stopWatch.getTime();

		double tps = ((double) totalTime) / (totalSentences * 1000);
		// double tps = ((double) lines.size()) / (totalTime * 1000);
		System.out.println(
				"Total time taken for " + totalSentences + " sentences" + " : " + stopWatch.toString() + " seconds");
		System.out.println("Total time taken per sentence : " + tps + " seconds");
		System.out.println("Total sentences : " + totalSentences);

		System.out.println("Now writing data by sentence length");

		String fpath1 = outputDir2 + File.separator + "small_conll.txt";
		String fpath2 = outputDir2 + File.separator + "mid_conll.txt";
		String fpath3 = outputDir2 + File.separator + "long_conll.txt";
		String fpath4 = outputDir2 + File.separator + "vlong_conll.txt";
		
		String fpath5 = outputDir2 + File.separator + "small_sentences.txt";
		String fpath6 = outputDir2 + File.separator + "mid_sentencesl.txt";
		String fpath7 = outputDir2 + File.separator + "long_sentences.txt";
		String fpath8 = outputDir2 + File.separator + "vlong_sentences.txt";

		
		//lets write conll sentences by size 
		Collection<String> list = sentenceCONLLMultiMap.get(small);
		StringBuilder outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			outputBuilder1.append(newline);
		}
		FileUtils.write(new File(fpath1), outputBuilder1.toString(), "UTF-8");

		list = sentenceCONLLMultiMap.get(mid);
		outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			outputBuilder1.append(newline);
		}
		FileUtils.write(new File(fpath2), outputBuilder1.toString(), "UTF-8");

		list = sentenceCONLLMultiMap.get(longs);
		outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			outputBuilder1.append(newline);
		}
		FileUtils.write(new File(fpath3), outputBuilder1.toString(), "UTF-8");
		
		
		list = sentenceCONLLMultiMap.get(vlong);
		outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			outputBuilder1.append(newline);
		}
		FileUtils.write(new File(fpath4), outputBuilder1.toString(), "UTF-8");
		
		
		
		// lets write sentences
		 list = sentenceMultiMap2.get(small);
		outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			
		}
		FileUtils.write(new File(fpath5), outputBuilder1.toString(), "UTF-8");

		list = sentenceMultiMap2.get(mid);
		outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			
		}
		FileUtils.write(new File(fpath6), outputBuilder1.toString(), "UTF-8");

		list = sentenceMultiMap2.get(longs);
		outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			
		}
		FileUtils.write(new File(fpath7), outputBuilder1.toString(), "UTF-8");
		
		
		list = sentenceMultiMap2.get(vlong);
		outputBuilder1 = new StringBuilder();
		for (String string : list) {
			outputBuilder1.append(string + newline);
			
		}
		FileUtils.write(new File(fpath8), outputBuilder1.toString(), "UTF-8");
		
		
		
		//build single files
		StringBuilder builder = new StringBuilder();
		for (String string : allsentenceCONLLList) {
			builder.append(string + newline);
			builder.append(newline);
		}
		String allCONLL = outputDir3 + File.separator + "all_sentences_conll.txt";
		String allSentences = outputDir3 + File.separator + "all_sentences.txt";

		FileUtils.write(new File(allCONLL), builder.toString(), "UTF-8");
		
		builder = new StringBuilder();
		for (String string : allSentenceList) {
			builder.append(string + newline);
		}
		FileUtils.write(new File(allSentences), builder.toString(), "UTF-8");
		
	}

	private void createOrEmptyDirectory(File outputDir) throws IOException {
		// TODO Auto-generated method stub
		
		if (outputDir.exists()) {
			FileUtils.cleanDirectory(outputDir);
		} else {
			outputDir.mkdirs();
		}
	}

	private String transformToCONLL2009(Tree tree, String conllDepOutput) throws Exception {

		String newCONLLString = "";
		List<String> tokens = new ArrayList<>();

		// indexes in conll stanford dependency output
		int index = 0;
		int tokenIndex = 1;
		int POSIndex = 3;
		int depIndex = 6;
		int depRelIndex = 7;

		String emptyValue = "_";

		ArrayList<ArrayList<String>> listOfLists = readCONLLFormatFromString(conllDepOutput);

		int rowCount = listOfLists.get(0).size();

		ArrayList<ArrayList<String>> newDataList = new ArrayList<>();

		// create empty COlumn list depending on the number of tokens
		ArrayList<String> emptyColumnList = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			emptyColumnList.add(emptyValue);
		}

		// Lets recreate the conll data for srl

		// add indexlist
		ArrayList<String> indexList = listOfLists.get(index);
		newDataList.add(indexList);

		// add tokenlist
		ArrayList<String> tokenList = listOfLists.get(tokenIndex);
		newDataList.add(tokenList);

		// the next two columns are copy of tokenlist

		newDataList.add(tokenList);
		newDataList.add(tokenList);

		// lets add pos list
		ArrayList<String> posList = listOfLists.get(POSIndex);
		// here we want to replace the CTB POS with LTP POS

		// Lets add tokens from the sentence
		List<Tree> leaves = tree.getLeaves();

		for (Tree t : leaves) {
			String token = AnalysisUtilities.getInstance().treeToString(t);
			tokens.add(token);
		}
		String[] tokensArray = tokens.toArray(new String[tokens.size()]);
		// String[] tokens = {"爱", "的", "人", "也许", "在", "默默", "付出", "。"};
		String[] x = NLPService.getNature("", tokensArray);
		String[] x1 = tagConverter.convertHanLPToLTP(x);
		ArrayList<String> newPOSList = new ArrayList<String>(Arrays.asList(x1));

		newDataList.add(newPOSList);
		newDataList.add(newPOSList);

		// lets add empty feature list
		newDataList.add(emptyColumnList);
		newDataList.add(emptyColumnList);

		// lets add dependency columns
		ArrayList<String> depList = listOfLists.get(depIndex);
		ArrayList<String> depRelList = listOfLists.get(depRelIndex);

		newDataList.add(depList);
		newDataList.add(depList);

		newDataList.add(depRelList);
		newDataList.add(depRelList);

		// we will add two more column lists
		newDataList.add(emptyColumnList);
		newDataList.add(emptyColumnList);

		// Now we will generate the conll string again

		newCONLLString = generateCONLLString(newDataList);
		// System.out.println(newCONLLString);

		return newCONLLString;

	}

	/**
	 * Change dependencies relation recursively in directory
	 * 
	 * @param inputDir
	 * @param outputDirPath
	 * @throws Exception
	 */
	public void modifyDependencyTagsinDataOnDirectory(String inputDir, String outputDirPath) throws Exception {

		StopWatch stopWatch = new StopWatch();
		int counter = 0;

		List<File> files = new ArrayList<File>();

		StringBuilder outputBuilder = new StringBuilder();

		// lets empty the output directory first
		File outputDir;

		outputDir = new File(outputDirPath);
		if (outputDir.exists()) {
			FileUtils.cleanDirectory(outputDir);
		} else {
			outputDir.mkdirs();
		}

		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		System.out.println("Reading each file in directory :" + inputDir);
		long totalSentenes = 0;
		// iterate through all the files
		for (File file : files) {
			outputBuilder = new StringBuilder();

			ArrayList<CoNLLSentence> sentenceList = modifyDependencyTagsinData(file);

			counter++;
			// we do this as it takes time to load the srl model at
			// first instance
			if (counter == 1) {
				stopWatch.start();
			}

			for (CoNLLSentence coNLLSentence : sentenceList) {
				outputBuilder.append(coNLLSentence.getCoNLLSentence() + newline);
				outputBuilder.append(newline);
			}

			// String outputFile = outputDir + File.separator + file.getName();
			String outputFile = outputDir + File.separator + file.getPath().replace(inputDir, "");
			File f = new File(outputFile);

			FileUtils.write(f, outputBuilder.toString(), "UTF-8");
			System.out.println("Wrote new Data to : " + f.toString());

		}

		stopWatch.stop();

		// time in milli seconds
		long totalTime = stopWatch.getTime();

		double tps = ((double) totalTime) / (totalSentenes * 1000);
		// double tps = ((double) lines.size()) / (totalTime * 1000);
		System.out.println(
				"Total time taken for " + totalSentenes + " sentences" + " : " + stopWatch.toString() + " seconds");
		System.out.println("Total time taken per sentence : " + tps + " seconds");
	}

	/**
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @return
	 */
	public ArrayList<CoNLLSentence> modifyDependencyTagsinData(ArrayList<CoNLLSentence> sentenceList) {

		DataFormatConverter dfc = new DataFormatConverter();

		for (CoNLLSentence coNLLSentence : sentenceList) {

			// generate the conll output from stanford dependency parser. this
			// output has differnt number of columns and indexing.
			String output = generateStanfordDepCOnllFormat2(coNLLSentence);

			// get data in array format
			ArrayList<ArrayList<String>> stanfordDepLines = readCONLLFormatFromString(output);
			ArrayList<ArrayList<String>> listOfLists = dfc.readCONLLDataColumnwise(coNLLSentence);

			// get the new updated dependencies. This function will return the
			// updated data list
			ArrayList<ArrayList<String>> newlistOfLists = replaceDepColumns(listOfLists, stanfordDepLines);

			// get the newly updated conll string. this also updates the indexes
			// provided by stanford dependencies.
			String newConLLString = generateCONLLString(newlistOfLists);

			// lets set the newly updated information
			coNLLSentence.setCoNLLSentence(newConLLString);
			coNLLSentence.setDataList(newlistOfLists);

		}

		return sentenceList;

	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public ArrayList<CoNLLSentence> modifyDependencyTagsinData(File file) {

		DataFormatConverter dfc = new DataFormatConverter();

		ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);

		sentenceList = modifyDependencyTagsinData(sentenceList);

		return sentenceList;

	}

	/**
	 * Change dependencies relation for a given input file
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @throws IOException
	 */
	public void modifyDependencyTagsinData(String inputFile, String outputFile) throws IOException {

		StringBuilder outputBuilder = new StringBuilder();
		File file = new File(inputFile);
		ArrayList<CoNLLSentence> sentenceList = modifyDependencyTagsinData(file);

		for (CoNLLSentence coNLLSentence : sentenceList) {
			outputBuilder.append(coNLLSentence.getCoNLLSentence() + newline);
			outputBuilder.append(newline);
		}

		File f = new File(outputFile);

		FileUtils.write(f, outputBuilder.toString(), "UTF-8");
		System.out.println("wrote to : " + f.toString());

	}

	/**
	 * Generates the conll string.
	 * 
	 * @param newlistOfLists
	 * @return
	 */
	private String generateCONLLString(ArrayList<ArrayList<String>> newlistOfLists) {
		StringBuilder sb = new StringBuilder();

		int rows = newlistOfLists.get(0).size();
		int columns = newlistOfLists.size();

		int j = 0;
		for (int i = 0; i < rows; i++) {
			j = 0;
			for (int k = 0; k < columns; k++) {
				if (j < columns - 1) {
					sb.append(newlistOfLists.get(k).get(i) + delimiter);
				} else {
					sb.append(newlistOfLists.get(k).get(i));
				}
				j++;

			}

			if (i < rows - 1)
				sb.append(newline);

		}

		// System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * This function will replace the dependency columns in the srl data list
	 * with stanford dependencies
	 * 
	 * @param listOfLists
	 * @param stanfordDepLines
	 * @return
	 */
	private ArrayList<ArrayList<String>> replaceDepColumns(ArrayList<ArrayList<String>> listOfLists,
			ArrayList<ArrayList<String>> stanfordDepLines) {

		ArrayList<ArrayList<String>> newlistOfLists = new ArrayList<>(listOfLists);

		// the dep and deprel columns in the stanford dependency conll output;
		int stanford_dep_head_column_no = 6;
		int stanford_dep_rel_column_no = 7;

		ArrayList<String> stanford_dep_column = stanfordDepLines.get(stanford_dep_head_column_no);
		ArrayList<String> stanford_dep_rel_column = stanfordDepLines.get(stanford_dep_rel_column_no);

		// NOTE: we don't need to increment dep column list by 1. its a bug.
		// // the stanford dependency list starts from index 0. we need to
		// // increment it by 1 for our usage purpose.
		// ArrayList<String> stanford_dep_column_updated_list = new
		// ArrayList<>();
		// for (String string : stanford_dep_column) {
		// int entry = Integer.parseInt(string);
		// entry = entry + 1;
		//
		// String s = Integer.toString(entry);
		// stanford_dep_column_updated_list.add(s);
		//
		// }

		// we are going to replace the dependency and dependency relations
		// columns
		newlistOfLists.set(conllformat_head_column_no, stanford_dep_column);
		newlistOfLists.set(conllformat_phead_column_no, stanford_dep_column);

		newlistOfLists.set(conllformat_deprel_column_no, stanford_dep_rel_column);
		newlistOfLists.set(conllformat_pdeprel_column_no, stanford_dep_rel_column);

		return newlistOfLists;

	}

	public ArrayList<ArrayList<String>> readCONLLFormatFromString(String conllString) {

		String[] lines = (NEWLINE_PATTERN.split(conllString.toString()));
		boolean readError = false;

		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

		int total_columns = TAB_PATTERN.split(lines[0]).length;

		for (int i = 0; i < total_columns; i++) {
			listOfLists.add(new ArrayList<String>());
		}

		for (int k = 0; k < lines.length; k++) {
			String line = lines[k];
			String[] cols = TAB_PATTERN.split(line);

			for (int index = 0; index <= cols.length - 1; index++) {

				try {
					listOfLists.get(index).add(cols[index]);
				} catch (java.lang.IndexOutOfBoundsException e) {
					readError = true;

					e.printStackTrace();
					// badCaseList.add(coNLLSentence);
				}
			}

		}

		if (readError) {
			System.err.println("error reading correct number of columns: " + conllString);
		}

		return listOfLists;

	}

	/**
	 * There are two different output formats : collapsed dependencies and basic
	 * dependencies. http://nlp.stanford.edu/software/dependencies_manual.pdf
	 * 
	 * @param coNLLSentence
	 * @return
	 */
	private String generateStanfordDepCOnllFormat2(Tree tree) {

		String output = "";

		if (!basic && !collapsed) {
			if (conllx) {
				basic = true; // default to basic dependencies for conllx
			} else {
				collapsed = true; // otherwise, default to collapsed
									// dependencies
			}
		}

		Predicate<String> puncFilter;

		if (keepPunct) {
			puncFilter = Filters.acceptFilter();
		} else {
			puncFilter = new ChineseTreebankLanguagePack().punctuationWordRejectFilter();
		}

		GrammaticalStructure gs = new ChineseGrammaticalStructure(tree, puncFilter);

		// if (collapsed) {
		// if (basic || nonCollapsed) {
		// //System.out.println("----------- collapsed dependencies
		// -----------");
		// }
		// // printDependencies(gs,
		// // gs.typedDependenciesCollapsed(Extras.MAXIMAL), t, conllx,
		// // false);
		// output = GrammaticalStructure.dependenciesToString(gs,
		// gs.typedDependenciesCollapsed(Extras.MAXIMAL), t,
		// conllx, false);
		// //System.out.println(output);
		// }

		if (basic) {
			if (collapsed || nonCollapsed) {
				// System.out.println("------------- basic dependencies
				// ---------------");
			}
			output = GrammaticalStructure.dependenciesToString(gs, gs.typedDependenciesCollapsed(Extras.MAXIMAL), tree,
					conllx, false);
			// System.out.println(output);
		}

		return output;
	}

	/**
	 * There are two different output formats : collapsed dependencies and basic
	 * dependencies. http://nlp.stanford.edu/software/dependencies_manual.pdf
	 * 
	 * @param coNLLSentence
	 * @return
	 */
	private String generateStanfordDepCOnllFormat2(CoNLLSentence coNLLSentence) {

		String output = "";

		if (!basic && !collapsed) {
			if (conllx) {
				basic = true; // default to basic dependencies for conllx
			} else {
				collapsed = true; // otherwise, default to collapsed
									// dependencies
			}
		}

		List<String> tokenList = coNLLSentence.getTokenList();
		String[] tokens = tokenList.toArray(new String[tokenList.size()]);
		Tree t = AnalysisUtilities.getInstance().parseChineseSentence(tokens, false).parse;
		Predicate<String> puncFilter;

		if (keepPunct) {
			puncFilter = Filters.acceptFilter();
		} else {
			puncFilter = new ChineseTreebankLanguagePack().punctuationWordRejectFilter();
		}

		GrammaticalStructure gs = new ChineseGrammaticalStructure(t, puncFilter);

		// if (collapsed) {
		// if (basic || nonCollapsed) {
		// //System.out.println("----------- collapsed dependencies
		// -----------");
		// }
		// // printDependencies(gs,
		// // gs.typedDependenciesCollapsed(Extras.MAXIMAL), t, conllx,
		// // false);
		// output = GrammaticalStructure.dependenciesToString(gs,
		// gs.typedDependenciesCollapsed(Extras.MAXIMAL), t,
		// conllx, false);
		// //System.out.println(output);
		// }

		if (basic) {
			if (collapsed || nonCollapsed) {
				// System.out.println("------------- basic dependencies
				// ---------------");
			}
			output = GrammaticalStructure.dependenciesToString(gs, gs.typedDependenciesCollapsed(Extras.MAXIMAL), t,
					conllx, false);
			// System.out.println(output);
		}

		return output;
	}

	public String getCONLLDependency(Tree tree) {
		String output = "";

		return output;
	}

	/**
	 * Please ignore this test function
	 */
	private void test() {
		// TODO Auto-generated method stub

		String[] args = { "-conllx", "-collapsed", "-basic" };
		// String[] args = { "-conllx", "-collapsed" };
		Properties props = StringUtils.argsToProperties(args);

		String treeFileName = props.getProperty("treeFile");
		String treeDirname = props.getProperty("treeDir");
		String sentFileName = props.getProperty("sentFile");
		boolean conllx = props.getProperty("conllx") != null;
		boolean basic = props.getProperty("basic") != null;
		boolean nonCollapsed = props.getProperty("nonCollapsed") != null;
		boolean collapsed = props.getProperty("collapsed") != null;
		boolean parseTree = props.getProperty("parseTree") != null;
		boolean keepPunct = props.getProperty("keepPunct") != null;

		if (!basic && !collapsed) {
			if (conllx) {
				basic = true; // default to basic dependencies for conllx
			} else {
				collapsed = true; // otherwise, default to collapsed
									// dependencies
			}
		}

		List<String> trees = new ArrayList<>();
		trees.add(
				"(ROOT (IP (NP (NP (NR 海拉尔) (PU 、) (NR 满洲里) (ETC 等)) (NP (NN 城市))) (VP (ADVP (AD 先后)) (VP (VV 建起) (NP (ADJP (JJ 星级)) (NP (NN 宾馆) (NN 饭店))) (QP (CD 十余) (CLP (M 所))))) (PU 。))))");
		trees.add(
				"(ROOT (IP (NP (DNP (NP (NN 投资) (NN 环境)) (DEG 的)) (NP (NN 改善))) (PU ，) (VP (VV 吸引) (AS 了) (NP (DNP (NP (NP (NN 国内外)) (NP (NP (ADJP (JJ 大)) (NP (NN 财团))) (PU 、) (NP (ADJP (JJ 大)) (NP (NN 企业))))) (DEG 的)) (NP (NP (ADJP (JJ 雄厚)) (NP (NN 资金))) (PU 、) (NP (ADJP (JJ 先进)) (NP (NN 经验))) (PU 、) (NP (ADJP (JJ 先进)) (NP (NN 技术))))) (IP (VP (VV 接踵而至)))) (PU 。))))");

		for (String string : trees) {

			Tree t = AnalysisUtilities.getInstance().readTreeFromString(string);
			Predicate<String> puncFilter;

			if (keepPunct) {
				puncFilter = Filters.acceptFilter();
			} else {
				puncFilter = new ChineseTreebankLanguagePack().punctuationWordRejectFilter();
			}

			GrammaticalStructure gs = new ChineseGrammaticalStructure(t, puncFilter);

			if (collapsed) {
				if (basic || nonCollapsed) {
					System.out.println("----------- collapsed dependencies -----------");
				}
				// printDependencies(gs,
				// gs.typedDependenciesCollapsed(Extras.MAXIMAL), t, conllx,
				// false);
				String output = GrammaticalStructure.dependenciesToString(gs,
						gs.typedDependenciesCollapsed(Extras.MAXIMAL), t, conllx, false);
				System.out.println(output);
			}

			if (basic) {
				if (collapsed || nonCollapsed) {
					System.out.println("------------- basic dependencies ---------------");
				}
				String output = GrammaticalStructure.dependenciesToString(gs,
						gs.typedDependenciesCollapsed(Extras.MAXIMAL), t, conllx, false);
				System.out.println(output);
			}

		}

	}
}
