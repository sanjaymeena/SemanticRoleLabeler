package com.emotibot.srl.datastructures;

public enum StatusCode {
	OK(0), ERROR(1);
	
	private final int value;
	
	private StatusCode(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	public String getMessage() {
		switch (this) {
		case OK:
			return "ok";
		case ERROR:
			return "error";
		default:
			return "";

		}
	}
}
