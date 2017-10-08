
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

import com.emotibot.srl.datastructures.SRLOptions;
import com.google.common.base.Strings;

import se.lth.cs.srl.SemanticLabelerPipeLine;

/**
 * @author Sanjay
 *
 */
public class TestBatchSRL {
	static SemanticLabelerPipeLine slp_pipeline;

	/**
	 * Run batch SRL on Directory
	 * 
	 * @throws Exception
	 */
	public void runBatchSRLOnDir(String inputDir, String outputDir) throws Exception {

		StopWatch stopWatch = new StopWatch();
		int counter = 0;

		List<File> files = new ArrayList<File>();

		StringBuilder outputBuilder = new StringBuilder();
		SRLOptions options = new SRLOptions();
		options.model = 2;
		options.produceHITFormat = true;

		options.usePOSFromNLP = true;
		options.useDEPFromNLP = false;

		// options.case_type = "vip";

		String result = "";

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

//					result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChinese(input, options)
//							.toString();
					result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChinese(input, options)
							.getHitSentence();

					// result = SemanticLabelerPipeLine.getChineseInstance(options)
					// .performSRLForChineseForSRLVIZ(input,
					// options).toString();

					counter++;
					// we do this as it takes time to load the srl model at
					// first instance
					if (counter == 1) {
						stopWatch.start();
					}

					// System.out.println(result);
					outputBuilder.append(result + "\n");

				}
			}

			// String outputFile = outputDir + File.separator + file.getName();
			String outputFile = outputDir + File.separator + file.getPath().replace(inputDir, "");
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
	 * Run batch srl on sentences in a file.
	 * 
	 * @param inputFile
	 * @param outputFile
	 */
	public void runBatchSRLOnFile(String inputFile, String outputFile) {

		File file = new File(inputFile);
		File outputF = new File(outputFile);

		StringBuilder outputBuilder = new StringBuilder();
		SRLOptions options = new SRLOptions();
		options.model = 2;
		options.produceHITFormat = true;

		options.usePOSFromNLP = true;
		options.useDEPFromNLP = false;

		String result = "";

		try {
			List<String> contents = FileUtils.readLines(file, "UTF-8");

			for (String string : contents) {

				String input = string;
				if (!Strings.isNullOrEmpty(input)) {

					// result =
					// SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChinese(input,
					// options)
					// .toString();
					// result =
					// SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChinese(input,
					// options).getHitSentence();

					result = SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChinese(input, options)
							.getConllSentence();

					// result =
					// SemanticLabelerPipeLine.getChineseInstance(options).performSRLForChineseForSRLVIZ(input,
					// options)
					// .toString();

					// System.out.println(result);
					// outputBuilder.append(result + "\n");

					outputBuilder.append(result + "\n\n");

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileUtils.write(outputF, outputBuilder.toString(), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		TestBatchSRL batchSRL = new TestBatchSRL();

//		String inputFile = "data/cases/ctrip/ctrip_930.txt";
//		String outputFile = "data/cases/ctrip/oput/";
//		batchSRL.runBatchSRLOnFile(inputFile, outputFile);

		String inputDir = "data/cases/ctrip_20170414/input";
		String outputDir = "data/cases/ctrip_20170414/output";

		batchSRL.runBatchSRLOnDir(inputDir, outputDir);

	}

}
