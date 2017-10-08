/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.utilities.StatisticsGenerator;

/**
 * This class contains various methods to do some tests with LDC corpus 
 * @author Sanjay
 *
 */
public class TestLDCCorpus {
	
	
	private void generateStats() throws IOException{
		String dataFolder="data/conll2009-chinese-srl/hit/";
		String output="data/conll2009-chinese-srl/json/ldc_corpus.json";
		StatisticsGenerator smg = new StatisticsGenerator();
		smg.generateJSON(dataFolder,output); 
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void convertCONLLtoHIT() throws IOException{
		String data="data/conll2009-chinese-srl/CoNLL2009-ST-Chinese-train.txt";
		String output="data/conll2009-chinese-srl/hit/CoNLL2009-ST-Chinese-hit.txt";
		DataFormatConverter converter = new DataFormatConverter();
		ArrayList<CoNLLSentence> hitList = converter.convertCONLLtoHIT(new File(data));
		
		StringBuilder sb=new StringBuilder();
		for (CoNLLSentence coNLLSentence : hitList) {
			String hit=coNLLSentence.getHITSentence();
			sb.append(hit + "\n");
		}
		
		FileUtils.write(new File(output), sb.toString());
		
	}
	
	/**
	 * Extract long sentences from the given corpus
	 */
	private void extractLongSentences(){
		
		DataFormatConverter converter = new DataFormatConverter();
		String datapath="data/conll2009-chinese-srl/hit/CoNLL2009-ST-Chinese-hit.txt";
		ArrayList<CoNLLSentence> data=	converter.readCoNLLFormatCorpus(new File(datapath), Format.HIT,true);
	
		int counter=0;
		for (CoNLLSentence coNLLSentence : data) {
			
			int tokensNum=coNLLSentence.getLines().length;
			if(tokensNum >=13 && tokensNum <=30){
				System.out.println(coNLLSentence.getSentence());
				counter++;
			}
			
			int temp=0;
		}
		
		System.out.println(counter);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TestLDCCorpus ldc=new TestLDCCorpus();
		//ldc.convertCONLLtoHIT();
		ldc.generateStats();
		//ldc.extractLongSentences();
	}

}
