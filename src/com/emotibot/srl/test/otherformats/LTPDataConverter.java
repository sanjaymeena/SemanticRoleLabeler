/**
 * 
 */
package com.emotibot.srl.test.otherformats;

import static com.emotibot.srl.format.Constants.TAB_PATTERN;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.emotibot.srl.test.sense.CPBPredicateInformation;
import com.emotibot.srl.test.sense.Predicate;
import com.google.common.base.Strings;

/**
 * This class can convert from LTP SRL Format to CONLL2009 Format
 * 
 * @author Sanjay
 *
 */
public class LTPDataConverter {

	long global_error_counter = 0;

	int index_column = 0;
	int form_column = 1;
	int lemma_column = 2;
	int plemma_column = 3;
	int pos_column = 4;
	int ppos_column = 5;
	int feat_column = 6;
	int pfeat_column = 7;
	int dep_index_column = 8;
	int pdep_index_column = 9;
	int depRel_column = 10;
	int pdepRel_column = 11;
	int isPred_column = 12;
	int pred_column = 13;

	int total_default_columns = 14;

	int ltp_index_column = 0;
	int ltp_form_column = 1;
	int ltp_pos_column = 4;
	int ltp_dep_column = 6;
	int ltp_depRel_column = 7;
	int ltp_pred_column = 10;
	int ltp_srl_columns = 11;

	String default_predicate_sense = ".01";
	String delimiter = "_";
	String predicateString = "Y";

	List<CoNLLSentence> badCases = new ArrayList<>();
	List<CoNLLSentence> goodCases = new ArrayList<>();

	/**
	 * This class will convert from LTP SRL format to CONLL2009 Format
	 * 
	 * @throws IOException
	 */
	public void test() throws IOException {
		// TODO Auto-generated method stub

		String inputDir = "data2/ltp/test/input";
		String outputDir = "data2/ltp/test/output";

		DataFormatConverter dfc = new DataFormatConverter();
		List<File> files = new ArrayList<File>();

		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		if (!Strings.isNullOrEmpty(outputDir)) {
			File outputDir1 = new File(outputDir);
			FileUtils.cleanDirectory(outputDir1);
		}

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		System.out.println("Reading each file in directory :" + inputDir);
		// iterate through all the files

		//
		for (File file : files) {
			List<CoNLLSentence> sentences = dfc.readCoNLLFormatCorpus(file, Format.HIT, false);
			goodCases = new ArrayList<>();
			badCases = new ArrayList<>();

			for (CoNLLSentence coNLLSentence : sentences) {

				// do the format transformation
				testHelper(coNLLSentence);

			}

			StringBuilder sb1 = new StringBuilder();
			for (CoNLLSentence coNLLSentence : goodCases) {
				String sent = coNLLSentence.getHITSentence();
				if (!Strings.isNullOrEmpty(sent)) {
					sb1.append(sent + "\n");
				}
			}
			sb1.append("\n");

			String f = file.getName().replace(".txt", "_goodcase.txt");
			String out = outputDir + File.separator + f;
			FileUtils.write(new File(out), sb1.toString());

			sb1 = new StringBuilder();
			for (CoNLLSentence coNLLSentence : badCases) {
				String sent = coNLLSentence.getHITSentence();
				if (!Strings.isNullOrEmpty(sent)) {
					sb1.append(sent + "\n");
				}
			}
			sb1.append("\n");

			f = file.getName().replace(".txt", "_badcase.txt");
			out = outputDir + File.separator + f;
			FileUtils.write(new File(out), sb1.toString());

		}

	}

