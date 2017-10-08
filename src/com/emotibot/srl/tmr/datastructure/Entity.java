package com.emotibot.srl.tmr.datastructure;

import com.emotibot.srl.tmr.datastructure.Constants.Arg;

public class Entity extends Argument {


public Entity(){
	type=Arg.entity;
}

@Override
public String toString() {
	return "Entity [id=" + id + ", type=" + type + ", form=" + form
			+ ", getId()=" + getId() + ", getType()=" + getType()
			+ ", getForm()=" + getForm() + ", getClass()=" + getClass()
			+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
			+ "]";
}


}
