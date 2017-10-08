package com.emotibot.srl.pruner;

public class Constants {
	/**
	 * Enumeration for Rules used in Sentence Pruning
	 * 
	 * @author sanjay_meena
	 *
	 */
	public enum PRUNERRULES {

		IJ_with_sister_PU(1),
		/**
		 * 
		 */
		IJ_with_sister_AD(2),
		/**
		 * 
		 */
		IJ_remove_anywhere(3),
		/**
		 * 
		 */
		phrase_with_removable_token_sister_PU(4),
		/**
		 * 
		 */
		tokens_remove_anywhere(5);

		/**
		 * Please do not change the toString values as they are used in unit
		 * tests
		 */
		@Override
		public String toString() {

			String string = "";
			if (this == IJ_with_sister_PU) {
				string = "IJ_with_sister_PU";
			} else if (this == IJ_with_sister_AD) {
				string = "IJ_with_sister_AD";
			} else if (this == IJ_remove_anywhere) {
				string = "IJ_remove_anywhere";
			} else if (this == phrase_with_removable_token_sister_PU) {
				string = "phrase_with_removable_token_sister_PU";
			} else if (this == tokens_remove_anywhere) {
				string = "tokens_remove_anywhere";
			}

			return string;
		}

		public int id;

		/**
		 * @return id
		 */
		public int getValue() {
			return id;
		}

		PRUNERRULES(int id) {
			this.id = id;
		}

		public static PRUNERRULES convertedFrom(String typeString) {
			if (typeString == null || typeString.length() < 15)
				return null;
			return PRUNERRULES.valueOf(typeString.substring(14));
		}
	}

}
