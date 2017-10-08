/**
 * 
 */
package com.emotibot.srl.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.TrainSRL;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.google.common.base.Strings;

import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.SemanticLabelerPipeLine;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.languages.Language.L;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;

/**
 * @author Sanjay
 *
 */
public class SRLEvaluation {
	DataFormatConverter dfc;
	CompletePipeline chinesePipleLine;
	protected static final Pattern NEWLINE_PATTERN = Pattern.compile("\n");

	public SRLEvaluation() {
		dfc = new DataFormatConverter();
	}

	private String testFile = "evaluation/data/srlTestSentences.txt";
	private String srlDataFile = "evaluation/data/vip_srlDataFile.txt";
	//private String trainFile = "evaluation/data/srlTrainFile.txt";
	private String trainFile = "evaluation/data/srlTrainFile.txt";
	private String goldStandardFile = "evaluation/data/srlGoldStandard.txt";
	private String modelFile = "evaluation/models/srl_evaluation_model.mdl";
	private String evaluationScript = "evaluation/scripts/eval09.pl";
	private String resultFile = "evaluation/results.txt";

	private String goodGoldStandardFile = "evaluation/data/final/goodGoldStandardFile.txt";
	private String badGoldStandardFile = "evaluation/data/final/badGoldStandardFile.txt";
	private String goodSRLDataFile = "evaluation/data/final/goodSRLDataFile.txt";
	private String badSRLDataFile = "evaluation/data/final/badSRLDataFile.txt";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SRLEvaluation srlEvaluation = new SRLEvaluation();
		srlEvaluation.doEvaluation();

	}

	private void doEvaluation() throws Exception {

		 setupData();

		// Train model on training data
//		 trainModel();
//
//		setupModel();

		runModel();

		// run evaluation script

		// runEvaluationScript();

	}

	private void trainModel() {
		// TODO Auto-generated method stub
		String lang = "chi";

		String training_file = trainFile;
		String output_model_file = modelFile;
		String feature_dir = "-fdir resources/semantic_role_labeling/featuresets/chi";

		TrainSRL trainSRL = new TrainSRL();
		try {
			trainSRL.trainModel(lang, training_file, output_model_file, feature_dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Run model on the data
	 * 
	 * @throws Exception
	 */
	private void runModel() throws Exception {

		
		
		File file=new File(goldStandardFile);
		List<CoNLLSentence> srlData = dfc.readCoNLLFormatCorpus(file, Format.CONLL,true);

		List<EvaluationPair> evalPairList = new ArrayList<EvaluationPair>();

		int counter = 0;
		for (CoNLLSentence coNLLSentence : srlData) {
			counter++;

			CoNLLSentence systemSentence = new CoNLLSentence();
			systemSentence.setSentence(coNLLSentence.getSentence());

			EvaluationPair evPair = new EvaluationPair(counter, coNLLSentence, systemSentence);
			evalPairList.add(evPair);

		}

		if (chinesePipleLine == null) {
			setupModel();
		}

		SRLOptions options = new SRLOptions();
		options.model = 2;

		// First run the srl parser and add srl output
		for (EvaluationPair evaluationPair : evalPairList) {
			CoNLLSentence systemSentence = evaluationPair.getSystemSentence();
			String s = systemSentence.getSentence();
			Sentence sentence = chinesePipleLine.parse(s, options.model);

			String data = sentence.toString();
			data=data.concat("\n");
			String[] lines = (NEWLINE_PATTERN.split(data));

			systemSentence.setLines(lines);
			systemSentence.setCoNLLSentence(data);

		}

		// data validation
		// there are differences of tokenizer output between version. The output
		// from system might have different token length. We will filter those
		// cases

		List<EvaluationPair> goodEvaluationPair = new ArrayList<EvaluationPair>();
		List<EvaluationPair> badEvaluationPair = new ArrayList<EvaluationPair>();

		for (EvaluationPair evaluationPair : evalPairList) {

			CoNLLSentence goldSentence = evaluationPair.getGoldSentence();
			CoNLLSentence systemSentence = evaluationPair.getSystemSentence();

			int sLength = goldSentence.getLines().length;
			int gLength = systemSentence.getLines().length;

			if (gLength != sLength) {
				badEvaluationPair.add(evaluationPair);
			} else {
				goodEvaluationPair.add(evaluationPair);
			}

		}

		List<CoNLLSentence> goodSystemSentences = new ArrayList<CoNLLSentence>();
		List<CoNLLSentence> badSystemSentences = new ArrayList<CoNLLSentence>();
		List<CoNLLSentence> goodGoldSentences = new ArrayList<CoNLLSentence>();
		List<CoNLLSentence> badGoldSentences = new ArrayList<CoNLLSentence>();

		for (EvaluationPair evaluationPair : goodEvaluationPair) {
			CoNLLSentence goldSentence = evaluationPair.getGoldSentence();
			CoNLLSentence systemSentence = evaluationPair.getSystemSentence();

			goodSystemSentences.add(systemSentence);
			goodGoldSentences.add(goldSentence);
		}

		for (EvaluationPair evaluationPair : badEvaluationPair) {
			CoNLLSentence goldSentence = evaluationPair.getGoldSentence();
			CoNLLSentence systemSentence = evaluationPair.getSystemSentence();

			badSystemSentences.add(systemSentence);
			badGoldSentences.add(goldSentence);

		}

		writeData(goodGoldStandardFile, goodGoldSentences);
		writeData(badGoldStandardFile, badGoldSentences);
		writeData(goodSRLDataFile, goodSystemSentences);
		writeData(badSRLDataFile, badSystemSentences);

		// after all finish, we add one final new line
		
		//System.out.println("writing output to file : " + testFile);
		//FileUtils.write(new File(testFile), sb.toString());
	}

	/**
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws ZipException 
	 * 
	 */
	private void setupModel() throws ZipException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		Map<String, String> args = new LinkedHashMap<String, String>();

		args.put("srl1", modelFile);
		args.put("srl2", modelFile);

		SemanticLabelerPipeLine slrPipeline=new SemanticLabelerPipeLine();
		
		System.out.println("Loading srl model related data..");
		CompletePipelineCMDLineOptions options1 = slrPipeline.createOptionsForChinese(args);
		try {
			chinesePipleLine = slrPipeline.getCompletePipeline(options1, L.chi);

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void setupData() {

		
		File file=new File(srlDataFile);
		ArrayList<CoNLLSentence> srlData = dfc.readCoNLLFormatCorpus(file, Format.CONLL,true);
		int seed = 123;

		// shuffle the data first
		Collections.shuffle(srlData, new Random(seed));

		// create test train file split
		double split = 0.85;

		int dataLength = srlData.size();
		int trainSize = (int) (split * dataLength);
		int testSize = (int) ((1 - split) * dataLength);

		List<CoNLLSentence> trainData = new ArrayList<CoNLLSentence>(srlData.subList(0, trainSize));
		List<CoNLLSentence> testData = new ArrayList<CoNLLSentence>(srlData.subList(trainSize + 1, dataLength));

		System.out.println("total data size: " + dataLength);
		System.out.println("train data size: " + trainSize);
		System.out.println("test data size: " + testSize);

		
		writeData(trainFile, trainData);
		// writeData(testFile, testData);
		writeData(goldStandardFile, testData);
	}

	/**
	 * 
	 * @param file
	 * @param data
	 */
	private void writeData(String outputFile, List<CoNLLSentence> data) {
		StringBuilder sb = new StringBuilder();
		File file = new File(outputFile);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		int counter = 0;
		for (CoNLLSentence coNLLSentence : data) {
			// String sentence = coNLLSentence.getSentence();
			String sentence = coNLLSentence.getCoNLLSentence();
			/**
			 * 
			 */
			if (!Strings.isNullOrEmpty(sentence)) {
				sb.append(sentence + "\n");
				counter++;
			}
		}

		try {
			FileUtils.write(file, sb.toString());

			System.out.println("wrote to " + file.toString() + " total " + counter + " sentence written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void runEvaluationScript() {
		// TODO Auto-generated method stub

		// perl eval09.pl -q -g srl-emotibot-train_conll.txt -s
		// srl-emotibot-train_conll.txt

		File f1 = new File(evaluationScript);
		File f2 = new File(goldStandardFile);
		File f3 = new File(testFile);
		File f4 = new File(resultFile);

		StringBuilder sb = new StringBuilder();
		// sb.append("perl ");
		// sb.append(evaluationScript);
		// sb.append(" -q ");
		// sb.append(" -g " + goldStandardFile);
		// sb.append(" -s " + testFile);
		// sb.append(" -o " + resultFile);

		// sb.append("perl ");
		// sb.append("\"" +f1.getAbsolutePath()+ "\"");
		// sb.append(" -q ");
		sb.append(" -g " + "\"" + f2.getAbsolutePath() + "\"");
		sb.append(" -s " + "\"" + f3.getAbsolutePath() + "\"");
		sb.append(" > " + "\"" + f4.getAbsolutePath() + "\"");

		// String command = sb.toString();
		// String command2="perl -e \"print \"Hello World\"";
		// System.out.println(command);

		List<String> cmds = new ArrayList<String>();
		cmds.add("perl");
		cmds.add(f1.getAbsolutePath());
		cmds.add("-g ");
		cmds.add(f2.getAbsolutePath());
		cmds.add("-s");
		cmds.add(f3.getAbsolutePath());
		cmds.add(" > ");
		cmds.add(f4.getAbsolutePath());

		String[] cmd = cmds.toArray(new String[cmds.size()]);

		try {

			// Process proc = Runtime.getRuntime().exec(command);
			// System.out.println(proc.getOutputStream());
			// proc.toString();

			// String[] cmd = {"perl",f1.getAbsolutePath()," -g " +
			// f2.getAbsolutePath()," -s " + f3.getAbsolutePath() ," > " +
			// f4.getAbsolutePath() };
			// System.out.println(cmd.toString());
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = output.readLine();
			System.out.println(line);
			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
