package se.lth.cs.srl.languages;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import mate.is2.lemmatizer.Lemmatizer;
import mate.is2.parser.Parser;
import mate.is2.tag.Tagger;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.tokenization.EmotibotTokenizer;
import se.lth.cs.srl.preprocessor.tokenization.OpenNLPToolsTokenizerWrapper;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;


public abstract class Language {

	public enum L { cat, chi, cze, eng, ger, jap, spa, swe,fre,nul }
	
	private static Language language;
	static final Pattern BAR_PATTERN=Pattern.compile("\\|");
	

	public abstract String toLangNameString();
	
	public static Language getLanguage(){
		return language;
	}
	
	public static String getLsString(){
		return "chi, eng, ger";
	}
	
	public static Language setLanguage(L l){
		switch(l){
		case chi: language=new Chinese(); break;
		case eng: language=new English(); break;
		case ger: language=new German(); break;
		case swe: language=new Swedish(); break;
		case spa: language=new Spanish(); break;
		case fre: language=new French(); break;
		case nul: language=new NullLanguage(); break;
		default: throw new IllegalArgumentException("Unknown language: '"+l+"'");
		}
		return language;
	}

	public Pattern getFeatSplitPattern() {
		return BAR_PATTERN;
	} 
	public abstract String getDefaultSense(Predicate pred);
	public abstract String getCoreArgumentLabelSequence(Predicate pred,Map<Word, String> proposition);
	public abstract L getL();
	public abstract String getLexiconURL(Predicate pred);
	
	public Preprocessor getPreprocessor(FullPipelineOptions options) throws IOException {
		Tokenizer tokenizer=(options.loadPreprocessorWithTokenizer ? getTokenizer(options.tokenizer): null);
		Lemmatizer lemmatizer=getLemmatizer(options.lemmatizer);
		Tagger tagger=options.tagger==null?null:BohnetHelper.getTagger(options.tagger);
		mate.is2.mtag.Tagger mtagger=options.morph==null?null:BohnetHelper.getMTagger(options.morph);
		Parser parser=options.parser==null?null:BohnetHelper.getParser(options.parser);
		Preprocessor pp=new Preprocessor(tokenizer, lemmatizer, tagger, mtagger, parser);
		return pp;
	}
	
	public abstract String verifyLanguageSpecificModelFiles(FullPipelineOptions options);

	Tokenizer getDefaultTokenizer(){
		//return new WhiteSpaceTokenizer();
		return new EmotibotTokenizer();
	}
	public Tokenizer getTokenizer(File tokenModelFile) throws IOException{
		if(tokenModelFile==null)
			return getDefaultTokenizer();
		else
			return getTokenizerFromModelFile(tokenModelFile);
	}

	Tokenizer getTokenizerFromModelFile(File tokenModelFile) throws IOException {
		return OpenNLPToolsTokenizerWrapper.loadOpenNLPTokenizer(tokenModelFile);
	}
	
	Lemmatizer getLemmatizer(File lemmaModelFile) throws IOException{
		if(lemmaModelFile==null)
			return null;
		return BohnetHelper.getLemmatizer(lemmaModelFile);
	}

}
