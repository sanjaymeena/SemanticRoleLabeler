package com.emotibot.srl.test.frames;

public class LexicalUnit {
	String word;
	String pos;
	String lu;
	
	String synset_id;
	

	public LexicalUnit() {
		word = "";
		pos = "";
		lu = "";
	}

	/**
	 * 
	 * @param word
	 * @param pos
	 */
	public LexicalUnit(String word, String pos) {
		this.word = word;
		this.pos = pos;

		this.lu = word + "/" + pos;
	}
	
	/**
	 * 
	 * @param word
	 * @param pos
	 */
	public LexicalUnit(String word, String pos,String synsetId) {
		this.word = word;
		this.pos = pos;
		this.synset_id=synsetId;
		this.lu = word + "/" + pos;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the pos
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(lu);
		return builder.toString();
	}

	/**
	 * @return the lu
	 */
	public String getLu() {
		return lu;
	}

	/**
	 * @param lu the lu to set
	 */
	public void setLu(String lu) {
		this.lu = lu;
	}

	/**
	 * @return the synset_id
	 */
	public String getSynset_id() {
		return synset_id;
	}

	/**
	 * @param synset_id the synset_id to set
	 */
	public void setSynset_id(String synset_id) {
		this.synset_id = synset_id;
	}
}
