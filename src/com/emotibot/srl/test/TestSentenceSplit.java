/**
 * 
 */
package com.emotibot.srl.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sanjay
 *
 */
public class TestSentenceSplit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		String p1="[.]|[!?]+|[\u3002]|[\uFF01\uFF1F]+";
		
		String doc = "你好！很高兴认识你，我叫小明。\\n 我叫张三。我叫小花。\\n 哈哈，快叫我女汉子。\\n";
		
		System.out.println(doc);

//		Pattern p = Pattern.compile(p1);
//		Matcher m = p.matcher(input);
//
//		List<String> sentences = new ArrayList<String>();
//		while (m.find()) {
//			System.out.println("Found a " + m.group() );
//			int val = m.start();
//			int end=m.end();
//			sentences.add(m.group());
//		}
//		
//		
//		for (String string : sentences) {
//			System.out.println(string);
//		}
	}
	


}
