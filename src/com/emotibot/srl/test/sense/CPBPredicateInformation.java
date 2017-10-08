package com.emotibot.srl.test.sense;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

public class CPBPredicateInformation {

	private static String cbp3_verbs_file = "data/cpb/input/cpb3.0-verbs.txt";
	private static String cbp3_nouns_file = "data/cpb/input/cpb3.0-nouns.txt";
	private List<String> proposition_files;

	public CPBPredicateInformation() {
		proposition_files = new ArrayList<String>();
		proposition_files.add(cbp3_verbs_file);
		proposition_files.add(cbp3_nouns_file);
	}

	public void test() throws IOException {

		Map<String, Predicate> nounPredicatesMap = new HashMap<String, Predicate>();
		Map<String, Predicate> verbPredicatesMap = new HashMap<String, Predicate>();

		verbPredicatesMap = extractPredicateInformation(cbp3_verbs_file);
		nounPredicatesMap = extractPredicateInformation(cbp3_nouns_file);

		Set<String> verb_preds = verbPredicatesMap.keySet();
		Set<String> noun_preds = nounPredicatesMap.keySet();

		/**
		 * We want to see which words are acting as both nouns and verbs
		 */

		List<String> words_both_noun_verbs = new ArrayList<String>();
		for (String string : verb_preds) {
			if (noun_preds.contains(string)) {
				// System.out.println(string + " acts a both noun and verb
				// predicate");

				words_both_noun_verbs.add(string);
			}
		}

		System.out.println("total words acting as both verb and nouns : " + words_both_noun_verbs.size());
		System.out.println(words_both_noun_verbs);
	}

	/**
	 * Returns the predicate information map derived from nuon and verb
	 * predicates
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, Predicate> getPredicateInformationMap() throws IOException {

		Map<String, Predicate> nounPredicatesMap = new HashMap<String, Predicate>();
		Map<String, Predicate> verbPredicatesMap = new HashMap<String, Predicate>();

		verbPredicatesMap = extractPredicateInformation(cbp3_verbs_file);
		nounPredicatesMap = extractPredicateInformation(cbp3_nouns_file);

		verbPredicatesMap.putAll(nounPredicatesMap);

		return verbPredicatesMap;
	}

	public Map<String, Predicate> extractPredicateInformation(String cpb_file) throws IOException {
		// TODO Auto-generated method stub

		// String outputFile = "data/cpb/output/cpb3.0-verbs.txt";
		File file = new File(cpb_file);

		// predicate map
		Map<String, Predicate> predMap = new HashMap<String, Predicate>();

		List<String> contents = FileUtils.readLines(file, "UTF-8");

		for (String string : contents) {

			String row = string;
			if (!Strings.isNullOrEmpty(row)) {

				String[] vals = row.split(" ");
				String verb = vals[4];

				verb = verb.replace(".", " ");
				String[] val2 = verb.split(" ");
				String p = val2[0];
				String s = val2[1];

				int senseVal = 0;
				if (!s.equals("XX")) {
					senseVal = Integer.parseInt(s);

				}

				Predicate pred = new Predicate();
				if (predMap.containsKey(p)) {
					pred = predMap.get(p);
				} else {

					pred.setString(p);
				}

				Map<Integer, Integer> map1 = pred.getSenseMap();
				if (map1.containsKey(senseVal)) {
					int origCount = map1.get(senseVal);
					map1.put(senseVal, origCount + 1);
				} else {
					map1.put(senseVal, 1);
				}

				predMap.put(p, pred);
			}

			else {

			}

		}

		Map<String, Predicate> mulPredSenseMap = new HashMap<String, Predicate>();

		for (String string : predMap.keySet())

		{
			Predicate pred = predMap.get(string);
			Map<Integer, Integer> sMap = pred.getSenseMap();
			if (sMap.keySet().size() > 1) {
				mulPredSenseMap.put(string, pred);
				pred.setHasMultipleSenses(true);
			}
			System.out.println("pred: " + string + "    ||sense=count : " + pred.getSenseMap());
		}

		int singleSensePredicates = predMap.keySet().size() - mulPredSenseMap.keySet().size();
		System.out.println("Total predicates : " + predMap.keySet().size());
		System.out.println("Total predicates with multiple sense : " + mulPredSenseMap.keySet().size());
		System.out.println(mulPredSenseMap.keySet());
		System.out.println("Total predicates with single sense : " + singleSensePredicates);

		return predMap;
	}

	/**
	 * This returns the CPB Predicate map of : Form -> Predicate for the LDC CPB
	 * 8.0 data
	 * 
	 * @return
	 * @throws IOException
	 */
	public Map<String, Predicate> getCPBPredicateMap() throws IOException {
		// predicate map
		Map<String, Predicate> predMap = new HashMap<String, Predicate>();
		File file = new File(cbp3_verbs_file);

		List<String> contents = FileUtils.readLines(file, "UTF-8");

		for (String string : contents) {

			String row = string;
			if (!Strings.isNullOrEmpty(row)) {

				String[] vals = row.split(" ");
				String verb = vals[4];

				verb = verb.replace(".", " ");
				String[] val2 = verb.split(" ");
				String p = val2[0];
				String s = val2[1];

				int senseVal = 0;
				if (!s.equals("XX")) {
					senseVal = Integer.parseInt(s);

				}

				Predicate pred = new Predicate();
				if (predMap.containsKey(p)) {
					pred = predMap.get(p);
				} else {

					pred.setString(p);
				}

				Map<Integer, Integer> map1 = pred.getSenseMap();
				if (map1.containsKey(senseVal)) {
					int origCount = map1.get(senseVal);
					map1.put(senseVal, origCount + 1);
				} else {
					map1.put(senseVal, 1);
				}

				predMap.put(p, pred);
			}

		}

		return predMap;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		CPBPredicateInformation cbp = new CPBPredicateInformation();
		// cbp.extractPredicateInformation(cbp3_verbs_file);

		cbp.test();
	}

}
