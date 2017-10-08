package se.lth.cs.srl.languages;

import java.io.IOException;

import mate.is2.lemmatizer.Lemmatizer;
import mate.is2.parser.Parser;
import mate.is2.tag.Tagger;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.tokenization.StanfordFrenchTokenizer;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;

public class French extends AbstractDummyLanguage{

	@Override
	public L getL() {
		return L.fre;
	}
	
	@Override
	public Preprocessor getPreprocessor(FullPipelineOptions options) throws IOException {
		Tokenizer tokenizer=options.loadPreprocessorWithTokenizer ? getTokenizer(options.tokenizer):null;
		Lemmatizer lemmatizer=options.lemmatizer==null?null:BohnetHelper.getLemmatizer(options.lemmatizer);
		Tagger tagger=options.tagger==null?null:BohnetHelper.getTagger(options.tagger);
		mate.is2.mtag.Tagger mtagger=options.morph==null?null:BohnetHelper.getMTagger(options.morph);
		Parser parser=options.parser==null?null:BohnetHelper.getParser(options.parser);
		Preprocessor pp=new Preprocessor(tokenizer, lemmatizer, tagger, mtagger, parser);
		return pp;
	}

	Tokenizer getDefaultTokenizer(){
		return new StanfordFrenchTokenizer();
	}

	@Override
	public String toLangNameString() {
		return "French";
	}

}
