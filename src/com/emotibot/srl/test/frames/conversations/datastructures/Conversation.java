package com.emotibot.srl.test.frames.conversations.datastructures;

import java.util.LinkedList;
import java.util.List;

public class Conversation {

	private List<Utterance> coversationList;
	private List<Utterance> robotUtterances;
	private List<Utterance> userUtterances;

	public Conversation() {
		coversationList = new LinkedList<>();
		robotUtterances = new LinkedList<>();
		userUtterances = new LinkedList<>();
	}

	/**
	 * @return the coversationList
	 */
	public List<Utterance> getCoversationList() {
		return coversationList;
	}

	/**
	 * @param coversationList the coversationList to set
	 */
	public void setCoversationList(List<Utterance> coversationList) {
		this.coversationList = coversationList;
	}

	/**
	 * @return the robotUtterances
	 */
	public List<Utterance> getRobotUtterances() {
		return robotUtterances;
	}

	/**
	 * @param robotUtterances the robotUtterances to set
	 */
	public void setRobotUtterances(List<Utterance> robotUtterances) {
		this.robotUtterances = robotUtterances;
	}

	/**
	 * @return the userUtterances
	 */
	public List<Utterance> getUserUtterances() {
		return userUtterances;
	}

	/**
	 * @param userUtterances the userUtterances to set
	 */
	public void setUserUtterances(List<Utterance> userUtterances) {
		this.userUtterances = userUtterances;
	}
}
