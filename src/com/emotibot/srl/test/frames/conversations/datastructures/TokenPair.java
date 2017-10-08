package com.emotibot.srl.test.frames.conversations.datastructures;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.emotibot.srl.test.frames.conversations.Constants;
import com.google.common.base.Strings;

public class TokenPair {

	String token;
	String ne;
	private boolean hasEntity;
	// this variable is to decide if this token is composed of multiple tokens
	// or not.
	boolean multipleTokens;
	boolean multipleSegmentsFromDict;
	//in this case , item was multiple token and the string has been compacted to remove spaces
	boolean compactedTokenString;
	/**
	 * This is the word segment list form a dictionary source. There can be
	 * multiple segments.
	 */
	List<List<String>> multipleSegmentListFromDict;
	/**
	 * This is a trained data segment list
	 */
	List<String> trainDataMultipleTokenList;
	int index;
	String emptyNER = "O";

	public TokenPair() {
		token = "";
		ne = "";
		multipleTokens = false;
		multipleSegmentsFromDict=false;
		hasEntity = false;
		index = 0;
		// multipleTokenList=new LinkedList<>();
	}

	/**
	 * This constructor is used when we are dealing with a dictionary containing
	 * multiple segments.
	 * 
	 * @param token
	 * @param ne
	 * @param index
	 * @param multipleTokenList
	 */
	public TokenPair(String token, String ne, int index, List<List<String>> multipleSegmentListFromDict) {
		this.token = token;
		this.ne = ne;
		this.index = index;

		// check if this token pair has entity or not
		if (!Strings.isNullOrEmpty(ne) && !ne.equals(emptyNER)) {
			hasEntity = true;
		}

		// lets check if this has multiple tokens or not
		if (!Strings.isNullOrEmpty(token) && multipleSegmentListFromDict != null && multipleSegmentListFromDict.size() > 0) {

			multipleTokens = true;
			multipleSegmentsFromDict=true;
			this.multipleSegmentListFromDict=multipleSegmentListFromDict;

		}

	}

	/**
	 * This constructor is used when reading data from a crf format training
	 * data file.
	 * 
	 * @param token
	 * @param ne
	 * @param index
	 */
	public TokenPair(String token, String ne, int index) {
		this.token = token;
		this.ne = ne;
		this.index = index;

		// check if this token pair has entity or not
		if (!Strings.isNullOrEmpty(ne) && !ne.equals(emptyNER)) {
			hasEntity = true;
		}

		// lets check if this has multiple tokens or not
		if (!Strings.isNullOrEmpty(token)) {
			String[] tokens = token.split(Constants.SPACE);

			if (tokens.length > 1) {
				if (trainDataMultipleTokenList == null) {
					trainDataMultipleTokenList = new LinkedList<>();
				}
				multipleTokens = true;

				List<String> tkList = Arrays.asList(tokens);
				trainDataMultipleTokenList = tkList;
			}
		}

	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the ne
	 */
	public String getNe() {
		return ne;
	}

	/**
	 * @param ne
	 *            the ne to set
	 */
	public void setNe(String ne) {
		this.ne = ne;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("token[token=");
		builder.append(token);
		builder.append(", ne=");
		builder.append(ne);
		builder.append(", hasEntity=");
		builder.append(hasEntity);
		builder.append(", multipTokens=");
		builder.append(isMultipleTokens());
		builder.append(", multipSegmentsFromDict=");
		builder.append(multipleSegmentsFromDict);
		builder.append("]");
		builder.append("\n");
		return builder.toString();
	}

	/**
	 * @return the hasEntity
	 */
	public boolean hasEntity() {
		return hasEntity;
	}

	/**
	 * @param hasEntity
	 *            the hasEntity to set
	 */
	public void setHasEntity(boolean hasEntity) {
		this.hasEntity = hasEntity;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the multipleTokens
	 */
	public boolean isMultipleTokens() {
		return multipleTokens;
	}

	/**
	 * @param multipleTokens
	 *            the multipleTokens to set
	 */
	public void setMultipleTokens(boolean multipleTokens) {
		this.multipleTokens = multipleTokens;
	}

	/**
	 * @return the hasEntity
	 */
	public boolean isHasEntity() {
		return hasEntity;
	}

	

	/**
	 * @return the emptyNER
	 */
	public String getEmptyNER() {
		return emptyNER;
	}

	/**
	 * @param emptyNER
	 *            the emptyNER to set
	 */
	public void setEmptyNER(String emptyNER) {
		this.emptyNER = emptyNER;
	}

	/**
	 * @return the multipleSegmentListFromDict
	 */
	public List<List<String>> getMultipleSegmentListFromDict() {
		return multipleSegmentListFromDict;
	}

	/**
	 * @param multipleSegmentListFromDict
	 *            the multipleSegmentListFromDict to set
	 */
	public void setMultipleSegmentListFromDict(List<List<String>> multipleSegmentListFromDict) {
		this.multipleSegmentListFromDict = multipleSegmentListFromDict;
		if(multipleSegmentListFromDict!=null && multipleSegmentListFromDict.size()>0){
			multipleSegmentsFromDict=true;
		}
			
	}

	/**
	 * @return the trainDataMultipleTokenList
	 */
	public List<String> getTrainDataMultipleTokenList() {
		return trainDataMultipleTokenList;
	}

	/**
	 * @param trainDataMultipleTokenList
	 *            the trainDataMultipleTokenList to set
	 */
	public void setTrainDataMultipleTokenList(List<String> trainDataMultipleTokenList) {
		this.trainDataMultipleTokenList = trainDataMultipleTokenList;
	}

	/**
	 * @return the compactedTokenString
	 */
	public boolean isCompactedTokenString() {
		return compactedTokenString;
	}

	/**
	 * @param compactedTokenString the compactedTokenString to set
	 */
	public void setCompactedTokenString(boolean compactedTokenString) {
		this.compactedTokenString = compactedTokenString;
	}

}
