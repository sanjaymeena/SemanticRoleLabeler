/**
 * 
 */
package com.emotibot.srl.test.frames;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Sanjay
 *
 */
public class FrameElement {

	String label_en;
	String label_zh;
	String abbrv;
	String description;
	boolean isOptional;
	String raw;
	
	public FrameElement() {
		label_en = "";
		label_zh = "";
		abbrv = "";
		description = "";
		isOptional = false;
		raw = "";
		
	}

	public FrameElement(String label_zh,String label_en,  String abrv,  boolean isOptional,String description,
			String raw) {
		this.label_en = label_en;
		this.label_zh = label_zh;
		this.abbrv = abrv;
		this.description = description;
		this.isOptional = isOptional;
		this.raw = raw;
	}

	/**
	 * @return the label_en
	 */
	public String getLabel_en() {
		return label_en;
	}

	/**
	 * @param label_en
	 *            the label_en to set
	 */
	public void setLabel_en(String label_en) {
		this.label_en = label_en;
	}

	/**
	 * @return the label_zh
	 */
	public String getLabel_zh() {
		return label_zh;
	}

	/**
	 * @param label_zh
	 *            the label_zh to set
	 */
	public void setLabel_zh(String label_zh) {
		this.label_zh = label_zh;
	}

	/**
	 * @return the abbrv
	 */
	public String getAbbrv() {
		return abbrv;
	}

	/**
	 * @param abbrv
	 *            the abbrv to set
	 */
	public void setAbbrv(String abbrv) {
		this.abbrv = abbrv;
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

	/**
	 * @return the isOptional
	 */
	public boolean isOptional() {
		return isOptional;
	}

	/**
	 * @param isOptional
	 *            the isOptional to set
	 */
	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	/**
	 * @return the raw
	 */
	public String getRaw() {
		return raw;
	}

	/**
	 * @param raw
	 *            the raw to set
	 */
	public void setRaw(String raw) {
		this.raw = raw;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(raw);
		return builder.toString();
	}

}
