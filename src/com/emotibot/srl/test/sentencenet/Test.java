/**
 * 
 */
package com.emotibot.srl.test.sentencenet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Sanjay
 *
 */
public class Test {

	Multimap<String, String> CU_Classes_Multimap = LinkedListMultimap.create();
	List<String> errorList;
	List<String> intentExceptionList;
	StringBuilder globalQueryBuilder;
	
	public Test(){
		errorList=new ArrayList<>();
		intentExceptionList=new ArrayList<>();
		
		intentExceptionList.add("记帐app_查消费统计");
		intentExceptionList.add("记帐app_出帐");
		
		globalQueryBuilder=new StringBuilder();
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Test test = new Test();

		test.createHierarchyGraphs();

		test.createDataQuery();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void createHierarchyGraphs() throws IOException {

		// create intent graph
		System.out.println("//Create intent graph");
		createIntentGraph();

		// create intent graph
		System.out.println("//Create intent application graph");
		createIntentApplicationGraph();

		// create topic graph
		System.out.println("//Create topic graph");
		createTopicGraph();

		// create speech act graph
		System.out.println("//Create speech act graph");
		createSpeechActGraph();
		System.out.println("//----------------------------");
		// create emotion graph
		System.out.println("//Create emotion graph");
		createEmotionGraph();
		System.out.println("//----------------------------");
		// create sentenceType graph
		System.out.println("//Create sentence graph");
		createSentenceTypeGraph();
		
		
		int temp=10;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void createIntentApplicationGraph() throws IOException {

		String f = "data2/sentencenet/classifiers/intent_tags";
		boolean recursive = true;
		File rootDir = new File(f);
		Collection<File> files = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		if (recursive) {
			final String[] SUFFIX = { "txt" };
			files = FileUtils.listFiles(rootDir, SUFFIX, true);
		}

		Multimap<String, String> intent_ApplicationMultimap = LinkedListMultimap.create();
		for (File file : files) {
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			String filename = file.getName().replace(".txt", "");
			filename = removeDelimiters(filename);
			for (String string : lines) {
				if (!Strings.isNullOrEmpty(string)) {
					String[] vals = string.split("，");
					string = removeDelimiters(string);
					if (vals.length > 1) {
						// String verb = vals[0];
						// String noun = vals[1];
						intent_ApplicationMultimap.put(filename, string);
					} else if (vals.length == 1) {
						// intent_multimap.put(string, string);
						System.err.println("//Wrong size. expected 2 elements : " + string);
					}
				}
			}

		}

		Collection<String> intent_values = intent_ApplicationMultimap.values();
		HashSet<String> valueSet = new HashSet<>(intent_values);
//		for (String string : valueSet) {
//			// match intent type set
//			string=string.trim();
//			sb.append("MATCH (" + string + ":INTENT_CLASS" + "{id:" + "'" + string + "'" + "})");
//			sb.append( "\n");
//
//		}
		// String output = sb.toString();
		// output = output.substring(0, output.length() - 2);

		sb.append("CREATE  ");
		sb.append("\n");
		sb.append("(" + "IntentApplicationOntology" + ":INTENTAPPLICATION" + " {type:'ontology'" + ", text:" + "'"
				+ "INTENTAPPLICATION" + "'" + "} " + ")");
		sb.append("," + "\n");

		Collection<String> intent_keys = intent_ApplicationMultimap.keySet();
		for (String string2 : intent_keys) {
			string2 = removeDelimiters(string2);

			sb.append("(" + string2 + ":INTENTAPPLICATION_CLASS" + " {text: " + "'" + string2 + "'" + ", id:" + "'"
					+ string2 + "'" + ", type:'INTENTAPPLICATION_CLASS'" + "} " + ")");
			sb.append("," + "\n");
			sb.append("(" + string2 + ")" + "-" + "[:is_Part_Of]" + " -> (" + "IntentApplicationOntology" + ")");
			sb.append("," + "\n");
		}

		for (String key : intent_ApplicationMultimap.keySet()) {
			Collection<String> vals = intent_ApplicationMultimap.get(key);
			
			for (String value : vals) {
				// match intent type set

				sb.append("(" + value + ")" + "-" + "[:is_Used_In]" + " -> (" + key + ")");
				sb.append("," + "\n");

			}
		}
		
		String output = sb.toString();
		output = output.substring(0, output.length() - 2);
		System.out.println(output);
		globalQueryBuilder.append(output);	
		globalQueryBuilder.append("\n");
	}

	private void createIntentGraph() throws IOException {

		String f = "data2/sentencenet/classifiers/intent_tags.txt";
		File file = new File(f);

		List<String> lines = FileUtils.readLines(file, "UTF-8");
		StringBuilder sb = new StringBuilder();

		Multimap<String, String> intent_multimap = LinkedListMultimap.create();

		int counter = 0;
		for (String string : lines) {
			if (!Strings.isNullOrEmpty(string)) {

				// the first level is
				// System.out.println(string);
				String[] vals = string.split("，");
				string = removeDelimiters(string);
				
				//add all classes to global multimap
				CU_Classes_Multimap.put("intent", string);
				
				if (vals.length > 1) {
					String verb = vals[0];
					String noun = vals[1];
					intent_multimap.put(verb, string);
				} else if (vals.length == 1) {
					// intent_multimap.put(string, string);
					System.err.println("//Wrong size. expected 2 elements : " + string);
				}

			}

		}

		sb.append("CREATE  ");
		sb.append("\n");
		sb.append("(" + "IntentOntology" + ":INTENT" + " {type:'ontology'" + ", text:" + "'" + "IntentOntology" + "'"
				+ "} " + ")");
		sb.append("," + "\n");
		for (String string : intent_multimap.keySet()) {
			Collection<String> values = intent_multimap.get(string);
			HashSet<String> valueSet = new HashSet<>(values);

			sb.append("(" + string + ":INTENT_CLASS" + " {text: " + "'" + string + "'" + ", id:" + "'" + string + "'"
					+ ", type:'INTENT_CLASS'" + "} " + ")");
			sb.append("," + "\n");
			sb.append("(" + string + ")" + "-" + "[:is_A]" + " -> (IntentOntology)");
			sb.append("," + "\n");

			for (String c : valueSet) {
				String string2 = c;
				string2 = removeDelimiters(string2);

				sb.append("(" + string2 + ":INTENT_CLASS" + " {text: " + "'" + string2 + "'" + ", id:" + "'" + string2
						+ "'" + ", type:'INTENT_CLASS'" + "} " + ")");
				sb.append("," + "\n");
				sb.append("(" + string2 + ")" + "-" + "[:is_A]" + " -> (" + string + ")");
				sb.append("," + "\n");
			}

		}
		String output = sb.toString();
		output = output.substring(0, output.length() - 2);
		System.out.println(output);
		globalQueryBuilder.append(output);	
		globalQueryBuilder.append("\n");
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void createTopicGraph() throws IOException {
		// TODO Auto-generated method stub
		String f = "data2/sentencenet/classifiers/topic_tags.txt";
		File file = new File(f);

		Map<String,Integer> classLabelCount=new HashMap<>();
		
		
		List<String> lines = FileUtils.readLines(file, "UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE  ");
		sb.append("\n");
		sb.append("(" + "TopicOntology" + ":TOPIC" + " {type:'ontology'" + ", text:" + "'" + "TopicOntology" + "'"
				+ "} " + ")");
		sb.append("," + "\n");
		int counter = 0;
		for (String string : lines) {
			if (!Strings.isNullOrEmpty(string)) {

				// the first level is
				String[] vals = string.split("\t");

				String level1 = vals[0];
				level1 = removeDelimiters(level1);
				
				//add class count 
				addClassCount(classLabelCount,level1);
				
				//add to multimap
				CU_Classes_Multimap.put("topic", level1);

				sb.append("(" + level1 + ":TOPIC_CLASS" + " {text: " + "'" + level1 + "'" + ", id:" + "'" + level1 + "'"
						+ ", type:'TOPIC_CLASS'" + "} " + ")");
				sb.append("," + "\n");
				sb.append("(" + level1 + ")" + "-" + "[:is_A]" + " -> (TopicOntology)");
				sb.append("," + "\n");
				
				
				

				for (int i = 1; i < vals.length; i++) {
					String string2 = vals[i];
					string2 = removeDelimiters(string2);
					
					//add to multimap
					CU_Classes_Multimap.put("topic", string2);
					//add class count 
					addClassCount(classLabelCount,string2);
					
					int classCount=classLabelCount.get(string2);
					if(classCount==1)
					{
						sb.append("(" + string2 + ":TOPIC_CLASS" + " {text: " + "'" + string2 + "'" + ", id:" + "'"
								+ string2 + "'" + ", type:'TOPIC_CLASS'" + "} " + ")");
						sb.append("," + "\n");
						
					}
					else{
						//multiple class labels are present.
						
						System.err.println("//multiple class labels found : " + string2 + " : count " + classCount);
					}
					
					
					sb.append("(" + string2 + ")" + "-" + "[:is_A]" + " -> (" + level1 + ")");
					sb.append("," + "\n");
					
					
					
				}

			}

			// counter++;
			// if (counter != lines.size()) {
			// //sb.append("," + "\n");
			// } else {
			// sb.append("\n");
			// }
		}

		String output = sb.toString();

		output = output.substring(0, output.length() - 2);

		System.out.println(output);
		globalQueryBuilder.append(output);	
		globalQueryBuilder.append("\n");
	}

	/**
	 * This map keep tracks of the total occurances of each class label
	 * @param classLabelCount
	 * @param key
	 */
	private void addClassCount(Map<String, Integer> classLabelCount, String key) {
		
		if(!classLabelCount.containsKey(key)){
			classLabelCount.put(key, 1);
		}
		else{
			int count=classLabelCount.get(key);
			classLabelCount.put(key, count+1);
		}
		
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void createSentenceTypeGraph() throws IOException {
		// TODO Auto-generated method stub
		String f = "data2/sentencenet/classifiers/sentencetype_tags.txt";
		File file = new File(f);

		List<String> lines = FileUtils.readLines(file, "UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE  ");
		sb.append("\n");
		sb.append("(" + "SENTENCETYPES" + ":SENTENCETYPES" + " {type:'ontology'" + ", text:" + "'" + "SENTENCETYPES"
				+ "'" + "} " + ")");
		sb.append("," + "\n");
		int counter = 0;
		for (String string : lines) {
			if (!Strings.isNullOrEmpty(string)) {
				
				string=removeDelimiters(string);
				//add to multimap
				CU_Classes_Multimap.put("sentencetype", string);
				
				
				sb.append("(" + string + ":SENTENCETYPE_CLASS" + " {text: " + "'" + string + "'" + ", id:" + "'"
						+ string + "'" + ", type:'SENTENCETYPE_CLASS'" + "} " + ")");
				sb.append("," + "\n");
				sb.append("(" + string + ")" + "-" + "[:is_A]" + " -> (SENTENCETYPES)");
			}

			counter++;
			if (counter != lines.size()) {
				sb.append("," + "\n");
			} else {
				sb.append("\n");
			}
		}
		globalQueryBuilder.append(sb);	
		globalQueryBuilder.append("\n");

		System.out.println(sb.toString());
	}

	private void createEmotionGraph() throws IOException {
		// TODO Auto-generated method stub
		String f = "data2/sentencenet/classifiers/emotion_tags.txt";
		File file = new File(f);

		List<String> lines = FileUtils.readLines(file, "UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE  ");
		sb.append("\n");
		sb.append(
				"(" + "EMOTIONS" + ":EMOTIONS" + " {type:'ontology'" + ", text:" + "'" + "EMOTIONS" + "'" + "} " + ")");
		sb.append("," + "\n");
		int counter = 0;
		for (String string : lines) {
			
			string=removeDelimiters(string);
			//add to multimap
			CU_Classes_Multimap.put("emotion", string);
			
			
			if (!Strings.isNullOrEmpty(string)) {
				sb.append("(" + string + ":EMOTION_CLASS" + " {text: " + "'" + string + "'" + ", id:" + "'" + string
						+ "'" + ", type:'EMOTION_CLASS'" + "} " + ")");
				sb.append("," + "\n");
				sb.append("(" + string + ")" + "-" + "[:is_A]" + " -> (EMOTIONS)");
			}

			counter++;
			if (counter != lines.size()) {
				sb.append("," + "\n");
			} else {
				sb.append("\n");
			}
		}
		globalQueryBuilder.append(sb);	
		globalQueryBuilder.append("\n");
		System.out.println(sb.toString());
	}

	private void createSpeechActGraph() throws IOException {
		// TODO Auto-generated method stub
		String f = "data2/sentencenet/classifiers/speechact_tags.txt";
		File file = new File(f);

		List<String> lines = FileUtils.readLines(file, "UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE  ");
		sb.append("\n");
		sb.append("(" + "SPEECHACTS" + ":SPEECHACTS" + " {type:'ontology'" + ", text:" + "'" + "SPEECHACTS" + "'" + "} "
				+ ")");
		sb.append("," + "\n");
		int counter = 0;
		for (String string : lines) {
			
			string=removeDelimiters(string);
			//add to multimap
			CU_Classes_Multimap.put("speechacts", string);
			
			
			
			if (!Strings.isNullOrEmpty(string)) {
				sb.append("(" + string + ":SPEECHACT_CLASS" + " {text: " + "'" + string + "'" + ", id:" + "'" + string
						+ "'" + ", type:'SPEECHACT_CLASS'" + "} " + ")");
				sb.append("," + "\n");
				sb.append("(" + string + ")" + "-" + "[:is_A]" + " -> (SPEECHACTS)");
			}

			counter++;
			if (counter != lines.size()) {
				sb.append("," + "\n");
			} else {
				sb.append("\n");
			}
		}
		globalQueryBuilder.append(sb);	
		globalQueryBuilder.append("\n");
		System.out.println(sb.toString());
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private String removeDelimiters(String string) {
		String string2 = string;
		string2 = string2.replace("/", "_");
		string2 = string2.replace("-", "_");
		string2 = string2.replace(",", "_");
		string2 = string2.replace("，", "_");
		string2 = string2.replace("：", "_");
		string2 = string2.replace("。", "");
		string2 = string2.replace("！", "");
		string2 = string2.replace("？", "");
		
		string2 = string2.replace("'", "");
		
		string2=string2.trim();

		return string2;
	}

	/**
	 * 
	 * @throws IOException
	 */
	void createDataQuery() throws IOException {
		String out="data2/sentencenet/data/cypher/sentence_cypher.txt";
		String f1 = "data2/sentencenet/data/out2.csv";
		String f2 = "data2/sentencenet/data/cu_tagged_data_all.csv";
		//List<SentenceDS> sentencesData = readData(f1);
		List<SentenceDS> sentencesData = readData(f1);

		// We need to remove delimiters : / , - etc from the nodes.

		StringBuilder sb = new StringBuilder();

		Set<String> speechActSet = new HashSet<>();
		Set<String> emotionSet = new HashSet<>();
		Set<String> sentenceTypeSet = new HashSet<>();
		Set<String> topicSet = new HashSet<>();
		Set<String> intentSet = new HashSet<>();

		for (SentenceDS sentenceDS : sentencesData) {
			List<String> sentenceTypeList = sentenceDS.getSentenceTypeList();
			List<String> speechActList = sentenceDS.getSpeechActList();
			List<String> topicsList = sentenceDS.getTopicsList();
			List<String> emotionList = sentenceDS.getEmotionList();
			List<String> intentList = sentenceDS.getIntentList();

			// speechActs
			for (String string2 : speechActList) {
				string2 = removeDelimiters(string2);

				speechActSet.add(string2);

			}

			// emotions
			for (String string2 : emotionList) {
				string2 = removeDelimiters(string2);
				emotionSet.add(string2);

			}

			// sentence types
			for (String string2 : sentenceTypeList) {
				string2 = removeDelimiters(string2);
				sentenceTypeSet.add(string2);

			}

			// topic list
			for (String string2 : topicsList) {
				string2 = removeDelimiters(string2);
				topicSet.add(string2);

			}

			// intent list
			for (String string2 : intentList) {
				string2 = removeDelimiters(string2);
				intentSet.add(string2);

			}

		}

//		// match speech act nodes
//		for (String string : speechActSet) {
//			sb.append("MATCH (" + string + ":SPEECHACT_CLASS" + "{id:" + "'" + string + "'" + "})");
//			sb.append("\n");
//		}
//
//		// match emotion nodes
//		for (String string : emotionSet) {
//			sb.append("MATCH (" + string + ":EMOTION_CLASS" + "{id:" + "'" + string + "'" + "})");
//			sb.append("\n");
//		}
//
//		// match sentence type nodes
//		for (String string : sentenceTypeSet) {
//			sb.append("MATCH (" + string + ":SENTENCETYPE_CLASS" + "{id:" + "'" + string + "'" + "})");
//			sb.append("\n");
//		}
//
//		// match topic nodes
//		for (String string : topicSet) {
//			sb.append("MATCH (" + string + ":TOPIC_CLASS" + "{id:" + "'" + string + "'" + "})");
//			sb.append("\n");
//		}
//
//		// match intent type set
//		for (String string : intentSet) {
//			
//			//this is a temp solution for some intents which are not have proper logical cut
//			if(!intentExceptionList.contains(string)){
//				sb.append("MATCH (" + string + ":INTENT_CLASS" + "{id:" + "'" + string + "'" + "})");
//				sb.append("\n");
//			}
//			
//		}

		sb.append("CREATE  ");
		sb.append("\n");
		sb.append("(" + "SENTENCENET" + ":SENTENCENET" + " {type:'ontology'" + ", text:" + "'" + "SENTENCENET" + "'" + "} "
				+ ")");
		sb.append("," + "\n");
		
		
		
		
		int counter = 0;
		for (SentenceDS sentenceDS : sentencesData) {

			String string = sentenceDS.getSentence();
			if (!Strings.isNullOrEmpty(string)) {

				List<String> sentenceTypeList = sentenceDS.getSentenceTypeList();
				List<String> speechActList = sentenceDS.getSpeechActList();
				List<String> topicsList = sentenceDS.getTopicsList();
				List<String> emotionList = sentenceDS.getEmotionList();
				List<String> intentList = sentenceDS.getIntentList();

				string = removeDelimiters(string);
				String text=string;
				string= "s"+Integer.toString(counter);
				sb.append("(" + string + ":Sentence" + " {type:'Sentence'" + ", text: " + "'" + text + "', lang: "
						+ "'" + "zh" + "'" + "} " + ")");
				sb.append("," + "\n");
				sb.append("(" + string + ")" + "-" + "[:is_Part_Of" + "{role:'part of'}" + "]" + " -> (SENTENCENET" + ")");
				// sb.append("(" + string + ":Sentence" + " {te: " + "'" +
				// string + "'" + "} " + ")");

				sb.append("," + "\n");

				for (String string2 : speechActList) {
					
					
					string2 = removeDelimiters(string2);
					if(!CU_Classes_Multimap.values().contains(string2)){
						errorList.add(string2);
					}

					sb.append("(" + string + ")" + "-" + "[:has_SpeechAct" + "{role:'speechact'}" + "]" + " -> ("
							+ string2 + ")");
					sb.append("," + "\n");
				}

				for (String string2 : sentenceTypeList) {
					string2 = removeDelimiters(string2);
					
					if(!CU_Classes_Multimap.values().contains(string2)){
						errorList.add(string2);
					}
					
					
					sb.append("(" + string + ")" + "-" + "[:has_SentenceType" + "{role:'sentencetype'}" + "]" + " -> ("
							+ string2 + ")");
					sb.append("," + "\n");
				}

				for (String string2 : emotionList) {
					string2 = removeDelimiters(string2);
					
					if(!CU_Classes_Multimap.values().contains(string2)){
						errorList.add(string2);
					}
					
					

					sb.append("(" + string + ")" + "-" + "[:has_Emotion" + "{role:'emotion'}" + "]" + " -> (" + string2
							+ ")");
					sb.append("," + "\n");
				}

				for (String string2 : intentList) {
					string2 = removeDelimiters(string2);
					
					if(!CU_Classes_Multimap.values().contains(string2)){
						errorList.add(string2);
					}
					
					
					sb.append("(" + string + ")" + "-" + "[:has_Intent" + "{role:'intent'}" + "]" + " -> (" + string2
							+ ")");
					sb.append("," + "\n");
				}

				for (String string2 : topicsList) {
					string2 = removeDelimiters(string2);
					
					if(!CU_Classes_Multimap.values().contains(string2)){
						errorList.add(string2);
					}
					
					
					sb.append("(" + string + ")" + "-" + "[:has_Topic" + "{role:'topic'}" + "]" + " -> (" + string2
							+ ")");
					sb.append("," + "\n");
				}

			}

			counter++;
			if (counter != sentencesData.size()) {
				// sb.append("," + "\n");
			} else {
				sb.append("\n");
			}
		}
		String output = sb.toString();

		output = output.substring(0, output.length() - 3);
		System.out.println(output);
		
		
		Set<String> str=new HashSet<>(errorList);
		for (String string2 : str) {
			System.err.println(string2);
		}
		
		globalQueryBuilder.append(output);	
		globalQueryBuilder.append("\n");
		
		FileUtils.writeStringToFile(new File(out), globalQueryBuilder.toString());
	}

	/**
	 * @throws IOException
	 * 
	 */
	private List<SentenceDS> readData(String f) throws IOException {
		List<SentenceDS> sentencesData = new ArrayList<SentenceDS>();
		
		File file = new File(f);

		List<String> lines = FileUtils.readLines(file, "UTF-8");

		for (String row : lines) {

			if (!Strings.isNullOrEmpty(row)) {
				String[] columns = row.split("\t");

				String sentence;
				String sid;
				String lang = "zh";

				List<String> sentenceTypeList = new ArrayList<>();
				List<String> speechActList = new ArrayList<>();
				List<String> topicsList = new ArrayList<>();
				List<String> emotionList = new ArrayList<>();
				List<String> intentList = new ArrayList<>();

				sentence = columns[0];
				if (!Strings.isNullOrEmpty(sentence)) {
					if (!Strings.isNullOrEmpty(columns[1])) {
						String[] vals = columns[1].split(";");
						speechActList = Arrays.asList(vals);
					}

					if (!Strings.isNullOrEmpty(columns[2])) {
						String[] vals = columns[2].split(";");
						sentenceTypeList = Arrays.asList(vals);
					}

					if (!Strings.isNullOrEmpty(columns[3])) {
						String[] vals = columns[3].split(";");
						intentList = Arrays.asList(vals);
					}
					if (!Strings.isNullOrEmpty(columns[4])) {
						String[] vals = columns[4].split(";");
						topicsList = Arrays.asList(vals);
					}
					if (!Strings.isNullOrEmpty(columns[5])) {
						String[] vals = columns[5].split(";");
						emotionList = Arrays.asList(vals);
					}

					SentenceDS sds = new SentenceDS();

					sds.setSentence(sentence);
					sds.setLang(lang);

					sds.setRow(row);

					sds.setTopicsList(topicsList);
					sds.setEmotionList(emotionList);
					sds.setIntentList(intentList);
					sds.setSpeechActList(speechActList);
					sds.setSentenceTypeList(sentenceTypeList);

					sentencesData.add(sds);
				}

			}
		}

		return sentencesData;
	}

}
