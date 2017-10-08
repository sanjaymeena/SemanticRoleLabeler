package com.emotibot.srl.regressiontest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import static com.emotibot.srl.format.Constants.NEWLINE_PATTERN;
import static com.emotibot.srl.format.Constants.TAB_PATTERN;
import static com.emotibot.srl.evaluation.Metric.*;
import com.emotibot.srl.datastructures.CoNLLSentence;
import com.emotibot.srl.datastructures.SRLJsonDataStructure;
import com.emotibot.srl.datastructures.SRLOptions;
import com.emotibot.srl.evaluation.EvaluationPair;
import com.emotibot.srl.format.DataFormatConverter;
import com.emotibot.srl.format.DataFormatConverter.Format;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import se.lth.cs.srl.SemanticLabelerPipeLine;

public class RegressionTester {

	Table<String, String, String> report;

	DataFormatConverter dfc;

	private static String evaluationScript = "evaluation/scripts/eval09.pl";

	private static String oldModelReportFilePath = "evaluation/report/SRL_0622_latest.csv";
	private static String newModelReportFilePath = "evaluation/report/SRL_0630_latest.csv";
	private static String comparisonFilePath = "evaluation/report/improvement_0630_latest.csv";
	
	private static String HEADER_FILE = "File";
	private static String HEADER_NUM = "Num of sentences";
	private static String[] metrics = {LABELED_PRECISION, LABELED_RECALL, LABELED_F1};
	private static String[] header = {HEADER_FILE, HEADER_NUM, LABELED_PRECISION, LABELED_RECALL, LABELED_F1};
	
	private File goodGoldStandardFile;
	private File goodSystemFile;

