package com.emotibot.srl.test.frames.conversations;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.base.Strings;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class FERCommandlineDemo {
	EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();
	AbstractSequenceClassifier<CoreLabel> fe_classifier_prod;
	AbstractSequenceClassifier<CoreLabel> fe_classifier_dev;
	static String modelPath1 = "resources/semantic_frame/model/frame_elements_crf_model.ser.gz";
	static String modelPath2 = "data/temp/frames_data/model_related/model/frame_elements_crf_model.ser.gz";

	public FERCommandlineDemo() throws ClassCastException, ClassNotFoundException, IOException {
		loadModel();
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
				"|                                  Chinese NER                            							|\n");
		sb.append(
				"======================================================================================================== \n");

		sb.append(
				"                                   1. Run FER for input sentence  (prod model)                                        \n");
		sb.append(
				"                                   2. Run FER for input sentence  (dev model)                                        \n");
		sb.append(
				"                                   3. Run FER API (using classifySentence for segmentation)                  \n");
		sb.append(
				"                                   4. Run FER using classifySentence for segmentation                              \n");
		sb.append(
				"                                   5. Run FER using classifySentence for segmentation (VIP seg)                              \n");
		sb.append("                                 6. quit                                                \n");

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

						StringBuilder sb1 = new StringBuilder();
						for (String string : tokens) {
							sb1.append(string + Constants.SPACE);
						}

						// String
						// output=fe_classifier.classifyToString(wordDictBuilder.toString());
						String spaceTokenizedSentence = sb1.toString();
						String output1 = fe_classifier_prod.classifyToString(spaceTokenizedSentence, "slashTags", false);

						/**
						 * This is to get the datastructure of the program
						 */
						List<List<CoreLabel>> lcl1 = fe_classifier_prod.classify(spaceTokenizedSentence);

						for (List<CoreLabel> lcl : lcl1) {
							for (CoreLabel cl : lcl) {
								// System.out.print(cl.keySet());

								String answer = cl.get(CoreAnnotations.AnswerAnnotation.class);
								String t = cl.get(CoreAnnotations.TextAnnotation.class);
								System.out.print(t + " ");
								System.out.print(answer + " ");
								// System.out.print(i++ + ": ");
								// System.out.println(cl.toShorterString());
							}
						}

						System.out.println();
						// another kind of output format
						String output2 = fe_classifier_prod.classifyWithInlineXML(spaceTokenizedSentence);
						System.out.println("Input tokens:  " + spaceTokenizedSentence);
						System.out.println(output2);
						System.out.println(output1);

					} else
						System.out.println("empty input");

					break;
					
					
				case 2:
					input = readLine();

					if (!Strings.isNullOrEmpty(input)) {
						String[] tokens = emotibotTokenzier.tokenize(input);

						StringBuilder sb1 = new StringBuilder();
						for (String string : tokens) {
							sb1.append(string + Constants.SPACE);
						}

						// String
						// output=fe_classifier.classifyToString(wordDictBuilder.toString());
						String spaceTokenizedSentence = sb1.toString();
						String output1 = fe_classifier_dev.classifyToString(spaceTokenizedSentence, "slashTags", false);

						/**
						 * This is to get the datastructure of the program
						 */
						List<List<CoreLabel>> lcl1 = fe_classifier_dev.classify(spaceTokenizedSentence);
						int length=lcl1.size();
						for (List<CoreLabel> lcl : lcl1) {
							for (CoreLabel cl : lcl) {
								// System.out.print(cl.keySet());

								String answer = cl.get(CoreAnnotations.AnswerAnnotation.class);
								String t = cl.get(CoreAnnotations.TextAnnotation.class);
								System.out.print(t + " ");
								System.out.print(answer + " ");
								// System.out.print(i++ + ": ");
								// System.out.println(cl.toShorterString());
							}
						}

						System.out.println();
						// another kind of output format
						String output2 = fe_classifier_dev.classifyWithInlineXML(spaceTokenizedSentence);
						System.out.println("Input tokens:  " + spaceTokenizedSentence);
						System.out.println(output2);
						System.out.println(output1);

					} else
						System.out.println("empty input");

					break;
					
				case 3:
					input = readLine();

					if (!Strings.isNullOrEmpty(input)) {
						String[] tokens = emotibotTokenzier.tokenize(input);

						String str = FERModel.getFERString(tokens);
						
						System.out.println(str);


					} else
						System.out.println("empty input");

					break;


				case 4:
					input = readLine();

					if (!Strings.isNullOrEmpty(input)) {
						String[] tokens = emotibotTokenzier.tokenize(input);

						List<CoreLabel> tokenCoreLabelList = edu.stanford.nlp.ling.Sentence.toCoreLabelList(tokens);
						List<CoreLabel> cl_list = fe_classifier_dev.classifySentence(tokenCoreLabelList);
						for (CoreLabel cl : cl_list) {
							System.out.println(cl.toShorterString());
							//System.out.println(cl.toShorterString("Answer"));
//							System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class));

						}
						

					} else
						System.out.println("empty input");

					break;
					
					
				case 5:
					input = readLine();

					if (!Strings.isNullOrEmpty(input)) {
						String[] tokens = emotibotTokenzier.tokenize(input, "vip");

						List<CoreLabel> tokenCoreLabelList = edu.stanford.nlp.ling.Sentence.toCoreLabelList(tokens);
						List<CoreLabel> cl_list = fe_classifier_dev.classifySentence(tokenCoreLabelList);
						for (CoreLabel cl : cl_list) {
							System.out.println(cl.toShorterString());
							//System.out.println(cl.toShorterString("Answer"));
//							System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class));

						}
						

					} else
						System.out.println("empty input");

					break;
					
				case 6:
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

	private void loadModel() throws ClassCastException, ClassNotFoundException, IOException {

		Properties props = new Properties();
//		props.put("tokenizerOptions", "tokenizeNLs=true");
//		props.put("tokenizerFactory", "edu.stanford.nlp.process.WhitespaceTokenizer");

		fe_classifier_prod = CRFClassifier.getClassifierNoExceptions(modelPath1);
		fe_classifier_dev = CRFClassifier.getClassifierNoExceptions(modelPath2);


//		fe_classifier_prod = CRFClassifier.getClassifier(modelPath1, props);
//		fe_classifier_dev = CRFClassifier.getClassifier(modelPath2, props);

		

	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("resources/emotibot-srl/config/log4j.properties");

		FERCommandlineDemo sRLDemo = new FERCommandlineDemo();

		sRLDemo.printMenu();

	}
}
