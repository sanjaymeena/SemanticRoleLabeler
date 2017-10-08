/**
 * 
 */
package com.emotibot.srl.test;

import com.emotibot.enlp.NLPService;

import mate.is2.data.SentenceData09;

/**
 * @author Sanjay
 *
 */
public class TestNLPSRLService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SentenceData09 instance=NLPService.getDependencyParse("1200", "");
		System.out.println(instance);
		
		instance=NLPService.getDependencyParse("1200", null);
		System.out.println(instance);
		
		instance=NLPService.getDependencyParse( "使用关键词后处理");
		System.out.println(instance);
		
		instance=NLPService.getDependencyParse("1200", "真不错，你的员工们会很开心的。");
		System.out.println(instance);
		
	}

}
