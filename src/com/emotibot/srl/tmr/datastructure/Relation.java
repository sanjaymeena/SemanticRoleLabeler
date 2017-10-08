/**
 * 
 */
package com.emotibot.srl.tmr.datastructure;

import com.emotibot.srl.tmr.datastructure.Constants.RelationEnum;

/**
 * @author sanjay_meena
 *
 */
public class Relation {
String id;
RelationEnum relationType;
String form;
Argument arg1;
Argument arg2;


public Relation(){
	
}


public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}

public String getForm() {
	return form;
}
public void setForm(String form) {
	this.form = form;
}
public Argument getArg1() {
	return arg1;
}
public void setArg1(Argument arg1) {
	this.arg1 = arg1;
}
public Argument getArg2() {
	return arg2;
}
public void setArg2(Argument arg2) {
	this.arg2 = arg2;
}


public RelationEnum getRelationType() {
	return relationType;
}


public void setRelationType(RelationEnum relationType) {
	this.relationType = relationType;
}




}
