/**
 * 
 */
package com.emotibot.srl.test.frames.conversations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.test.frames.conversations.datastructures.DictionaryItem;
import com.emotibot.srl.test.frames.conversations.datastructures.GeneratedSentence;
import com.emotibot.srl.test.frames.conversations.datastructures.TemplateSentence;
import com.emotibot.srl.test.frames.conversations.datastructures.TokenPair;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Sanjay
 *
 */
public class SentenceTemplateGeneration {
	
	
	String taggedCRFDataFolder = "data/temp/frames_data/conversations/tagged/crf";

	String autotemplateDataFile = "data/temp/frames_data/conversations/tagged/auto_template_sentences.txt";
	
	String templateDataFile = "data/temp/frames_data/conversations/tagged/template_sentences.txt";
	String trainingDataFile = "data/temp/frames_data/conversations/tagged/training_data_fer.txt";

	String autoTemplateDataFile = "data/temp/frames_data/conversations/tagged/auto_template_sentences.txt";

	String template_generated_sentences_info = "data/temp/frames_data/conversations/tagged/template_generated_sentences_info.txt";

	String tokenSegmentsSource_dict = "dictionary";
	String tokenSegmentsSource_train = "taggedData";

	String gazetteFile = "data/temp/frames_data/conversations/tagged/cosmetic.gaz.txt";
	String dictionaryDir = "data/temp/frames_data/conversations/gazetter/input";

	// Usually this can be a field rather than a method variable
	Random rand = new Random();
	DialogFramesConversations dfc = new DialogFramesConversations();


	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SentenceTemplateGeneration conv = new SentenceTemplateGeneration();
		
