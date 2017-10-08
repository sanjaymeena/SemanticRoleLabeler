package com.emotibot.srl.pruner;

import com.emotibot.srl.pruner.Constants.PRUNERRULES;

public class Rule {
	String name;
	PRUNERRULES prunerEnum;
	String rule;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the prunerEnum
	 */
	public PRUNERRULES getPrunerEnum() {
		return prunerEnum;
	}

	/**
	 * @param prunerEnum
	 *            the prunerEnum to set
	 */
	public void setPrunerEnum(PRUNERRULES prunerEnum) {
		this.prunerEnum = prunerEnum;
	}

	/**
	 * @return the rule
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}
}
