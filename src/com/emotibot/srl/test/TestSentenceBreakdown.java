package com.emotibot.srl.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.google.common.base.Strings;

public class TestSentenceBreakdown {

	public static void main(String args[]){
		//[u'~','~',u' ',u',', u' ', '、',u'、', u',', u'，', u'。', ',', u'?',' ', u'？', u'!', '?', '？', '！']
	Set<String> tokens=new HashSet<>();
	//tokens.add(e)
	
	String text="对呀不好意思找他要。。。";
	String delimiter="~、，。?!";
	
	String[] strings = StringUtils.split(text, delimiter);
	for (String string : strings) {
		System.out.println("str  " + string);
	}
	
	test2();
	
	}

	private static void test2() {
		// TODO Auto-generated method stub
		ArrayList<CoNLLSentence> conLLSentenceList=new ArrayList<CoNLLSentence>();
		String file = "data/emotibot/bad_cases/srl_bad_cases.txt";
	
		String delimiter = "~、，。?!";

		System.out.println("Opening reader for " + file + "...");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			String str;

			// new sentence at every line.
			while ((str = in.readLine()) != null) {

				if (str.trim().equals("")) {
					continue;
				} else {

					String[] strings = StringUtils.split(str, delimiter);
					for (String string : strings) {
						
						if (string.endsWith("（*/∇＼*）")) {
							string.replace("（*/∇＼*）", "");
						}
						if (string.endsWith("（*/∇＼*）")) {
							string.replace("（*/∇＼*）", "");
						}
						
						
						//string = string.replaceAll("(?m)^[ \t]*\r?\n", "");
						if (!Strings.isNullOrEmpty(string) && !StringUtils.isBlank(string) && string.length()>0 && !string.equals("\n")) {
							CoNLLSentence sentence = new CoNLLSentence();
							sentence.setSentence(string);
							conLLSentenceList.add(sentence);
						}
					}

					// CoNLLSentence sentence = new CoNLLSentence();
					// sentence.setSentence(str);

					// break;
				}
			}

		} catch (IOException e) {
			System.out.println("Failed: " + e.toString());
			System.exit(1);
		}

		
		for (CoNLLSentence coNLLSentence : conLLSentenceList) {
			System.out.println(coNLLSentence.getSentence());
		}
		System.out.println("Finished reading  " + file + "...");
	}
	
	
}
