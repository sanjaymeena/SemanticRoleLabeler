package com.emotibot.srl.tmr.datastructure;

import com.emotibot.srl.tmr.datastructure.Constants.RelationEnum;

public class SemanticRelation extends Relation{

	
	
public SemanticRelation(){	
	relationType=RelationEnum.semantic;
}

@Override
public String toString() {
	return form + ":"+ id+"("
					+ arg1.getForm() + ":" + arg1.getId()+
					", " + arg2.getForm() + ":" + arg2.getId()+ ")";
}






}
