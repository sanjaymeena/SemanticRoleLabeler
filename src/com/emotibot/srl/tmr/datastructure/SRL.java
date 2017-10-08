package com.emotibot.srl.tmr.datastructure;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class SRL {

	
	String output_srl_table_format;
	List<SRLRow> srl_list=new ArrayList<SRLRow>();
	
	String document;
	Set<String> tmr =new LinkedHashSet<String>();
	
	public String getSrl_table_format() {
		return output_srl_table_format;
	}
	public void setSrl_table_format(String srl_table_format) {
		this.output_srl_table_format = srl_table_format;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	
	
	public Set<String> getTmr() {
		return tmr;
	}
	public void setTmr(Set<String> tmr) {
		this.tmr = tmr;
	}
	
	public String getOutput_srl_table_format() {
		return output_srl_table_format;
	}
	public void setOutput_srl_table_format(String output_srl_table_format) {
		this.output_srl_table_format = output_srl_table_format;
	}
	public List<SRLRow> getSrl_list() {
		return srl_list;
	}
	public void setSrl_list(List<SRLRow> srl_list) {
		this.srl_list = srl_list;
	}
	@Override
	public String toString() {
		
		StringBuilder sb=new StringBuilder();
		sb.append("Document:" + document + "\n");
		
		sb.append("Raw SRL : \n");
		for(SRLRow arg: srl_list){
			sb.append(arg + "\n") ;
		}
		sb.append("TMR: \n");
		for(String arg: tmr){
			sb.append(arg + "\n") ;
		}
		sb.append("\nTable of info: \n");
		sb.append(output_srl_table_format +"\n");
		
		return sb.toString();
	}
}
