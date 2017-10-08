package com.emotibot.srl.server;

import com.google.common.collect.ImmutableMultimap;

public class Constants {

	public static final String VERSION = "2.3.7";

	public static final int english_srl_port = 5558;
	public static final int chinese_srl_port = 5559;
	public static final String host = "127.0.0.1";

	public static final int chineseStanfordParserServerPort = 5555;
	public static final String chineseStanfordParseGrammarFile = "resources/stanford/stanfordparser/models/lexparser/zh/chinesePCFG.ser.gz";
	public static final int chineseStanfordParserMaxLength = 70;
	public static final int chineseStanfordParserMinLength = 3;

	// SRL Server Model

	public static final String CHINESE_LEMMATIZER = "resources/semantic_role_labeling/models/chinese";

	public static final String POS_TAGGER = "resources/semantic_role_labeling/models/chinese/CoNLL2009-ST-Chinese-ALL.anna-3.3.postagger.model";

	public static final String POS_TAGGER_TEST = "resources/emotibot-srl/models/test/ltp_pos_tagger.mdl";
	public static final String CHINESE_DEPENDENCY_PARSER = "resources/emotibot-srl/models/test/ltp_dependency_parser_test.mdl";
	public static final String CHINESE_SRL_MODEL_1 = "resources/semantic_role_labeling/models/chinese/CoNLL2009-ST-Chinese-ALL.anna-3.3.srl-4.1.srl.model";
	public static final String CHINESE_SRL_MODEL_2 = "resources/emotibot-srl/models/emotibot.srl.mdl";

	// srl word list
	public static String srl_word_list_directory = "resources/emotibot-srl/word_lists";
	public static ImmutableMultimap<String, String> instanceToSRLClassMultimap;

	// srl bad cases
	public static String SRL_BAD_CASES_DIRECTORY = "resources/emotibot-srl/srl_bad_cases";
	// srl bad cases
	// public static String
	// SRL_BAD_CASES_DIRECTORY="resources/emotibot-srl/srl_bad_cases";

	// main dirs
	public static String CONLL_TRAINING_DATA_DIR = "resources/emotibot-srl/srl_training_data/conll_format";
	public static String HIT_TRAINING_DATA_DIR = "resources/emotibot-srl/srl_training_data/hit_format";

	// Final Training File dir
	public static String FINAL_TRAINING_DATA_FILE = "resources/emotibot-srl/srl_training_data/singlefile/srl_train.txt";

	// Final output file
	public static String SRL_MODEL_FILE = "resources/emotibot-srl/models/emotibot.srl.mdl";

	//
	public static String SENTENCES_DIR = "resources/emotibot-srl/srl_training_data/sentences";

	/**
	 * ENUM for SRL Relations
	 */
	public enum SRLRELATIONS {
		/**
		   * 
		   */
		MOD(1);

		public int id;

		SRLRELATIONS(int id) {
			this.id = id;
		}

		/**
		 * Please do not change the toString values as they are used in unit
		 * tests
		 */
		@Override
		public String toString() {

			String string = "";
			if (this == MOD) {
				string = "MOD";
			}

			return string;
		}

		/**
		 * @return id
		 */
		public int getValue() {
			return id;
		}
	}
	
	/**
	 * ENUM for SRL Relations
	 */
	public enum DEPRELATIONS {
		/**
		   * 
		   */
		HED(1);

		public int id;

		DEPRELATIONS(int id) {
			this.id = id;
		}

		/**
		 * Please do not change the toString values as they are used in unit
		 * tests
		 */
		@Override
		public String toString() {

			String string = "";
			if (this == HED) {
				string = "HED";
			}

			return string;
		}

		/**
		 * @return id
		 */
		public int getValue() {
			return id;
		}
	}
}
