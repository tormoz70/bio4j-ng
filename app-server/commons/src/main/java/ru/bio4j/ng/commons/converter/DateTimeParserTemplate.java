package ru.bio4j.ng.commons.converter;

public class DateTimeParserTemplate {
	private String format;
	private String regex;

	public DateTimeParserTemplate(String format, String regex) {
		this.format = format;
		this.regex = regex;
	}
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
}
