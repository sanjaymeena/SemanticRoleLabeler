/**
 * 
 */
package com.emotibot.srl.datastructures;

/**
 * @author Sanjay
 *
 */
public class SRLOptions {

	// whether to segment sentence or not
	public boolean segmentSentence;
	// perform ner . Not integrated yet
	public boolean doNER;
	// enable manual mode
	public boolean manualMode;
	// parse syntactic tree for sentence
	public boolean doTree;
	// perform sentence pruner
	public boolean doPruner;
	// perform FER
	public boolean doFER;

	// also create HIT format of data
	public boolean produceHITFormat;
	// choose which model to use : options are 1 and 2. Any other option
	// defaults to 1
	public int model;
	// whether to use new format of data or not.
	public String format;

	// this is the case
	public String case_type;

	// Use preprocessor from NLP service. We have integrated mate code in NLP
	// service.

	public boolean usePOSFromNLP;
	public boolean useDEPFromNLP;


	public boolean isSegmentSentence() {
		return segmentSentence;
	}

	public void setSegmentSentence(boolean segmentSentence) {
		this.segmentSentence = segmentSentence;
	}

	public boolean isDoNER() {
		return doNER;
	}

	public void setDoNER(boolean doNER) {
		this.doNER = doNER;
	}

	public boolean isManualMode() {
		return manualMode;
	}

	public void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}

	public boolean isDoTree() {
		return doTree;
	}

	public void setDoTree(boolean doTree) {
		this.doTree = doTree;
	}
	
	public boolean isDoPruner() {
		return doPruner;
	}

	public void setDoPruner(boolean doPruner) {
		this.doPruner = doPruner;
	}
	
	public boolean isDoFER() {
		return doFER;
	}

	public void setDoFER(boolean doFER) {
		this.doFER = doFER;
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 
	 */
	public SRLOptions() {

		segmentSentence = false;
		doNER = false;
		manualMode = true;
		doTree = true;
		doPruner = false;
		doFER = false;
		produceHITFormat = true;
		model = 2;
		
		usePOSFromNLP = true;
		useDEPFromNLP = false;
	}

	/**
	 * @return the produceHITFormat
	 */
	public boolean isProduceHITFormat() {
		return produceHITFormat;
	}

	/**
	 * @param produceHITFormat
	 *            the produceHITFormat to set
	 */
	public void setProduceHITFormat(boolean produceHITFormat) {
		this.produceHITFormat = produceHITFormat;
	}

	/**
	 * @return the case_type
	 */
	public String getCase_type() {
		return case_type;
	}

	/**
	 * @param case_type
	 *            the case_type to set
	 */
	public void setCase_type(String case_type) {
		this.case_type = case_type;
	}


	public boolean isUsePOSFromNLP() {
		return usePOSFromNLP;
	}

	public void setUsePOSFromNLP(boolean usePOSFromNLP) {
		this.usePOSFromNLP = usePOSFromNLP;
	}

	public boolean isUseDEPFromNLP() {
		return useDEPFromNLP;
	}

	public void setUseDEPFromNLP(boolean useDEPFromNLP) {
		this.useDEPFromNLP = useDEPFromNLP;
	}

}