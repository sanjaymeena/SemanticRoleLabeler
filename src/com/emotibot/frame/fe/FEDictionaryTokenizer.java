package com.emotibot.frame.fe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class FEDictionaryTokenizer {

	String[] case_type_list = {"", "vip"};
	
	String inputDirPath = "dictionary_w_sen"; 
	String outputDirPath = "dict_new";
	
	private EmotibotTokenizer tokenizer;
	
	public FEDictionaryTokenizer() {
		tokenizer = new EmotibotTokenizer();
	}
	
	public static void main(String[] args) throws IOException {
		
		FEDictionaryTokenizer fedt = new FEDictionaryTokenizer();
		
		fedt.updateDict();
	}
	
	public void updateDict() throws IOException {
		
		File outputDir = new File(outputDirPath);
		if (outputDir.exists()) {
			FileUtils.cleanDirectory(outputDir);
		} else {
			outputDir.mkdirs();
		}
		
		
		File inputDir = new File(inputDirPath);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(inputDir, SUFFIX, true);

		for (File file : files) {
		    try {
		    	StringBuilder sb = new StringBuilder();
		    	
		        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));         

		        String line;
		        while ((line = br.readLine()) != null) {

		        	if (Strings.isNullOrEmpty(line.trim())) {
						continue;
					} else {
						List<String> tokenizedSenList = new ArrayList<String>();
						
						String[] sentenceList = line.trim().split("\t");
						String entity = sentenceList[0];
						tokenizedSenList.add(entity);
						
						for (String sentence : sentenceList) {
							for (String case_type : case_type_list) {
								String spaceTokenizedSentence = getTokenizationOfEntity(entity, sentence, case_type);
				        		if (!Strings.isNullOrEmpty(spaceTokenizedSentence) && !tokenizedSenList.contains(spaceTokenizedSentence))
				        			tokenizedSenList.add(spaceTokenizedSentence);
							}
						}
			        	
			        	sb.append(String.join("\t", tokenizedSenList));
			        	sb.append("\n");
			        	
					}

		        }
		        br.close();
		        
		        Path outputPath = Paths.get(outputDirPath, file.getName());
		        
				FileUtils.write(outputPath.toFile(), sb.toString());

		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	
	/**
	 * Extract tokenized entity based on sentence
	 * @param entity
	 * @param sentence
	 * @param case_type
	 * @return
	 */
	private String getTokenizationOfEntity(String entity, String sentence, String case_type) {
		
		String ret = "";
		
		// Here we assume entity occur only once
		int word_start_idx = sentence.indexOf(entity); 
		int word_end_idx = word_start_idx + entity.length();
		
		String[] tokens = tokenizer.tokenize(sentence, case_type);
		
		int acc = 0;		
		int token_start_idx = -1;
		int token_end_idx = -1;
		for (int idx = 0 ; idx < tokens.length ; idx++) {
			if (acc == word_start_idx) 
				token_start_idx = idx;

			acc += tokens[idx].length();

			if (acc == word_end_idx)
				token_end_idx = idx;

		}
		
		if (token_start_idx != -1 && token_end_idx != -1) {
			ret = String.join(" ", Arrays.copyOfRange(tokens, token_start_idx, token_end_idx + 1));
		} 
		
		return ret;
		
	}

}
