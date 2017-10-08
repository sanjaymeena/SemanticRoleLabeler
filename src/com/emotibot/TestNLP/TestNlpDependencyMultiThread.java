package com.emotibot.TestNLP;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.emotibot.enlp.NLPService;
import com.emotibot.enlp.dependency.MateDependencyParser;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.test.SRLService;

import mate.is2.data.SentenceData09;
import mate.is2.parser.Parser;
import mate.is2.util.OptionsSuper;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.SemanticLabelerPipeLine;

public class TestNlpDependencyMultiThread {

	public TestNlpDependencyMultiThread() {

	}
	
	
	public void test1() {
		int num_threads = 4;
		String appid = "";
		String sentence = "买的好奇银装纸尿裤刚收到货怎么昨天都降价啦";
		NLPService.getDependencyParse(sentence);
		List<Thread> workers = new ArrayList<Thread>();

		// NLPService.init();

		// run thread to test dependency
		for (int i = 0; i < num_threads; i++) {
			Thread worker = new Thread() {
				private String query;

				Thread initalise(String _query) {
					query = _query;
					return this;
				}

				public void run() {
					for (int i = 0; i < 1; i++) {
						long start = new Date().getTime();
						try {
							// Case 1: original code
							MateDependencyParser mateParser = new MateDependencyParser();
							SentenceData09 instance = mateParser.getNLPSegmentation(appid, sentence);
							instance = mateParser.parse(instance);

							// Case 2: try to create dep parser directly
							// OptionsSuper options = new OptionsSuper();
							// String SRL_DEPENDENCY_TAGGER_MODEL =
							// "resources/emotibot-srl/models/test/ltp_dependency_parser_test.mdl";
							// options.modelName = SRL_DEPENDENCY_TAGGER_MODEL;
							//
							// System.out.println("Loading srl-mate dependency
							// parser information..");
							// Parser dependencyParser = new Parser(options);
							//
							// MateDependencyParser mateParser = new
							// MateDependencyParser();
							// SentenceData09 instance =
							// mateParser.getNLPSegmentation(appid, sentence);
							// instance = dependencyParser.apply(instance);

							System.out.println(instance);

						} catch (Exception e) {
						}
						long end = new Date().getTime();
						System.out.println(
								String.format("线程%s,顺序%d,时间%dms", Thread.currentThread().getName(), i, end - start));
					}
				}
			}.initalise(sentence);
			worker.start();
			workers.add(worker);
		}

		// collect thread
		for (int i = 0; i < num_threads; i++) {
			Thread worker = workers.get(i);
			try {
				worker.join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
	}

	public void test() {
		int num_threads = 2;
		String appid = "";
		String sentence = "买的好奇银装纸尿裤刚收到货怎么昨天都降价啦";
		List<Thread> workers = new ArrayList<Thread>();

		// NLPService.init();

		// run thread to test dependency
		for (int i = 0; i < num_threads; i++) {
			Thread worker = new Thread() {
				private String query;

				Thread initalise(String _query) {
					query = _query;
					return this;
				}

				public void run() {
					for (int i = 0; i < 1; i++) {
						long start = new Date().getTime();
						try {
							
							 //Case 2: try to create dep parser directly
							 OptionsSuper options = new OptionsSuper();
							 String SRL_DEPENDENCY_TAGGER_MODEL =
							 "resources/emotibot-srl/models/test/ltp_dependency_parser_test.mdl";
							 options.modelName = SRL_DEPENDENCY_TAGGER_MODEL;
							
							 System.out.println("Loading srl-mate dependency parser information..");
							 Parser dependencyParser = new Parser(options);
							//
							 MateDependencyParser mateParser = new
							 MateDependencyParser();
							 SentenceData09 instance = mateParser.getNLPSegmentation(appid, sentence);
							 instance = dependencyParser.apply(instance);

							System.out.println(instance);

						} catch (Exception e) {
						}
						long end = new Date().getTime();
						System.out.println(
								String.format("线程%s,顺序%d,时间%dms", Thread.currentThread().getName(), i, end - start));
					}
				}
			}.initalise(sentence);
			worker.start();
			workers.add(worker);
		}

		// collect thread
		for (int i = 0; i < num_threads; i++) {
			Thread worker = workers.get(i);
			try {
				worker.join();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
	}

	public static void main(String[] args) {
		TestNlpDependencyMultiThread tnlp = new TestNlpDependencyMultiThread();
		tnlp.test();
	}

}
