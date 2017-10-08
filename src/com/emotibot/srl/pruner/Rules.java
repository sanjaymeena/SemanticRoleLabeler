package com.emotibot.srl.pruner;

public class Rules {
	
	
	// Our goal is to write rules which are applicable over large amount of sentences and different sentence structures. 
	//We want our rules to be generic in nature

	/**
	 * Prune IJ $ PU at any level E.g. 嗯，我能不能把他换成那个什么套餐那个七块钱的那个什么套餐啊
	 * 
	 */
	public static String IJ_with_sister_PU = "(IJ=prune_this $ PU=prune2_this)";

	/**
	 * Prune IJ $+ AD E.g. 喔好的好的行那过几天过两天再查恩好
	 * 
	 * 
	 */
	public static String IJ_with_sister_AD = "(IJ=prune_this $+ AD=prune2_this)";
	/**
	 * Prune IJ at any level E.g. 恩...密码知道的，恩
	 * 
	 */
	public static String IJ_remove_anywhere = "(ROOT << IJ=prune_this)";
	/**
	 * If the phrase before first PU has these tokens, press the phrase+ PU
	 * 
	 */
	public static String phrase_with_removable_token_sister_PU = "(NP|NN|VP|VV|VA|VE|NN|NNS|FLR=prune_this <<  恩|对|ㄜ|疴|你好|您好|嗯 ) $+ PU=prune2_this";
	/**
	 * Prune these tokens no matter where there positions is
	 * 
	 */
	public static String tokens_remove_anywhere = "ROOT << 恩|阿|额|ㄟ|您好|你好|疴|就是|那么|欸||算了|摁=prune_this";

}