		conv.performSteps();
		//conv.createTemplatesFromHumanAnnotatedData();
	}

	/**
	 * Perform the steps for the sentence template generation
	 * @throws IOException
	 */
	private void performSteps() throws IOException {

		// read sentences in template data structure

		// List<TemplateSentence> templateSentenceList1 = readSentences();

		List<TemplateSentence> templateSentenceList=new ArrayList<>();

		// Step 1 : read template sentences from file and store them in proper
		// datastructure.
		templateSentenceList.addAll(readTemplateSentences(templateDataFile));
		
		createTemplatesFromHumanAnnotatedData();
		List<TemplateSentence> templateSentenceList2 = readAutoGeneratedTemplateSentences(autotemplateDataFile);
		
		templateSentenceList.addAll(templateSentenceList2);

		System.out.println("");

		for (TemplateSentence templateSentence : templateSentenceList) {
			if (templateSentence.isHasTemplateLabels())
				System.out.print(templateSentence.toString());
		}

		Multimap<TemplateSentence, GeneratedSentence> completeDataMapByTemplate;
		// Step 2 : program to generate template sentences
		completeDataMapByTemplate = generateTemplatesSentenes(templateSentenceList);

		// lets get the generated sentences by type of fer .
		//Multimap<String, GeneratedSentence> mapByFERType = getMapByBrand(completeDataMapByTemplate);

		// Step 3 : Generate training data in crf format

		// boolean to add manually tagged data for training as well
		boolean addManualTaggedData = true;
		int totalGeneratedSentences = completeDataMapByTemplate.values().size();
		int totalTemplates = completeDataMapByTemplate.keySet().size();

		//int idealNumberSentencePerTemplate = getIdealSentenceNumber(totalGeneratedSentences, totalTemplates);
		
		int idealNumberSentencePerTemplate = 2;
		
		
		// idealNumberSentencePerTemplate = idealNumberSentencePerTemplate ;
		Multimap<TemplateSentence, GeneratedSentence> subsetDataMap = getSubsetDataMap(completeDataMapByTemplate,
				idealNumberSentencePerTemplate);
				// int subsetDatSize = subsetDataMap.size();

		// generate training data file
		// generateTrainingDataFile(subsetDataMap, addManualTaggedData);
		generateTrainingDataFile(subsetDataMap, addManualTaggedData);

		// create crf dictionary file
		dfc.createGazetterDictionary(dictionaryDir, gazetteFile);

		// Step 4 : Generate info related to generated templates and sentences
		generateStatsForGeneratedInformation(completeDataMapByTemplate, subsetDataMap);

	}

	

	private Multimap<String, GeneratedSentence> getMapByBrand(
			Multimap<TemplateSentence, GeneratedSentence> completeDataMapByTemplate) {

		Multimap<String, GeneratedSentence> tempMap = LinkedHashMultimap.create();
		Multimap<String, GeneratedSentence> tempMap2 = LinkedHashMultimap.create();

		Multimap<String, GeneratedSentence> fullMap = LinkedHashMultimap.create();
		Multimap<String, GeneratedSentence> mapbyMultipleEntityType = LinkedHashMultimap.create();
		Multimap<String, GeneratedSentence> singleEntityTypeMap = LinkedHashMultimap.create();

		for (TemplateSentence template : completeDataMapByTemplate.keySet()) {
			Set<String> entities = template.getTemplateMap().keySet();
			List<String> entityList = new ArrayList<>(entities);

			for (String string : entityList) {
				Collection<GeneratedSentence> generatedSentences = completeDataMapByTemplate.get(template);
				for (GeneratedSentence generatedSentence : generatedSentences) {
					Multimap<String, Integer> templateMap = generatedSentence.getTemplateMap();
					fullMap.put(string, generatedSentence);
					int totalSlots = templateMap.keySet().size();
					if (totalSlots == 1) {
						singleEntityTypeMap.put(string, generatedSentence);
					} else {
						mapbyMultipleEntityType.put(string, generatedSentence);
					}

				}

			}

		}

		System.out.println(" ");
		System.out.println("Single slot info :");
		for (String string : singleEntityTypeMap.keySet()) {
			Collection<GeneratedSentence> values = singleEntityTypeMap.get(string);
			for (GeneratedSentence generatedSentence : values) {
				Set<Integer> indices = generatedSentence.getTemplateIndices();
				int temp = 10;
			}
			System.out.println(string + "  " + "total generated sentences : " + values.size());
		}
		System.out.println(" ");
		System.out.println("Multiple slot info :");
		for (String string : fullMap.keySet()) {
			Collection<GeneratedSentence> values = fullMap.get(string);
			for (GeneratedSentence generatedSentence : values) {
				Set<Integer> indices = generatedSentence.getTemplateIndices();
				int temp = 10;
			}
			System.out.println(string + "  " + "total generated sentences : " + values.size());
		}

		int avg = singleEntityTypeMap.values().size() / singleEntityTypeMap.keySet().size();

		int heuristic = 2;
		int total = singleEntityTypeMap.values().size();
		Set<String> overGenerated = new HashSet<>();
		for (String string : singleEntityTypeMap.keySet()) {
			int size = singleEntityTypeMap.get(string).size();

			int prod = heuristic * size;
			if (prod > total) {
				overGenerated.add(string);
			}

		}

		for (String string : mapbyMultipleEntityType.keySet()) {
			if (!overGenerated.contains(string)) {

				Collection<GeneratedSentence> values = mapbyMultipleEntityType.get(string);
				for (GeneratedSentence generatedSentence : values) {
					Multimap<String, Integer> templateMap = generatedSentence.getTemplateMap();
					Set<String> keySet = templateMap.keySet();

					boolean intersection = keySet.containsAll(overGenerated);
					if (!intersection) {
						tempMap.put(string, generatedSentence);
						int t = 10;

					} else {
						int t = 10;
					}
					int t = 10;
				}
			}

		}
		Set<String> vals = tempMap.keySet();
		System.out.println(" ");
		for (String string : tempMap.keySet()) {
			Collection<GeneratedSentence> values = tempMap.get(string);
			List<GeneratedSentence> generatedSentences2 = getRandomElementsFromList(values, avg);
			tempMap2.putAll(string, generatedSentences2);
			System.out.println(string + "  " + "total generated sentences : " + values.size());
		}

		// lets put overgenerated sentences now
		for (String string : overGenerated) {
			Collection<GeneratedSentence> values = fullMap.get(string);
			List<GeneratedSentence> generatedSentences2 = getRandomElementsFromList(values, avg);
			tempMap2.putAll(string, generatedSentences2);

		}

		for (String string : tempMap2.keySet()) {
			Collection<GeneratedSentence> values = tempMap2.get(string);
			System.out.println(string + "  " + "total generated sentences : " + values.size());
		}

		return tempMap2;
	}

	/**
	 * 
	 * @param totalGeneratedSentences
	 * @param totalTemplates
	 * @return
	 */
	private int getIdealSentenceNumber(int totalGeneratedSentences, int totalTemplates) {

		int idealNumberSentencePerTemplate = totalGeneratedSentences / totalTemplates;
		int reduceBy = 1000;

		int finalNumber = idealNumberSentencePerTemplate - reduceBy;
		System.out.println("Total idealNumberSentencePerTemplate is  " + idealNumberSentencePerTemplate);
		System.out.println("Final number selected per template is  " + finalNumber);
		return finalNumber;
	}

	/**
	 * 
	 * Read templates from human annotation
	 * 
	 * @throws IOException
	 * 
	 */
	public void createTemplatesFromHumanAnnotatedData() throws IOException {

		StringBuilder templateDataBuilder = new StringBuilder();

		Set<String> hashSet=new HashSet<>();
		List<TemplateSentence> manulCRFData = readCRFData(taggedCRFDataFolder,true);
		for (TemplateSentence templateSentence : manulCRFData) {
			if(hashSet.contains(templateSentence.toString())){
				
				templateDataBuilder.append(templateSentence.getTemplateString(true));
				templateDataBuilder.append(Constants.newline);
			}
			else{
				hashSet.add(templateSentence.toString());
			}
			
			//templateDataBuilder.append(Constants.newline);

		}

		File out = new File(autoTemplateDataFile);
		FileUtils.write(out, templateDataBuilder.toString(), "UTF-8");
		System.out.println("Wrote template sentences  data to : " + out.toString());
	}

	/**
	 * 
	 * @param completeDataMapByTemplate
	 * @param idealNumberSentencePerTemplate
	 * @return
	 */
	private Multimap<TemplateSentence, GeneratedSentence> getSubsetDataMap(
			Multimap<TemplateSentence, GeneratedSentence> completeDataMapByTemplate,
			int idealNumberSentencePerTemplate) {

		Multimap<TemplateSentence, GeneratedSentence> subsetDataMapByTemplate = LinkedHashMultimap.create();

		for (TemplateSentence key : completeDataMapByTemplate.keySet()) {
			Collection<GeneratedSentence> generatedSentences = completeDataMapByTemplate.get(key);
			int size = generatedSentences.size();

			List<GeneratedSentence> generatedSentences1 = new ArrayList<>();

			// lets first select sentences which have multiple segments defined
			// in dictionary
			for (GeneratedSentence generatedSentence : generatedSentences) {
				boolean multipleSegments = generatedSentence.isHasTokensWithMultipleSegments();
				if (multipleSegments) {
					generatedSentences1.add(generatedSentence);
				}
			}

			List<GeneratedSentence> generatedSentences2 = null;
			if (generatedSentences1.size() > idealNumberSentencePerTemplate) {
				generatedSentences2 = getRandomElementsFromList(generatedSentences1, idealNumberSentencePerTemplate);
			} else {
				generatedSentences2 = getRandomElementsFromList(generatedSentences, idealNumberSentencePerTemplate);
			}

			for (GeneratedSentence generatedSentence : generatedSentences2) {
				subsetDataMapByTemplate.put(key, generatedSentence);
			}
		}

		return subsetDataMapByTemplate;
	}

	/**
	 * 
	 * @param completeDataMapByTemplate
	 * @param addManuallTaggedData
	 * @throws IOException
	 */
	private void generateTrainingDataFile(Multimap<TemplateSentence, GeneratedSentence> subsetDataMap,
			boolean addManuallTaggedData) throws IOException {
		StringBuilder trainingDataBuilder = new StringBuilder();
		int totalSentences = 0;
		// this block reads data from the human annotated data
		if (addManuallTaggedData) {
			List<TemplateSentence> manulCRFData = readCRFData(taggedCRFDataFolder,true);
			totalSentences=manulCRFData.size();
			
			for (TemplateSentence templateSentence : manulCRFData) {
				Multimap<String, Integer> templateMap = templateSentence.getTemplateMap();
				Map<Integer, TokenPair> newTokensMap = templateSentence.getTokenMap();
				Set<Integer> templateIndices = templateSentence.getGenerativeInformationMap().keySet();
				GeneratedSentence generatedSentence = new GeneratedSentence(newTokensMap, templateIndices, templateMap);
				boolean useMultipleTokenInfo = generatedSentence.isBreakableTokens();

				List<String> generatedCRFStringList = new ArrayList<>();
				generatedCRFStringList = generatedSentence.generateCRFModelData(useMultipleTokenInfo);
				for (String crfString : generatedCRFStringList) {
					trainingDataBuilder.append(crfString);
					
				}

				trainingDataBuilder.append(Constants.newline);
				
			}
		}

		// Generate training data from the generated template file

		 LinkedHashSet<String> allCRFStringSet = new LinkedHashSet<String>();
		
		 for (TemplateSentence key : subsetDataMap.keySet()) {
		 Collection<GeneratedSentence> generatedSentences =
		 subsetDataMap.get(key);
		
		 for (GeneratedSentence generatedSentence : generatedSentences) {
		 boolean useMultipleTokenInfo = generatedSentence.isBreakableTokens();
		 List<String> generatedCRFStringList = new ArrayList<>();
		
		 // please note we are calling another function here.
		 List<String> temp =
		 generatedSentence.generateCRFModelData1(useMultipleTokenInfo);
		 generatedCRFStringList.addAll(temp);
		
		 // Lets call another function which only takes tokenized
		 // dictionary items
		 temp = generatedSentence.generateCRFModelData2(useMultipleTokenInfo);
		 generatedCRFStringList.addAll(temp);
		
		 allCRFStringSet.addAll(generatedCRFStringList);
		
		 }
		
		 // System.out.println(generatedSentence);
		
		 }
		
		 int totalTemplateSentences = allCRFStringSet.size();
		 for (String crfString : allCRFStringSet) {
		 trainingDataBuilder.append(crfString);
		 trainingDataBuilder.append(Constants.newline);
		 }
		trainingDataBuilder.append(Constants.newline);

		
		File out = new File(trainingDataFile);
		FileUtils.write(out, trainingDataBuilder.toString(), "UTF-8");
		System.out.println("Wrote training data to : " + out.toString());
		System.out.println("total  sentences written : " + (totalTemplateSentences+totalSentences));
		System.out.println("total template sentences written : " + totalTemplateSentences);
	}

	// /**
	// *
	// * @param completeDataMapByTemplate
	// * @param addManuallTaggedData
	// * @throws IOException
	// */
	// private void generateTrainingDataFile(Multimap<TemplateSentence,
	// GeneratedSentence> subsetDataMap,
	// boolean addManuallTaggedData) throws IOException {
	// StringBuilder trainingDataBuilder = new StringBuilder();
	//
	// // this block reads data from the human annotated data
	// if (addManuallTaggedData) {
	// List<TemplateSentence> manulCRFData = readCRFData(manualCRFDataFiles);
	// for (TemplateSentence templateSentence : manulCRFData) {
	// Multimap<String, Integer> templateMap =
	// templateSentence.getTemplateMap();
	// Map<Integer, TokenPair> newTokensMap = templateSentence.getTokenMap();
	// Set<Integer> templateIndices =
	// templateSentence.getGenerativeInformationMap().keySet();
	// GeneratedSentence generatedSentence = new GeneratedSentence(newTokensMap,
	// templateIndices, templateMap);
	// boolean useMultipleTokenInfo = generatedSentence.isBreakableTokens();
	//
	// List<String> generatedCRFStringList = new ArrayList<>();
	// generatedCRFStringList =
	// generatedSentence.generateCRFModelData(useMultipleTokenInfo);
	// for (String crfString : generatedCRFStringList) {
	// trainingDataBuilder.append(crfString);
	// }
	//
	// trainingDataBuilder.append(Constants.newline);
	// }
	// }
	//
	// // Generate training data from the generated template file
	//
	// LinkedHashSet<String> allCRFStringSet = new LinkedHashSet<String>();
	//
	// for (TemplateSentence key : subsetDataMap.keySet()) {
	// Collection<GeneratedSentence> generatedSentences =
	// subsetDataMap.get(key);
	//
	// for (GeneratedSentence generatedSentence : generatedSentences) {
	// boolean useMultipleTokenInfo = generatedSentence.isBreakableTokens();
	// List<String> generatedCRFStringList = new ArrayList<>();
	//
	// // please note we are calling another function here.
	// List<String> temp =
	// generatedSentence.generateCRFModelData1(useMultipleTokenInfo);
	// generatedCRFStringList.addAll(temp);
	//
	// // Lets call another function which only takes tokenized
	// // dictionary items
	// temp = generatedSentence.generateCRFModelData2(useMultipleTokenInfo);
	// generatedCRFStringList.addAll(temp);
	//
	// allCRFStringSet.addAll(generatedCRFStringList);
	//
	// }
	//
	// // System.out.println(generatedSentence);
	//
	// }
	//
	// int totalSentences = allCRFStringSet.size();
	// for (String crfString : allCRFStringSet) {
	// trainingDataBuilder.append(crfString);
	// trainingDataBuilder.append(Constants.newline);
	// }
	// trainingDataBuilder.append(Constants.newline);
	//
	// File out = new File(trainingDataFile);
	// FileUtils.write(out, trainingDataBuilder.toString(), "UTF-8");
	// }

	/**
	 * Gives randomized collection of elements given max size
	 * 
	 * @param collection
	 * @param maxElements
	 * @return
	 */
	private List<GeneratedSentence> getRandomElementsFromList(Collection<GeneratedSentence> collection,
			int maxElements) {

		List<GeneratedSentence> list = new ArrayList<GeneratedSentence>(collection);

		List<GeneratedSentence> randomElementList = new ArrayList<>();
		if (list.size() < maxElements) {
			randomElementList = (List<GeneratedSentence>) list;
		} else {
			Collections.shuffle(list);
			for (int i = 0; i < maxElements; i++) {
				randomElementList.add(list.get(i));
			}
		}

		return randomElementList;
	}

	/**
	 * 
	 * @param completeDataMapByTemplate
	 * @param subsetDataMap
	 * @throws IOException
	 */
	private void generateStatsForGeneratedInformation(
			Multimap<TemplateSentence, GeneratedSentence> completeDataMapByTemplate,
			Multimap<TemplateSentence, GeneratedSentence> subsetDataMap) throws IOException {
		// TODO Auto-generated method stub
		StringBuilder sb1 = new StringBuilder();
		for (TemplateSentence template : subsetDataMap.keySet()) {
			Collection<GeneratedSentence> values = subsetDataMap.get(template);
			String tp = template.toString().replace(Constants.newline, "");
			sb1.append("Template:  " + tp + Constants.newline);
			for (GeneratedSentence generatedSentence : values) {
				sb1.append(Constants.delimiter + "generated sentence :  " + generatedSentence.toString()
						+ Constants.newline);
			}

		}
		sb1.append(" --------------");
		sb1.append("Printing template info.." + Constants.newline);
		for (TemplateSentence template : subsetDataMap.keySet()) {
			Collection<GeneratedSentence> values = subsetDataMap.get(template);
			String tp = template.toString().replace(Constants.newline, "");
			sb1.append("Template:  " + tp + " | total generated sentences: " + values.size() + Constants.newline);

		}

		File out1 = new File(template_generated_sentences_info);
		FileUtils.write(out1, sb1.toString(), "UTF-8");
		System.out.println("Wrote the template generated sentences informatino to : " + out1.toString());
		// System.out.println(wordDictBuilder.toString());

		List<String> breakableTokensList = new ArrayList<>();
		for (GeneratedSentence generatedSentence : subsetDataMap.values()) {

			// System.out.println(generatedSentence);
			boolean useMultipleTokenInfo = generatedSentence.isBreakableTokens();

			if (useMultipleTokenInfo) {
				breakableTokensList.add(generatedSentence.toString());
			}
		}

		System.out.println("Total templates used to generate sentence : " + completeDataMapByTemplate.keySet().size());
		System.out.println("total generated sentences :  " + completeDataMapByTemplate.values().size());
		System.out.println("total generated sentences selected : " + subsetDataMap.values().size());
		System.out.println("sentences with breakable tokens : " + breakableTokensList.size());

	}

	/**
	 * 
	 * @param templateSentenceList
	 * @throws IOException
	 */
	public Multimap<TemplateSentence, GeneratedSentence> generateTemplatesSentenes(
			List<TemplateSentence> templateSentenceList) throws IOException {

		// lets get the gazetter map
		DialogFramesConversations dfc = new DialogFramesConversations();
		Multimap<String, DictionaryItem> gazetter_multimap = dfc.readFrameDictionary();

		Multimap<String, TemplateSentence> fe_to_templateSentences = LinkedHashMultimap.create();
		// lets create the frame element - sentence template map
		for (TemplateSentence tmplSent : templateSentenceList) {
			Multimap<String, Integer> templateMap = tmplSent.getTemplateMap();
			Set<String> frameElements = templateMap.keySet();

			for (String string : frameElements) {
				fe_to_templateSentences.put(string, tmplSent);
			}

		}

		Multimap<String, GeneratedSentence> completeDataMap = LinkedHashMultimap.create();
		Multimap<TemplateSentence, GeneratedSentence> completeDataMapByTemplate = LinkedHashMultimap.create();

		//
		for (String key : fe_to_templateSentences.keySet()) {
			Collection<TemplateSentence> templates = fe_to_templateSentences.get(key);
			// list of indices where the frame element can be replaced.
			for (TemplateSentence templateSentence : templates) {

				// call sentence generator helper function
				Multimap<String, GeneratedSentence> multipleTokenTemplateSentenceList = sentenceGeneratorHelper(
						templateSentence, gazetter_multimap);

				completeDataMapByTemplate.putAll(templateSentence, multipleTokenTemplateSentenceList.values());

				completeDataMap.putAll(multipleTokenTemplateSentenceList);

			}

		}

		return completeDataMapByTemplate;

	}

	/**
	 * This function will generate the necessary generative information about
	 * input template and add to its data structure
	 * 
	 * @param templateSentence
	 * @param gazetter_multimap
	 */
	private Multimap<String, GeneratedSentence> sentenceGeneratorHelper(TemplateSentence templateSentence,
			Multimap<String, DictionaryItem> gazetter_multimap) {

		// Step 1 : create the generative information

		sentenceGeneratorHelper1(templateSentence, gazetter_multimap);

		// now finally create the templates

		Multimap<String, GeneratedSentence> generatedSentenceMap;

		boolean containsDummyToken = true;
		generatedSentenceMap = createGeneratedDataMap(templateSentence, containsDummyToken);

		return generatedSentenceMap;
	}

	/**
	 * Function which creates the map containing all the generated data
	 * 
	 * @param templateSentence
	 */
	private Multimap<String, GeneratedSentence> createGeneratedDataMap(TemplateSentence templateSentence,
			boolean containsDummyToken) {

		Multimap<String, GeneratedSentence> generatedSentenceMap = LinkedHashMultimap.create();

		Map<Integer, TokenPair> tokensMap = templateSentence.getTokenMap();
		Multimap<String, Integer> tmpLateMap = templateSentence.getTemplateMap();
		Multimap<Integer, TokenPair> generativeInformationMap = templateSentence.getGenerativeInformationMap();

		Set<Integer> templateIndices = generativeInformationMap.keySet();

		// Let's first add the original template sentence;
		if (!containsDummyToken) {
			GeneratedSentence gs = new GeneratedSentence(tokensMap, templateIndices, tmpLateMap);
			for (String ne : tmpLateMap.keySet()) {
				generatedSentenceMap.put(ne, gs);
			}
		}

		// listMap.add(gs);

		int templateLabelsSize = generativeInformationMap.keySet().size();
		// means there are more than one template elements;
		if (templateLabelsSize > 1) {

			Multimap<String, GeneratedSentence> multipleTokenTemplateSentenceMap = handleMultipleTemplateTokens(
					templateSentence);
			generatedSentenceMap.putAll(multipleTokenTemplateSentenceMap);

		} else {

			for (Integer index : generativeInformationMap.keySet()) {
				// Map<Integer, TokenPair> newTokensMap=new
				// LinkedHashMap<>(tokensMap);
				Collection<TokenPair> newTokenPairs = generativeInformationMap.get(index);

				// for every token : eg. token[token=妙巴黎, ne=BRAND,
				// hasEntity=true, multipTokens=true,
				// multipSegmentsFromDict=true]
				// create a new sentence. We do this by replacing the token at
				// index i in origina sentence with genrative token.
				for (TokenPair tokenPair : newTokenPairs) {
					String ne = tokenPair.getNe();
					Map<Integer, TokenPair> newTokensMap = new LinkedHashMap<>(tokensMap);
					newTokensMap.put(index, tokenPair);

					GeneratedSentence gs1 = new GeneratedSentence(newTokensMap, templateIndices, tmpLateMap);
					// listMap.add(gs1);

					generatedSentenceMap.put(ne, gs1);
				}
			}
		}
		return generatedSentenceMap;
	}

	private Multimap<String, GeneratedSentence> handleMultipleTemplateTokens(TemplateSentence templateSentence) {

		Map<Integer, TokenPair> tokensMap = templateSentence.getTokenMap();
		Multimap<String, Integer> tmpLateMap = templateSentence.getTemplateMap();
		Multimap<Integer, TokenPair> generativeInformationMap = templateSentence.getGenerativeInformationMap();

		Multimap<String, GeneratedSentence> generatedSentenceMap = LinkedHashMultimap.create();
		Set<Integer> templateIndices = generativeInformationMap.keySet();

		List<Integer> sizes = new ArrayList<>();

		for (Integer index : generativeInformationMap.keySet()) {
			// Map<Integer, TokenPair> newTokensMap=new
			// LinkedHashMap<>(tokensMap);
			Collection<TokenPair> newTokenPairs = generativeInformationMap.get(index);
			sizes.add(newTokenPairs.size());

		}

		// we have simple generative strategy for now. we simply sum all
		// elements sizes and want to generate examples based on the sum .
		// we will choose fe randomly in the loop
		int sum = sizes.stream().mapToInt(Integer::intValue).sum();

		// now we generate the template sentence combinations
		for (int i = 0; i < sum; i++) {

			Map<Integer, TokenPair> tempTPMap = new LinkedHashMap<>();
			for (Integer index : generativeInformationMap.keySet()) {
				// Map<Integer, TokenPair> newTokensMap=new
				// LinkedHashMap<>(tokensMap);
				Collection<TokenPair> newTokenPairs = generativeInformationMap.get(index);
				List<TokenPair> list = new ArrayList<TokenPair>(newTokenPairs);

				int randomNum = generateRandomNumber(0, newTokenPairs.size() - 1);
				TokenPair tp1 = list.get(randomNum);
				tempTPMap.put(index, tp1);
			}

			// now lets generate the sentences
			Map<Integer, TokenPair> newTokensMap = new LinkedHashMap<>(tokensMap);
			for (Integer index : tempTPMap.keySet()) {
				TokenPair tokenPair = tempTPMap.get(index);
				newTokensMap.put(index, tokenPair);

			}
			GeneratedSentence gs = new GeneratedSentence(newTokensMap, templateIndices, tmpLateMap);

			// we iterate through the map to add generated sentences by ne type
			for (Integer index : tempTPMap.keySet()) {
				TokenPair tokenPair = tempTPMap.get(index);
				String ne = tokenPair.getNe();
				generatedSentenceMap.put(ne, gs);

			}

			// listMap.add(gs);
		}
		return generatedSentenceMap;
	}

	/**
	 * 
	 * @param i
	 * @param size
	 * @return
	 */
	private int generateRandomNumber(int min, int max) {
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	private void sentenceGeneratorHelper1(TemplateSentence templateSentence,
			Multimap<String, DictionaryItem> gazetter_multimap) {
		// TODO Auto-generated method stub
		Multimap<String, Integer> templateMap = templateSentence.getTemplateMap();

		Map<Integer, String> templateIndexMap = templateSentence.getTemplateIndexMap();

		Map<Integer, TokenPair> tokensMap = templateSentence.getTokenMap();

		Set<String> frameElements = templateMap.keySet();

		// lets create fe element map
		Multimap<String, DictionaryItem> fe_dictionaryMap = LinkedHashMultimap.create();

		// lets collect fe dictionary items for this sentence;
		for (String string : frameElements) {
			Collection<DictionaryItem> feInstances = gazetter_multimap.get(string);
			fe_dictionaryMap.putAll(string, feInstances);
		}

		Multimap<Integer, TokenPair> generativeInformationMap = LinkedHashMultimap.create();

		// lets collect fe dictionary items for this sentence;
		for (Integer indice : templateIndexMap.keySet()) {
			String fe = templateIndexMap.get(indice);
			Collection<DictionaryItem> feValues = fe_dictionaryMap.get(fe);

			TokenPair tp = tokensMap.get(indice);

			// Here we enter dictioanry item value as well as any kind of
			// tokenizations if present.
			for (DictionaryItem dictItem : feValues) {

				TokenPair newTP = null;

				// this is the entry for dictionary item
				String val = dictItem.getItem();
				List<List<String>> segments = dictItem.getSegmentations();
				String[] vals = val.split(Constants.SPACE);
				if (vals.length > 1) {

					List<String> entrySegments = Arrays.asList(vals);
					// System.out.println(entrySegments);
					val = val.replace(Constants.SPACE, "");

					if (segments == null) {
						segments = new LinkedList<>();
					}

					segments.add(entrySegments);

					newTP = new TokenPair(val, tp.getNe(), indice, segments);
					// we define that the token string is compacted.
					newTP.setCompactedTokenString(true);
					// we set the entry segments
					newTP.setTrainDataMultipleTokenList(entrySegments);

				} else {
					newTP = new TokenPair(dictItem.getItem(), tp.getNe(), indice, dictItem.getSegmentations());
				}

				generativeInformationMap.put(indice, newTP);

			}
		}

		templateSentence.setGenerativeInformationMap(generativeInformationMap);
		// System.out.println(templateIndexMap + " : " +
		// generativeInformationMap);
	}

	
	
	
	private List<TemplateSentence> readAutoGeneratedTemplateSentences(String autotemplateDataFile2) throws IOException {
		// TODO Auto-generated method stub
		return readTemplateSentences(autotemplateDataFile2);
	}
	
	
	private List<TemplateSentence> readTemplateSentences(String dataFile) throws IOException {
		File file = new File(dataFile);
		// read tagged ne data
		System.out.println("Reading the file : " + file);
		List<String> contents = FileUtils.readLines(file, "UTF-8");

		List<String> templates = new LinkedList<>();
		for (String string : contents) {
			if (!Strings.isNullOrEmpty(string)) {
				templates.add(string);
			}
		}

		List<List<String>> sentenceList = new ArrayList<>();

		for (String string : templates) {
			List<String> neTokensList = new ArrayList<>();
			String[] values = string.split(Constants.delimiter);
			List<String> array = Arrays.asList(values);

			String tokenPair = "";
			for (String string2 : array) {
				if (string2.contains("<") && string2.contains(">")) {
					string2 = string2.replace("<", "");
					string2 = string2.replace(">", "");
					tokenPair = "dummy" + Constants.delimiter + string2;
				} else {
					tokenPair = string2 + Constants.delimiter + Constants.emptyNER;
				}

				neTokensList.add(tokenPair);
			}
			sentenceList.add(neTokensList);
		}

		// call the function to generate the template sentences;
		List<TemplateSentence> templateSentences = createTemplateSentences(sentenceList);

		return templateSentences;
	}

	/**
	 * This function reads the crf data from the list;
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<TemplateSentence> readCRFData(List<String> dataFiles) throws IOException {
		List<TemplateSentence> allData = new ArrayList<>();

		for (String file : dataFiles) {
			List<TemplateSentence> sentences = readCRFData(file);
			allData.addAll(sentences);
		}

		return allData;
	}
	
	
	/**
	 * This function reads the crf data from the list;
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<TemplateSentence> readCRFData(String inputDir,boolean isDirectory) throws IOException {
		
		
		List<TemplateSentence> allData = new ArrayList<>();
		
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
			
			List<TemplateSentence> sentences = readCRFData(file.toString());
			allData.addAll(sentences);
		}

		return allData;
	}

	/**
	 * This function reads the crf data from the list;
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<TemplateSentence> readCRFData(String crfDataFile) throws IOException {
		File file = new File(crfDataFile);
		// read tagged ne data
		System.out.println("Reading the file : " + file);
		List<String> contents = FileUtils.readLines(file, "UTF-8");

		List<List<String>> sentenceList = new LinkedList<>();
		List<String> neTokensList = new LinkedList<>();
		for (String string : contents) {

			if (!Strings.isNullOrEmpty(string)) {
				neTokensList.add(string);
			} else {

				if (neTokensList.size() > 0) {
					sentenceList.add(neTokensList);

				}

				neTokensList = new LinkedList<>();
			}

		}

		// call the function to generate the template sentences;
		List<TemplateSentence> templateSentences = createTemplateSentences(sentenceList);

		return templateSentences;
	}

	/**
	 * This function reads the crf data from the list;
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<TemplateSentence> readSentences(List<String> dataFiles) throws IOException {
		List<TemplateSentence> allData = new ArrayList<>();

		for (String file : dataFiles) {
			List<TemplateSentence> sentences = readSentences(file);
			allData.addAll(sentences);
		}

		return allData;
	}

	/**
	 * This function reads the sentences from the list;
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<TemplateSentence> readSentences(String dataFile) throws IOException {
		File file = new File(dataFile);
		// read tagged ne data
		System.out.println("Reading the file : " + file.getName());
		List<String> contents = FileUtils.readLines(file, "UTF-8");

		List<List<String>> sentenceList = new LinkedList<>();
		List<String> neTokensList = new LinkedList<>();
		for (String string : contents) {

			if (!Strings.isNullOrEmpty(string)) {
				neTokensList.add(string);
			} else {

				if (neTokensList.size() > 0) {
					sentenceList.add(neTokensList);

				}

				neTokensList = new ArrayList<>();
			}

		}

		// call the function to generate the template sentences;
		List<TemplateSentence> templateSentences = createTemplateSentences(sentenceList);

		return templateSentences;
	}

	/**
	 * Function that returns the template Sentences Datastructure
	 * 
	 * @param sentenceList
	 * @return
	 */
	private List<TemplateSentence> createTemplateSentences(List<List<String>> sentenceList) {
		String temp = "";
		List<TemplateSentence> templateSentences = new LinkedList<>();
		try {

			for (List<String> list : sentenceList) {
				TemplateSentence sent = new TemplateSentence();
				List<TokenPair> tokenPairs = new LinkedList<>();
				List<String> tokens = new LinkedList<>();
				// System.out.println(list);
				int index = -1;
				for (String neToken : list) {
					index++;
					// neToken = neToken.trim();
					temp = neToken;
					String[] values = neToken.split(Constants.delimiter);

					String token = values[0];
					String ne = values[1];

					TokenPair tp = new TokenPair(token, ne, index);
					tokenPairs.add(tp);
					// System.out.println(tokenPairs);

				}
				// this is important function

				sent.setTokenList(tokenPairs);

				// very important function
				templateSentences.add(sent);
			}

		} catch (IndexOutOfBoundsException err) {
			System.out.println(temp);
			err.printStackTrace();

		}
		return templateSentences;
	}

}
