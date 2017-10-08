package com.emotibot.srl.evaluation;

import java.lang.reflect.Field;

public final class Metric {

	public static final String LABELED_PRECISION = "Labeled precision";
	public static final String LABELED_RECALL = "Labeled recall";
	public static final String LABELED_F1 = "Labeled F1";
	public static final String UNLABELED_PRECISION = "Unlabeled precision";
	public static final String UNLABELED_RECALL = "Unlabeled recall";
	public static final String UNLABELED_F1 = "Unlabeled F1";
	public static final String PROP_PRECISION = "Proposition precision";
	public static final String PROP_RECALL = "Proposition recall";
	public static final String PROP_F1 = "Proposition F1";
	public static final String EXACT_SEM_MATCH = "Exact semantic match";

	public static boolean isLegalMetric(String str) {
		Field[] fields = Metric.class.getDeclaredFields();
		for (Field f : fields) {
			try {
				Object val = f.get(null);
				if (str.equals(val.toString())) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		String str = "Labeled recal";
		System.out.println(Metric.isLegalMetric(str));
	}
}

//public enum Metric {
//	LABELED_PRECISION, LABELED_RECALL, LABELED_F1, 
//	UNLABELED_PRECISION, UNLABELED_RECALL, UNLABELED_F1, 
//	PROP_PRECISION, PROP_RECALL, PROP_F1, EXACT_SEM_MATCH;
//
//	@Override
//	public String toString() {
//		switch (this) {
//		case LABELED_PRECISION:
//			return "Labeled precision";
//		case LABELED_RECALL:
//			return "Labeled recall";
//		case LABELED_F1:
//			return "Labeled F1";
//		case UNLABELED_PRECISION:
//			return "Unlabeled precision";
//		case UNLABELED_RECALL:
//			return "Unlabeled recall";
//		case UNLABELED_F1:
//			return "Unlabeled F1";
//		case PROP_PRECISION:
//			return "Proposition precision";
//		case PROP_RECALL:
//			return "Proposition recall";
//		case PROP_F1:
//			return "Proposition F1";
//		case EXACT_SEM_MATCH:
//			return "Exact semantic match";
//		default:
//			return "UNDEFINEED";
//		}
//	}
//
//	public static boolean isLegalMetric(String str) {
//		for (Metric metric : Metric.values()) {
//			if (str.equals(metric.toString())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public static void main(String[] args) throws Exception {
//		String str = "Labeled recall";
//		System.out.println(Metric.isLegalMetric(str));
//	}
//
//}
