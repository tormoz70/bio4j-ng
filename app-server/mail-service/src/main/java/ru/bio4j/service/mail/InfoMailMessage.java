package ru.bio4j.service.mail;

public class InfoMailMessage extends MailMessage {

	private static final String MESSAGE_PROP = "message";
	
	public InfoMailMessage() {
		super();
	}

	public void setMessage(String message) {
		addToContext(MESSAGE_PROP, message);
	}
}
