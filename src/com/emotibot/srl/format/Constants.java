package com.emotibot.srl.format;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Constants {
	
	public static final String ENCODING="UTF-8";
	public static final Pattern NEWLINE_PATTERN = Pattern.compile("\n");
	public static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
	public static final Pattern TAB_PATTERN = Pattern.compile("\t");
	public static final String NEW_LINE = System.getProperty("line.separator");
	
	public static final Pattern VERB_SENSE_PATTERN = Pattern.compile(".+\\.\\d{2}$");

	public static int conllformat_verb_column_no = 13;
	public static int conllformat_first_arg_column_no = 14;
	public static int conllformat_form_column_no = 1;
	public static int conllformat_pos_column_no = 4;
	
	public static int conllformat_ppos_column_no = 5;
	
	public static int conllformat_head_column_no = 8;
	public static int conllformat_phead_column_no = 9;
	public static int conllformat_deprel_column_no = 10;
	public static int conllformat_pdeprel_column_no = 11;

	public static final List<String> LEGAL_SRL_TAGS = Arrays.asList("v", "A0", "A1", "A2", "A3", "A4", "A5", "ATA", "ATP",
												   					"BNF", "CND", "DIR", "EXT", "FRQ", "LOC", "MNR", "PRP",
												   					"TMP", "CAU", "NEG", "CTS", "TPC", "ADV", "MOD"); 
	
	/**
	 * Cases Enum
	 */
	public enum ErrorCases {
		/**
		   * 
		   */
		HUMAN(1),
		/**
		   * 
		   */
		GOOD(2),
		/**
		   * 
		   */
		NLP_ERRORS(3),
		/**
		   * 
		   */
		NO_NLP_TAGS(4),
		/**
		   * 
		   */
		LEGACY_TAGS(5);

		public int id;

		ErrorCases(int id) {
			this.id = id;
		}

		/**
		 * Please do not change the toString values as they are used in unit
		 * tests
		 */
		@Override
		public String toString() {

			String string = "";
			if (this == HUMAN) {
				string = "human_errors";
			} else if (this == GOOD) {
				string = "good";
			} else if (this == NLP_ERRORS) {
				string = "nlp_errors";

			} else if (this == NO_NLP_TAGS) {
				string = "no_nlp_tags";
			} else if (this == LEGACY_TAGS) {
				string = "missing_first_token";
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
