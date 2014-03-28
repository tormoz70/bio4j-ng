package ru.bio4j.service.mail.builder;

import static ru.bio4j.service.mail.impl.MailConstants.ERRORS_MESSAGE_TPL;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.mail.internet.MimeMessage;

import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.impl.MailServiceConfig;
import freemarker.template.Template;

public class ExceptionMailMessageBuilder extends MailMessageBuilder {

	public ExceptionMailMessageBuilder(MailMessage mailMessage, MailServiceConfig serviceConfig) {
		super(mailMessage, serviceConfig);
	}

	@Override
	protected Template getTemplate() throws Exception {
		return getConfiguration().getTemplate(ERRORS_MESSAGE_TPL);
	}

	@Override
	protected void fillMessage(MimeMessage mimeMessage) throws Exception {
		mimeMessage.setSubject(getResourceValue("mail.message.error.subject"));
	}

	@Override
	protected void fillContent(MimeMessage message) throws Exception {
		Exception exp = (Exception) getMailMessage().getContext().get("exception");
		StringWriter out = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(out)) {
            exp.printStackTrace(printWriter);
            getMailMessage().getContext().put("exception", out.toString());
        }
		super.fillContent(message);
	}

}
