/**
 * 
 */
package com.emotibot.srl.pruner;

import java.io.IOException;

/**
 * @author Sanjay
 *
 */
public class TestParseTreeOnTokenizedText {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		TestParseTrees testTrees = new TestParseTrees();
		String tokenized_file = "data/syntactictree/others/tokenized_sentences.txt";
		testTrees.parseTreeOnTokenizedText(tokenized_file);
	}
	
	
}