package com.emotibot.srl.test.frames;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class Frame {
	private String file;
	private String key;
	private String content;
	private String frame_en;
	private String frame_zh;
	private String description;
	private List<String> lexical_units;
	private List<String> frame_element_rows;
	private Multimap<String, FrameElement> frameElementMultiMap;

	/**
	 * 
	 */
	public Frame() {
		key="";
		file = "";
		content = "";
		frame_en = "";
		frame_zh = "";
		description = "";
		lexical_units = new ArrayList<>();
		frame_element_rows = new ArrayList<>();
		frameElementMultiMap = LinkedListMultimap.create();
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the frame_en
	 */
	public String getFrame_en() {
		return frame_en;
	}

	/**
	 * @param frame_en
	 *            the frame_en to set
	 */
	public void setFrame_en(String frame_en) {
		this.frame_en = frame_en;
	}

	/**
	 * @return the frame_zh
	 */
	public String getFrame_zh() {
		return frame_zh;
	}

	/**
	 * @param frame_zh
	 *            the frame_zh to set
	 */
	public void setFrame_zh(String frame_zh) {
		this.frame_zh = frame_zh;
	}

	/**
	 * @return the lexical_units
	 */
	public List<String> getLexical_units() {
		return lexical_units;
	}

	/**
	 * @param lexical_units
	 *            the lexical_units to set
	 */
	public void setLexical_units(List<String> lexical_units) {
		this.lexical_units = lexical_units;
	}

	/**
	 * @return the frame_element_rows
	 */
	public List<String> getFrame_element_rows() {
		return frame_element_rows;
	}

	/**
	 * @param frame_element_rows
	 *            the frame_element_rows to set
	 */
	public void setFrame_element_rows(List<String> frame_element_rows) {
		this.frame_element_rows = frame_element_rows;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(key );
		return builder.toString();
	}

	/**
	 * @return the frameElementMultiMap
	 */
	public Multimap<String, FrameElement> getFrameElementMultiMap() {
		return frameElementMultiMap;
	}

	/**
	 * @param frameElementMultiMap the frameElementMultiMap to set
	 */
	public void setFrameElementMultiMap(Multimap<String, FrameElement> frameElementMultiMap) {
		this.frameElementMultiMap = frameElementMultiMap;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
}