	public RegressionTester() {
		
		dfc = new DataFormatConverter();

		report = HashBasedTable.create();

		try {
			goodGoldStandardFile = File.createTempFile("goodGoldStandardFile", ".txt");
			goodSystemFile = File.createTempFile("goodSystemFile", ".txt");
			goodGoldStandardFile.deleteOnExit();
			goodSystemFile.deleteOnExit();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		RegressionTester rt = new RegressionTester();

		String goldFolderPath = "evaluation/gold_data";
		String predHITFolderPath = "evaluation/pred_HIT";
		String predTripleFolderPath = "evaluation/pred_triple";

		rt.doEvaluationByScript(goldFolderPath, predHITFolderPath, predTripleFolderPath);
		
		rt.compareTwoReports(oldModelReportFilePath, newModelReportFilePath, comparisonFilePath);

	}

	public void doEvaluationByScript(String goldFolderPath, String predHITFolderPath, String predTripleFolderPath) throws Exception {

		File goldFolder = new File(goldFolderPath);
		final String[] SUFFIX = { "txt" };
		Collection<File> files = FileUtils.listFiles(goldFolder, SUFFIX, true);

		for (File file : files) {
			String gold_HIT_path = file.getAbsolutePath();
			String pred_HIT_path = file.getAbsolutePath().replaceAll(goldFolderPath, predHITFolderPath);
			String pred_Triple_path = file.getAbsolutePath().replaceAll(goldFolderPath, predTripleFolderPath);

			// Step 1. Based on tokenization of every gold sentence, generate
			// the model output
			// Workaround: we need to keep POS and DEP the same between gold and
			// pred files
			// POS and DEP of the gold file are used to replace those in pred
			// file
			generateModelPredFile(gold_HIT_path, pred_HIT_path, pred_Triple_path);

			// Step 2. Use script to compare two files and get the score map.
			String finalGoldFilePath = goodGoldStandardFile.getAbsolutePath();
			String finalSysFilePath = goodSystemFile.getAbsolutePath();
			Map<String, String> metricScoreMap = getScoreMapByScript(finalGoldFilePath, finalSysFilePath);
			for (String metric : metrics) {
				report.put(file.getName(), metric, metricScoreMap.get(metric));
			}

		}
		printReports();
		
		writeReportCSV(newModelReportFilePath, report);
		
	}

	/**
	 * Run model on the data
	 * 
	 * @throws Exception
	 */
	private void generateModelPredFile(String gold_HIT_path, String pred_HIT_path, String pred_Triple_path) throws Exception {

		// Read from gold HIT file and use token list to generate system
		// CoNLLSentences
		File file = new File(gold_HIT_path);

		List<CoNLLSentence> srlData = dfc.readCoNLLFormatCorpus(file, Format.HIT, true);
		System.out.printf("Read %d sentences from %s\n", srlData.size(), file.getName());
		for (CoNLLSentence conllSentence : srlData) {
			dfc.convertHITtoCONLL2009(conllSentence);
		}

		List<EvaluationPair> evalPairList = new ArrayList<EvaluationPair>();

		int counter = 0;
		for (CoNLLSentence coNLLSentence : srlData) {
			boolean badCase = false;
			for (String token : coNLLSentence.getTokenList()) {
				if (Strings.isNullOrEmpty(token.trim())) {
					badCase = true;
				} else if (token.contains(" ")) {
					badCase = true;
				}
			}
			if (!badCase) {
				counter++;

				CoNLLSentence systemSentence = new CoNLLSentence();
				systemSentence.setSentence(coNLLSentence.getSentence());

				EvaluationPair evPair = new EvaluationPair(counter, coNLLSentence, systemSentence);
				evalPairList.add(evPair);	
			}
			
		}

		SRLOptions options = new SRLOptions();
		options.model = 2;
		options.manualMode = false;
		options.produceHITFormat = true;

		options.usePOSFromNLP = true;
		options.useDEPFromNLP = false;
		
		List<SRLJsonDataStructure> jsonDSList = new ArrayList<SRLJsonDataStructure>();
		// First run the srl parser and add srl output
		for (EvaluationPair evaluationPair : evalPairList) {
			CoNLLSentence conllSentence = evaluationPair.getGoldSentence();
			CoNLLSentence systemSentence = evaluationPair.getSystemSentence();

			List<String> tokensList = conllSentence.getTokenList();
			String[] tokens = tokensList.toArray(new String[tokensList.size()]);

			SRLJsonDataStructure res = SemanticLabelerPipeLine.getChineseInstance(options)
					.performSRLUsingHybridPreprocessor(tokens, options);

			jsonDSList.add(res);
			// After get the SRL output, we have to keep their POS and DEP the
			// same so that we can run the script
			// POS and DEP of the gold file are used to replace those in pred
			// file
			String newSysCoNLL;
			if (!Strings.isNullOrEmpty(conllSentence.getCoNLLSentence())) {
				newSysCoNLL = genNewConllString(conllSentence.getCoNLLSentence(), res.getConllSentence());
			} else {
				newSysCoNLL = genNewConllString(conllSentence.getHITSentence(), res.getConllSentence());
				conllSentence.setCoNLLSentence(conllSentence.getHITSentence());
			}

			String[] lines = (NEWLINE_PATTERN.split(newSysCoNLL));

			systemSentence.setLines(lines);
			systemSentence.setCoNLLSentence(newSysCoNLL);
			
			// For human check. The POS and DEP are from model
			systemSentence.setHITSentence(res.getHitSentence());

		}

		// The tokenization, POS and DEP are same. Thus, we just write the data
		// into files.
		List<CoNLLSentence> goodGoldSentences = new ArrayList<CoNLLSentence>();
		List<CoNLLSentence> goodSystemSentences = new ArrayList<CoNLLSentence>();
		for (EvaluationPair evaluationPair : evalPairList) {
			CoNLLSentence goldSentence = evaluationPair.getGoldSentence();
			CoNLLSentence systemSentence = evaluationPair.getSystemSentence();
			
			goodGoldSentences.add(goldSentence);
			goodSystemSentences.add(systemSentence);
		}

		writeData(goodGoldStandardFile.getAbsolutePath(), goodGoldSentences, Format.CONLL);
		writeData(goodSystemFile.getAbsolutePath(), goodSystemSentences, Format.CONLL);
		
		writeData(pred_HIT_path, goodSystemSentences, Format.HIT);
		writeTripleData(pred_Triple_path, jsonDSList);

		String patternName = file.getName();
		report.put(patternName, HEADER_NUM, String.valueOf(goodGoldSentences.size()));
	}

	private Map<String, String> getScoreMapByScript(String gold_file_path, String pred_file_path) {
	
		// perl eval09.pl -q -g srl-emotibot-train_conll.txt -s
		// srl-emotibot-train_conll.txt

		File f_script = new File(evaluationScript);
		File f_gold = new File(gold_file_path);
		File f_pred = new File(pred_file_path);

		StringBuilder sb = new StringBuilder();
		sb.append("perl " + "\"" + f_script.getAbsolutePath() + "\"");
		sb.append(" -g " + "\"" + f_gold.getAbsolutePath() + "\"");
		sb.append(" -s " + "\"" + f_pred.getAbsolutePath() + "\"");
		sb.append(" | grep -A 10 \"  SEMANTIC SCORES: \"");

		List<String> cmds = new ArrayList<String>();

		cmds.add("/bin/sh");
		cmds.add("-c");
		cmds.add(sb.toString());

		String[] cmd = cmds.toArray(new String[cmds.size()]);

		Map<String, String> map = new HashMap<String, String>();
		
		try {

			Pattern score_pattern = Pattern.compile("\\d*\\.\\d+");

			Process p = Runtime.getRuntime().exec(cmd);

			BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = output.readLine()) != null) {
				String[] line_fragments = line.split(":");
				String metric_name = line_fragments[0].trim();
				Matcher m = score_pattern.matcher(line);
				// Weak parsing... XD
				while (m.find()) {
					if (Arrays.asList(metrics).contains(metric_name)) {
						map.put(metric_name, m.group());
					}
				}
			}
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		 return map;
	}

