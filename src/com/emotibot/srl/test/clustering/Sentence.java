package com.emotibot.srl.test.clustering;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Sanjay
 *
 */
public class Sentence {

	String sentence;
	Map<String, List<String>> predSynsetMap;
	String rootVerb = "";
	
	List<String> predicates;
	
	
	/**
	 * @return the sentence
	 */
	public String getSentence() {
		return sentence;
	}
	/**
	 * @param sentence the sentence to set
	 */
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	/**
	 * @return the predSynsetMap
	 */
	public Map<String, List<String>> getPredSynsetMap() {
		return predSynsetMap;
	}
	/**
	 * @param predSynsetMap the predSynsetMap to set
	 */
	public void setPredSynsetMap(Map<String, List<String>> predSynsetMap) {
		this.predSynsetMap = predSynsetMap;
	}
	/**
	 * @return the rootVerb
	 */
	public String getRootVerb() {
		return rootVerb;
	}
	/**
	 * @param rootVerb the rootVerb to set
	 */
	public void setRootVerb(String rootVerb) {
		this.rootVerb = rootVerb;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(sentence);
		return builder.toString();
	}
	/**
	 * @return the predicates
	 */
	public List<String> getPredicates() {
		return predicates;
	}
	/**
	 * @param predicates the predicates to set
	 */
	public void setPredicates(List<String> predicates) {
		this.predicates = predicates;
	}
	
	

}
