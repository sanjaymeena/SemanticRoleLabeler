package com.emotibot.srl.test.frames.conversations;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class FERModel {
	
	private static final String FER_NO_TAG = "O";
	
	
	private volatile static AbstractSequenceClassifier<CoreLabel> fe_classifier;
	
	static String modelPath = "resources/semantic_frame/model/frame_elements_crf_model.ser.gz";

	public static void main(String[] args) {
		
	}
	
	public synchronized static AbstractSequenceClassifier<CoreLabel> getFEClassifierInstance() {
		if (fe_classifier == null) {
			try {
				loadModel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fe_classifier;
	}
	
	public static String getFERString(String[] tokens) {
		
		List<CoreLabel> tokenCoreLabelList = edu.stanford.nlp.ling.Sentence.toCoreLabelList(tokens);
		List<CoreLabel> cl_list = FERModel.getFEClassifierInstance().classifySentence(tokenCoreLabelList);
		
		StringBuilder sb = new StringBuilder();
		
		String prev_ans = FER_NO_TAG;
		for (CoreLabel cl : cl_list) {
			
			String ans = cl.get(CoreAnnotations.AnswerAnnotation.class);
			String t = cl.get(CoreAnnotations.TextAnnotation.class);
			
			if (!ans.equals(prev_ans) && !prev_ans.equals(FER_NO_TAG)) {
				sb.append("<END>");
			}
			
			if (!ans.equals(prev_ans) && !ans.equals(FER_NO_TAG)) {
				sb.append(String.format("<START:%s>", ans));
			}
			
			sb.append(t);
			
			prev_ans = ans;
			
		}
		if (!prev_ans.equals(FER_NO_TAG)) {
			sb.append("<END>");
		}
		
		return sb.toString();
	}
	
	private static void loadModel() throws ClassCastException, ClassNotFoundException, IOException {

		Properties props = new Properties();
		props.put("tokenizerOptions", "tokenizeNLs=true");
		props.put("tokenizerFactory", "edu.stanford.nlp.process.WhitespaceTokenizer");

		fe_classifier = CRFClassifier.getClassifier(modelPath, props);

	}

}
