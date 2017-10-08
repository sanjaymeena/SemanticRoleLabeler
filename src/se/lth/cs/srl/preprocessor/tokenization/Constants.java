package se.lth.cs.srl.preprocessor.tokenization;

import java.util.HashMap;

public class Constants {

	private static final HashMap<String, String> caseFidMap;
	static {
		caseFidMap = new HashMap<String, String>();
		caseFidMap.put("knowledge", "1000");
		caseFidMap.put("youzu", "1100");
		caseFidMap.put("vip", "1200");
		caseFidMap.put("letv", "1300");
		caseFidMap.put("sports", "1000000");
	}
	
	public static String getFidByCaseString(String case_type) {
		return caseFidMap.get(case_type);
	}

}
