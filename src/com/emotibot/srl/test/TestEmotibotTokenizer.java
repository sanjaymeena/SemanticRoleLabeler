package com.emotibot.srl.test;

import java.util.ArrayList;
import java.util.List;

import com.emotibot.enlp.EWord;
import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.SegmentResult;

/**
 * @author Sanjay
 *
 */
public class TestEmotibotTokenizer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String text = "唯品国际商品为什么没有防伪溯源码";
		// NLPService.check();
		// getWords1(text);
		// getWords(text);
		// getWordsAll(text);
		// getWords(text);

		tokenize(text, "dfd");
	}

	/**
	 * Test function for VIP Case for tokenization
	 * 
	 * @param text
	 * @return
	 */
	public static String[] tokenize(String text, String case_type) {
		String[] tokens = null;

		ArrayList<String> tokensList = new ArrayList<String>();
		String fid = "1200";
		// SegmentResult segmentResult = NLPService.getWords(text);
		SegmentResult segmentResult = NLPService.getWords(fid, text);

		for (EWord w : segmentResult.wordList) {
			// sometimes there were empty strings.
			// String token = w.word.trim();
			// if (!token.equals("")) {
			tokensList.add(w.word);
			// }

		}
		tokens = tokensList.toArray(new String[tokensList.size()]);
		for (String string : tokens) {
			System.out.println(string + " ");
		}

		return tokens;
	}

	public static SegmentResult getWords1(String text) {
		SegmentResult segmentResult = NLPService.getWords(text);
		ArrayList<String> tokensList = new ArrayList<String>();

		for (EWord w : segmentResult.wordList) {
			// sometimes there were empty strings.
			// String token = w.word.trim();
			// if (!token.equals("")) {
			tokensList.add(w.word);
			// }

		}
		String[] tokens = tokensList.toArray(new String[tokensList.size()]);
		for (String string : tokens) {
			System.out.print(string + " ");
		}

		return segmentResult;
	}

	public static SegmentResult getWords(String text) {
		SegmentResult segmentResult = NLPService.getWords(text);
		int counter = 0;
		String[] tokens;
		ArrayList<String> tokensList = new ArrayList<String>();
		for (EWord w : segmentResult.wordList) {
			tokensList.add(w.word);
			counter++;
			System.out.println(counter + " : " + w.word + "/" + w.nature.toString() + " ");
		}
		return segmentResult;
	}

	public static List<SegmentResult> getWordsAll(String text) {
		List<SegmentResult> segmentResultList = NLPService.getWordsAll(text);
		System.out.println(text);
		for (SegmentResult segmentResult : segmentResultList) {
			System.out.println("word segment:" + segmentResult.modelId);

			for (EWord w : segmentResult.wordList) {
				System.out.print(w.word + "/" + w.nature.toString() + "  ");
			}
		}
		return segmentResultList;
	}

}