	private void writeData(String outputFile, List<CoNLLSentence> data, Format format) {
		StringBuilder sb = new StringBuilder();
		File file = new File(outputFile);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int counter = 0;
		for (CoNLLSentence coNLLSentence : data) {
			String sentence = "";
			switch (format) {
			case CONLL:
				sentence = coNLLSentence.getCoNLLSentence();
				break;
			case HIT:
				sentence = coNLLSentence.getHITSentence();
				break;
			default:
				System.out.println("Should not be here");
				break;
			}

			if (!Strings.isNullOrEmpty(sentence)) {
				sb.append(sentence + "\n");
				counter++;
			}
		}

		try {
			FileUtils.write(file, sb.toString());
			System.out.println("wrote to " + file.toString() + " total " + counter + " sentence written");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeTripleData(String outputFile, List<SRLJsonDataStructure> data) {
		StringBuilder sb = new StringBuilder();
		File file = new File(outputFile);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int counter = 0;
		for (SRLJsonDataStructure srlDS : data) {

			if (!Strings.isNullOrEmpty(srlDS.toString())) {
				sb.append(srlDS.toString() + "\n");
				counter++;
			}
		}

		try {
			FileUtils.write(file, sb.toString());
			System.out.println("wrote to " + file.toString() + " total " + counter + " sentence written");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void compareTwoReports(String oldReportPath, String newReportPath, String compFilePath) throws IOException {
		
		Table<String, String, String> oldTable = readReportCSV(oldReportPath);
		Table<String, String, String> newTable = readReportCSV(newReportPath);
		Table<String, String, String> compTable = HashBasedTable.create();
		
		String[] fileNames = newTable.rowKeySet().toArray(new String[0]);
		Arrays.sort(fileNames);
		
		String NA = "N/A";
		// To handle only metrics
		Map<String, Map<String, String>> newTableRowMap = newTable.rowMap();
		for (String fileName : fileNames) {
			Map<String, String> m = newTableRowMap.get(fileName);
			compTable.put(fileName, HEADER_NUM, m.get(HEADER_NUM));
			for (String metric : metrics) {
				String oldEntry = oldTable.get(fileName, metric);
				if (!Strings.isNullOrEmpty(oldEntry)) {
					 Float diff = Float.parseFloat(newTable.get(fileName, metric)) - Float.parseFloat(oldEntry);
					 compTable.put(fileName, metric, String.format("%.2f", diff));
				} else {
					compTable.put(fileName, metric, NA);
				}
			}
		}

		writeReportCSV(compFilePath, compTable);
	}
	
	private Table<String, String, String> readReportCSV(String inputPath) throws IOException {
		
		Table<String, String, String> table = HashBasedTable.create();
		
		FileReader reader = new FileReader(inputPath);
		// CSVParser parser = new CSVParser(in, CSVFormat.RFC4180.withFirstRecordAsHeader());
		// String[] headers = parser.getHeaderMap().keySet().toArray(new String[0]);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
		for (CSVRecord record : records) {
			table.put(record.get(HEADER_FILE), HEADER_NUM, record.get(HEADER_NUM));
			for (String metric : metrics) {
				table.put(record.get(HEADER_FILE), metric, record.get(metric));
			}
		}
		return table;
	}
	
	private void writeReportCSV(String outputPath, Table<String, String, String> table) throws IOException {
		
		File file = new File(outputPath);
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}
		
		CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath), CSVFormat.RFC4180);
		printer.printRecord(header);
		
		String[] fileNames = table.rowKeySet().toArray(new String[0]);
		Arrays.sort(fileNames);
		
		for (String fileName : fileNames) {
			String[] row = {fileName, 
							table.get(fileName, HEADER_NUM),
							table.get(fileName, LABELED_PRECISION), 
							table.get(fileName, LABELED_RECALL), 
							table.get(fileName, LABELED_F1)};
			printer.printRecord(row);
		}
		printer.flush();
		printer.close();

	}

	private void printReports() {

		String[] fileNames = report.rowKeySet().toArray(new String[0]);
		Arrays.sort(fileNames);
		
		for (String fileName : fileNames) {
			System.out.println(fileName);
			System.out.printf("There %s sentences\n", report.get(fileName, HEADER_NUM));
			for (String metric : metrics) {
				System.out.printf("%-22s: %s\n", metric, report.get(fileName, metric));
			}
		}
	}

	private String genNewConllString(String standard, String srl) {
		
		String[] standard_lines = (NEWLINE_PATTERN.split(standard));
		String[] srl_lines = (NEWLINE_PATTERN.split(srl));

		for (int row_idx = 0; row_idx < srl_lines.length; row_idx++) {
			String[] cols = TAB_PATTERN.split(srl_lines[row_idx]);
			String[] sta_cols = TAB_PATTERN.split(standard_lines[row_idx]);
			for (int col_idx = 0; col_idx < 12; col_idx++) {
				cols[col_idx] = sta_cols[col_idx];
			}
			srl_lines[row_idx] = String.join("\t", cols);
		}
		String ret = String.join("\n", srl_lines);
		ret = ret.concat("\n");

		return ret;
	}
	
	
}
