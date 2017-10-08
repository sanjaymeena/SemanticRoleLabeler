package com.emotibot.srl.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class CoNLLSentence {
	
	private int id;
	private String sentence;
	private String processedSentence;
	private String coNLLSentence;
	private String HITSentence;
	private String source;
	private String original_sentence;
	private String parseTree;

	private String[] lines;
	private Map<String, Integer> relationMap;
	private List<String> predicateList;
	private List<String> tokenList;
	private ArrayList<ArrayList<String>> dataList;
	
	private boolean hasMultiplePredicates;
	private boolean isBadCase;
	
	private int sentenceTokenLength;
	
	public CoNLLSentence(){
		relationMap = new HashMap<String, Integer>();
		predicateList=new  ArrayList<String>();
		hasMultiplePredicates=false;
		tokenList=new ArrayList<String>();
		dataList=new ArrayList<ArrayList<String>>();
	}
	
	
	
	
	
	/**
	 * @return the predicateList
	 */
	public List<String> getPredicateList() {
		return predicateList;
	}

	/**
	 * @param predicateList the predicateList to set
	 */
	public void setPredicateList(List<String> predicateList) {
		this.predicateList = predicateList;
	}

	/**
	 * @return the hasMultiplePredicates
	 */
	public boolean isHasMultiplePredicates() {
		return hasMultiplePredicates;
	}

	/**
	 * @param hasMultiplePredicates the hasMultiplePredicates to set
	 */
	public void setHasMultiplePredicates(boolean hasMultiplePredicates) {
		this.hasMultiplePredicates = hasMultiplePredicates;
	}


	public String preprocessSentence() {

		processedSentence = StringUtils.normalizeSpace(sentence);
		if (processedSentence.endsWith("...")) {
			processedSentence = processedSentence.substring(0, processedSentence.length() - 3 + 1);

		}
		if (processedSentence.endsWith("..")) {
			processedSentence = processedSentence.substring(0, processedSentence.length() - 2 + 1);

		}

		if (processedSentence.contains("...")) {

			processedSentence = processedSentence.replace("...", "，");
		}

		if (processedSentence.contains("..")) {

			processedSentence = processedSentence.replace("..", "，");

		}

		processedSentence = processedSentence.trim();
		return processedSentence;
	}

	public Map<String, Integer> getRelationMap() {
		return relationMap;
	}

	public void setRelationMap(Map<String, Integer> relationMap) {
		this.relationMap = relationMap;
	}

	public boolean isBadCase() {
		return isBadCase;
	}

	public void setBadCase(boolean isBadCase) {
		this.isBadCase = isBadCase;
	}

	

	public CoNLLSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getParseTree() {
		return parseTree;
	}

	public void setParseTree(String parseTree) {
		this.parseTree = parseTree;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getOriginal_sentence() {
		return original_sentence;
	}

	public void setOriginal_sentence(String original_sentence) {
		this.original_sentence = original_sentence;
	}

	public String getHITSentence() {
		return HITSentence;
	}

	public String getProcessedSentence() {
		return processedSentence;
	}

	public void setProcessedSentence(String processedSentence) {
		this.processedSentence = processedSentence;
	}

	public void setHITSentence(String hITSentence) {
		HITSentence = hITSentence;
	}

	

	public String[] getLines() {
		return lines;
	}

	public void setLines(String[] lines) {
		this.lines = lines;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getCoNLLSentence() {
		return coNLLSentence;
	}

	public void setCoNLLSentence(String coNLLSentence) {
		this.coNLLSentence = coNLLSentence;
	}





	/**
	 * @return the dataList
	 */
	public ArrayList<ArrayList<String>> getDataList() {
		return dataList;
	}





	/**
	 * @param dataList the dataList to set
	 */
	public void setDataList(ArrayList<ArrayList<String>> dataList) {
		this.dataList = dataList;
	}





	/**
	 * @return the sentenceTokenLength
	 */
	public int getSentenceTokenLength() {
		return sentenceTokenLength;
	}





	/**
	 * @param sentenceTokenLength the sentenceTokenLength to set
	 */
	public void setSentenceTokenLength(int sentenceTokenLength) {
		this.sentenceTokenLength = sentenceTokenLength;
	}





	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}





	/**
	 * @return the tokenList
	 */
	public List<String> getTokenList() {
		return tokenList;
	}





	/**
	 * @param tokenList the tokenList to set
	 */
	public void setTokenList(List<String> tokenList) {
		this.tokenList = tokenList;
	}

}
