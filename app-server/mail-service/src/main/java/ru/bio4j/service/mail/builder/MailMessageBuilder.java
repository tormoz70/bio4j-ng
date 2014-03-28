package ru.bio4j.service.mail.builder;

import static ru.bio4j.service.mail.impl.MailConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.impl.MailServiceConfig;
import ru.bio4j.service.mail.impl.MailServiceReactor;
import ru.bio4j.service.mail.impl.WhomToSend;
import freemarker.cache.URLTemplateLoader;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public abstract class MailMessageBuilder {

	private final static String HTML_MIME_TYPE = "text/html";

	private static ResourceBundle resource = ResourceBundle.getBundle("l10n.messages", Locale.getDefault(), MailMessageBuilder.class.getClassLoader());

	private static Configuration configuration;

	private static Inet4Address host = InetAddressHelper.resolveHost();

	static {
		try {
			Logger.selectLoggerLibrary(Logger.LIBRARY_SLF4J);
		} catch (ClassNotFoundException e) {
			;
		}
		configuration = new Configuration();
		configuration.setDefaultEncoding("UTF8");
		configuration.setTemplateLoader(new URLTemplateLoader() {

			@Override
			protected URL getURL(String name) {
				return MailServiceReactor.class.getClassLoader().getResource(TEMPLATE_PATH + name);
			}

		});
		configuration.setObjectWrapper(new DefaultObjectWrapper());
	}

	private final MailMessage mailMessage;
	
	private final MailServiceConfig serviceConfig;

	public MailMessageBuilder(MailMessage mailMessage, MailServiceConfig serviceConfig) {
		super();
		this.mailMessage = mailMessage;
		this.serviceConfig = serviceConfig;
	}

	protected MailMessage getMailMessage() {
		return mailMessage;
	}

	protected String getResourceValue(String key) {
		return resource.getString(key);
	}

	protected Configuration getConfiguration() {
		return configuration;
	}

	public MimeMessage build(Session session, WhomToSend whomToSend) throws Exception {
		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(new InternetAddress(getServiceConfig().getFrom()));
		mimeMessage.setHeader("MIME-Version", "1.0");
		mimeMessage.setHeader("Content-Type", HTML_MIME_TYPE);
		mimeMessage.setHeader("X-Priority", "1");
		if (mailMessage.getTo() != null) {
			for (String address : mailMessage.getTo()) {
				addRecipient(mimeMessage, Message.RecipientType.TO, address);
			}
		}
		addRecipient(mimeMessage, Message.RecipientType.TO, whomToSend.getTo());
		fillMessage(mimeMessage);
		fillContent(mimeMessage);
		try {
			String hostName = (host != null) ? host.getCanonicalHostName() : InetAddress.getLocalHost().getCanonicalHostName();
			mimeMessage.setSubject(mimeMessage.getSubject() + " (" + hostName + ")");
		} catch (UnknownHostException ue) {
			;
		}
		for (String cc : whomToSend.getCc()) {
			addRecipient(mimeMessage, Message.RecipientType.CC, cc);
		}
		return mimeMessage;
	}

	protected void addRecipient(MimeMessage mimeMessage, Message.RecipientType type, String cc) throws MessagingException, AddressException {
		if ((cc != null) && (!"".equals(cc))) {
			mimeMessage.addRecipient(type, new InternetAddress(cc));
		}
	}

	protected void fillContent(MimeMessage message) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (OutputStreamWriter writer = new OutputStreamWriter(out)) {
                Template template = getTemplate();
                template.process(getMailMessage().getContext(), writer);
                writer.flush();
                message.setContent(out.toString(), HTML_MIME_TYPE);
            }
        }
	}
	
	public MailServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	protected abstract Template getTemplate() throws Exception;

	protected abstract void fillMessage(MimeMessage message) throws Exception;

}
