package com.emotibot.srl.datastructures;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import se.lth.cs.srl.corpus.Sentence;

public class SRLJsonDataStructure {

	String jsonOutput;
	String conllSentence;
	String hitSentence;
	String sentence;
	String tree;

	Sentence sentenceDS;

	String root_index;
	boolean manual_case;

	StatusCode status_code;

	List<String> tokens;
	
	Map<String, String> tokenMap;
	Map<String, String> posMap;

	Multimap<String, Relation> srl_multimap;
	Multimap<String, Relation> verbRelMap;
	Map<String,String> predRelationMap;

	/**
	 * 
	 */
	public SRLJsonDataStructure() {
		sentence = "";
		tokenMap = new LinkedHashMap<String, String>();
		srl_multimap = ArrayListMultimap.create();
		verbRelMap = ArrayListMultimap.create();
		manual_case = false;
		
		status_code = StatusCode.ERROR;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("----------------------------\n");
		sb.append("Document:" + sentence + "\n");

		sb.append("SRL : \n");
		sb.append("---\n");

		Set<String> set = new LinkedHashSet<>();
		StringBuilder sb1 = new StringBuilder();
		// createVerbRelationMap();
		for (String key : srl_multimap.keys()) {

			Collection<Relation> relations = srl_multimap.get(key);
			for (Relation relation : relations) {
				String rel = relation.getSrl_relation();
				String arg1 = relation.getArg1();
				String arg2 = relation.getArg2();

				String d = rel + " (" + arg1 + " , " + arg2 + ")" + "\n";
				set.add(d);

			}
		}

		for (String string : set) {
			sb.append(string);

		}

		if (!Strings.isNullOrEmpty(hitSentence)) {
			sb.append("\nTable of info - Emotibot Format: \n");
			sb.append(hitSentence + "\n");
		}

		if (!Strings.isNullOrEmpty(conllSentence)) {
			sb.append("\nTable of info - CONLL 2009 Format: \n");
			sb.append(conllSentence + "\n");
		}

		return sb.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String toStringSRLTriple() {

		StringBuilder sb = new StringBuilder();

		Set<String> set = new LinkedHashSet<>();

		// createVerbRelationMap();
		for (String key : srl_multimap.keys()) {

			Collection<Relation> relations = srl_multimap.get(key);
			for (Relation relation : relations) {
				String rel = relation.getSrl_relation();
				String arg1 = relation.getArg1();
				String arg2 = relation.getArg2();

				String d = rel + " (" + arg1 + " , " + arg2 + ")" + "\n";
				set.add(d);

			}
		}

		for (String string : set) {
			sb.append(string);

		}
		return sb.toString();
	}

	/**
	 * Create verb relation map . The key is going to be the token index to deal with multiple same verbs in a sentence.
	 */
	public void createVerbRelationMap() {
		for (String key : srl_multimap.keys()) {

			Collection<Relation> relations = srl_multimap.get(key);

			for (Relation relation : relations) {

				//String arg1 = relation.getArg1();
				// List<String> arg2List = relation.getArg2_index_array();
				List<String> arg1_index = relation.getArg1_index_array();
				if (!verbRelMap.containsValue(relation)) {
					if(arg1_index!=null && arg1_index.size()>0 ){
						verbRelMap.put(arg1_index.get(0), relation);
					}
					
				}

				

			}
		}
	}
	
	public void checkSRLStatus() {
		status_code = StatusCode.OK;
	}

	public String getTree() {
		return tree;
	}

	public void setTree(String tree) {
		this.tree = tree;
	}

	public String getRoot_index() {
		return root_index;
	}

	public void setRoot_index(String root_index) {
		this.root_index = root_index;
	}

	public Map<String, String> getPosMap() {
		return posMap;
	}

	public void setPosMap(Map<String, String> posMap) {
		this.posMap = posMap;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public Map<String, String> getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(Map<String, String> tokenMap) {
		this.tokenMap = tokenMap;
	}

	public Multimap<String, Relation> getSrl_multimap() {
		return srl_multimap;
	}

	public void setSrl_multimap(Multimap<String, Relation> srl_multimap) {
		this.srl_multimap = srl_multimap;
	}

	public boolean isManual_case() {
		return manual_case;
	}

	public void setManual_case(boolean manual_case) {
		this.manual_case = manual_case;
	}

	/**
	 * @return the conllSentence
	 */
	public String getConllSentence() {
		return conllSentence;
	}

	/**
	 * @param conllSentence
	 *            the conllSentence to set
	 */
	public void setConllSentence(String conllSentence) {
		this.conllSentence = conllSentence;
	}

	/**
	 * @return the verbRelMap
	 */
	public Multimap<String, Relation> getVerbRelMap() {
		return verbRelMap;
	}

	/**
	 * @param verbRelMap
	 *            the verbRelMap to set
	 */
	public void setVerbRelMap(Multimap<String, Relation> verbRelMap) {
		this.verbRelMap = verbRelMap;
	}

	/**
	 * @return the jsonOutput
	 */
	public String getJsonOutput() {
		return jsonOutput;
	}

	/**
	 * @param jsonOutput
	 *            the jsonOutput to set
	 */
	public void setJsonOutput(String jsonOutput) {
		this.jsonOutput = jsonOutput;
	}

	/**
	 * @return the hitSentence
	 */
	public String getHitSentence() {
		return hitSentence;
	}

	/**
	 * @param hitSentence
	 *            the hitSentence to set
	 */
	public void setHitSentence(String hitSentence) {
		this.hitSentence = hitSentence;
	}

	/**
	 * @return the sentenceDS
	 */
	public Sentence getSentenceDS() {
		return sentenceDS;
	}

	/**
	 * @param sentenceDS
	 *            the sentenceDS to set
	 */
	public void setSentenceDS(Sentence sentenceDS) {
		this.sentenceDS = sentenceDS;
	}

	/**
	 * @return the tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}

	/**
	 * @param tokens the tokens to set
	 */
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	/**
	 * @return the predRelationMap
	 */
	public Map<String, String> getPredRelationMap() {
		return predRelationMap;
	}

	/**
	 * @param predRelationMap the predRelationMap to set
	 */
	public void setPredRelationMap(Map<String, String> predRelationMap) {
		this.predRelationMap = predRelationMap;
	}
	
	public StatusCode getStatus_code() {
		return status_code;
	}

}
