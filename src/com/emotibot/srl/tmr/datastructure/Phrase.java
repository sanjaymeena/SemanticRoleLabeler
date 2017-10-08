/**
 * 
 */
package com.emotibot.srl.tmr.datastructure;

import com.emotibot.srl.tmr.datastructure.Constants.Arg;

/**
 * @author sanjay_meena
 * 
 */
public class Phrase extends Argument {

	public Phrase() {
		type = Arg.phrase;
	}

	@Override
	public String toString() {
		return "Phrase [id=" + id + ", type=" + type + ", form=" + form
				+ ", getId()=" + getId() + ", getType()=" + getType()
				+ ", getForm()=" + getForm() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
