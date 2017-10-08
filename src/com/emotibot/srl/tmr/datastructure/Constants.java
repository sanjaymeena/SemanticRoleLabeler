package com.emotibot.srl.tmr.datastructure;


public class Constants {

	public enum RelationEnum {
		/**
		 *
		 */
		semantic(1),
		/**
		 * 
		 */
		relation(2),
		/**
		 * 
		 */
		other(3);
		public int id;

		RelationEnum(int id) {
			this.id = id;
		}

		public int getValue() {
			return id;
		}
	}

	public enum Arg {
		/**
		 *
		 */
		predicate(1),
		/**
		 * 
		 */
		entity(2),
		/**
		 * 
		 */
		phrase(3);
		public int id;

		Arg(int id) {
			this.id = id;
		}

		public int getValue() {
			return id;
		}
	}

	public enum SemanticPredicate {
		/**
		 *
		 */
		A0(1),
		/**
		 * 
		 */
		A1(2),
		/**
		 * 
		 */
		A2(3),
		/**
		 * 
		 */
		A3(4),
		/**
		 * 
		 */
		A4(5),
		/**
		 * 
		 */
		AM_MNR(6),

		/**
		 * 
		 */
		AM_DIR(7),
		/**
		 * 
		 */
		AM_LOC(8),
		/**
		 * 
		 */
		AM_TMP(9),
		/**
		 * 
		 */
		AM_EXT(10),
		/**
		 * 
		 */
		AM_REC(11),
		/**
		 * 
		 */
		AM_PNC(12),
		/**
		 * 
		 */
		AM_CAU(13),
		/**
		 * 
		 */
		AM_DIS(14),
		/**
		 * 
		 */
		AM_MOD(15),
		/**
		 * 
		 */
		AM_NEG(16),
		/**
		 * 
		 */
		AM_COM(17),
		/**
		 * 
		 */
		AM_GOL(18),
		/**
		 * 
		 */
		AM_PRD(19),
		/**
		 * 
		 */
		AM_ADJ(17),
		/**
		 * 
		 */
		AM_SLC(18),
		/**
		 * 
		 */
		AM_LVB(19),
		/**
		 * 
		 */
		AM_ADV(20),
		/**
		 * 
		 */
		AM_PRP(21);
		public int id;

		SemanticPredicate(int id) {
			this.id = id;
		}

		@Override
		public String toString() {

			String string = "";
			if (this == A0) {
				string = "agent";
			} else if (this == A1) {
				string = "patient";
			} else if (this == A2) {
				// instrument, attribute, benefactive
				string = "attribute";
			} else if (this == A3) {
				// starting point, attribute, benefactive
				string = "attribute";
			} else if (this == A4) {
				string = "ending_point";
			} else if (this == AM_MNR) {
				string = "manner";
			} else if (this == AM_DIR) {
				string = "direction";
			} else if (this == AM_LOC) {
				string = "location";
			} else if (this == AM_TMP) {
				string = "time";
			} else if (this == AM_EXT) {
				string = "extent";
			} else if (this == AM_REC) {
				string = "reciprocal";
			} else if (this == AM_PNC) {
				string = "purpose";
			} else if (this == AM_CAU) {
				string = "cause";
			} else if (this == AM_DIS) {
				string = "discourse";
			} else if (this == AM_ADV) {
				string = "adverbial";
			} else if (this == AM_MOD) {
				string = "modal";
			} else if (this == AM_NEG) {
				string = "negation";
			} else if (this == AM_COM) {
				string = "comitative";
			} else if (this == AM_GOL) {
				string = "goal";
			} else if (this == AM_PRD) {
				string = "purpose";
			} else if (this == AM_ADJ) {
				string = "adjectival";
			} else if (this == AM_SLC) {
				string = "relative_clause";
			} else if (this == AM_LVB) {
				string = "light_verb";
			} else if (this == AM_ADV) {
				string = "adverbial";
			}
			else if (this == AM_PRP) {
				string = "purpose";
			}
			return string;
		}

		public int getValue() {
			return id;
		}
	}
	public  enum IDPrefix {
		/**
		 * Relation
		 */
		r(1),
		/**
		 * Predicate
		 */
		p(2),
		/**
		 * Entity
		 */
		e(3),
		/**
		 * Phrase
		 */
		ph(4);
		public int id;

		IDPrefix(int id) {
			this.id = id;
		}
		/**
		 * 
		 * @param currentId
		 */
		public static String generateRelationId(IDPrefix prefix, int currentId) {
			String id = "";

			switch (prefix) {
			case r:
				id = IDPrefix.r.name() + currentId;
				break;
			case p:
				id = IDPrefix.p.name() + currentId;
				break;
			case e:
				id = IDPrefix.e.name() + currentId;
				break;
			case ph:
				id = IDPrefix.ph.name() + currentId;

				break;
			default:
				break;
			}

			return id;

		}
	}
}
