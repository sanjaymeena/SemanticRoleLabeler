package com.emotibot.srl.test.frames;

import java.util.List;
import java.util.Scanner;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emotibot.srl.datastructures.SRLOptions;
import com.google.common.base.Strings;

public class WordnetCommandline {
	private final static Logger log = LoggerFactory.getLogger(WordnetCommandline.class);

	@SuppressWarnings("resource")
	private String readLine() {
		Scanner scanner = null;
		String doc = "";

		scanner = new Scanner(System.in);
		System.err.println("\nInput Text:");
		doc = scanner.nextLine();

		return doc;
	}

	private void println(Object obj) {
		// TODO Auto-generated method stub
		System.out.println(obj);
	}

	private void printMenu() {

		int selection = 0;

		StringBuilder sb = new StringBuilder();
		sb.append(
				"======================================================================================================== \n");
		sb.append(
				"|                                  Wordnet                           							|\n");
		sb.append(
				"======================================================================================================== \n");

		sb.append("                                   1. Only Word                                         \n");
		sb.append("                                   2. Word with POS                                        \n");

		sb.append("                                 3. quit                                                \n");

		println(sb);
		println("Select your choice..");

		
		while (true) {

			Scanner scanner = null;
			String input = "";
			
			try {
				scanner = new Scanner(System.in);
				while (!scanner.hasNextInt()) {
					println("Please select from the given choices");
					scanner.nextLine();
				}
				selection = scanner.nextInt();
			
				switch (selection) {

				case 1:
					input = readLine();

					if (!Strings.isNullOrEmpty(input)) {
						List<List<String>> result = OpenWordnet.instance().getSimilarWords(input, false);
						System.out.println(result);

					} else
						System.out.println("empty input");

					break;

				case 2:
					input = readLine();
					if (!Strings.isNullOrEmpty(input)) {
						List<List<String>> result = OpenWordnet.instance().getSimilarWords(input, true);
						System.out.println(result);

					} else
						System.out.println("empty input");
					break;

				case 3:
					println("Bye");
					System.exit(0);
					break;

				default:
					println("Please select from the given choices");

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("resources/emotibot-srl/config/log4j.properties");
		// 江苏养猪场“地下藏毒万吨”将中国的土壤污染问题再次推上风头浪尖，最佳环境报道奖的“最佳影响力奖”李显峰报道。

		WordnetCommandline wordnetCommandline = new WordnetCommandline();

		String doc2 = "我在台北工作，每天下班都去小巨蛋。";
		wordnetCommandline.printMenu();

	}
}
