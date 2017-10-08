/**
 * 
 */
package com.emotibot.srl.test.conll05;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Sanjay
 *
 */
public class DeepData {
	StringBuilder propositionFileBuilder;
	StringBuilder wordsFileBuilder;

	String delimiter = "\t";
	String newline = "\n";

	Set<String> wordDictSet;
	Set<String> targetDictSet;
	Set<String> verbDictSet;
	Set<String> targetDictLabels;

	List<String> wordsFileData;
	List<String> propositionFileData;

	StringBuilder wordDictBuilder;
	StringBuilder verbDictBuilder;
	StringBuilder targetLabelDictBuilder;

	public DeepData() {

		wordsFileData = new LinkedList<>();
		propositionFileData = new LinkedList<>();

		propositionFileBuilder = new StringBuilder();
		wordsFileBuilder = new StringBuilder();

		wordDictSet = new LinkedHashSet<>();
		verbDictSet = new LinkedHashSet<>();
		targetDictLabels = new LinkedHashSet<>();

		wordDictBuilder = new StringBuilder();
		verbDictBuilder = new StringBuilder();
		targetLabelDictBuilder = new StringBuilder();
	}

	/**
	 * Run builder on data
	 */
	public void runBuilders() {

		// build proposition file
		for (String string : propositionFileData) {

			propositionFileBuilder.append(string);

		}

		// build words file
		for (String string : wordsFileData) {

			wordsFileBuilder.append(string);

		}

		// build words dictionary set
		for (String string : wordDictSet) {

			wordDictBuilder.append(string);
			wordDictBuilder.append(newline);

		}

		// build verb dictionary set
		for (String string : verbDictSet) {

			verbDictBuilder.append(string);
			verbDictBuilder.append(newline);

		}

		// build target dictionary set
		for (String string : targetDictLabels) {

			targetLabelDictBuilder.append(string);
			targetLabelDictBuilder.append(newline);

		}
	}

	/**
	 * @return the propositionFileBuilder
	 */
	public StringBuilder getPropositionFileBuilder() {
		return propositionFileBuilder;
	}

	/**
	 * @param propositionFileBuilder
	 *            the propositionFileBuilder to set
	 */
	public void setPropositionFileBuilder(StringBuilder propositionFileBuilder) {
		this.propositionFileBuilder = propositionFileBuilder;
	}

	/**
	 * @return the wordsFileBuilder
	 */
	public StringBuilder getWordsFileBuilder() {
		return wordsFileBuilder;
	}

	/**
	 * @param wordsFileBuilder
	 *            the wordsFileBuilder to set
	 */
	public void setWordsFileBuilder(StringBuilder wordsFileBuilder) {
		this.wordsFileBuilder = wordsFileBuilder;
	}

	/**
	 * @return the wordDictSet
	 */
	public Set<String> getWordDictSet() {
		return wordDictSet;
	}

	/**
	 * @param wordDictSet
	 *            the wordDictSet to set
	 */
	public void setWordDictSet(Set<String> wordDictSet) {
		this.wordDictSet = wordDictSet;
	}

	/**
	 * @return the verbDictSet
	 */
	public Set<String> getVerbDictSet() {
		return verbDictSet;
	}

	/**
	 * @param verbDictSet
	 *            the verbDictSet to set
	 */
	public void setVerbDictSet(Set<String> verbDictSet) {
		this.verbDictSet = verbDictSet;
	}

	/**
	 * @return the targetDictLabels
	 */
	public Set<String> getTargetDictLabels() {
		return targetDictLabels;
	}

	/**
	 * @param targetDictLabels
	 *            the targetDictLabels to set
	 */
	public void setTargetDictLabels(Set<String> targetDictLabels) {
		this.targetDictLabels = targetDictLabels;
	}

	/**
	 * @return the wordsFileData
	 */
	public List<String> getWordsFileData() {
		return wordsFileData;
	}

	/**
	 * @param wordsFileData
	 *            the wordsFileData to set
	 */
	public void setWordsFileData(List<String> wordsFileData) {
		this.wordsFileData = wordsFileData;
	}

	/**
	 * @return the propositionFileData
	 */
	public List<String> getPropositionFileData() {
		return propositionFileData;
	}

	/**
	 * @param propositionFileData
	 *            the propositionFileData to set
	 */
	public void setPropositionFileData(List<String> propositionFileData) {
		this.propositionFileData = propositionFileData;
	}

	/**
	 * @return the wordDictBuilder
	 */
	public StringBuilder getWordDictBuilder() {
		return wordDictBuilder;
	}

	/**
	 * @param wordDictBuilder
	 *            the wordDictBuilder to set
	 */
	public void setWordDictBuilder(StringBuilder wordDictBuilder) {
		this.wordDictBuilder = wordDictBuilder;
	}

	/**
	 * @return the verbDictBuilder
	 */
	public StringBuilder getVerbDictBuilder() {
		return verbDictBuilder;
	}

	/**
	 * @param verbDictBuilder
	 *            the verbDictBuilder to set
	 */
	public void setVerbDictBuilder(StringBuilder verbDictBuilder) {
		this.verbDictBuilder = verbDictBuilder;
	}

	/**
	 * @return the targetLabelDictBuilder
	 */
	public StringBuilder getTargetLabelDictBuilder() {
		return targetLabelDictBuilder;
	}

	/**
	 * @param targetLabelDictBuilder
	 *            the targetLabelDictBuilder to set
	 */
	public void setTargetLabelDictBuilder(StringBuilder targetLabelDictBuilder) {
		this.targetLabelDictBuilder = targetLabelDictBuilder;
	}

}
