/**
 * 
 */
package com.emotibot.srl.pruner;

import java.util.Scanner;

import com.emotibot.srl.utilities.AnalysisUtilities;
import com.google.common.base.Strings;

import edu.stanford.nlp.trees.Tree;

/**
 * @author Sanjay
 *
 */
public class TestParseTreeCommandLine {
	TestParseTrees testParseTrees=new TestParseTrees();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		TestParseTreeCommandLine testTrees = new TestParseTreeCommandLine();
		testTrees.printMenu();
	}
	
	
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
				"|                                  Run Tree Operations                         							|\n");
		sb.append(
				"======================================================================================================== \n");

		sb.append(
				"                                   1. Run tree operations on defined file                                         \n");
		sb.append(
				"                                   2. Run tree operations on input sentence;                                         \n");
		sb.append(
				"                                   3. Run tree operations on input tree;                                         \n");
		sb.append("                                   4. quit                                        \n");

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
				input = readLine();
				if (!Strings.isNullOrEmpty(input)) {
					switch (selection) {

					case 1:

						break;

					case 2:
						testParseTrees.pruneSentence(input);
						break;

					case 3:
						Tree t=AnalysisUtilities.getInstance().readTreeFromString(input);
						t=testParseTrees.pruneTree(t);
						String output = AnalysisUtilities.getInstance().treeToString(t);
						output = output.replace(" ", "");
						System.out.println("pruned:  " + output);
						
						break;
					case 4:
						println("Bye");
						System.exit(0);
						break;

					default:
						println("Please select from the given choices");

					}
				} else {
					System.out.println("empty input");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
