package ru.bio4j.service.mail.builder;

import ru.bio4j.service.mail.CustomMailMessage;
import ru.bio4j.service.mail.ExceptionMailMessage;
import ru.bio4j.service.mail.InfoMailMessage;
import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.impl.MailServiceConfig;

public class MailMessageBuilderResolver {

	private MailMessageBuilderResolver() {}
	
	public static MailMessageBuilder createBuilder(MailMessage mailMessage, MailServiceConfig serviceConfig) {
		MailMessageBuilder messageBuilder;
		if (mailMessage instanceof InfoMailMessage) {
			messageBuilder = new InfoMailMessageBuilder(mailMessage, serviceConfig);
		} else if (mailMessage instanceof ExceptionMailMessage) {
			messageBuilder = new ExceptionMailMessageBuilder(mailMessage, serviceConfig);
		} else if (mailMessage instanceof CustomMailMessage) {
			messageBuilder = new CustomMailMessageBuilder(mailMessage, serviceConfig);
		} else {
			throw new IllegalArgumentException("Unsupported mail message " + mailMessage);
		}
		return messageBuilder;
	}
	
}
