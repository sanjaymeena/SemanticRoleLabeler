package com.emotibot.srl.test.frames.conversations.datastructures;

import java.util.LinkedList;
import java.util.List;

public class DictionaryItem {

	String item;
	List<List<String>> segmentations;
	boolean multipleSegmentations;
	//in this case , item was multiple token and the string has been compacted to remove spaces
	boolean compactedItem;

	public DictionaryItem() {
		item = "";
		segmentations = new LinkedList<>();
		multipleSegmentations = false;
	}

	public DictionaryItem(String item, List<List<String>> segmentations, boolean multipleSegmentations) {
		this.item = item;
		this.segmentations = segmentations;
		this.multipleSegmentations = multipleSegmentations;

	}

	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(String item) {
		this.item = item;
	}

	/**
	 * @return the segmentations
	 */
	public List<List<String>> getSegmentations() {
		return segmentations;
	}

	/**
	 * @param segmentations
	 *            the segmentations to set
	 */
	public void setSegmentations(List<List<String>> segmentations) {
		this.segmentations = segmentations;
	}

	/**
	 * @return the multipleSegmentations
	 */
	public boolean isMultipleSegmentations() {
		return multipleSegmentations;
	}

	/**
	 * @param multipleSegmentations
	 *            the multipleSegmentations to set
	 */
	public void setMultipleSegmentations(boolean multipleSegmentations) {
		this.multipleSegmentations = multipleSegmentations;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("");
		builder.append(item);
		builder.append(", segmentations=");
		builder.append(segmentations);
		
		return builder.toString();
	}

	/**
	 * @return the compactedItem
	 */
	public boolean isCompactedItem() {
		return compactedItem;
	}

	/**
	 * @param compactedItem the compactedItem to set
	 */
	public void setCompactedItem(boolean compactedItem) {
		this.compactedItem = compactedItem;
	}

}
