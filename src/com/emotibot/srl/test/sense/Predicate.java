package com.emotibot.srl.test.sense;

import java.util.HashMap;
import java.util.Map;

public class Predicate {

	
	String type;
	String string;
	boolean hasMultipleSenses;
	Map<Integer, Integer> senseMap;
	
	public Predicate(){
		hasMultipleSenses=false;
		type="";
		string="";
		senseMap=new HashMap<Integer, Integer>();
	}
	
	/**
	 * @return the string
	 */
	public String getString() {
		return string;
	}
	/**
	 * @param string the string to set
	 */
	public void setString(String string) {
		this.string = string;
	}
	/**
	 * @return the senseMap
	 */
	public Map<Integer, Integer> getSenseMap() {
		return senseMap;
	}
	/**
	 * @param senseMap the senseMap to set
	 */
	public void setSenseMap(Map<Integer, Integer> senseSet) {
		this.senseMap = senseSet;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the hasMultipleSenses
	 */
	public boolean isHasMultipleSenses() {
		return hasMultipleSenses;
	}

	/**
	 * @param hasMultipleSenses the hasMultipleSenses to set
	 */
	public void setHasMultipleSenses(boolean hasMultipleSenses) {
		this.hasMultipleSenses = hasMultipleSenses;
	}
}
