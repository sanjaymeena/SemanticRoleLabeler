package com.emotibot.srl.test.conll05;

import java.util.ArrayList;

import com.emotibot.srl.datastructures.CoNLLSentence;

/**
 * 
 * @author Sanjay
 *
 */
public class Corpus {
	private ArrayList<CoNLLSentence> trainSRLSentences;
	private ArrayList<CoNLLSentence> testSRLSentences;
	private ArrayList<CoNLLSentence> conll2009CorpusSentences;
	
	
	public Corpus(){
		
	}
	
	
	public ArrayList<CoNLLSentence> getAllData() {
		ArrayList<CoNLLSentence> totalData=new ArrayList<>();
		totalData.addAll(trainSRLSentences);
		totalData.addAll(testSRLSentences);
		totalData.addAll(conll2009CorpusSentences);
		
		return totalData;
	}
	
	/**
	 * @return the trainSRLSentences
	 */
	public ArrayList<CoNLLSentence> getTrainSRLSentences() {
		return trainSRLSentences;
	}
	/**
	 * @param trainSRLSentences the trainSRLSentences to set
	 */
	public void setTrainSRLSentences(ArrayList<CoNLLSentence> trainSRLSentences) {
		this.trainSRLSentences = trainSRLSentences;
	}
	/**
	 * @return the testSRLSentences
	 */
	public ArrayList<CoNLLSentence> getTestSRLSentences() {
		return testSRLSentences;
	}
	/**
	 * @param testSRLSentences the testSRLSentences to set
	 */
	public void setTestSRLSentences(ArrayList<CoNLLSentence> testSRLSentences) {
		this.testSRLSentences = testSRLSentences;
	}
	/**
	 * @return the conll2009CorpusSentences
	 */
	public ArrayList<CoNLLSentence> getConll2009CorpusSentences() {
		return conll2009CorpusSentences;
	}
	/**
	 * @param conll2009CorpusSentences the conll2009CorpusSentences to set
	 */
	public void setConll2009CorpusSentences(ArrayList<CoNLLSentence> conll2009CorpusSentences) {
		this.conll2009CorpusSentences = conll2009CorpusSentences;
	}

}
