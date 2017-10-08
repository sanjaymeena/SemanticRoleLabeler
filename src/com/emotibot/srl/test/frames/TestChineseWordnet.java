package com.emotibot.srl.test.frames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class TestChineseWordnet {

	Multimap<String, String> wordnet_mapping = LinkedHashMultimap.create();
	Multimap<String, String> synset_mapping = LinkedHashMultimap.create();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		TestChineseWordnet tf = new TestChineseWordnet();

		String inputDir = "resources/semantic_frame/chinese-open-wordnet/wn-data-cmn.csv";
		tf.readCWN(inputDir);

		//tf.testLexicalUnits();
	}

	public void testLexicalUnits() throws IOException {
		String inputDir = "resources/semantic_frame/chinese_frames/qq_sentences/qq_sentences.txt";
		EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();
		Set<String> lexicalUnitSet = wordnet_mapping.keySet();

		File file = new File(inputDir);

		int totalSentenes;
		List<String> contents = FileUtils.readLines(file, "UTF-8");
		totalSentenes = contents.size();
		int counter = 1;

		String frame = "";
		String description = "";
		List<String> no_lexical_unit_sent = new ArrayList<>();
		Frame sframe = new Frame();
		for (String string : contents) {

			boolean found = false;
			List<String> arr = new ArrayList<>();

			ArrayList<String> tokens = emotibotTokenzier.tokenizeandPOS(string,"",true);
			getProperPOSMapping(tokens);
			System.out.println(tokens);
			//List<String> list = Arrays.asList(tokens);
			for (String str : tokens) {

				if (lexicalUnitSet.contains(str)) {
					arr.add(str);
					found = true;
				}

			}

			List<List<String>> lol = new ArrayList<>();
			if (found) {
				Collection<String> synset = wordnet_mapping.get(arr.get(0));
				for (String string2 : synset) {
					List<String> temp = new ArrayList<>();
					Collection<String> similar_words = synset_mapping.get(string2);
					for (String string3 : similar_words) {
						temp.add(string3);
					}
					lol.add(temp);

					
				}
			}

			else {
				no_lexical_unit_sent.add(string);
			}
			System.out.println(string + " :   " + arr+ "  : similar words :"  + lol);

		}

		System.out.println("total sentences with no lexical unit: " + no_lexical_unit_sent.size());

	}

	private void getProperPOSMapping(ArrayList<String> tokens) {
		// TODO Auto-generated method stub
		for (String string : tokens) {
			String[] values = string.split("/");
			String pos=values[1];
			String token=values[0];
			
			int totalSentenes=10;
			
		}
	}

	private void readCWN(String inputDir) throws IOException {
		File file = new File(inputDir);

		Set<String> posSet=new HashSet<>();
		
		int totalSentenes;
		List<String> contents = FileUtils.readLines(file, "UTF-8");
		totalSentenes = contents.size();
		int counter = 1;

		for (String string : contents) {

			if (counter == 1) {
				counter++;
				continue;
			}
			String[] values = string.split("\t");
			
			String[] vs = values[0].split("-");
			LexicalUnit lu=new LexicalUnit(values[2],vs[1],values[0]);
			
			posSet.add(vs[1]);
			wordnet_mapping.put(lu.toString(), values[0]);
			synset_mapping.put(values[0],lu.toString());
			counter++;
		}

		System.out.println(wordnet_mapping.keySet().size());
		
		for (String string : synset_mapping.keySet()) {
			
			System.out.println(string + " :   " + synset_mapping.get(string));
		}
		
		//pos tags info
		System.out.println("POS in Wordnet..");
		for (String string : posSet) {
			System.out.println(string);
		}
	}
}
