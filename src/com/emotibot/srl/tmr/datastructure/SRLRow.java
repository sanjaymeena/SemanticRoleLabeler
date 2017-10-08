package com.emotibot.srl.tmr.datastructure;

public class SRLRow {
String apredType;
String predString;
String predLemma;
String string;
public String getApredType() {
	return apredType;
}
public void setApredType(String apredType) {
	this.apredType = apredType;
}
public String getPredString() {
	return predString;
}
public void setPredString(String predString) {
	this.predString = predString;
}
public String getPredLemma() {
	return predLemma;
}
public void setPredLemma(String predLemma) {
	this.predLemma = predLemma;
}

public String getString() {
	return string;
}
public void setString(String string) {
	this.string = string;
}
@Override
public String toString() {
	return "[apredType=" + apredType + ", predString=" + predString
			+ ", predLemma=" + predLemma + ", string=" + string+"]";
}

}
