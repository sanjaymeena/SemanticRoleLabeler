package se.lth.cs.srl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;

import se.lth.cs.srl.io.AllCoNLL09Reader;
import se.lth.cs.srl.io.SentenceReader;
import se.lth.cs.srl.options.LearnOptions;
import se.lth.cs.srl.pipeline.Pipeline;
import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.util.BrownCluster;
import se.lth.cs.srl.util.Util;

/**
 * Version of Learner which is more robust and configurable to input data.
 * 
 * @author Sanjay
 *
 */
public class Learn2 {

	static DataFormatConverter dfc;
	public static LearnOptions learnOptions;

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		learnOptions = new LearnOptions(args);
		learn();
		System.out
				.println("Total time consumtion: " + Util.insertCommas(System.currentTimeMillis() - startTime) + "ms");
	}

	public static void learn() throws IOException {

		dfc = new DataFormatConverter();

		ZipOutputStream zos = new ZipOutputStream(
				new BufferedOutputStream(new FileOutputStream(learnOptions.modelFile1)));
		if (learnOptions.trainReranker) {
			new Reranker(learnOptions, zos);
		} else {
			BrownCluster bc = Learn2.learnOptions.brownClusterFile == null ? null
					: new BrownCluster(Learn2.learnOptions.brownClusterFile);
			SentenceReader reader = new AllCoNLL09Reader(learnOptions.inputCorpus);
			Pipeline.trainNewPipeline(reader, learnOptions.getFeatureFiles(), zos, bc);
		}
		zos.close();
	}

	public ArrayList<CoNLLSentence> readCoNLLFormatCorpus(String datapath, Format format) {

		ArrayList<CoNLLSentence> corpus = new ArrayList<CoNLLSentence>();
		
		File directory = new File(datapath);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(directory, SUFFIX, true);

		// iterate through all the files
		for (File file : files) {

			ArrayList<CoNLLSentence> sentenceList = dfc.readCoNLLFormatCorpus(file, format,true);
			corpus.addAll(sentenceList);
		}

		return corpus;
	}

}
