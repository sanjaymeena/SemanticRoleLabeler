package se.lth.cs.srl.preprocessor.tokenization;

import java.io.IOException;
import java.util.ArrayList;

import com.emotibot.enlp.EWord;
import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.SegmentResult;
import com.emotibot.srl.format.TagConverter;

public class EmotibotTokenizer implements Tokenizer {
	TagConverter tagConverter;

	/**
	 * 
	 */
	public EmotibotTokenizer() {
		// we add tag converter to convert from hanlp tags to srl pos tags
		try {
			tagConverter = new TagConverter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String[] tokenize(String sentence) {
		SegmentResult segmentResult = NLPService.getWords(sentence);
		ArrayList<String> tokensList = new ArrayList<String>();

		for (EWord w : segmentResult.wordList) {
			// sometimes there were empty strings.
			// String token = w.word.trim();
			// if (!token.equals("")) {
			tokensList.add(w.word);
			// }

		}
		String[] tokens = tokensList.toArray(new String[tokensList.size()]);

		return tokens;
	}

	/**
	 * 
	 * @param sentence
	 * @param addDummy
	 * @return
	 */
	public String[] tokenize(String sentence, boolean addDummy) {
		SegmentResult segmentResult = NLPService.getWords(sentence);
		ArrayList<String> tokensList = new ArrayList<String>();

		// We add this token to fix first token missing in the pos tokenizer
		if (addDummy) {
			tokensList.add("dummy");
		}

		for (EWord w : segmentResult.wordList) {
			// sometimes there were empty strings.
			// String token = w.word.trim();
			// if (!token.equals("")) {
			tokensList.add(w.word);
			// }

		}
		String[] tokens = tokensList.toArray(new String[tokensList.size()]);

		return tokens;
	}

	/**
	 * Test function for VIP Case for tokenization
	 * 
	 * @param text
	 * @return
	 */
	public String[] tokenize(String sentence, String case_type) {
		String fid = Constants.getFidByCaseString(case_type);
		if (fid == null) {
			return tokenize(sentence);
		}

		String[] tokens = null;

		ArrayList<String> tokensList = new ArrayList<String>();
		SegmentResult segmentResult = NLPService.getWords(fid, sentence);

		for (EWord w : segmentResult.wordList) {
			// sometimes there were empty strings.
			// String token = w.word.trim();
			// if (!token.equals("")) {
			tokensList.add(w.word);
			// }
		}
		tokens = tokensList.toArray(new String[tokensList.size()]);
		// for (String string : tokens) {
		// System.out.print(string + " ");
		// }
		return tokens;
	}

	/**
	 * This function provides both token+ pos in form : token/pos. We can also
	 * choose whether to convert hanlp tagset to CTB. We use tagconverter to
	 * convert from hanlp pos tagset to CTB tagset
	 * 
	 * @param sentence
	 * @param case_type
	 * @param convertToCTB
	 * @return
	 */
	public ArrayList<String> tokenizeandPOS(String sentence, String case_type, boolean convertToCTB) {
		String fid = Constants.getFidByCaseString(case_type);
		SegmentResult segmentResult;
		if (fid != null) {
			segmentResult = NLPService.getWords(fid, sentence);
		} else {
			segmentResult = NLPService.getWords(sentence);
		}

		ArrayList<String> tokensList = new ArrayList<String>();
		ArrayList<String> posList = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> result2 = new ArrayList<String>();
		for (EWord w : segmentResult.wordList) {

			tokensList.add(w.word);
			posList.add(w.nature.toString());
			result2.add(w.toString());

		}

		if (!convertToCTB) {
			result = result2;
		}

		else {

			String[] posArray = posList.toArray(new String[posList.size()]);
			try {
				posArray = tagConverter.convertHanLPToLTP(posArray);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < tokensList.size(); i++) {
				String r = tokensList.get(i) + "/" + posArray[i];
				result.add(r);
			}

		}

		return result;
	}

	public static void main(String[] args) throws IOException {
		// String text = "学校那么无聊，我再也不想去了";
		// String text = "我平时都是用货到付款为什么这一次就不可以使用了";
		String text = "阿联酋国家男子足球队一号门将是谁？";

		String[] tokens = new EmotibotTokenizer().tokenize(text);
		// String[] tokens = new EmotibotTokenizer().tokenize(text, "vip");
		// String[] tokens = new EmotibotTokenizer().tokenize(text, "letv");

		for (String token : tokens)
			System.out.println(token);
	}

}
