/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.emotibot.enlp.EWord;
import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.SegmentResult;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.utilities.AnalysisUtilities;
import com.google.common.base.Strings;

import edu.stanford.nlp.trees.Tree;
import se.lth.cs.srl.SemanticLabelerPipeLine;

/**
 * @author Sanjay
 *
 */
public class SyntacticParseTreeGeneration {

	
	/**
	 * Generate Syntactic parse tree
	 * 
	 * @param inputFolder
	 * @param outputFolder
	 * @throws IOException
	 */
	public void generateSyntaxTrees(String inputDir, String outputDir,boolean recursive) throws IOException {
		// TODO Auto-generated method stub
		StopWatch stopWatch = new StopWatch();
		int counter = 0;

		
		File inputF=new File(inputDir);
		File outputF = new File(outputDir);

		

		StringBuilder outputBuilder = new StringBuilder();
		
		
		FileUtils.forceDelete(outputF);
		
		System.out.println("Reading  file " + inputDir);
		long totalSentenes = 0;
		// iterate through all the files
		
			outputBuilder = new StringBuilder();
			List<String> contents = FileUtils.readLines(inputF, "UTF-8");
			totalSentenes += contents.size();

			for (String string : contents) {

				String input = string;
				if (!Strings.isNullOrEmpty(input)) {

					// result = SemanticLabelerPipeLine.getChineseInstance(options)
					// .performSemanticRoleLabelingForChineseJSON(input,
					// options);
					//
					// System.out.println(result);

					Tree tree = AnalysisUtilities.getInstance().parseChineseSentence(input, false).parse;

					counter++;
					// we do this as it takes time to load the srl model at
					// first instance
					if (counter == 1) {
						stopWatch.start();
					}

					if (tree != null) {
						
						String out=tree.toString() + "\n";
						FileUtils.writeStringToFile(outputF, out, "UTF-8", true);
						
					}
					// System.out.println(result);

				}
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
	 * Generate Syntactic parse tree
	 * 
	 * @param inputFolder
	 * @param outputFolder
	 * @throws IOException
	 */
	public void generateSyntaxTrees(String inputDir, String outputDir) throws IOException {
		// TODO Auto-generated method stub
		StopWatch stopWatch = new StopWatch();
		int counter = 0;

		File outputF = new File(outputDir);

		List<File> files = new ArrayList<File>();

		StringBuilder outputBuilder = new StringBuilder();
		
		

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
			List<String> contents = FileUtils.readLines(file, "UTF-8");
			totalSentenes += contents.size();

			for (String string : contents) {

				String input = string;
				if (!Strings.isNullOrEmpty(input)) {

					// result = SemanticLabelerPipeLine.getChineseInstance(options)
					// .performSemanticRoleLabelingForChineseJSON(input,
					// options);
					//
					// System.out.println(result);

					Tree tree = AnalysisUtilities.getInstance().parseChineseSentence(input, false).parse;

					counter++;
					// we do this as it takes time to load the srl model at
					// first instance
					if (counter == 1) {
						stopWatch.start();
					}

					if (tree != null) {
						outputBuilder.append(tree + "\n");
					}
					// System.out.println(result);

				}
			}

			String outputFile = outputDir + File.separator + file.getName();
			File f = new File(outputFile);

			FileUtils.write(f, outputBuilder.toString(), "UTF-8");

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
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		SyntacticParseTreeGeneration sytg = new SyntacticParseTreeGeneration();
		String inputFolder = "data/syntactictree/input";
		String outputFolder = "data/syntactictree/output";

		sytg.generateSyntaxTrees(inputFolder, outputFolder);
	}

}
