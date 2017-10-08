package com.emotibot.srl.test.frames.conversations;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * 
 */

/**
 * @author Sanjay
 *
 */
public class FrameElementsModelTraining {

	static String modelPath = "data/temp/frames_data/model_related/model/frame_elements_crf_model.ser.gz";
	static String[] frameElementModelArgs = { "-prop", "data/temp/frames_data/model_related/frame-elements.props" };
	static String spaceTokenizedSentenceFile = "data/temp/frames_data/model_related/test/space_tokenized_utterances.txt";
	static String spaceTokenizedSentenceFile2 = "data/temp/frames_data/model_related/test/space_tokenized_utterances2.txt";

	
	String evalFile="data/temp/frames_data/model_related/test/evalFile.txt";
	
	/**
	 * 
	 */
	public FrameElementsModelTraining() {

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		FrameElementsModelTraining frameNE = new FrameElementsModelTraining();
		frameNE.test();
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void test() throws Exception {

		// Train the NER Model based on the properties file
		trainFER();

		evaluateFER(evalFile);

		// RUn ner on a test file
		//testFER();

	}

	/**
	 * Evaluate NER
	 * @throws Exception
	 */
	private void evaluateFER(String file) throws Exception {

		String[] args1 = { "-loadClassifier", modelPath, "-testFile", file };
		CRFClassifier.trainCRFClassifier(args1);
	}

	private static void testFER() throws ClassCastException, ClassNotFoundException, IOException {

		Properties props = new Properties();
		props.put("tokenizerOptions", "tokenizeNLs=true");
		props.put("tokenizerFactory", "edu.stanford.nlp.process.WhitespaceTokenizer");

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(modelPath, props);

		String testFile = spaceTokenizedSentenceFile2;
		File file = new File(testFile);
		List<String> contents = FileUtils.readLines(file, "UTF-8");

		int i=0;
		for (String string : contents) {
			i++;
			
			String output1=classifier.classifyToString(string);
			String output2=classifier.classifyWithInlineXML(string);
			if(i==47){
				int temp=10;
				output1=classifier.classifyToString(string);
			}
			
			
//			List<List<CoreLabel>> labels = classifier.classify(string);
//			        for (List<CoreLabel> lcl : labels) {
//			          for (CoreLabel cl : lcl) {
//			            System.out.print(i++ + ": ");
//			            System.out.println(cl.toShorterString());
//			          }
//			        }
			      
			
			System.out.println(i + " : " + output2);
		}

	}

	private void trainFER() throws Exception {

		CRFClassifier.trainCRFClassifier(frameElementModelArgs);
	}
}
