package com.emotibot.srl.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.emotibot.srl.datastructures.Relation;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.server.Constants.DEPRELATIONS;
import com.emotibot.srl.server.Constants.SRLRELATIONS;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Utility class to fix various bad cases related to srl output
 * 
 * @author Sanjay
 *
 */
public class SRLBadCaseFixer {

	/**
	 * Check Bad Cases
	 * 
	 * @param json_ds
	 */
	public void checkBadCases(SRLJsonDataStructure json_ds) {

		// Collection<Relation> relations = relationsMap.values();

		checkBadCasesForIndividualRelations(json_ds);
		checkBadCasesForGroupRelations(json_ds);
	}

	private void checkBadCasesForGroupRelations(SRLJsonDataStructure json_ds) {
		// TODO Auto-generated method stub
		Multimap<String, Relation> relationsMap = json_ds.getSrl_multimap();
		Multimap<String, Relation> relationsMapNew = ArrayListMultimap.create();
		relationsMapNew.putAll(relationsMap);

		boolean foundVerbWithMultipleArgs = false;

		for (String key : relationsMap.keySet()) {

			Collection<Relation> relations = relationsMap.get(key);

			// total relations should be greater than 1 atleast
			if (relations.size() > 1) {

				Multimap<String, Relation> verbRelMap = ArrayListMultimap.create();

				for (Relation relation : relations) {

					String arg1Index = relation.getArg1_index_array().get(0);
					// List<String> arg2List = relation.getArg2_index_array();

					verbRelMap.put(arg1Index, relation);

				}

				List<Relation> newrelationList = new ArrayList<Relation>();
				boolean verbWithMultipleRelations = false;

				// This loop will essentially create new relation list
				for (String k : verbRelMap.keySet()) {

					Collection<Relation> rels = verbRelMap.get(k);
					// total relations for the verb should be greater than 1
					// atleast
					if (rels.size() > 1) {

						verbWithMultipleRelations = true;
						foundVerbWithMultipleArgs = true;

						int longestArg2 = 0;
						Relation longestRelation = new Relation();
						for (Relation relation : rels) {

							List<String> arg2List = relation.getArg2_index_array();
							int arg2Listsize = arg2List.size();
							if (arg2Listsize > longestArg2) {
								longestArg2 = arg2Listsize;
								longestRelation = relation;
							}

						}

						newrelationList.add(longestRelation);
						for (Relation relation : rels) {

							if (!relation.equals(longestRelation)) {

								List<String> longRel = longestRelation.getArg2_index_array();
								List<String> rel = relation.getArg2_index_array();

								if (longRel != null && rel != null && !longRel.containsAll(rel)) {
									newrelationList.add(relation);
								}

							}

						}
					} else {
						newrelationList.addAll(rels);
					}

				}

				if (verbWithMultipleRelations) {
					// replace the old relations with new one
					relationsMapNew.replaceValues(key, newrelationList);
				}

			}

		}

		if (foundVerbWithMultipleArgs) {
			json_ds.setSrl_multimap(relationsMapNew);
		}

	}

	private void checkBadCasesForGroupRelationsHelper(String key, Collection<Relation> relations) {
		// TODO Auto-generated method stub

	}

	/**
	 * Checks bad cases for Individual Relations
	 * 
	 * @param json_ds
	 */
	private void checkBadCasesForIndividualRelations(SRLJsonDataStructure json_ds) {

		Multimap<String, Relation> relationsMap = json_ds.getSrl_multimap();

		for (String key : relationsMap.keys()) {

			Collection<Relation> relations = relationsMap.get(key);

			for (Relation relation : relations) {

				checkVerbPresenceInArg2(relation, json_ds);

			}
		}

	}

	/**
	 * This function checks for cases where arg2 tokens contains a verb. The
	 * form is : relation (arg1, arg2)
	 * 
	 * E.g. <br>
	 * sentence : 我喜欢周杰伦的歌 <br>
	 * patient": [  "arg2": [ "3" ] }, { "喜欢": "我喜欢周杰伦的歌", "pred ": "喜欢.01", "
	 * arg1": "2", "arg2": [ "1", "2", "3", "4", "5" ] }
	 * 
	 * @param relation
	 * @param json_ds
	 * @param predicateIndexList
	 * @param list
	 * @return
	 */
	private boolean checkVerbPresenceInArg2(Relation relation, SRLJsonDataStructure json_ds) {

		boolean verbFound = false;
		List<String> arg1L = relation.getArg1_index_array();
		List<String> arg2L = relation.getArg2_index_array();

		Map<String, String> predicate_dependencyMap = json_ds.getPredRelationMap();
		String verbIndexString = arg1L.get(0);
		if (arg2L.contains(verbIndexString)) {
			verbFound = true;

			Map<String, String> relationTokenMap = relation.getTokenMap();
			List<Integer> arg1List = new ArrayList<Integer>();
			List<Integer> arg2List = new ArrayList<Integer>();

			for (String s : arg1L)
				arg1List.add(Integer.valueOf(s));
			for (String s : arg2L)
				arg2List.add(Integer.valueOf(s));

			int verbIndex = arg1List.get(0);

			try {
				/**
				 * If arg2 contains arg1
				 */
				if (arg2List.contains(verbIndex)) {

					int vi = arg2List.indexOf(verbIndex);

				

					String rel = relation.getSrl_relation();
					// case 1 : There is a MOD relation. MOD(arg1,arg2)
					// E.g. 我想打篮球 => Here We want , MOD (打，想) . E.g arg1 : [3] ,
					// arg2 : [1,2,3,4] => new arg2 : [2]
					if (rel.equals(SRLRELATIONS.MOD.toString())) {
						arg2List.remove(vi);
						Set<String> predicates = predicate_dependencyMap.keySet();
						for (String string : predicates) {

							Integer intVal = Integer.parseInt(string);
							String depRel = predicate_dependencyMap.get(string);
							if (arg2List.contains(intVal) && depRel.equals(DEPRELATIONS.HED.toString())) {

								String newArg2 = relationTokenMap.get(string);
								relation.setArg2(newArg2);
								relation.setArg2_index_array(Arrays.asList(string));

							}
						}
					}

					// case 2: We want to keep the tokens only after arg1
					// index.
					// E.g arg1 : [3] , arg2 : [1,2,3,4,] => new arg2 : [4]
					else {

						
						List<Integer> newList = arg2List.subList(vi + 1, arg2List.size());
						List<String> arg1LNew = new ArrayList<String>();
						for (Integer index : newList) {
							arg1LNew.add(index.toString());
						}

						StringBuilder sb = new StringBuilder();
						for (String string : arg1LNew) {
							String token = relationTokenMap.get(string);
							sb.append(token);
						}
						relation.setArg2(sb.toString());

						// set argument index
						relation.setArg2_index_array(arg1LNew);
						// int t = 20;
					}
				}

			} catch (IndexOutOfBoundsException ioe) {
				ioe.printStackTrace();
			}

		}
		return verbFound;
	}
}
