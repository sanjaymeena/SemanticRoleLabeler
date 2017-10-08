package se.lth.cs.srl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import mate.is2.lemmatizer.Lemmatizer;
import mate.is2.parser.Parser;
import mate.is2.tag.Tagger;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.options.Options;

public class BohnetHelper {

	public static Lemmatizer getLemmatizer(File modelFile) throws FileNotFoundException, IOException{
		String[] argsL={"-model",modelFile.toString()};
		//return new Lemmatizer(new is2.lemmatizer.Options(argsL));
		return new Lemmatizer(new mate.is2.lemmatizer.Options(argsL));
	}
	
	public static Tagger getTagger(File modelFile) {
		String[] argsT={"-model",modelFile.toString()};
		
		return new Tagger(new mate.is2.tag.Options(argsT));
	}
	
	public static mate.is2.mtag.Tagger getMTagger(File modelFile) throws IOException{
		String[] argsMT={"-model",modelFile.toString()};
		return new mate.is2.mtag.Tagger(new mate.is2.mtag.Options(argsMT));
	}
	
	public static Parser getParser(File modelFile){
		String[] argsDP={"-model",modelFile.toString(),"-cores",Integer.toString(Math.min(Options.cores,FullPipelineOptions.cores))};
		return new Parser(new mate.is2.parser.Options(argsDP));
	}
	
}
