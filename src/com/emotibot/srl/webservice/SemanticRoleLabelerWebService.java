package com.emotibot.srl.webservice;
import com.emotibot.srl.tmr.datastructure.SRL;
import com.emotibot.srl.webservice.adaptar.URLUTF8Encoder;
import com.emotibot.srl.webservice.adaptar.WebServiceCaller;
import com.emotibot.srl.webservice.adaptar.WebServiceCaller.Language;
import com.google.gson.Gson;

/**
 * This class can connect to user input analysis system web service and fetch
 * json data. Json data is converted to a datastructure.
 * 
 * @author Sanjay_Meena
 * 
 */
public class SemanticRoleLabelerWebService {
	
	static String baseURL = "http://192.168.1.237:9000/SemanticRoleLabelingService";

	public static void main(String[] args) {
		SemanticRoleLabelerWebService srlws = new SemanticRoleLabelerWebService();
		String englishText = "Jack is a going to eat food at taipei before going to hongkong.";
		String chineseText="因為昨天下雨，所以我沒有去運動。";
		SRL srl;
		srl=srlws.performSemanticRoleLabelingUsingWebService(englishText, Language.english);
		srl=srlws.performSemanticRoleLabelingUsingWebService(chineseText, Language.chinese);

	
	}

	
	/**
	 * This function fetch the data depending on the call made
	 * 
	 * @param english
	 * 
	 * @param queryType
	 * @param vargs
	 * @return
	 */
	public SRL performSemanticRoleLabelingUsingWebService(String text,
			Language lang) {

		String endpoint;

		String url = baseURL;
		text=URLUTF8Encoder.encode(text);
		//text = text.replaceAll(" ", "+");
		url = url + "?" + "text=" + text;

		switch (lang) {
		case english:
			url = url + "&option=1";
			break;
		case chinese:
			url = url + "&option=2";
			break;

		default:
			break;
		}

		endpoint = url;

		String result = WebServiceCaller.getInstance().sendGetRequest(endpoint,
				null);

		Gson gson=new Gson();
		SRL srl=gson.fromJson(result, SRL.class);
		System.out.println(srl);

		return srl;
	}

	

	public void println(Object obj) {
		System.out.println(obj);
	}
	public static String getBaseURL() {
		return baseURL;
	}

	public static void setBaseURL(String baseURL) {
		SemanticRoleLabelerWebService.baseURL = baseURL;
	}

}
