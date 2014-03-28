package ru.bio4j.service.mail;

import java.io.InputStream;

public class MailMessageFactory {
	
	private MailMessageFactory() {}
	
	public static MailMessage createErrorMessage(Exception e) {
		return createErrorMessage(null, e);
	}
	
	public static MailMessage createErrorMessage(String[] to, Exception e) {
		ExceptionMailMessage mailMessage = new ExceptionMailMessage();
		mailMessage.setTo(to);
		mailMessage.setException(e);
		return mailMessage;
	}

	public static MailMessage createInfoMessage(String message) {
		return createInfoMessage(null, message);
	}
	
	public static MailMessage createInfoMessage(String[] to, String message) {
		InfoMailMessage infoMailMessage = new InfoMailMessage();
		infoMailMessage.setTo(to);
		infoMailMessage.setMessage(message);
		return infoMailMessage;
	}
	
	public static MailMessage createCustomMessage(String subject, InputStream templateStream, String... to) {
		CustomMailMessage customMailMessage = new CustomMailMessage();
		customMailMessage.setTo(to);
		customMailMessage.setSubject(subject);
		customMailMessage.setTemplateStream(templateStream);
		return customMailMessage;
	}

}
