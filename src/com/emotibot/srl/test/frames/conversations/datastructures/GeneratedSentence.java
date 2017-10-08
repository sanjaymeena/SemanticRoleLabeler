package com.emotibot.srl.test.frames.conversations.datastructures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.emotibot.srl.test.frames.conversations.Constants;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class GeneratedSentence {
	Map<Integer, TokenPair> tokenMap;
	boolean hasTokensWithMultipleSegments;

	Set<Integer> templateIndices;
	Multimap<String, Integer> templateMap;
	// this boolean functions checks if the token map contains token strings
	// which can be further broken down;
	boolean breakableTokens;

	/* 
	*/
	public GeneratedSentence() {

		tokenMap = new LinkedHashMap<>();
		breakableTokens = false;
		templateIndices = new LinkedHashSet<>();
	}

	public GeneratedSentence(Map<Integer, TokenPair> newTokensMap) {
		this.tokenMap = newTokensMap;
	}

	public GeneratedSentence(Map<Integer, TokenPair> tokensMap, Set<Integer> templateIndices, Multimap<String, Integer> tmpLateMap) {
		this.tokenMap = tokensMap;
		this.templateIndices = templateIndices;
		
		
		//we set if this sentence has multiple segments or not
		for (Integer index : tokenMap.keySet()) {
			TokenPair tp = tokenMap.get(index);
			

			List<List<String>> multipleSegments = tp.getMultipleSegmentListFromDict();
			if (multipleSegments != null && multipleSegments.size() > 0) {
				hasTokensWithMultipleSegments=true;
			}
			
		}
		
		this.templateMap=tmpLateMap;
		
	}

	/**
	 * 
	 * @param useMultipleTokenInfo
	 * @return
	 */
	public List<String> generateCRFModelData2(boolean useMultipleTokenInfo) {
		List<String> multipleOutputs = new LinkedList<>();

		StringBuilder outputBuilder = new StringBuilder();

		if (!useMultipleTokenInfo) {
			outputBuilder = new StringBuilder();
			for (Integer index : tokenMap.keySet()) {
				TokenPair tp = tokenMap.get(index);
				String fe = tp.getNe();
				String token = tp.getToken();

				outputBuilder.append(token + Constants.delimiter + fe);
				outputBuilder.append(Constants.newline);
			}

		}

		else {
			outputBuilder = new StringBuilder();
			for (Integer index : tokenMap.keySet()) {
				TokenPair tp = tokenMap.get(index);
				String fe = tp.getNe();
				String token = tp.getToken();
				// System.out.print(token);

				List<List<String>> multipleSegments = tp.getMultipleSegmentListFromDict();
				if (multipleSegments != null && multipleSegments.size() > 0) {

					List<String> tokenList = multipleSegments.get(0);
					for (String subToken : tokenList) {
						outputBuilder.append(subToken + Constants.delimiter + fe);
						outputBuilder.append(Constants.newline);
					}

				} else {
					outputBuilder.append(token + Constants.delimiter + fe);
					outputBuilder.append(Constants.newline);
				}
			}

		}
		multipleOutputs.add(outputBuilder.toString());

		return multipleOutputs;
	}

	/***
	 * This function will use dictionary with multiple sentence segments to
	 * generated the information
	 * 
	 * @param useMultipleTokenInfo
	 * @return
	 */
	public List<String> generateCRFModelData1(boolean useMultipleTokenInfo) {

		List<String> multipleOutputs = new LinkedList<>();

		StringBuilder outputBuilder = new StringBuilder();

		if (!useMultipleTokenInfo) {
			outputBuilder = new StringBuilder();
			for (Integer index : tokenMap.keySet()) {
				TokenPair tp = tokenMap.get(index);
				String fe = tp.getNe();
				String token = tp.getToken();

				outputBuilder.append(token + Constants.delimiter + fe);
				outputBuilder.append(Constants.newline);
			}

		}

		else {
			outputBuilder = new StringBuilder();
			for (Integer index : tokenMap.keySet()) {
				TokenPair tp = tokenMap.get(index);
				String fe = tp.getNe();
				String token = tp.getToken();
				// System.out.print(token);

				List<String> multipleTokenList = tp.getTrainDataMultipleTokenList();
				if (multipleTokenList != null && multipleTokenList.size() > 0) {

					for (String subToken : multipleTokenList) {

						outputBuilder.append(subToken + Constants.delimiter + fe);
						outputBuilder.append(Constants.newline);
					}
				} else {
					outputBuilder.append(token + Constants.delimiter + fe);
					outputBuilder.append(Constants.newline);
				}
			}

		}
		multipleOutputs.add(outputBuilder.toString());

		return multipleOutputs;

	}

	private Multimap<Integer, StringBuilder> test(List<Integer> templateIndicesList) {

		Multimap<Integer, StringBuilder> slotBuilderMap = LinkedHashMultimap.create();
		for (int i = 0; i < templateIndicesList.size(); i++) {

			int currentSlotCounter = templateIndicesList.get(i);

			TokenPair tp = tokenMap.get(currentSlotCounter);
			String fe = tp.getNe();

			List<List<String>> multipleSegments = tp.getMultipleSegmentListFromDict();
			if (multipleSegments != null && multipleSegments.size() > 0) {

				for (List<String> list : multipleSegments) {
					StringBuilder sb1 = generateHelper(list, fe);
					slotBuilderMap.put(currentSlotCounter, sb1);

				}
			}
		}
		return slotBuilderMap;

	}

	private StringBuilder generateHelper(List<String> list, String fe) {

		StringBuilder sb = new StringBuilder();
		for (String token : list) {
			sb.append(token + Constants.delimiter + fe);
			sb.append(Constants.newline);
		}

		return sb;
	}

	public List<String> generateCRFModelData(boolean useMultipleTokenInfo) {

		List<String> multipleOutputs = new LinkedList<>();

		StringBuilder outputBuilder = new StringBuilder();

		if (!useMultipleTokenInfo) {
			outputBuilder = new StringBuilder();
			for (Integer index : tokenMap.keySet()) {
				TokenPair tp = tokenMap.get(index);
				String fe = tp.getNe();
				String token = tp.getToken();

				outputBuilder.append(token + Constants.delimiter + fe);
				outputBuilder.append(Constants.newline);
			}

		}

		else {
			outputBuilder = new StringBuilder();
			for (Integer index : tokenMap.keySet()) {
				TokenPair tp = tokenMap.get(index);
				String fe = tp.getNe();
				String token = tp.getToken();
				// System.out.print(token);

				List<String> multipleTokenList = tp.getTrainDataMultipleTokenList();
				if (multipleTokenList != null && multipleTokenList.size() > 0) {

					for (String subToken : multipleTokenList) {

						outputBuilder.append(subToken + Constants.delimiter + fe);
						outputBuilder.append(Constants.newline);
					}
				} else {
					outputBuilder.append(token + Constants.delimiter + fe);
					outputBuilder.append(Constants.newline);
				}
			}

		}
		multipleOutputs.add(outputBuilder.toString());

		return multipleOutputs;

	}

	/**
	 * @return the tokenMap
	 */
	public Map<Integer, TokenPair> getTokenMap() {
		return tokenMap;
	}

	/**
	 * @param tokenMap
	 *            the tokenMap to set
	 */
	public void setTokenMap(Map<Integer, TokenPair> tokenMap) {
		this.tokenMap = tokenMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Integer idx : tokenMap.keySet()) {
			TokenPair tp = tokenMap.get(idx);
			String tok = tp.getToken();
			builder.append(tok + Constants.SPACE);
		}

		return builder.toString();
	}

	/**
	 * @return the breakableTokens
	 */
	public boolean isBreakableTokens() {

		for (Integer index : tokenMap.keySet()) {
			TokenPair tp = tokenMap.get(index);

			boolean isMultipletoken = tp.isMultipleTokens();
			if (isMultipletoken) {
				setBreakableTokens(isMultipletoken);
			}

		}

		return breakableTokens;
	}

	/**
	 * @param breakableTokens
	 *            the breakableTokens to set
	 */
	public void setBreakableTokens(boolean breakableTokens) {
		this.breakableTokens = breakableTokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (breakableTokens ? 1231 : 1237);
		result = prime * result + ((tokenMap == null) ? 0 : tokenMap.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneratedSentence other = (GeneratedSentence) obj;
		if (toString() != other.toString())
			return false;

		return true;
	}

	/**
	 * @return the hasTokensWithMultipleSegments
	 */
	public boolean isHasTokensWithMultipleSegments() {
		return hasTokensWithMultipleSegments;
	}

	/**
	 * @param hasTokensWithMultipleSegments
	 *            the hasTokensWithMultipleSegments to set
	 */
	public void setHasTokensWithMultipleSegments(boolean hasTokensWithMultipleSegments) {
		this.hasTokensWithMultipleSegments = hasTokensWithMultipleSegments;
	}

	/**
	 * @return the templateIndices
	 */
	public Set<Integer> getTemplateIndices() {
		return templateIndices;
	}

	/**
	 * @param templateIndices the templateIndices to set
	 */
	public void setTemplateIndices(Set<Integer> templateIndices) {
		this.templateIndices = templateIndices;
	}

	/**
	 * @return the templateMap
	 */
	public Multimap<String, Integer> getTemplateMap() {
		return templateMap;
	}

	/**
	 * @param templateMap the templateMap to set
	 */
	public void setTemplateMap(Multimap<String, Integer> templateMap) {
		this.templateMap = templateMap;
	}

}
