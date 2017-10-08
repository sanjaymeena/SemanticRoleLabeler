package com.emotibot.srl.test.sentencenet;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emotibot.srl.pruner.TestParseTrees;
import com.google.common.base.Strings;

public class TestQQData {
	private final static Logger log = LoggerFactory.getLogger(TestParseTrees.class);

	StringBuilder globalQueryBuilder;
	Set<String> alreadyDeclared = new LinkedHashSet<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("resources/emotibot-srl/config/log4j.properties");
		TestQQData test = new TestQQData();

		test.createDataQuery();
	}

	private void createDataQuery() throws IOException {
		// TODO Auto-generated method stub
		String f = "data2/sentencenet/qq/test/vip_cat_data.txt";
		File file = new File(f);

		Set<String> level1 = new LinkedHashSet<>();
		Set<String> level2 = new LinkedHashSet<>();
		Set<String> level3 = new LinkedHashSet<>();
		Set<String> level4 = new LinkedHashSet<>();

		List<String> lines = FileUtils.readLines(file, "UTF-8");
		Set<String> datarowSet = new LinkedHashSet<>();
		int counter = 0;

		for (String string : lines) {
			if (!Strings.isNullOrEmpty(string)) {
				datarowSet.add(string);

				String[] vals = string.split("\t");
				if (!Strings.isNullOrEmpty(vals[0])) {
					level1.add(vals[0]);
				}
				if (!Strings.isNullOrEmpty(vals[1])) {
					level2.add(vals[1]);
				}
				if (!Strings.isNullOrEmpty(vals[2])) {
					level3.add(vals[2]);
				}
				if (!Strings.isNullOrEmpty(vals[3])) {
					level4.add(vals[3]);
				}

			}
		}

		for (String string : datarowSet) {
			System.out.println(string);
		}
		
		log.info("data row size :" + datarowSet.size());
		System.out.println("data row size :" + datarowSet.size());
		System.out.println("level1 size :" + level1.size());
		System.out.println("level2 size :" + level2.size());
		System.out.println("level3 size :" + level3.size());
		System.out.println("level4 size :" + level4.size());

		String datarow_query = createDataQueryhelper(datarowSet);

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE  ");
		sb.append("\n");
		sb.append("(" + "VIPQQ" + ":VIPQQ" + " {type:'ontology'" + ", text:" + "'" + "VIP QQ Ontology" + "'"
				+ ", domain: " + "'" + "e-commerce" + "'" + "} " + ")");

		for (String string : level1) {
			string = removeDelimiters(string);

			if (!alreadyDeclared.contains(string)) {
				sb.append("," + "\n");
				sb.append("(" + string + ":CLASS" + " {type:'class'" + ", text:" + "'" + string + "'" + ", level: 'l1'" +"} " + ")");
				sb.append("," + "\n");
				sb.append("(" + string + ")" + "-" + "[:is_A]" + " -> (VIPQQ)");
				
				alreadyDeclared.add(string);
			}

		}

		for (String string : level2) {
			string = removeDelimiters(string);
			if (!alreadyDeclared.contains(string)) {
				sb.append("," + "\n");
				sb.append("(" + string + ":CLASS" + " {type:'class'" + ", text:" + "'" + string + "'" +", level: 'l2'" + "} " + ")");
				alreadyDeclared.add(string);
			}

			

		}
		for (String string : level3) {
			string = removeDelimiters(string);
			if (!alreadyDeclared.contains(string)) {
				sb.append("," + "\n");
				sb.append("(" + string + ":CLASS" + " {type:'class'" + ", text:" + "'" + string + "'" +", level: 'l3'" + "} " + ")");
				alreadyDeclared.add(string);

			}

			
		}
		for (String string : level4) {
			string = removeDelimiters(string);
			if (!alreadyDeclared.contains(string)) {
				sb.append("," + "\n");
				sb.append("(" + string + ":CLASS" + " {type:'class'" + ", text:" + "'" + string + "'" +  ", level: 'l4'" +"} " + ")");
				alreadyDeclared.add(string);

			}

			
		}

		sb.append(datarow_query);
		System.out.println(sb.toString());
		String out = "data2/sentencenet/data/cypher/qq_cypher.txt";
		FileUtils.writeStringToFile(new File(out), sb.toString());

	}

	/**
	 * 
	 * @param datarowSet
	 * @return
	 */
	private String createDataQueryhelper(Set<String> datarowSet) {
		StringBuilder sb = new StringBuilder();

		Set<String> builderSet = new LinkedHashSet<>();

		for (String string : datarowSet) {
			String[] vals = string.split("\t");

			String l1 = vals[0];
			String l2 = vals[1];
			String l3 = vals[2];
			String l4 = vals[3];

			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();

			if (!empty(l1) && !empty(l2)) {
				l1 = removeDelimiters(l1);
				l2 = removeDelimiters(l2);
				sb1.append("(" + l2 + ")" + "-" + "[:is_A" + "{role:'subclass'}" + "]" + " -> (" + l1 + ")");

				builderSet.add(sb1.toString());

			}
			if (!empty(l2) && !empty(l3)) {
				l2 = removeDelimiters(l2);
				l3 = removeDelimiters(l3);

				sb2.append("(" + l3 + ")" + "-" + "[:is_A" + "{role:'subclass'}" + "]" + " -> (" + l2 + ")");
				builderSet.add(sb2.toString());

			}
			if (!empty(l3) && !empty(l4)) {
				l3 = removeDelimiters(l3);
				l4 = removeDelimiters(l4);

				sb3.append("(" + l4 + ")" + "-" + "[:is_A" + "{role:'subclass'}" + "]" + " -> (" + l3 + ")");

				builderSet.add(sb3.toString());
			}

		}

		for (String string2 : builderSet) {
			sb.append("," + "\n");
			sb.append(string2);
		}

		// System.out.println(sb.toString());

		return sb.toString();
	}

	public boolean empty(String string) {
		boolean isEmpty = false;
		if (Strings.isNullOrEmpty(string)) {
			isEmpty = true;
		}
		return isEmpty;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private String removeDelimiters(String string) {
		String string2 = string;
		string2 = string2.replace("/", "_");
		string2 = string2.replace("-", "_");
		string2 = string2.replace(",", "_");
		string2 = string2.replace("，", "_");
		string2 = string2.replace("：", "_");
		string2 = string2.replace("。", "");
		string2 = string2.replace("！", "");
		string2 = string2.replace("？", "");
		string2 = string2.replace("、", "");
		string2 = string2.replaceAll("��", "");

		string2 = string2.replace("'", "");

		string2 = string2.trim();

		return string2;
	}
}
