/**
 * 
 */
package com.emotibot.srl.test.frames.conversations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.test.frames.conversations.datastructures.Conversation;
import com.emotibot.srl.test.frames.conversations.datastructures.DictionaryItem;
import com.emotibot.srl.test.frames.conversations.datastructures.Utterance;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

/**
 * @author Sanjay
 *
 */
public class DialogFramesConversations {


	String conversationDir = "data/temp/frames_data/conversations/conversation_data/data_0401";
		
	String crfDataFile = "data/temp/frames_data/conversations/ne/cosmetic_ne_0401_crf.txt";
		
	String space_tokenized_utterancesFile = "data/temp/frames_data/conversations/ne/space_tokenized_utterances_0401.txt";
	
	String dictionaryDir = "data/temp/frames_data/conversations/gazetter/input";

	EmotibotTokenizer emotibotTokenzier = new EmotibotTokenizer();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		DialogFramesConversations conv = new DialogFramesConversations();
		conv.performSteps();
	}

	private void test() throws Exception {
		// String directory = "data/temp/frames_data/conversations/data";
		//
		// List<Conversation> allConversationsList =
		// readConversations(directory);
		//
		// processConversations(allConversationsList);
	}

	private void performSteps() throws Exception {

		// Step 1 : Create gazetter dictionary

		// String gazetteFile = "data/temp/frames_data/conversations/gazetter/output/cosmetic.gaz2.txt";
		// createGazetterDictionary(dictionaryDir, gazetteFile);

		// Step 2 : Read and process conversations
		
		List<Conversation> allConversationsList = readConversations(conversationDir);
		processConversations(allConversationsList, crfDataFile, space_tokenized_utterancesFile);

	}

	/**
	 * Gazetter File creation for entities based on
	 * 
	 * @param inputDir
	 * @param outputGazetteFile
	 * @throws IOException
	 */
	public void createGazetterDictionary(String inputDir, String outputGazetteFile) throws IOException {

		System.out.println("Creating gazeeter dictionary that will be saved at : "+ outputGazetteFile);
		Multimap<String, DictionaryItem> gazetter_multimap = readFrameDictionary(inputDir);

		StringBuilder gazetterOutputBuilder = new StringBuilder();

		// Now we will create required format of gazetter
		for (String key : gazetter_multimap.keySet()) {

			Collection<DictionaryItem> values = gazetter_multimap.get(key);
			for (DictionaryItem dictItem : values) {
				String string=dictItem.getItem();
				if(dictItem.isMultipleSegmentations()){
					List<List<String>> segmentList = dictItem.getSegmentations();
					for (List<String> list : segmentList) {
						StringBuilder segment=new StringBuilder();
						for (String string2 : list) {
							segment.append(string2 + Constants.SPACE );
						}
						 //String entry=segment.toString().trim();
						 String entry=segment.toString();
						 gazetterOutputBuilder.append(key + Constants.SPACE + entry + Constants.newline);
					}
				}
				
					gazetterOutputBuilder.append(key + Constants.SPACE + string + Constants.newline);
				
				
			}

		}

		//System.out.println(gazetterOutputBuilder.toString());
		File out = new File(outputGazetteFile);
		FileUtils.write(out, gazetterOutputBuilder.toString(), "UTF-8");
	}

	/**
	 * Reads the gazetter dictionary from the input
	 * 
	 * @param inputDir
	 * @return
	 * @throws IOException
	 */
	public Multimap<String, DictionaryItem> readFrameDictionary() throws IOException {
		return readFrameDictionary(dictionaryDir);
	}

	/**
	 * Reads the gazetter dictionary from the input
	 * 
	 * @param inputDir
	 * @return
	 * @throws IOException
	 */
	public Multimap<String, DictionaryItem> readFrameDictionary(String inputDir) throws IOException {
		//Multimap<String, String> gazetter_multimap = LinkedHashMultimap.create();
		Multimap<String, DictionaryItem> gazetter_multimap1 = LinkedHashMultimap.create();
		
		boolean multipleSegmentation=false;
		File rootDir = new File(inputDir);
		final String[] SUFFIX = { "txt" };

		Collection<File> allFiles = new ArrayList<>();
		if (rootDir.isDirectory()) {
			Collection<File> temp = FileUtils.listFiles(rootDir, SUFFIX, true);
			ArrayList<File> files = new ArrayList<File>(temp);
			allFiles.addAll(files);
		} else {
			allFiles.add(rootDir);

		}

		// iterate through all the files
		String replaceSuffix = ".txt";
		for (File file : allFiles) {

			String key = file.getName();
			key = key.replace(replaceSuffix, "");
			List<String> contents = FileUtils.readLines(file, "UTF-8");
			for (String string : contents) {
				multipleSegmentation=false;
				if (!Strings.isNullOrEmpty(string)) {
					//string=string.trim();
					String[] vals = string.split(Constants.delimiter);
					String entry=vals[0];
					
				
					
					if(vals.length>1){
						multipleSegmentation=true;
					}
					
					
					
					
					DictionaryItem dictItem;
					
					if(!multipleSegmentation){
						
						dictItem=new DictionaryItem(entry,null,multipleSegmentation);
						
					}
					else{
						List<List<String>> segmentations=new LinkedList<>();
						
						//we start with index 1 as index 0 = item
						for (int i = 1; i < vals.length; i++) {
							String temp=vals[i];
							if(!Strings.isNullOrEmpty(temp)){
								String[] tokens = temp.split(Constants.SPACE);
								List<String> list = Arrays.asList(tokens);
								segmentations.add(list);
								int t=10;
							}
						}
						
						dictItem=new DictionaryItem(entry,segmentations,multipleSegmentation);
					}
					
					gazetter_multimap1.put(key, dictItem);
				}
			}
		}

		return gazetter_multimap1;
	}

	/**
	 * Create Sentences with tokenization delimiter is space
	 */
	public List<String> getSpaceTokenizedSentenes(List<String> contents) {

		List<String> outputList = new ArrayList<>();

		for (String string : contents) {

			String input = string;
			StringBuilder outputBuilder = new StringBuilder();
			if (!Strings.isNullOrEmpty(input)) {
				String[] tokens = emotibotTokenzier.tokenize(input);
				for (String string2 : tokens) {

					outputBuilder.append(string2 + Constants.SPACE);

				}

				String out = outputBuilder.toString().trim();
				outputList.add(out);
			}

		}

		return outputList;

	}

	private void processConversations(List<Conversation> allConversationsList,String crfOutputFile,String spaceTokenizedFile) throws IOException {
		List<String> userUtterances = new LinkedList<>();
		for (Conversation conversation : allConversationsList) {
			// processConversations(conversation);

			List<Utterance> utterances = conversation.getCoversationList();
			for (Utterance utterance : utterances) {
				String speaker = utterance.getSpeaker();
				String utter = utterance.getUtterance();

				// if (isRobot(speaker)) {
					System.out.println(utter);
					userUtterances.add(utter);
				// }

			}

		}
		System.out.println(userUtterances.size());

		CRFClassifierData crfData = new CRFClassifierData();
		String nedata = crfData.createCRFClassifierData(userUtterances);
		File out = new File(crfOutputFile);
		FileUtils.write(out, nedata, "UTF-8");

		// we also need space tokenized sentences
		List<String> spaceTokenziedSentenceList = getSpaceTokenizedSentenes(userUtterances);
		StringBuilder sb = new StringBuilder();
		for (String string : spaceTokenziedSentenceList) {
			sb.append(string + Constants.newline);
		}

		System.out.println(nedata);
		System.out.println(sb.toString());
		out = new File(spaceTokenizedFile);
		FileUtils.write(out, sb.toString(), "UTF-8");
	}

	private void processConversations(Conversation conversation) {

		List<String> temp = new LinkedList<>();

		List<Utterance> utterances = conversation.getCoversationList();
		for (Utterance utterance : utterances) {
			String speaker = utterance.getSpeaker();
			String utter = utterance.getUtterance();

			if (!isRobot(speaker)) {
				System.out.println(utter);
				temp.add(utter);
			}

		}

		System.out.println(temp.size());
	}

	private boolean isRobot(String speaker) {
		boolean isRobot = false;
		if (speaker.equals(Constants.speaker_robot)) {
			isRobot = true;
		}

		return isRobot;
	}

	/**
	 * Read conversations
	 * 
	 * @throws Exception
	 */
	public List<Conversation> readConversations(String directory) throws Exception {

		File rootDir = new File(directory);

		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);

		List<Conversation> allConversationsList = new LinkedList<>();

		for (File file : files) {

			Conversation conversation = new Conversation();
			List<String> contents = FileUtils.readLines(file, "UTF-8");

			List<Utterance> utterances = new LinkedList<>();
			for (String string : contents) {

				String input = string;

				if (!Strings.isNullOrEmpty(input)) {
					String[] row = input.split(Constants.delimiter);
					String speaker = row[0];
					String utterance = row[1];

					if (utterance.startsWith("A.") || utterance.startsWith("B.")) {
						utterance = utterance.replace("A.", "");
						utterance = utterance.replace("B.", "");
					}

					Utterance utter = new Utterance();
					utter.setSpeaker(speaker);
					utter.setUtterance(utterance);

					utterances.add(utter);
				}
			}

			conversation.setCoversationList(utterances);
			allConversationsList.add(conversation);
		}
		return allConversationsList;
	}

}
