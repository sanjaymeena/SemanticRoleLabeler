/**
 * 
 */
package com.emotibot.srl.tmr.datastructure;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sanjay_meena
 * 
 */
public class TextMeaningRepresentation {

	String sentence;
	Map<String, Predicate> predicateMap;
	Map<String, Relation> relationMap;
	Map<String, Entity> entityMap;
	Map<String, Phrase> phraseMap;

	public TextMeaningRepresentation() {
		sentence="";
		predicateMap = new LinkedHashMap<String, Predicate>();
		relationMap = new LinkedHashMap<String, Relation>();
		entityMap = new LinkedHashMap<String, Entity>();
		phraseMap = new LinkedHashMap<String, Phrase>();

	}


	
	/**
	 * Get the root predicate
	 * @return
	 */
	public Predicate getRootPredicate(){
	
		Collection<Predicate> predicateList = predicateMap.values();
		for (Predicate predicate : predicateList) {
			if(predicate.isRoot()){
				return predicate;
			}
		}
		return null;
		
	}
	
	public Map<String, Predicate> getPredicateMap() {
		return predicateMap;
	}

	public void setPredicateMap(Map<String, Predicate> predicateMap) {
		this.predicateMap = predicateMap;
	}

	public Map<String, Relation> getRelationMap() {
		return relationMap;
	}

	public void setRelationMap(Map<String, Relation> relationMap) {
		this.relationMap = relationMap;
	}

	public Map<String, Entity> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, Entity> entityMap) {
		this.entityMap = entityMap;
	}

	public Map<String, Phrase> getPhraseMap() {
		return phraseMap;
	}

	public void setPhraseMap(Map<String, Phrase> phraseMap) {
		this.phraseMap = phraseMap;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("sentence: "+ sentence + "\n");
		
		Predicate p=getRootPredicate();
		
		sb.append("root: "+ p.form+":"+p.getId() + "\n");
		sb.append("TMR:" + "\n");
		Collection<Relation> values = relationMap.values();
		for (Relation relation : values) {
			sb.append(relation.toString() + "\n");
		}

		return sb.toString();
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

}
