package com.emotibot.srl.datastructures;

import java.util.List;
import java.util.Map;

public class Relation {
	
String srl_relation;
String relation_string;
String arg1;
String arg1_type;
String arg1_sense;
String arg2;
Map<String,String> tokenMap;


List<String> arg1_index_array;

List<String> arg2_index_array;


@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(srl_relation);
	builder.append(" (");
	builder.append(arg1);
	builder.append(",  ");
	builder.append(arg2);
	builder.append(")");
	return builder.toString();
}


public String getSrl_relation() {
	return srl_relation;
}
public void setSrl_relation(String srl_relation) {
	this.srl_relation = srl_relation;
}
public String getRelation_string() {
	return relation_string;
}
public void setRelation_string(String relation_string) {
	this.relation_string = relation_string;
}
public String getArg1() {
	return arg1;
}
public void setArg1(String arg1) {
	this.arg1 = arg1;
}
public String getArg2() {
	return arg2;
}
public void setArg2(String arg2) {
	this.arg2 = arg2;
}
public List<String> getArg1_index_array() {
	return arg1_index_array;
}
public void setArg1_index_array(List<String> arg1_index_array) {
	this.arg1_index_array = arg1_index_array;
}
public List<String> getArg2_index_array() {
	return arg2_index_array;
}
public void setArg2_index_array(List<String> arg2_index_array) {
	this.arg2_index_array = arg2_index_array;
}


/**
 * @return the arg1_type
 */
public String getArg1_type() {
	return arg1_type;
}


/**
 * @param arg1_type the arg1_type to set
 */
public void setArg1_type(String arg1_type) {
	this.arg1_type = arg1_type;
}


/**
 * @return the tokenMap
 */
public Map<String, String> getTokenMap() {
	return tokenMap;
}


/**
 * @param tokenMap the tokenMap to set
 */
public void setTokenMap(Map<String, String> tokenMap) {
	this.tokenMap = tokenMap;
}


/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((arg1 == null) ? 0 : arg1.hashCode());
	result = prime * result + ((arg2 == null) ? 0 : arg2.hashCode());
	result = prime * result + ((arg2_index_array == null) ? 0 : arg2_index_array.hashCode());
	return result;
}


/* (non-Javadoc)
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
	Relation other = (Relation) obj;
	if (arg1 == null) {
		if (other.arg1 != null)
			return false;
	} else if (!arg1.equals(other.arg1))
		return false;
	if (arg2 == null) {
		if (other.arg2 != null)
			return false;
	} else if (!arg2.equals(other.arg2))
		return false;
	if (arg2_index_array == null) {
		if (other.arg2_index_array != null)
			return false;
	} else if (!arg2_index_array.equals(other.arg2_index_array))
		return false;
	return true;
}


/**
 * @return the arg1_sense
 */
public String getArg1_sense() {
	return arg1_sense;
}


/**
 * @param arg1_sense the arg1_sense to set
 */
public void setArg1_sense(String arg1_sense) {
	this.arg1_sense = arg1_sense;
}


}
