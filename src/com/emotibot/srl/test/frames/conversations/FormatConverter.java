package com.emotibot.srl.test.frames.conversations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;

import javafx.util.Pair;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;

public class FormatConverter {

	private static Pattern pattern_start = Pattern.compile("<START:.+?>");
	private static Pattern pattern_end = Pattern.compile("<END>");
	private static Pattern pattern_entity = Pattern.compile("<START:(.+?)>(.+?)<END>");
	
	EmotibotTokenizer tokenizer = new EmotibotTokenizer();
	
	public static void main(String[] args) throws IOException {

//		String inputPath = "data/temp/test/answer_20170329.txt";
//		String outputPath = "data/temp/test/answer_crf_20170329.txt";
//		String inputPath = "data/temp/test/spFormat.txt";
//		String outputPath = "data/temp/test/spFormat_crf_20170329_vip.txt";
		String inputPath = "data/temp/data_0331/0930_badcase.txt";
		String outputPath = "data/temp/data_0331/0930_badcase_crf.txt";
		
		FormatConverter fc = new FormatConverter();
		
//		String result = fc.convertInlineTaggedToSentence("我想要<START:COLOR>珠光正红、玫瑰粉和兰花裸色<END>的");
//		System.out.println(result);
//		
//		result = fc.convertInlineTaggedToCRFFormat("我想要<START:COLOR>珠光正红、玫瑰粉和兰花裸色<END>的");
//		System.out.println(result);
//		
		fc.convertInlineTaggedFileToCRFFile(inputPath, outputPath);
		
//		fc.convertSPFormatInWechatGroupToCRFFormat(inputPath, outputPath);
	}
	
	public void convertSPFormatInWechatGroupToCRFFormat(String inputPath, String outputPath) throws IOException {
		File f = new File(inputPath);
		List<String> lines = FileUtils.readLines(f, "UTF-8");
		
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			String[] split_result = line.split("\t");
			if (split_result.length == 2) {
		
				String sentence = split_result[0];
				List<Pair<String, String>> entityList = extractEntityList(split_result[1]);
				
				String crf_string = convertSentenceToCRFwithEntityList(sentence, entityList);
				sb.append(crf_string);
			}
		}
		
		File fout = new File(outputPath);
		FileUtils.write(fout, sb.toString(), "UTF-8");
	}
	
	public void convertInlineTaggedFileToCRFFile(String inputPath, String outputPath) throws IOException {
		File f = new File(inputPath);
		List<String> lines = FileUtils.readLines(f, "UTF-8");
		
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			
			String crf_string = convertInlineTaggedToCRFFormat(line);
			sb.append(crf_string);
		}
		
		File fout = new File(outputPath);
		FileUtils.write(fout, sb.toString(), "UTF-8");
	}
	
	public String convertInlineTaggedToSentence(String str) {
		
		str = str.replaceAll(pattern_start.toString(), "");
		str = str.replaceAll(pattern_end.toString(), "");
		
		return str;
	}
	
	public String convertInlineTaggedToCRFFormat(String str) {
		
		String sen = convertInlineTaggedToSentence(str);
		
		List<Pair<String, String>> entityList = extractEntityList(str);
		
		return convertSentenceToCRFwithEntityList(sen, entityList);

	}
	
	private String convertSentenceToCRFwithEntityList(String sentence, List<Pair<String, String>> entityList) {
		
		StringBuilder sb = new StringBuilder();
		
		if (!Strings.isNullOrEmpty(sentence)) {
			String[] tokens = tokenizer.tokenize(sentence);
			for (String token : tokens) {

				// inefficient method lol
				String nerTag = "";
				for (Pair<String, String> entity : entityList) {
					
					if (entity.getValue().contains(token)) {
						nerTag = entity.getKey();
						break;
					}
				}
				
				if (!Strings.isNullOrEmpty(nerTag)) {
					sb.append(token + Constants.delimiter + nerTag);
				} else {
					sb.append(token + Constants.delimiter + Constants.emptyNER);
				}
				sb.append(Constants.newline);
			}
			// after every new sentence end, append new line
			sb.append(Constants.newline);
		}
		
		return sb.toString();
		
	}
	
	private List<Pair<String, String>> extractEntityList(String str) {
		
		List<Pair<String, String>> entityList = new ArrayList<Pair<String, String>>();
		Matcher m = pattern_entity.matcher(str);
		while (m.find()) {
			entityList.add(new Pair<String, String>(m.group(1), m.group(2)));
		}
		
		return entityList;
	}
}
