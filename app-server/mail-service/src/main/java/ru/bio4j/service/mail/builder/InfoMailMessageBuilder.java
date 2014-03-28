package ru.bio4j.service.mail.builder;

import static ru.bio4j.service.mail.impl.MailConstants.INFO_MESSAGE_TPL;

import javax.mail.internet.MimeMessage;

import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.impl.MailServiceConfig;
import freemarker.template.Template;

public class InfoMailMessageBuilder extends MailMessageBuilder {

	public InfoMailMessageBuilder(MailMessage mailMessage, MailServiceConfig serviceConfig) {
		super(mailMessage, serviceConfig);
	}

	@Override
	protected Template getTemplate() throws Exception {
		return getConfiguration().getTemplate(INFO_MESSAGE_TPL);
	}

	@Override
	protected void fillMessage(MimeMessage message) throws Exception {
		message.setSubject(getResourceValue("mail.message.info.subject"));
	}

}
