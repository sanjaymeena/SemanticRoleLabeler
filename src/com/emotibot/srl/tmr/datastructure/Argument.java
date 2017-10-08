/**
 * 
 */
package com.emotibot.srl.tmr.datastructure;

import com.emotibot.srl.tmr.datastructure.Constants.Arg;

/**
 * @author sanjay_meena
 *
 */
public abstract class Argument {
String id;
Arg type;
String form;


public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public Arg getType() {
	return type;
}
public void setType(Arg type) {
	this.type = type;
}
public String getForm() {
	return form;
}
public void setForm(String form) {
	this.form = form;
}


}
