package ru.bio4j.service.mail.builder;

import freemarker.template.Template;
import ru.bio4j.service.mail.CustomMailMessage;
import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.impl.MailServiceConfig;

import javax.mail.internet.MimeMessage;
import java.io.InputStreamReader;
import java.util.Random;

public class CustomMailMessageBuilder extends MailMessageBuilder {

	private final static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	private String templateName;
	
	public CustomMailMessageBuilder(MailMessage mailMessage, MailServiceConfig serviceConfig) {
		super(mailMessage, serviceConfig);
		this.templateName = generateTemplateName();
	}

	@Override
	protected Template getTemplate() throws Exception {
		CustomMailMessage mailMessage = (CustomMailMessage) getMailMessage();
		return new Template(templateName, new InputStreamReader(mailMessage.getTemplateStream()), getConfiguration());
	}

	@Override
	protected void fillMessage(MimeMessage message) throws Exception {
		message.setSubject(getMailMessage().getSubject());
	}

	private String generateTemplateName() {
		Random random = new Random();
		char[] name = new char[6];
		for (int i = 0; i < 6; i++) {
			name[i] = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
		}
		return new String(name);
	}
	
}
