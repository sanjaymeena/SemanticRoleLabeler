package com.emotibot.srl.test.frames.conversations.datastructures;

public class Segment {
StringBuilder sb;

/**
 * @return the sb
 */
public StringBuilder getSb() {
	return sb;
}

/**
 * @param sb the sb to set
 */
public void setSb(StringBuilder sb) {
	this.sb = sb;
}

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Segment [sb=");
	builder.append(sb);
	builder.append("]");
	return builder.toString();
}
}
