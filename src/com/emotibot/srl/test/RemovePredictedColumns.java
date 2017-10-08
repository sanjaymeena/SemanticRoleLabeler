/**
 * 
 */
package com.emotibot.srl.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.google.common.base.Strings;

import static com.emotibot.srl.format.Constants.*;

/**
 * @author Sanjay
 *
 */
public class RemovePredictedColumns {

	public void removePredictedColumnsFromCONLL() throws IOException {
//		String f = "data/pos/srl-emotibot-train-pos.txt";
//		String f1 = "data/pos/srl-emotibot-train-pos_clean.txt";
		
		String f = "data/pos/srl-emotibot-train_conll.txt";
		String f1 = "data/pos/srl-emotibot-train_conll_clean.txt";
		
		File file = new File(f);
		
		DataFormatConverter dfc = new DataFormatConverter();
		ArrayList<CoNLLSentence> conllList = dfc.readCoNLLFormatCorpus(file, Format.CONLL,true);
		List<Integer> predictedColumnList = new ArrayList<>();

		predictedColumnList.add(conllformat_ppos_column_no);
		predictedColumnList.add(conllformat_phead_column_no);
		predictedColumnList.add(conllformat_pdeprel_column_no);

		dfc.removePredictedColumnsFromCONLLSentence(conllList, predictedColumnList);

		StringBuilder sb = new StringBuilder();
		for (CoNLLSentence c : conllList) {
			String s = c.getCoNLLSentence();
			if (!Strings.isNullOrEmpty(s)) {
				sb.append(s + "\n");
			}

		}
		
		
		FileUtils.writeStringToFile(new File(f1), sb.toString(),ENCODING);
		System.out.println("wrote to file : " + f1);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		RemovePredictedColumns rpc = new RemovePredictedColumns();
		rpc.removePredictedColumnsFromCONLL();
	}

}
