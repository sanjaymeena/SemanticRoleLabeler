package se.lth.cs.srl.corpus;

import static com.emotibot.srl.format.Constants.TAB_PATTERN;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import mate.is2.data.SentenceData09;
import mate.is2.io.CONLLReader09;

public class Sentence extends ArrayList<Word> {

	private static final long serialVersionUID = 10;

	private List<Predicate> predicates;

	private Sentence() {
		Word BOS = new Word(this);
		super.add(BOS); // Add the root token
		predicates = new ArrayList<Predicate>();
	}

	public Sentence(SentenceData09 data, boolean skipTree) {
		this();
		for (int i = 0; i < data.forms.length; ++i) {
			Word nextWord = new Word(data.forms[i], data.plemmas[i], data.ppos[i], data.pfeats[i], this, i + 1);
			super.add(nextWord);
		}
		if (skipTree)
			return;
		for (int i = 0; i < data.forms.length; ++i) {
			Word curWord = super.get(i + 1);
			curWord.setHead(super.get(data.pheads[i]));
			curWord.setDeprel(data.plabels[i]);
		}
		this.buildDependencyTree();
	}

	public Sentence(String[] words, String[] lemmas, String[] tags, String[] morphs) {
		this();
		for (int i = 1; i < words.length; ++i) { // Skip root-tokens.
			Word nextWord = new Word(words[i], lemmas[i], tags[i], morphs[i], this, i);
			super.add(nextWord);
		}
	}

	private void addPredicate(Predicate pred) {
		predicates.add(pred);
	}

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public void buildDependencyTree() {
		for (int i = 1; i < size(); ++i) {
			Word curWord = get(i);
			curWord.setHead(get(curWord.getHeadId()));
			//System.out.println(curWord);
		}
	}

	public void buildSemanticTree() {
		for (int i = 0; i < predicates.size(); ++i) {
			Predicate pred = predicates.get(i);
			for (int j = 1; j < super.size(); ++j) {
				Word curWord = get(j);
				String arg = curWord.getArg(i);
				if (!arg.equals("_")){
					pred.addArgMap(curWord, arg);
				}
				
				else{
//					System.err.println("error in creating semantic tree for : ");
//					System.err.println(toString() );
				}
					
			}
		}
		for (Word w : this) // Free this memory as we no longer need this string
							// array
			w.clearArgArray();
	}

	public String toString() {
		String tag;
		StringBuilder ret = new StringBuilder();
		for (int i = 1; i < super.size(); ++i) {
			Word w = super.get(i);
			ret.append(i).append("\t").append(w.toString());
			if (!(w instanceof Predicate)) // If its not a predicate add the
											// FILLPRED and PRED cols
				ret.append("\t_\t_");
			for (int j = 0; j < predicates.size(); ++j) {
				ret.append("\t");
				Predicate pred = predicates.get(j);
				ret.append((tag = pred.getArgumentTag(w)) != null ? tag : "_");
			}
			ret.append("\n");
		}
		
		return ret.toString().trim();
	}

	public void makePredicate(int wordIndex) {
		Predicate p = new Predicate(super.get(wordIndex));
		super.set(wordIndex, p);
		addPredicate(p);
	}

	/*
	 * Functions used when interfacing with Bohnets parser These need to be
	 * fixed. Or rather the Sentence object should go altogether.
	 */
	public String[] getFormArray() {
		String[] ret = new String[this.size()];
		// ret[0]="<root>";
		for (int i = 0; i < this.size(); ++i)
			ret[i] = this.get(i).Form;
		return ret;
	}

	public String[] getFeats() {
		String[] ret = new String[this.size()];
		ret[0] = CONLLReader09.NO_TYPE;
		for (int i = 1; i < this.size(); ++i)
			ret[i] = this.get(i).getFeats();
		return ret;
	}

	public String[] getPOSArray() {
		String[] ret = new String[this.size()];
		// ret[0]="<root-POS>";
		for (int i = 0; i < this.size(); ++i)
			ret[i] = this.get(i).POS;
		return ret;
	}

	public void setHeadsAndDeprels(int[] heads, String[] deprels) {
		for (int i = 0; i < heads.length; ++i) {
			Word w = this.get(i + 1);
			w.setHead(this.get(heads[i]));
			w.setDeprel(deprels[i]);
		}
	}

	public static Sentence newDepsOnlySentence(String[] lines) {
		Sentence ret = new Sentence();
		Word nextWord;
		int ix = 1;
		for (String line : lines) {
			String[] cols = TAB_PATTERN.split(line, 13);
			nextWord = new Word(cols, ret, ix++);
			ret.add(nextWord);
		}
		ret.buildDependencyTree();
		return ret;

	}

