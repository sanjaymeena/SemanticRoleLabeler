package com.emotibot.srl.test.frames.conversations.datastructures;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.emotibot.srl.test.frames.conversations.Constants;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class TemplateSentence {

	List<TokenPair> tokenList;
	List<TokenPair> templatetokenList;
	Map<Integer, TokenPair> tokenMap;
	Multimap<String, Integer> templateMap;
	Map<Integer, String> templateIndexMap;
	Multimap<Integer, TokenPair> generativeInformationMap;
	
	boolean hasTemplateLabels;

	/**
	 * 
	 */
	public TemplateSentence() {
		tokenList = new LinkedList<>();
		templatetokenList = new LinkedList<>();

		tokenMap = new LinkedHashMap<>();
		templateMap = LinkedHashMultimap.create();
		generativeInformationMap = LinkedHashMultimap.create();
		
		hasTemplateLabels = false;
	}

	/**
	 * This function performs necessary steps to generate the template maps
	 */
	public void generateMaps() {
		int prevEntityIndex = -1;
		String prevEntity = "";
		int newIndex = 0;
		boolean addIndex = true;
		for (int i = 0; i < tokenList.size(); i++) {

			TokenPair tp_current = tokenList.get(i);
			int currIndex = tp_current.getIndex();
			String currEntity = tp_current.getNe();
			
			if (tp_current.hasEntity()) {
				
				//first token;
				
				if(!currEntity.equals(prevEntity)|| (i==0)){
					tokenMap.put(newIndex, tp_current);
					prevEntityIndex = currIndex;
					prevEntity = currEntity;
				}

				
				// the current entity tag is same as previous entity tag, which
				// means its consecutive tokens
				else {
					TokenPair tp_previous = tokenMap.get(newIndex - 1);
					tp_previous.setMultipleTokens(true);
					List<String> tokenList = tp_previous.getTrainDataMultipleTokenList();
					if (tokenList == null || tokenList.size() == 0) {
						tokenList = new LinkedList<>();
						tokenList.add(tp_previous.getToken());
					}
					tokenList.add(tp_current.getToken());
					tp_previous.setTrainDataMultipleTokenList(tokenList);

					// update the previous token list
					tokenMap.put(newIndex - 1, tp_previous);

					// we do this to make sure that indices are consecutive
					addIndex = false;
				}

			} else {
				tokenMap.put(newIndex, tp_current);
				prevEntityIndex = currIndex;
				prevEntity = currEntity;
				addIndex = true;
			}

			if (addIndex) {
				newIndex++;
			}

		}

		// now since we have original map we can create template map : Fe to
		// Index

		for (Integer key : tokenMap.keySet()) {

			TokenPair tp = tokenMap.get(key);

			String entity = tp.getNe();

			if (tp.hasEntity()) {
				templateMap.put(entity, key);
			}
		}

		if (templateMap.size() > 0) {
			hasTemplateLabels = true;
		}

		// we also create template Index to FE map
		templateIndexMap = new LinkedHashMap<>();
		for (String key : templateMap.keySet()) {
			Collection<Integer> values = templateMap.get(key);
			for (Integer integer : values) {
				templateIndexMap.put(integer, key);
			}
		}

	}

	public String getTemplateString(boolean tabDelimiter) {

		StringBuilder sb = new StringBuilder();

		for (Integer key : tokenMap.keySet()) {
			TokenPair tp = tokenMap.get(key);

			String entity = tp.getNe();
			String token = tp.getToken();
			String t = "";
			if (tp.hasEntity()) {

				t = "<" + entity + ">";
				if(tabDelimiter){
					sb.append(t + Constants.delimiter);
				}
				else{
					sb.append(t + Constants.SPACE);
				}
				

			} else {
				if(tabDelimiter){
					sb.append(token + Constants.delimiter);
				}
				else{
					sb.append(token + Constants.SPACE);
				}
			}
		}
		return sb.toString();
	}
	
	

	/**
	 * @return the tokenList
	 */
	public List<TokenPair> getTokenList() {
		return tokenList;
	}

	/**
	 * @param tokenList
	 *            the tokenList to set
	 */
	public void setTokenList(List<TokenPair> tokenList) {
		this.tokenList = tokenList;
		// here we call the function to generate the maps as well
		generateMaps();
	}

	/**
	 * @return the templatetokenList
	 */
	public List<TokenPair> getTemplatetokenList() {
		return templatetokenList;
	}

	/**
	 * @param templatetokenList
	 *            the templatetokenList to set
	 */
	public void setTemplatetokenList(List<TokenPair> templatetokenList) {
		this.templatetokenList = templatetokenList;
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

	/**
	 * @return the templateMap
	 */
	public Multimap<String, Integer> getTemplateMap() {
		return templateMap;
	}

	/**
	 * @param templateMap
	 *            the templateMap to set
	 */
	public void setTemplateMap(Multimap<String, Integer> templateMap) {
		this.templateMap = templateMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getTemplateString(false));
		builder.append("\n");
		return builder.toString();
	}

	/**
	 * @return the templateIndexMap
	 */
	public Map<Integer, String> getTemplateIndexMap() {
		return templateIndexMap;
	}

	/**
	 * @param templateIndexMap
	 *            the templateIndexMap to set
	 */
	public void setTemplateIndexMap(Map<Integer, String> templateIndexMap) {
		this.templateIndexMap = templateIndexMap;
	}

	/**
	 * @return the generativeInformationMap
	 */
	public Multimap<Integer, TokenPair> getGenerativeInformationMap() {
		return generativeInformationMap;
	}

	/**
	 * @param generativeInformationMap
	 *            the generativeInformationMap to set
	 */
	public void setGenerativeInformationMap(Multimap<Integer, TokenPair> generativeInformationMap) {
		this.generativeInformationMap = generativeInformationMap;
	}

	/**
	 * @return the hasTemplateLabels
	 */
	public boolean isHasTemplateLabels() {
		return hasTemplateLabels;
	}

	/**
	 * @param hasTemplateLabels
	 *            the hasTemplateLabels to set
	 */
	public void setHasTemplateLabels(boolean hasTemplateLabels) {
		this.hasTemplateLabels = hasTemplateLabels;
	}

	

}
