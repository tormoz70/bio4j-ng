package ru.bio4j.service.mail.impl;

import java.util.Arrays;

public class WhomToSend {

	private final String to;
	
	private final String[] cc;

	public WhomToSend(String to, String ... cc) {
		super();
		this.to = to;
		this.cc = cc;
	}

	public String getTo() {
		return to;
	}

	public String[] getCc() {
		return cc;
	}

	@Override
	public String toString() {
		return "WhomToSend [to=" + to + ", cc=" + Arrays.toString(cc) + "]";
	}
	
}
