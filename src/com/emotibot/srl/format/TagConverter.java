package com.emotibot.srl.format;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

public class TagConverter {

	private static HashMap<String, String> hanLPToLTP;
	private int colIdxHanLP = 0;
	private int colIdxLTP = 1;

	private static final String posMappingFile = "resources/nlp/data/posmap/enlp2ltp.properties";
	
	DataFormatConverter dfc = new DataFormatConverter();

	public TagConverter() throws IOException {
		readPOSMappingFile();
	}

	public static void main(String[] args) throws Exception {
		TagConverter tc = new TagConverter();

	}
	
	/**
	 * @throws IOException
	 * 
	 */
	private void readPOSMappingFile() throws IOException {
		hanLPToLTP = new HashMap<String, String>();
		List<String> lines = FileUtils.readLines(new File(posMappingFile), "UTF-8");

		for (String string : lines) {
			if (!Strings.isNullOrEmpty(string)) {

				String[] strArr = string.split(" ");
				hanLPToLTP.put(strArr[colIdxHanLP], strArr[colIdxLTP]);
			}

		}
	}

	/**
	 * Convert from Hanlp pos tagset to LTP Tagset
	 * 
	 * @param tagArray
	 * @return
	 * @throws Exception
	 */
	public String[] convertHanLPToLTP(String[] hanlpTagArray) throws Exception {
		String[] ltpTagArray = new String[hanlpTagArray.length];
		for (int i = 0; i < hanlpTagArray.length; i++) {
			String ltpTag = hanLPToLTP.get(hanlpTagArray[i]);
			if (Strings.isNullOrEmpty(ltpTag)) {
				throw new Exception(String.format("Wrong POS tag: %s", hanlpTagArray[i]));
			}
			ltpTagArray[i] = ltpTag;
		}

		return ltpTagArray;
	}
	
}
