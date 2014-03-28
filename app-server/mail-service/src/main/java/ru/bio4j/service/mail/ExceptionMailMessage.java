package ru.bio4j.service.mail;

public class ExceptionMailMessage extends MailMessage {

	private final static String EXCEPTION_PROP = "exception";
	
	public ExceptionMailMessage() {
		super();
	}

	public void setException(Exception exception) {
		addToContext(EXCEPTION_PROP, exception);
	}
	
}
