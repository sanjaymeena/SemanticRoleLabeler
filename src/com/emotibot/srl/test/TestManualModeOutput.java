/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.server.SRLParserHelper;
import com.google.common.base.Strings;

/**
 * This class is to read a SRL tagged sentence in either HIT or CONLL2009 format
 * and provide results from manual parser
 * 
 * @author Sanjay
 *
 */
public class TestManualModeOutput {
	SRLParserHelper srlParserHelper = new SRLParserHelper();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TestManualModeOutput tmo = new TestManualModeOutput();
		tmo.testManualModeOutput();
	}

	private void testManualModeOutput() throws IOException {
		// create srl options
		SRLOptions options = new SRLOptions();
		options.produceHITFormat = true;

		String directory = "data/manualmode/hit";
		Format inputDataFormat = Format.HIT;

		testManualModeOutputHelper(directory, options, inputDataFormat);

	}

	/**
	 * @param inputDataFormat
	 * @throws IOException
	 * 
	 */
	private void testManualModeOutputHelper(String directory, SRLOptions srlOptions, Format inputDataFormat)
			throws IOException {

		ArrayList<CoNLLSentence> conLLSentenceList = new ArrayList<CoNLLSentence>();

		File rootDir = new File(directory);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		DataFormatConverter dfc = new DataFormatConverter();

		// iterate through all the files in the given directory and read the
		// data in HIT format
		for (File file : files) {
			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, inputDataFormat, true);
			conLLSentenceList.addAll(sentenceList);
		}

		
		// depending on input format, we decide what additional processing steps are necessary
		switch (inputDataFormat) {
		case HIT:
			for (CoNLLSentence coNLLSentence : conLLSentenceList) {
				coNLLSentence.setProcessedSentence(coNLLSentence.getSentence());
				dfc.convertHITtoCONLL2009(coNLLSentence);
				if (Strings.isNullOrEmpty(coNLLSentence.getCoNLLSentence())) {
					System.out.println("Wrong format: " + coNLLSentence.getProcessedSentence());
				}
			}
			break;

		case CONLL:
			for (CoNLLSentence coNLLSentence : conLLSentenceList) {
				coNLLSentence.setProcessedSentence(coNLLSentence.getSentence());
				
			}
			break;

		default:
			break;
		}

		for (CoNLLSentence coNLLSentence : conLLSentenceList) {

			String doc = coNLLSentence.getSentence();
			String conll = coNLLSentence.getCoNLLSentence();

			if (!Strings.isNullOrEmpty(conll)) {
				SRLJsonDataStructure json = srlParserHelper.createSRLJsonDataStructure(coNLLSentence, srlOptions);
				String conllOutput = json.getConllSentence();

				if (!Strings.isNullOrEmpty(conllOutput)) {

					System.out.println(json.toString());

				}

			}

		}

	}

}
