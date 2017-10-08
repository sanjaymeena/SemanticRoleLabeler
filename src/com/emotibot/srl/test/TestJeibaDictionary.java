/**
 * 
 */
package com.emotibot.srl.test;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import static com.emotibot.srl.format.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sanjay
 *
 */
public class TestJeibaDictionary {

	public void test() throws IOException {

		//String f = "data/jieba/dictionary/dict.txt.big";
		String f = "data/jieba/dictionary/dict.txt.small";
		String outputFolder = "data/jieba/dictionary/output/";
		File file = new File(f);

		List<String> lines = FileUtils.readLines(file, ENCODING);
		
		
		Table<String, String, Integer> wordTable = HashBasedTable.create();

		Multimap<String, String> wordToPOSMap = LinkedListMultimap.create();
		Multimap<String, String> posToWordMap = LinkedListMultimap.create();

		long counter=0;
		for (String string : lines) {

			// token frequency pos
			String[] tokens = string.split(" ");
			counter++;
			
			if (tokens.length == 3) {
				String form = tokens[0].trim();
				int freq = Integer.parseInt(tokens[1]);
				String pos = tokens[2].trim();

				boolean exists = wordToPOSMap.get(form).contains(pos);

				
				//this is to avoid duplicate string in the values of multimap for word to pos map
				if (!exists && !Strings.isNullOrEmpty(form) && !Strings.isNullOrEmpty(pos)) {
					wordToPOSMap.put(form, pos);
				}

				//this is to avoid duplicate string in the values of multimap for pos to word map
				exists = posToWordMap.get(pos).contains(form);
				if (!exists && !Strings.isNullOrEmpty(form) && !Strings.isNullOrEmpty(pos)) {
					posToWordMap.put(pos, form);
				}

				
				//put values in a map
				wordTable.put(string, pos, freq);
				
			} else {
				System.err.println("token array length is not equal to 3");
			}
			System.out.println(counter);
			
		}
		
		
		// Now we want to create ambiugous words , words which has more than one
				// POS detected by Tagger
		Set<String> ambiguous_words = new HashSet<String>();
		
		for (String key : wordToPOSMap.keySet()) {
			Collection<String> vals = wordToPOSMap.get(key);
			if (vals.size() > 1) {
				ambiguous_words.add(key);
			}
			System.out.println(key + " : " + vals.toString());
		}
		
		System.out.println("ambigious words : "+ ambiguous_words);

		//System.out.println(wordTable);
		
		for (String key : wordTable.rowKeySet()) {
			
			Map<String, Integer> vals = wordTable.row(key);
			System.out.println( key + " " +vals);
		}
		
		
		if (!Strings.isNullOrEmpty(outputFolder)) {
			File outputDir = new File(outputFolder);
			FileUtils.cleanDirectory(outputDir);
		}

		for (String key : posToWordMap.keySet()) {
			Collection<String> vals = posToWordMap.get(key);

			StringBuilder sb = new StringBuilder();

			for (String string : vals) {

				// we don't want to include ambiguous words in the pos list. So
				// we check for it here.
				
					sb.append(string + "\n");
				

			}

			String f1 = outputFolder + File.separator + key + ".txt";
			File file1 = new File(f1);
			FileUtils.write(file1, sb.toString(), "UTF-8");
			System.out.println("wrote output to " + file.toString());
		}

		
		Set<String> unique = new HashSet<String>(posToWordMap.values());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println("Printing word count per POS Category...");
		for (String key : posToWordMap.keySet()) {
			Collection<String> vals = posToWordMap.get(key);

			Set<String> unique1 = new HashSet<String>(vals);
			// System.out.println(key + " : " + vals.toString());
			System.out.println(key + "  : " + unique1.size());
		}
		System.out.println("Total pos tag types : " + posToWordMap.keySet().size());
		System.out.println("Total number of unique words in the data : " + unique.size());
		System.out.println("Total ambiguous words/(words with multiple pos) :  " + ambiguous_words.size());

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TestJeibaDictionary tjd = new TestJeibaDictionary();
		tjd.test();
	}

}
