package mate.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import mate.is2.data.SentenceData09;
import mate.is2.lemmatizer.Lemmatizer;
import mate.is2.lemmatizer.Options;

/**
 * @author Bernd Bohnet, 13.09.2010
 * 
 * Illustrates the application of some components: lemmatizer, tagger, and parser
 */
public class MorphTagger {

	
	/**
	 * How to lemmatize a sentences?
	 */
	public static void main(String[] args) throws IOException {

		
		// Create a data container for a sentence
		SentenceData09 i = new SentenceData09();

		if (args.length==1) { // input might be a sentence: "This is another test ." 
			StringTokenizer st = new StringTokenizer(args[0]);
			ArrayList<String> forms = new ArrayList<String>();
			
			forms.add("<root>");
			while(st.hasMoreTokens()) forms.add(st.nextToken());
			
			i.init(forms.toArray(new String[0]));
			
		} else {
			// provide a default sentence 
			i.init(new String[] {"<root>","Häuser","hat","ein","Umlaut","."});
		}

		//print the forms
		for (String l : i.forms) System.out.println("forms : "+l);

		// tell the lemmatizer the location of the model
		mate.is2.lemmatizer.Options optsLemmatizer = new Options(new String[] {"-model","models/lemma-ger.model"});

		// create a lemmatizer
		Lemmatizer lemmatizer = new Lemmatizer(optsLemmatizer.modelName);

		// lemmatize a sentence; the result is stored in the stenenceData09 i 
		lemmatizer.apply(i);

		
		// output the lemmata
		for (String l : i.plemmas) System.out.println("lemma : "+l);
			
		
		mate.is2.mtag.Options morphologicTaggerOptions = new  mate.is2.mtag.Options(new String[] {"-model","models/mtag-ger.model"});
		
		mate.is2.mtag.Tagger mt = new mate.is2.mtag.Tagger(morphologicTaggerOptions);
		
		try {
		
			
	//		SentenceData09 snt = mate.is2.mtag.Main.out(i.forms, lemmata);
			
			SentenceData09 snt = mt.apply(i);
			for(String f : snt.pfeats) System.out.println("feats "+f);
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	
}
