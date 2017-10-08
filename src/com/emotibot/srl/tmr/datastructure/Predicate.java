package com.emotibot.srl.tmr.datastructure;

import com.emotibot.srl.tmr.datastructure.Constants.Arg;


public class Predicate extends Argument {

String pos;
boolean isRoot;
String lemma;
String form;


public Predicate(){
	type=Arg.predicate;
	
}


@Override
public String toString() {
	return "Predicate [pos=" + pos + ", isRoot=" + isRoot + ", lemma=" + lemma
			+ ", word=" + form + ", id=" + id + ", type=" + type + "]";
}


public String getPos() {
	return pos;
}


public void setPos(String pos) {
	this.pos = pos;
}


public boolean isRoot() {
	return isRoot;
}


public void setRoot(boolean isRoot) {
	this.isRoot = isRoot;
}


public String getLemma() {
	return lemma;
}


public void setLemma(String lemma) {
	this.lemma = lemma;
}


public String getForm() {
	return form;
}


public void setForm(String form) {
	this.form = form;
}



}
