package com.emotibot.srl.test.sentencenet;

import java.util.List;

public class SentenceDS {

	String sentence;
	String sid;
	String lang;

	
	String row;
	List<String> sentenceTypeList;
	List<String> speechActList;
	List<String> topicsList;
	List<String> emotionList;
	List<String> intentList;
	
	
	/**
	 * @return the sentence
	 */
	public String getSentence() {
		return sentence;
	}
	/**
	 * @param sentence the sentence to set
	 */
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	/**
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}
	/**
	 * @param sid the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}
	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}
	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	/**
	 * @return the sentenceTypeList
	 */
	public List<String> getSentenceTypeList() {
		return sentenceTypeList;
	}
	/**
	 * @param sentenceTypeList the sentenceTypeList to set
	 */
	public void setSentenceTypeList(List<String> sentenceTypeList) {
		this.sentenceTypeList = sentenceTypeList;
	}
	/**
	 * @return the speechActList
	 */
	public List<String> getSpeechActList() {
		return speechActList;
	}
	/**
	 * @param speechActList the speechActList to set
	 */
	public void setSpeechActList(List<String> speechActList) {
		this.speechActList = speechActList;
	}
	/**
	 * @return the topicsList
	 */
	public List<String> getTopicsList() {
		return topicsList;
	}
	/**
	 * @param topicsList the topicsList to set
	 */
	public void setTopicsList(List<String> topicsList) {
		this.topicsList = topicsList;
	}
	/**
	 * @return the emotionList
	 */
	public List<String> getEmotionList() {
		return emotionList;
	}
	/**
	 * @param emotionList the emotionList to set
	 */
	public void setEmotionList(List<String> emotionList) {
		this.emotionList = emotionList;
	}
	/**
	 * @return the intentList
	 */
	public List<String> getIntentList() {
		return intentList;
	}
	/**
	 * @param intentList the intentList to set
	 */
	public void setIntentList(List<String> intentList) {
		this.intentList = intentList;
	}
	/**
	 * @return the row
	 */
	public String getRow() {
		return row;
	}
	/**
	 * @param row the row to set
	 */
	public void setRow(String row) {
		this.row = row;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(row);
		builder.append("\n");
		return builder.toString();
	}

	
}
