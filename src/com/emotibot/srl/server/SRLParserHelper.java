package com.emotibot.srl.server;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.emotibot.srl.conll2009.parser.IWord;
import com.emotibot.srl.conll2009.parser.Parser;
import com.emotibot.srl.conll2009.parser.Predicate;
import com.emotibot.srl.conll2009.parser.Sentence;
import com.emotibot.srl.conll2009.parser.Word2009;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.Relation;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.datastructures.StatusCode;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.pruner.TestParseTrees;
import com.emotibot.srl.server.Constants.DEPRELATIONS;
import com.emotibot.srl.test.frames.conversations.FERModel;
import com.emotibot.srl.tmr.datastructure.SRL;
import com.emotibot.srl.tmr.datastructure.SRLRow;
import com.emotibot.srl.utilities.AnalysisUtilities;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import edu.stanford.nlp.trees.Tree;

public class SRLParserHelper {

	SRLBadCaseFixer srlBadCaseFixer;
	DataFormatConverter dfc;
	TestParseTrees testParseTrees;

	public SRLParserHelper() {
		srlBadCaseFixer = new SRLBadCaseFixer();
		dfc = new DataFormatConverter();
		testParseTrees = new TestParseTrees();
	}

	public SRL createSRLOutput(String doc, String result) {
		// TODO Auto-generated method stub

		// createSRLJsonOutput(doc,result);
		SRL srl = new SRL();
		if (Strings.isNullOrEmpty(result))
			return srl;

		Parser p = new Parser();
		Sentence sent = p.parse(result);
		sent.processPredArguments();
		Set<String> fm = new LinkedHashSet<String>();
		Map<String, String> fm1 = new LinkedHashMap<String, String>();
		List<SRLRow> srl_list = new ArrayList<SRLRow>();

		for (Predicate pred : sent.getPredicates()) {

			IWord iWord = pred.getPredWord();
			Word2009 w = (Word2009) iWord;
			String root = w.getDeprel();
			String pos = w.getPos();
			
			// it is a verb
			if (pos.toLowerCase().startsWith("v") || pos.toLowerCase().startsWith("a")) {
				com.emotibot.srl.tmr.datastructure.Predicate argx = new com.emotibot.srl.tmr.datastructure.Predicate();

				if (root.equals("HED")) {
					argx.setRoot(true);
				}
			}

			for (com.emotibot.srl.conll2009.parser.Argument arg : pred.getArguments()) {
				SRLRow srl_row = new SRLRow();
				String patternString = "";

				// prepare regex pattern: ((word_a) (\s)* (word_b))
				for (Iterator<IWord> i = arg.getWords().iterator(); i.hasNext();) {

					IWord word = (IWord) i.next();
					// Word2009 w=(Word2009) word;

					patternString += "(";
					patternString += "(" + regexSafe(word.getForm()) + ")";
					if (i.hasNext()) {
						patternString += "(\\s)*";

						String id_string = w.getId();
						// int id = Integer.parseInt(id_string);
					}
					patternString += ")";

				}

				// // specify features
				//
				// // argument type
				// fm1.put("apredType", arg.getArgType());
				// // predicate surface form
				// fm1.put("predString", arg.getPredicateString());
				// // predicate lemma
				// fm1.put("predLemma", arg.getPredicateLemma());

				// List<Integer> windex=new ArrayList<Integer>();

				srl_row.setApredType(arg.getArgType());
				srl_row.setPredLemma(arg.getPredicateLemma());
				srl_row.setPredString(arg.getPredicateString());

				SortedSet<IWord> ws = arg.getWords();
				Set<String> ids = new HashSet<String>();
				for (IWord iWord2 : ws) {
					ids.add(iWord2.getId());
				}

				String argument = getArgumentAfterMapping(arg.getArgType());

				Pattern pattern = Pattern.compile(patternString);
				Matcher matcher = pattern.matcher(doc);
				while (matcher.find()) {
					// add feature
					try {

						// argument surface form
						SortedSet<IWord> wordlist = arg.getWords();

						srl_row.setString(matcher.group());
						argument = getArgumentAfterMapping(arg.getArgType());

						srl_list.add(srl_row);
						String resultarg = argument + "(" + arg.getPredicateLemma() + "," + matcher.group() + ")";
						fm.add(resultarg);
						// System.out.println(fm.toString());
						// outputAs.add((long)matcher.start(),
						// (long)matcher.end(), OUTPUT_LABEL, fm);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

		srl.setTmr(fm);
		srl.setDocument(doc);
		srl.setSrl_table_format(result);
		// srl.setSrl_list(srl_list);

		// System.out.println(srl.toString());

		return srl;
	}

	/**
	 * Create SRL json output for the given sentence and options
	 * 
	 * @param conllSentence
	 * @param options
	 * @return
	 */
	public String createSRLJsonOutput(CoNLLSentence conllSentence, SRLOptions options) {

		// Create json datastructure and also fix some bad cases in output
		SRLJsonDataStructure json_ds = createSRLJsonDataStructure(conllSentence, options);

		// create json output
		JsonObjectBuilder textBuilder = createJsonOutputHelper(json_ds, options);
		JsonObject jsonObject = textBuilder.build();

		// set json output
		// json_ds.setJsonOutput(json.toString());

		return jsonObject.toString();

	}

	public String createSRLJsonOutput(List<CoNLLSentence> sentenceList, SRLOptions options) {

		JsonBuilderFactory factory = Json.createBuilderFactory(new LinkedHashMap<String, String>());
		JsonArrayBuilder srlInfoBuilder = factory.createArrayBuilder();

		for (CoNLLSentence coNLLSentence : sentenceList) {
			SRLJsonDataStructure json_ds = createSRLJsonDataStructure(coNLLSentence, options);
			JsonObjectBuilder jsonObj = createJsonOutputHelper(json_ds, options);
			srlInfoBuilder.add(jsonObj);
		}

		JsonArray testNodeJsonObject = srlInfoBuilder.build();

		// System.out.println(testNodeJsonObject.toString());
		return testNodeJsonObject.toString();
	}

	/**
	 * Create JSON datastructure
	 * 
	 * @param conllSentence
	 * @param options
	 * @return
	 */
	public SRLJsonDataStructure createSRLJsonDataStructure(CoNLLSentence conllSentence, SRLOptions options) {

		SRLJsonDataStructure json_ds = new SRLJsonDataStructure();

		try {
			String result = conllSentence.getCoNLLSentence();
			boolean isBadCase = conllSentence.isBadCase();
			String doc = conllSentence.getProcessedSentence();
			boolean rootFound = false;

			json_ds.setConllSentence(result);

			// If we need to produce HIT format
			if (options.produceHITFormat) {
				String hitFormat = dfc.convertCONLLtoHIT(result);
				json_ds.setHitSentence(hitFormat);
			}

			Parser p = new Parser();
			Sentence sent = p.parse(result);
			sent.processPredArguments();

			Set<String> fm = new LinkedHashSet<String>();
			List<SRLRow> srl_list = new ArrayList<SRLRow>();

			Map<String, String> tokenMap = new LinkedHashMap<String, String>();
			Map<String, String> posMap = new LinkedHashMap<String, String>();
			Map<String, String> predDepRelMap = new LinkedHashMap<String, String>();
			List<String> tokens = new ArrayList<String>();

			ArrayList<IWord> words = sent.getWords();
			for (IWord iWord : words) {
				Word2009 w = (Word2009) iWord;

				String word = w.getForm();
				String pos = w.getPos();
				String id = w.getId();

				String depRel = w.getDeprel();
				// here we need to do one step to add token with HED dependency
				// relation to predicate dependency relation map.
				if (depRel.equals(DEPRELATIONS.HED.toString())) {
					predDepRelMap.put(id, depRel);
				}

				tokenMap.put(id, word);
				tokens.add(word);
				posMap.put(id, pos);
			}

			json_ds.setSentence(doc);
			json_ds.setTokenMap(tokenMap);
			json_ds.setPosMap(posMap);
			json_ds.setTokens(tokens);

			if (isBadCase) {
				json_ds.setManual_case(true);
			}

			// create predicate map
			for (Predicate pred : sent.getPredicates()) {

				IWord iWord = pred.getPredWord();
				Word2009 w = (Word2009) iWord;
				String root = w.getDeprel();
				String pos = w.getPos();
				String id = w.getId();

				// add to pred depedency relation map
				predDepRelMap.put(id, w.getDeprel());

				String predForm = w.getPred();

				// it is root
				if (root.equals(DEPRELATIONS.HED.toString())) {
					
					rootFound = true;
					json_ds.setRoot_index(w.getId());
					
					if (!(pos.toLowerCase().startsWith("v") || pos.toLowerCase().startsWith("a"))) {
						continue;
					}
				}

				for (com.emotibot.srl.conll2009.parser.Argument arg : pred.getArguments()) {
					SRLRow srl_row = new SRLRow();
					String patternString = "";

					// prepare regex pattern: ((word_a) (\s)* (word_b))
					for (Iterator<IWord> i = arg.getWords().iterator(); i.hasNext();) {

						IWord word = (IWord) i.next();
						// Word2009 w=(Word2009) word;

						patternString += "(";
						patternString += "(" + regexSafe(word.getForm()) + ")";
						if (i.hasNext()) {
							patternString += "(\\s)*";

						}
						patternString += ")";

					}

					srl_row.setApredType(arg.getArgType());
					srl_row.setPredLemma(arg.getPredicateLemma());
					srl_row.setPredString(arg.getPredicateString());

					SortedSet<IWord> ws = arg.getWords();

					List<String> arg2_ids = new ArrayList<String>();
					List<String> arg1_ids = new ArrayList<String>();
					Map<String, String> tokenMapForRelation = new HashMap<String, String>();
					;

					String arg1_id = arg.getPredicate().getPredWord().getId();
					String arg1Token = arg.getPredicate().getPredWord().getForm();

					arg1_ids.add(arg1_id);
					tokenMapForRelation.put(arg1_id, arg1Token);

					for (IWord iWord2 : ws) {
						arg2_ids.add(iWord2.getId());
						tokenMapForRelation.put(iWord2.getId(), iWord2.getForm());
					}

					String argument = getArgumentAfterMapping(arg.getArgType());

					String arg1 = "";
					String arg2 = "";

					arg1 = arg.getPredicate().getPredWord().getForm();

					Pattern pattern = Pattern.compile(patternString);
					Matcher matcher = pattern.matcher(doc);
					while (matcher.find()) {
						// add feature
						try {

							// argument surface form

							srl_row.setString(matcher.group());
							argument = getArgumentAfterMapping(arg.getArgType());

							arg1 = arg.getPredicateLemma();
							arg2 = matcher.group();

							srl_list.add(srl_row);
							String resultarg = argument + "(" + arg1 + "," + arg2 + ")";

							String[] sensesList = predForm.split("\\.");
							String arg1_sense = "";
							if (sensesList.length >= 1) {
								arg1_sense = sensesList[1];
							}

							Relation rel = new Relation();
							rel.setArg1(arg1);
							rel.setArg1_type(predForm);
							rel.setArg1_sense(arg1_sense);
							rel.setArg2(arg2);
							rel.setArg1_index_array(arg1_ids);
							rel.setArg2_index_array(arg2_ids);
							rel.setSrl_relation(argument);
							rel.setTokenMap(tokenMapForRelation);

							json_ds.getSrl_multimap().put(argument, rel);

							fm.add(resultarg);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}

			// Check the case when root is not found
			if (!rootFound) {
				ArrayList<Predicate> preds = sent.getPredicates();
				if (preds.size() > 0) {

					for (Predicate pred : preds) {
						// Predicate pred = preds.get(0);
						IWord iWord = pred.getPredWord();
						Word2009 w = (Word2009) iWord;

						json_ds.setRoot_index(w.getId());

					}

				}

			}

			// add predicate dependency relation map
			json_ds.setPredRelationMap(predDepRelMap);

			// Lets Check for many bad cases before creating the json
			srlBadCaseFixer.checkBadCases(json_ds);
			
			// Add summary code for SRL output
			json_ds.checkSRLStatus();

			// create verb relation map
			json_ds.createVerbRelationMap();
			
		} catch (Exception e) {
			
			// if exception occurs, default json_ds with status code ERROR is returned  
			System.out.println(conllSentence.getProcessedSentence());
			e.printStackTrace();
		}

		return json_ds;
	}

	/**
	 * Function to generate the final json output.
	 * 
	 * @param json_ds
	 * @param srl_options
	 * @return
	 */
	public JsonObjectBuilder createJsonOutputHelper(SRLJsonDataStructure json_ds, SRLOptions srl_options) {
		JsonObjectBuilder textBuilder = Json.createObjectBuilder();

		boolean isFormat = false;

		if (srl_options != null && !Strings.isNullOrEmpty(srl_options.format)) {
			isFormat = true;
		}

		textBuilder.add("version", Constants.VERSION);
		textBuilder.add("sentence", json_ds.getSentence());

		// Add summary code for SRL output
		textBuilder.add("status", json_ds.getStatus_code().getValue());
		textBuilder.add("message", json_ds.getStatus_code().getMessage());
		
		// add information of language
		textBuilder.add("lang", "zh");

		// add information of whether it is manual case or not
		textBuilder.add("manual_case", json_ds.isManual_case());

		if (!json_ds.isManual_case()) {

			textBuilder.add("model", srl_options.model);
		}

		// adding case type variable
		if (!Strings.isNullOrEmpty(srl_options.case_type)) {
			textBuilder.add("case_type", srl_options.case_type);
		}
		
		// If the status code is ERROR, just return here
		if (json_ds.getStatus_code() == StatusCode.ERROR) {
			return textBuilder;
		}
		

		// if do parse tree option is true, add parse tree information.
		if (srl_options != null && srl_options.doTree) {
			List<String> tokens = json_ds.getTokens();
			String[] tokensArr = tokens.toArray(new String[0]);
			Tree tree = AnalysisUtilities.getInstance().parseChineseSentence(tokensArr, false).parse;
			if (tree != null) {
				textBuilder.add("tree", tree.toString());
			} else {
				textBuilder.add("tree", "");
			}
			
			if (srl_options.doPruner) {
				if (tree != null) {
					tree = testParseTrees.pruneTree(tree);
					String output = AnalysisUtilities.getInstance().treeToString(tree);
					output = output.replace(" ", "");
					textBuilder.add("pruned_sentence", output);
				} else {
					textBuilder.add("pruned_sentence", json_ds.getSentence());
				}
				textBuilder.add("sentence_pruner_note", "this is work in progress");
			}

		}
		
		// add frame element recognition
		if (srl_options != null && srl_options.doFER) {
			
			List<String> tokens = json_ds.getTokens();
			
			textBuilder.add("FER", FERModel.getFERString(tokens.toArray(new String[0])));

		}
		

		// add information of root verb
		if (json_ds.getRoot_index() != null)
			textBuilder.add("root_verb", json_ds.getRoot_index());

		// add information about

		// build token array object
		JsonArrayBuilder tokenBuilder = Json.createArrayBuilder();
		Map<String, String> pos_map = json_ds.getPosMap();
		for (Map.Entry<String, String> entry : json_ds.getTokenMap().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			JsonObjectBuilder paraElement = Json.createObjectBuilder();

			String token = value;
			String pos = pos_map.get(key);

			if (isFormat) {
				paraElement.add("id", key);
				paraElement.add("token", token);
				paraElement.add("pos", pos);
			} else {
				paraElement.add(key, token);
			}

			tokenBuilder.add(paraElement);

		}

		JsonObjectBuilder srl_builder = Json.createObjectBuilder();
		// JsonObjectBuilder srl_builder1 = Json.createObjectBuilder();
		JsonArrayBuilder srl_builder1 = Json.createArrayBuilder();

		if (isFormat) {
			// This is the test json where relations are sorted by verb
			Multimap<String, Relation> srl_map1 = json_ds.getVerbRelMap();

			Set<String> argSet1 = new HashSet<String>();

			// go through the relation verb map
			for (String keys : srl_map1.keySet()) {
				String verb_form = "";
				String verb_sense = "";
				JsonArrayBuilder builder_rel = Json.createArrayBuilder();

				Collection<Relation> relation_list = srl_map1.get(keys);
				ArrayListMultimap<String, Relation> local_multimap = ArrayListMultimap.create();

				int temp_counter = 0;
				for (Relation relation : relation_list) {

					String srlRelation = relation.getSrl_relation();
					local_multimap.put(srlRelation, relation);

					if (temp_counter == 0) {
						verb_form = relation.getArg1();
						verb_sense = relation.getArg1_sense();
					}
					temp_counter++;
				}

				// define the verb key
				// String verb_key=verb_form+"_"+keys;
				String verb_key = keys;
				JsonObjectBuilder listBuilder = Json.createObjectBuilder();
				listBuilder.add("form", verb_form);
				listBuilder.add("index", keys);
				listBuilder.add("sense", verb_sense);

				JsonObjectBuilder srl_builder_per_relation = Json.createObjectBuilder();
				// JsonArrayBuilder srl_builder_per_relation =
				// Json.createArrayBuilder();
				Set<String> argSet2 = new HashSet<String>();
				for (String key : local_multimap.keySet()) {
					relation_list = local_multimap.get(key);

					JsonArrayBuilder builder_rel_verb = Json.createArrayBuilder();
					for (Relation relation : relation_list) {

						JsonObjectBuilder element = Json.createObjectBuilder();

						String srlRelation = relation.getSrl_relation();
						String arg1 = relation.getArg1();
						// String argType = relation.getArg1_type();
						String arg2 = relation.getArg2();

						// List<String> arg1_index =
						// relation.getArg1_index_array();
						List<String> arg2_index = relation.getArg2_index_array();

						JsonArrayBuilder arg_index_builder = Json.createArrayBuilder();
						for (String string : arg2_index) {
							arg_index_builder.add(string);
						}

						String temp = srlRelation + ":" + arg1 + ":" + arg2;
						if (!argSet2.contains(temp)) {

							element.add("arg2_form", arg2);
							element.add("arg2_indices", arg_index_builder);
							builder_rel_verb.add(element);

							argSet2.add(temp);
						}

					}
					// srl_builder_per_relation.add(key, builder_rel_verb);
					srl_builder_per_relation.add(key, builder_rel_verb);
				}

				listBuilder.add("rel", srl_builder_per_relation);
				// srl_builder1.add(verb_key, listBuilder);
				srl_builder1.add(listBuilder);

			}
		}

		else {
			// build srl object
			Multimap<String, Relation> srl_map = json_ds.getSrl_multimap();

			Set<String> argSet = new HashSet<String>();

			// first create a set of relations to make sure we are not adding
			// duplicates.
			for (String keys : srl_map.keySet()) {

				Collection<Relation> relation_list = srl_map.get(keys);
				for (Relation relation : relation_list) {
					String arg1 = relation.getArg1();
					String arg2 = relation.getArg2();

					String temp = arg1 + ":" + arg2;
					argSet.add(temp);
				}

			}

			for (String keys : srl_map.keySet()) {

				JsonArrayBuilder builder = Json.createArrayBuilder();

				Collection<Relation> relation_list = srl_map.get(keys);
				for (Relation relation : relation_list) {

					int i = 0;
					JsonObjectBuilder element = Json.createObjectBuilder();

					String srlRelation = relation.getSrl_relation();
					String arg1 = relation.getArg1();
					String argType = relation.getArg1_type();
					String arg2 = relation.getArg2();

					List<String> arg1_index = relation.getArg1_index_array();
					List<String> arg2_index = relation.getArg2_index_array();

					JsonArrayBuilder arg_index_builder = Json.createArrayBuilder();
					for (String string : arg2_index) {
						arg_index_builder.add(string);
					}

					String temp = srlRelation + ":" + arg1 + ":" + arg2;
					if (!argSet.contains(temp)) {
						element.add(arg1, arg2);
						element.add("pred", argType);
						element.add("arg1", arg1_index.get(0));
						element.add("arg2", arg_index_builder);
						builder.add(element);

						argSet.add(temp);
					}

				}

				srl_builder.add(keys, builder);
			}
		}

		textBuilder.add("tokens", tokenBuilder);

		// if the format is specified, we add srl categorized by verb. else we
		// add srl categorized by relations
		if (isFormat) {
			textBuilder.add("srl", srl_builder1);
		} else {
			textBuilder.add("srl", srl_builder);
		}

		return textBuilder;
	}

	/**
	 * Conversion of Regular expression special characters that may occur in the
	 * text to safe notation.
	 * 
	 * @param string
	 *            to check for unsafe characters
	 * @return regex safe string
	 */
	public String regexSafe(String aRegexFragment) {
		final StringBuilder result = new StringBuilder();

		final StringCharacterIterator iterator = new StringCharacterIterator(aRegexFragment);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			/*
			 * All literals need to have backslashes doubled.
			 */
			if (character == '.') {
				result.append("\\.");
			} else if (character == '\\') {
				result.append("\\\\");
			} else if (character == '?') {
				result.append("\\?");
			} else if (character == '*') {
				result.append("\\*");
			} else if (character == '+') {
				result.append("\\+");
			} else if (character == '&') {
				result.append("\\&");
			} else if (character == ':') {
				result.append("\\:");
			} else if (character == '{') {
				result.append("\\{");
			} else if (character == '}') {
				result.append("\\}");
			} else if (character == '[') {
				result.append("\\[");
			} else if (character == ']') {
				result.append("\\]");
			} else if (character == '(') {
				result.append("\\(");
			} else if (character == ')') {
				result.append("\\)");
			} else if (character == '^') {
				result.append("\\^");
			} else if (character == '$') {
				result.append("\\$");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	private String getArgumentAfterMapping(String argType) {
		String argument = argType;
		if (argument.equalsIgnoreCase("A0")) {
			argument = "agent";
		} else if (argument.equalsIgnoreCase("A1")) {
			argument = "patient";
		}
		// else if (argument.equalsIgnoreCase("A2")) {
		// // argument = "benefective";
		// argument = "A2";
		// }
		// else if (argument.equalsIgnoreCase("AM-MNR")) {
		// argument = "manner";
		// } else if (argument.equalsIgnoreCase("AM-DIR")) {
		// argument = "directional";
		// } else if (argument.equalsIgnoreCase("AM-LOC")) {
		// argument = "location";
		// } else if (argument.equalsIgnoreCase("AM-TMP")) {
		// argument = "temporal";
		// } else if (argument.equalsIgnoreCase("AM-EXT")) {
		// argument = "extent";
		// } else if (argument.equalsIgnoreCase("AM-REC")) {
		// argument = "reciprocals";
		// } else if (argument.equalsIgnoreCase("AM-PNC")) {
		// argument = "purpose";
		// } else if (argument.equalsIgnoreCase("AM-CAU")) {
		// argument = "CAU";
		// } else if (argument.equalsIgnoreCase("AM-DIS")) {
		// argument = "DIS";
		// } else if (argument.equalsIgnoreCase("AM-ADV")) {
		// argument = "ADV";
		// } else if (argument.equalsIgnoreCase("AM-MOD")) {
		// argument = "MOD";
		// } else if (argument.equalsIgnoreCase("AM-NEG")) {
		// argument = "negation";
		// }
		// else if (argument.equalsIgnoreCase("A4")) {
		// //argument = "ending_point";
		// argument = "A4";
		// }
		return argument;
	}

}
