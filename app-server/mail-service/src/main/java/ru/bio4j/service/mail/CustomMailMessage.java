package ru.bio4j.service.mail;

import java.io.InputStream;

public class CustomMailMessage extends MailMessage {

	private InputStream templateStream;

	public InputStream getTemplateStream() {
		return templateStream;
	}

	public void setTemplateStream(InputStream templateStream) {
		this.templateStream = templateStream;
	}
	
}
