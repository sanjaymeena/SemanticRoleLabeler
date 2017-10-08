package se.lth.cs.srl.preprocessor.tokenization;

public interface Tokenizer {

	/**
	 * Tokenize a sentence. The returned array contains a root-token
	 * 
	 * @param sentence The sentence to tokenize
	 * @return a root token, followed by the forms
	 */
	public abstract String[] tokenize(String sentence);
	
	// tokenizer with option to add dummy variable as first token.
	public abstract String[] tokenize(String sentence,boolean addDummy);
	
	// tokenizer with special case for case_type . For e.g. VIP data has a special tokenization
	public abstract String[] tokenize(String sentence,String case_type);
	
}
