/**
 * 
 */
package com.emotibot.srl.test.frames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Sanjay
 *
 */
public class OpenWordnet {

	Multimap<String, String> word_with_pos_to_synset_mapping;
	Multimap<String, String> word_to_synset_mapping;
	Multimap<String, String> synset_mapping;
	Set<String> lexicalUnitSet;
	String inputDir = "resources/semantic_frame/chinese-open-wordnet/wn-data-cmn.csv";
	private volatile static OpenWordnet wordnetInstance;
	
	/**
	 * @throws IOException
	 * 
	 */
	public OpenWordnet() throws IOException {
		word_with_pos_to_synset_mapping = LinkedHashMultimap.create();
		word_to_synset_mapping = LinkedHashMultimap.create();
		synset_mapping = LinkedHashMultimap.create();

		readCWN(inputDir);

		lexicalUnitSet = word_with_pos_to_synset_mapping.keySet();

	}

	/**
	 * 
	 * @param inputDir
	 * @throws IOException
	 */
	private void readCWN(String inputDir) throws IOException {
		File file = new File(inputDir);
		Set<String> posSet = new HashSet<>();
		List<String> contents = FileUtils.readLines(file, "UTF-8");

		int counter = 1;

		for (String string : contents) {

			if (counter == 1) {
				counter++;
				continue;
			}
			String[] values = string.split("\t");

			String[] vs = values[0].split("-");
			LexicalUnit lu = new LexicalUnit(values[2], vs[1], values[0]);

			posSet.add(vs[1]);
			word_with_pos_to_synset_mapping.put(lu.toString(), values[0]);
			synset_mapping.put(values[0], lu.toString());
			word_to_synset_mapping.put(values[2], values[0]);
			counter++;
		}

		System.out.println("total unique words in wordnet: " + word_with_pos_to_synset_mapping.keySet().size());
		System.out.println("POS in wordnet: " + posSet.size() + " , POS list: " + posSet);

	}

	/**
	 * 
	 * @return
	 */
	public synchronized static OpenWordnet instance() {
		if (wordnetInstance == null) {
			try {

				synchronized (OpenWordnet.class) {
					if (wordnetInstance == null) {
						System.out.println("Creating new instance for chinese wordnet..");
						wordnetInstance = new OpenWordnet();

					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return wordnetInstance;

	}

	/**
	 * Get matched words
	 * 
	 * @param tokens
	 */
	public Map<Integer, String> getFoundWords(Map<Integer, String> tokensMap) {

		Map<Integer, String> foundTokensMap = new LinkedHashMap<>();
		for (Integer key : tokensMap.keySet()) {
			String value = tokensMap.get(key);
			if (isPresent(value)) {
				foundTokensMap.put(key, value);
			}
		}
		return foundTokensMap;
	}

	/**
	 * 
	 * @param tokens
	 */
	public List<String> getFoundWords(List<String> tokens) {
		List<String> array = new ArrayList<>();
		for (String str : tokens) {

			if (isPresent(str)) {
				array.add(str);
			}
		}
		return array;
	}

	public List<List<String>> getSimilarWords(String token, boolean usePOS) {

		List<List<String>> similarWords = new ArrayList<>();

		Collection<String> synset;
		if (usePOS) {
			synset = word_with_pos_to_synset_mapping.get(token);
		} else {
			synset = word_to_synset_mapping.get(token);
		}
		for (String string2 : synset) {
			List<String> temp = new ArrayList<>();
			Collection<String> similar_words = synset_mapping.get(string2);
			for (String string3 : similar_words) {
				temp.add(string3);
			}
			similarWords.add(temp);

		}

		return similarWords;
	}
	
	/**
	 * Get Synset List given the word and optionally pos
	 * @param token
	 * @param usePOS
	 * @return
	 */
	public List<String> getSynsetList(String token, boolean usePOS) {


		Collection<String> synset;
		if (usePOS) {
			synset = word_with_pos_to_synset_mapping.get(token);
		} else {
			synset = word_to_synset_mapping.get(token);
		}
		
		List<String> synsetList = new ArrayList<String>(synset);

		return synsetList;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPresent(String token) {
		boolean isPresent = false;
		if (lexicalUnitSet.contains(token)) {

			isPresent = true;
		}
		return isPresent;

	}

}
