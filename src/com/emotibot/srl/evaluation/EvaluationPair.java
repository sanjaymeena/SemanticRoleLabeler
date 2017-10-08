/**
 * 
 */
package com.emotibot.srl.evaluation;

import com.emotibot.srl.datastructures.CoNLLSentence;

/**
 * @author Sanjay
 *
 */
public class EvaluationPair {

	int id;
	CoNLLSentence systemSentence;
	CoNLLSentence goldSentence;

	public EvaluationPair(int counter, CoNLLSentence coNLLSentence, CoNLLSentence systemSentence2) {
		this.id = counter;
		this.goldSentence = coNLLSentence;
		this.systemSentence = systemSentence2;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the systemSentence
	 */
	public CoNLLSentence getSystemSentence() {
		return systemSentence;
	}

	/**
	 * @param systemSentence
	 *            the systemSentence to set
	 */
	public void setSystemSentence(CoNLLSentence systemSentence) {
		this.systemSentence = systemSentence;
	}

	/**
	 * @return the goldSentence
	 */
	public CoNLLSentence getGoldSentence() {
		return goldSentence;
	}

	/**
	 * @param goldSentence
	 *            the goldSentence to set
	 */
	public void setGoldSentence(CoNLLSentence goldSentence) {
		this.goldSentence = goldSentence;
	}

}