	public void testHelper(CoNLLSentence sentence) {

		boolean errorFound = false;

		ArrayList<String> vals = null;
		ArrayList<String> valsUpdate = new ArrayList<String>();

		String[] lines = sentence.getLines();

		ArrayList<ArrayList<String>> listOfListsTemp = new ArrayList<ArrayList<String>>();
		int total_columns = TAB_PATTERN.split(lines[0]).length;
		for (int i = 0; i < total_columns; i++) {
			listOfListsTemp.add(new ArrayList<String>());
		}

		for (int k = 0; k < lines.length; k++) {
			String line = lines[k];
			String[] cols = TAB_PATTERN.split(line);

			for (int index = 0; index <= cols.length - 1; index++) {

				try {
					listOfListsTemp.get(index).add(cols[index]);
				} catch (java.lang.IndexOutOfBoundsException e) {
					e.printStackTrace();
					global_error_counter++;
					System.err.println(sentence.getHITSentence());
					errorFound = true;
					badCases.add(sentence);

				}
			}

		}

		if (!errorFound) {

			// now we we need to perform various transformations and checks
			// lets add total columns
			int conll2009_total_columns = 14;
			ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < conll2009_total_columns; i++) {
				listOfLists.add(new ArrayList<String>());
			}

			// Step 1 : Add index
			vals = listOfListsTemp.get(ltp_index_column);
			for (String string : vals) {
				int index = Integer.parseInt(string);
				index += 1;
				valsUpdate.add(Integer.toString(index));
			}
			listOfLists.add(index_column, valsUpdate);

			// Step 2 : Add forms
			vals = listOfListsTemp.get(ltp_form_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {
				valsUpdate.add(string);
			}
			listOfLists.add(form_column, valsUpdate);

			// Step 3 : Add lemma col
			vals = listOfListsTemp.get(ltp_form_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {
				valsUpdate.add(string);
			}
			listOfLists.add(lemma_column, valsUpdate);

			// Step 4 : Add plemma col
			vals = listOfListsTemp.get(ltp_form_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {
				valsUpdate.add(delimiter);
			}
			listOfLists.add(plemma_column, valsUpdate);

			// Step 5 : Add pos col
			vals = listOfListsTemp.get(ltp_pos_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {
				valsUpdate.add(string);
			}
			listOfLists.add(pos_column, valsUpdate);

			// Step 6 : Add ppos col
			vals = listOfListsTemp.get(ltp_pos_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {
				valsUpdate.add(delimiter);
			}
			listOfLists.add(ppos_column, valsUpdate);

			// Step 7 & 8 : Add feat and pfeat column
			vals = listOfListsTemp.get(ltp_pos_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {
				valsUpdate.add(delimiter);
			}
			listOfLists.add(feat_column, valsUpdate);
			listOfLists.add(pfeat_column, valsUpdate);

			// Step 9 : Increment dependency numbers by 1
			vals = listOfListsTemp.get(ltp_dep_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {
				int index = Integer.parseInt(string);
				index += 1;
				valsUpdate.add(Integer.toString(index));
			}
			listOfLists.add(dep_index_column, valsUpdate);

			// Step 10 : Add pDep index column
			vals = listOfListsTemp.get(ltp_dep_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {

				valsUpdate.add(delimiter);
			}
			listOfLists.add(pdep_index_column, valsUpdate);

			// Step 11 : dependency relation column
			vals = listOfListsTemp.get(ltp_depRel_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {

				valsUpdate.add(string);
			}
			listOfLists.add(depRel_column, valsUpdate);

			// Step 12 : pdependency relation column
			vals = listOfListsTemp.get(depRel_column);
			valsUpdate = new ArrayList<String>();
			for (String string : vals) {

				valsUpdate.add(delimiter);
			}
			listOfLists.add(pdepRel_column, valsUpdate);

			// Step 13 : Here we will first deal with Predicate column.
			vals = listOfListsTemp.get(ltp_pred_column);
			ArrayList<String> predicateColList = new ArrayList<String>();
			List<Integer> predCols = new ArrayList<>();
			int counter = 0;
			for (String string : vals) {

				if (!string.equals(delimiter)) {
					// we add default predicate sense of ".01";

					predCols.add(counter);
					string = string + default_predicate_sense;
					// predCols.
				}

				counter++;
				predicateColList.add(string);
			}

			// we won't add the predicate column right away
			// listOfLists.add(pred_column, valsUpdate);

			valsUpdate = new ArrayList<String>();
			// Step 14 : Add the isPredicate column
			for (int i = 0; i < vals.size(); i++) {

				if (predCols.contains(i)) {
					valsUpdate.add(predicateString);
				} else {
					valsUpdate.add(delimiter);
				}
			}
			listOfLists.add(isPred_column, valsUpdate);
			listOfLists.add(pred_column, predicateColList);

			// Step 15: REst. now lets add all the remaining columns after
			// predicate
			int columnCounter = 0;
			for (int i = ltp_srl_columns; i < listOfListsTemp.size(); i++) {
				columnCounter++;
				vals = listOfListsTemp.get(i);
				listOfLists.add(pred_column + columnCounter, vals);

			}

			String delimiter1 = "\t";
			StringBuilder sb = new StringBuilder();
			// total columns is equal to columncounter+ default number of
			// columns
			total_columns = columnCounter + total_default_columns;

			try {
				for (int i = 0; i < lines.length; i++) {

					for (int j = 0; j < total_columns; j++) {
						if (listOfLists.get(j) != null) {
							ArrayList<String> vals1 = listOfLists.get(j);
							String c = vals1.get(i);

							if (j == total_columns - 1) {
								sb.append(c);
							} else {
								sb.append(c + delimiter1);
							}

						}

					}
					sb.append("\n");

				}

				sentence.setHITSentence(sb.toString());
				sentence.setDataList(listOfLists);

				
				
				ArrayList<String> isPredicateList = listOfLists.get(isPred_column);
				boolean hasPredicate=false;
				
				for (String string : isPredicateList) {
					if(string.equals("Y")){
						hasPredicate=true;
						break;
					}
				}
				
				if(!hasPredicate){
					badCases.add(sentence);
				}
				else{
					
					goodCases.add(sentence);
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(sentence.getHITSentence());
				badCases.add(sentence);
			}
		}

	}

	public void test2() throws IOException {
		DataFormatConverter dfc = new DataFormatConverter();
		List<File> files = new ArrayList<File>();

		CPBPredicateInformation cpb = new CPBPredicateInformation();
		Map<String, Predicate> predicateMap = cpb.getCPBPredicateMap();

		String inputDir = "data2/ltp/files";
		// String outputDir = "data2/ltp/test/output";

		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		// if (!Strings.isNullOrEmpty(outputDir)) {
		// File outputDir1 = new File(outputDir);
		// FileUtils.cleanDirectory(outputDir1);
		// }

		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			files = new ArrayList<File>(temp);
		} else {
			files.add(rootDir);
		}

		System.out.println("Reading each file in directory :" + inputDir);
		// iterate through all the files

		//
		for (File file : files) {
			List<CoNLLSentence> sentences = dfc.readCoNLLFormatCorpus(file, Format.HIT, false);
			goodCases = new ArrayList<>();
			badCases = new ArrayList<>();

			for (CoNLLSentence coNLLSentence : sentences) {
				test2Helper(coNLLSentence, predicateMap);
			}

			int t = 10;
		}
	}

	/**
	 * 
	 * @param coNLLSentence
	 * @param predicateMap
	 */
	private void test2Helper(CoNLLSentence coNLLSentence, Map<String, Predicate> predicateMap) {
		// TODO Auto-generated method stub
		String[] lines = coNLLSentence.getLines();

		String verbPOS = "v";
		long wrongPredicateCounter = 0;
		boolean wrongPredicate = false;

		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
		int total_columns = TAB_PATTERN.split(lines[0]).length;
		for (int i = 0; i < total_columns; i++) {
			listOfLists.add(new ArrayList<String>());
		}

		for (int k = 0; k < lines.length; k++) {
			String line = lines[k];
			String[] cols = TAB_PATTERN.split(line);

			for (int index = 0; index <= cols.length - 1; index++) {

				try {
					listOfLists.get(index).add(cols[index]);
				} catch (java.lang.IndexOutOfBoundsException e) {
					e.printStackTrace();

					System.err.println(coNLLSentence.getHITSentence());
				}
			}
		}

		if (listOfLists.size() > 1) {

			// check if there was any predicate in the list or not
			ArrayList<String> isPredicateList = listOfLists.get(isPred_column);
			boolean hasPredicate=false;
			
			for (String string : isPredicateList) {
				if(string.equals("Y")){
					hasPredicate=true;
					break;
				}
			}
			
			

			if(!hasPredicate){
			// get forms
			ArrayList<String> forms = listOfLists.get(form_column);
			ArrayList<String> posList = listOfLists.get(pos_column);

			int counter = 1;

			for (int i = 0; i < forms.size(); i++) {
				String string = forms.get(i);

				if (predicateMap.containsKey(string)) {

					Predicate key = predicateMap.get(string);
					String pos = posList.get(i);
					if (!pos.equals(verbPOS)) {

						int index = i + 1;
						System.out.println("Predicate not tagged as verb. : " + "index: " + index + " : string : "
								+ string + " : " + pos);
						wrongPredicateCounter++;
						wrongPredicate = true;
					}
				}

			}

		}

		if (wrongPredicate) {
			badCases.add(coNLLSentence);
			System.out.println(coNLLSentence.getHITSentence());
		} else {
			goodCases.add(coNLLSentence);
		}

		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		LTPDataConverter conv = new LTPDataConverter();
		conv.test();

		//conv.test2();
	}

}