	public static Sentence newSentence(String[] lines) {
		Sentence ret = new Sentence();
		Word nextWord;
		int ix = 1;

		//testFormatConverter(lines);
		
		if (lines.length > 1){

		 for(String line:lines){
		 String[] cols=TAB_PATTERN.split(line);
		 if(cols[12].equals("Y")){
		 Predicate pred=new Predicate(cols,ret,ix++);
		 ret.addPredicate(pred);
		 nextWord=pred;
		 } else {
		 nextWord=new Word(cols,ret,ix++);
		 }
		 ret.add(nextWord);
		 }
		 ret.buildDependencyTree();
		 ret.buildSemanticTree();

		}
		return ret;
	}

	private static void testFormatConverter(String[] lines) {
		// TODO Auto-generated method stub

		// int verb_column=13;
		// int init_arg_column=14;

		int verb_column = 10;
		int init_arg_column = 11;

		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

		int temp = TAB_PATTERN.split(lines[0]).length;
		int total_args_columns = temp - verb_column - 1;

		if (temp > 10) {
			// for (int i = 0; i < total_args_columns; i++) {
			// listOfLists.add(new ArrayList());
			// }

			for (int i = 0; i < temp; i++) {
				listOfLists.add(new ArrayList());
			}

			for (int k = 0; k < lines.length; k++) {
				String line = lines[k];
				String[] cols = TAB_PATTERN.split(line);

				for (int index = 0; index <= cols.length - 1; index++) {
					listOfLists.get(index).add(cols[index]);
				}

			}

		}
		System.out.println("total arg columns: " + listOfLists.size());

		String universal = "*";
		String universal_replace = "_";

		for (int i = init_arg_column; i < listOfLists.size(); i++) {

			Map<Integer, String> map = new LinkedHashMap<>();
			ArrayList<String> column = listOfLists.get(i);
			int size = column.size();
			int index = 0;
			int begin_index = -1;
			int end_index = -1;

			for (int j = 0; j < column.size(); j++) {

				String string = column.get(j);
				index++;
				// System.out.println(string);

				if (string.contains("(") && string.contains(")") && !string.contains("v")) {
					String new_s = string.replaceAll("[()*]", "").replace("and", "");
					map.put(index, new_s);
				}

				else if (string.contains("(") && !string.contains(")")) {
					String new_s = string.replaceAll("[()*]", "").replace("and", "");

					begin_index = index;
					map.put(begin_index, new_s);

				}

				else if (string.contains(")") && !string.contains("v")) {
					String new_s = string.replaceAll("[()*]", "").replace("and", "");
					end_index = index;
					String begin = map.get(begin_index);

					map.put(end_index, begin);
					map.put(begin_index, universal_replace);

				} else if (string.contains("(v*)")) {
					// String new_s = string.replaceAll("[(v)*]", "");
					// new_s="_";
					map.put(index, universal_replace);
				} else if (string.contains("*") && !string.contains("(") && !string.contains("v")
						&& !string.contains(")")) {
					map.put(index, universal_replace);
				} else {

				}

			}

			ArrayList<String> newString = new ArrayList<String>(map.values());
			listOfLists.set(i, newString);
		}

		String delimiter = "\t";
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < lines.length; i++) {

			for (int j = 0; j < temp; j++) {
				String c=listOfLists.get(j).get(i);
				
				if (j == temp - 1) {
					sb.append(c);
				} else {
					sb.append(c + delimiter);
				}
				
				
				int t=15;
			}
			sb.append("\n");
			int t=15;
		}

		

		int z = 10;

		System.out.println(sb.toString());
	}

	public static Sentence newSRLOnlySentence(String[] lines) {
		Sentence ret = new Sentence();
		Word nextWord;
		int ix = 1;
		for (String line : lines) {
			String[] cols = TAB_PATTERN.split(line, 13);
			if (cols[12].charAt(0) == 'Y') {
				Predicate pred = new Predicate(cols, ret, ix++);
				ret.addPredicate(pred);
				nextWord = pred;
			} else {
				nextWord = new Word(cols, ret, ix++);
			}
			ret.add(nextWord);
		}
		ret.buildDependencyTree();
		return ret;
	}

	public final Comparator<Word> wordComparator = new Comparator<Word>() {
		@Override
		public int compare(Word arg0, Word arg1) {
			return indexOf(arg0) - indexOf(arg1);
		}
	};
}
