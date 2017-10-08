package se.lth.cs.srl.http.whatswrongglue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.googlecode.whatswrong.Edge;
import com.googlecode.whatswrong.NLPCanvasRenderer;
import com.googlecode.whatswrong.NLPInstance;
import com.googlecode.whatswrong.SingleSentenceRenderer;
import com.googlecode.whatswrong.Token;
import com.googlecode.whatswrong.TokenProperty;

import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.io.AllCoNLL09Reader;
import se.lth.cs.srl.io.SentenceReader;

public class WhatsWrongHelper {

	private static NLPCanvasRenderer renderer = new SingleSentenceRenderer();
	
	private static final TokenProperty FORM = new TokenProperty("Form", 0);
	private static final TokenProperty INDEX = new TokenProperty("Index", 1);
	//private static final TokenProperty LEMMA = new TokenProperty("Lemma", 1);
	private static final TokenProperty POS = new TokenProperty("POS", 2);
	// private static final TokenProperty FEATS=new TokenProperty("Feats", 3);

	public static ByteArrayOutputStream renderJPG(NLPInstance instance, double scaleFactor) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Export.exportToJPG(baos, renderer, instance, scaleFactor);
		return baos;
	}

	public static ByteArrayOutputStream renderPNG(NLPInstance instance, double scaleFactor) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Export.exportToPNG(baos, renderer, instance, scaleFactor);
		return baos;
	}

	@SuppressWarnings("unchecked")
	public static NLPInstance getNLPInstance(Sentence s) {
		List<Token> tokens = new ArrayList<Token>();
		List<Edge> edges = new ArrayList<Edge>();
		for (int i = 0, size = s.size(); i < size; ++i) {
			tokens.add(new Token(i));
		}

		// Don't render feats -- it makes things to wide. Just uncomment the
		// feats lines below to get it back.

		tokens.get(0).addProperty(FORM, "<root>");
		for (int i = 1, size = s.size(); i < size; ++i) {
			Word w = s.get(i);
			Token t = tokens.get(i);
			String form = w.getForm();
			String lemma = w.getLemma();
			String pos = w.getPOS();
			String index=(Integer.toString(w.getIdx()));
			// String feats=w.getFeats();
			if (form == null || form.equals(""))
				form = "!";
			if (lemma == null || lemma.equals(""))
				lemma = "_";
			if (pos == null || pos.equals(""))
				pos = "_";
			// if(feats==null || feats.equals(""))
			// feats="_";
			t.addProperty(FORM, form);
			t.addProperty(POS, pos);
			t.addProperty(INDEX, index);
			// t.addProperty(FEATS, feats);
			int tokenHead = w.getHeadId();
			// if(tokenHead>=0) //XXX Note that root has head -1, but we skip
			// this now anyway.
			edges.add(new Edge(tokens.get(tokenHead), t, w.getDeprel(), "dep"));
		}

		NLPInstance instance = new NLPInstance(tokens, edges, NLPInstance.RenderType.single, Collections.EMPTY_LIST);
		return instance;
	}

	// This could be worth implementing at some point...
	// public static NLPInstance getNLPInstance(SentenceData09 s){
	// throw new Error("Not implemented!");
	// //return null;
	// }

	/**
	 * Encodes the byte array into base64 string
	 *
	 * @param imageByteArray
	 *            - byte array
	 * @return String a {@link java.lang.String}
	 */
	public static String encodeImage(byte[] imageByteArray) {
		return Base64.getEncoder().encodeToString(imageByteArray);
	}

	/**
	 * Decodes the base64 string into byte array
	 *
	 * @param imageDataString
	 *            - a {@link java.lang.String}
	 * @return byte array
	 */
	public static byte[] decodeImage(String imageDataString) {
		return Base64.getDecoder().decode(imageDataString);
	}

	/**
	 * Returns the image encoded in the form of a string. 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static String getDependencyParserImageString(Sentence s) throws IOException {

		NLPInstance instance = getNLPInstance(s);
		ByteArrayOutputStream baos = WhatsWrongHelper.renderPNG(instance, 1);

		String str = encodeImage(baos.toByteArray());

		return str;
	}

	public static void main(String[] args) throws IOException {
		SentenceReader reader = new AllCoNLL09Reader(new File("data/temp.txt"));
		Iterator<Sentence> it = reader.iterator();
		Sentence s = it.next();
		NLPInstance instance = getNLPInstance(s);
		ByteArrayOutputStream baos = WhatsWrongHelper.renderPNG(instance, 1);

		String str = encodeImage(baos.toByteArray());
		byte[] img = decodeImage(str);

		FileOutputStream fos = new FileOutputStream("whatswronghelper.png");
		// fos.write(baos.toByteArray());
		fos.write(img);
		fos.flush();
		fos.close();
	}
}
