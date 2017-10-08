/**
 * 
 */
package com.emotibot.srl.pruner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

/**
 * @author Sanjay
 *
 */
public class SentencePruningOnFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SentencePruningOnFile sf=new SentencePruningOnFile();
				
		String filePath="data/syntactictree/others/voice/input/pruner__cm_interns.txt";
		String outputFilePath="data/syntactictree/others/voice/output/results_pruned_sentences_output.txt";
		sf.runSentencePrunerOnFileSentences(filePath,false, outputFilePath);
		
		
		filePath="data/syntactictree/others/voice/input/pruner_cm_flytek.txt";
		outputFilePath="data/syntactictree/others/voice/output/results_pruner_cm_flytek.txt";
		sf.runSentencePrunerOnFileSentences(filePath,false, outputFilePath);
		
//		String data="data/syntactictree/others/test_data/sentence_pruner_test_sentences_100.txt";
//		sf.evaluateScores(data);
		
	}

	
	public void evaluateScores(String filepath) throws IOException{

		File f1 = new File(filepath);

		List<String> contents = FileUtils.readLines(f1, "UTF-8");
		
		int total_sentences=contents.size();
		double correct=0;
		int incorrect=0;
				
		for (String string : contents) {
			if (!Strings.isNullOrEmpty(string)) {
				String[] vals = string.split("\t");
				String s=vals[2];
				if(!Strings.isNullOrEmpty(s)){
					double score=Double.parseDouble(s);
					correct+=score;
					if(score==0){
						incorrect++;
					}
				}
				
			}
		}
		
		
		double acc=(correct/total_sentences)*100;
		System.out.println("Total sentences: " + total_sentences);
		System.out.println("Total incorrect: " + incorrect);
		System.out.println("Accuracy : " + acc);
	}
	
	/**
	 * 
	 * @param filePath
	 * @param outputFilePath
	 * @throws IOException
	 */
	public void runSentencePrunerOnFileSentences(String filePath,boolean isTree,String outputFilePath) throws IOException{
		
		
		StringBuilder sb=new StringBuilder();
		TestParseTrees testParseTrees=new TestParseTrees();
		File f1 = new File(filePath);

		List<String> contents = FileUtils.readLines(f1, "UTF-8");
		for (String string : contents) {
			if (!Strings.isNullOrEmpty(string)) {
				String prundedOutput="";
				if (!isTree){
					prundedOutput=testParseTrees.pruneSentence(string);
				}
				
				sb.append(string + "  --->  " +  prundedOutput  + "\t" +"0"+ "\n");
			}
		}
		
		
		System.out.println(sb.toString());
		File out=new File(outputFilePath);
		FileUtils.write(out, sb.toString(),"UTF-8");
		
	}
}
