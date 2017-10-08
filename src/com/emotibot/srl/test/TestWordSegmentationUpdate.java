/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.google.common.base.Strings;

import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;
import static com.emotibot.srl.format.Constants.*;

/**
 * @author Sanjay
 *
 */
public class TestWordSegmentationUpdate {

	

	public  void test() throws IOException {

		DataFormatConverter dfc = new DataFormatConverter();
		
		String prefixPath = "data/corpus_validation/good_cases/hit";
		
		String output1="data/wordsegfix/change/new";
		String output2="data/wordsegfix/change/orig";
		
		File rootDir = new File(prefixPath);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		Map<Long, CoNLLSentence> origSentenceMap = new LinkedHashMap<>();
		Map<Long, CoNLLSentence> newSentenceMap = new LinkedHashMap<>();
		
		Map<Long, CoNLLSentence> changedSentencesMap = new LinkedHashMap<>();

		

		//empty the directories beforehand so that there are no older files
		if (!Strings.isNullOrEmpty(output1)) {
			File outputDir = new File(output1);
			FileUtils.cleanDirectory(outputDir);
		}
		
		if (!Strings.isNullOrEmpty(output2)) {
			File outputDir = new File(output2);
			FileUtils.cleanDirectory(outputDir);
		}
		
		
		long counter = 0;

		// iterate through all the files
		for (File file : files) {

			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, Format.HIT,true);
			for (CoNLLSentence coNLLSentence : sentenceList) {
				counter++;
				origSentenceMap.put(counter, coNLLSentence);
			}

		}

		EmotibotTokenizer tokenizer = new EmotibotTokenizer();
		for (Long id : origSentenceMap.keySet()) {
			CoNLLSentence coNLLSentence = origSentenceMap.get(id);
			String sent = coNLLSentence.getSentence();

			String[] tokens = tokenizer.tokenize(sent);
			List<String> tokenList = new ArrayList<String>( Arrays.asList( tokens ) );

			List<String> origTokenList = coNLLSentence.getTokenList();
			
			
			
			String source="";
			String newSource="";
			
			source=coNLLSentence.getSource();
			
			if(tokenList.size()!=origTokenList.size()){
				//System.out.println(coNLLSentence.getSentence());
				System.out.println("old:" + origTokenList.size() + " :" + origTokenList  + "   ||NEW:"+ tokenList.size() + " :" + tokenList);

				changedSentencesMap.put(id, coNLLSentence);
				newSource=source.replace("data/corpus_validation/good_cases/hit", "data/wordsegfix/change/new");
			}
			
			else{
				 
				newSource=source.replace("data/corpus_validation/good_cases/hit", "data/wordsegfix/change/orig");
			
				
			}
			
			File f= new File(newSource);
			String data=coNLLSentence.getHITSentence()+ "\n";
			FileUtils.writeStringToFile(f, data, ENCODING,true);
			
			int temp = 0;
		}
		
		System.out.println(changedSentencesMap.size());

	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		TestWordSegmentationUpdate wsup = new TestWordSegmentationUpdate();
		wsup.test();
	}

}
