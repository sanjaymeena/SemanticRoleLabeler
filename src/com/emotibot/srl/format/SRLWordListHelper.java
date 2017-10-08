package com.emotibot.srl.format;

import static com.emotibot.srl.server.Constants.instanceToSRLClassMultimap;
import static com.emotibot.srl.server.Constants.srl_word_list_directory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;

public class SRLWordListHelper {

	LinkedListMultimap<String, String> srl_type_map = LinkedListMultimap.create();

	public SRLWordListHelper() throws IOException {
		srl_type_map = LinkedListMultimap.create();
		createSRLTokenMap(srl_word_list_directory);
	}

	/**
	 * Given a token, provide SRL class type for that token
	 * 
	 * @param token
	 * @return
	 */
	public List<String> getSRLClass(String token) {
		String srl_class = "";
		List<String> keys = new ArrayList<String>();
		if (!Strings.isNullOrEmpty(token)) {
			ImmutableCollection<String> collection = instanceToSRLClassMultimap.get(token);
			for (String string : collection) {
				keys.add(string);
			}

		}

		return keys;
	}

	void createSRLTokenMap(String srl_tokens_path) throws IOException {

		// String dir = "";

		String[] types = { "txt" };

		File directory = new File(srl_tokens_path);
		Collection<File> files2 = FileUtils.listFiles(directory, types, true);
		System.out.println("Reading directory for word lists :" + srl_tokens_path);
		for (File file : files2) {
			System.out.println(file.getName());
			Set<String> srl_tag_set = new HashSet<String>();
			String key = file.getName().replace(".txt", "");

			LineIterator it = FileUtils.lineIterator(file, "UTF-8");

			try {
				while (it.hasNext()) {

					String line = it.nextLine();// do something with line
					if (line != null && line.length() > 0) {
						line = line.trim();
						srl_tag_set.add(line);
					}
				}

			} finally {
				LineIterator.closeQuietly(it);
			}

			for (String string : srl_tag_set) {
				srl_type_map.put(key, string);
			}

		}

		System.out.println("Creating srl instance token - class map ");
		ImmutableMultimap<String, String> inverseMultimap1 = ImmutableMultimap.copyOf(srl_type_map);

		// create inverse map of instance to class
		instanceToSRLClassMultimap = inverseMultimap1.inverse();
		// for (String t : instanceToSRLClassMultimap.keys()) {
		// System.out.println(t + " : " + instanceToSRLClassMultimap.get(t));
		// }
		int t = 10;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String path = "resources/semantic_role_labeling/word_lists";
		SRLWordListHelper fc = new SRLWordListHelper();
		// fc.createSRLTokenMap(path);
		List<String> res = fc.getSRLClass("激烈");
		System.out.println(res);

	}

}
