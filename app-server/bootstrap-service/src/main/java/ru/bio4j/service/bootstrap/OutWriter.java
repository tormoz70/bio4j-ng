package ru.bio4j.service.bootstrap;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutWriter {
	
	private final static Logger LOG = LoggerFactory.getLogger(OutWriter.class);
	
	private PrintStream out;
	
	public OutWriter() {
	}
	
	public OutWriter(PrintStream out) {
		this.out = out;
	}
	
	public void write(String string, Object ... params) {
		 write(out, string, params);
	}

	public static void write(PrintStream out, String string, Object ... params) {
		String format = MessageFormat.format(string, params);
		if (out != null) {
			out.println(format);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(format);
		}
	}
}
