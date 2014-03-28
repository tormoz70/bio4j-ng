package ru.bio4j.service.mail.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.builder.MailMessageBuilder;
import ru.bio4j.service.mail.builder.MailMessageBuilderResolver;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.LinkedBlockingQueue;

public class MailServiceReactor extends Thread {

	private static class StopMessage extends MailMessage { }
	
	private final static Logger LOG = LoggerFactory.getLogger(MailServiceReactor.class);
	
	private final LinkedBlockingQueue<MailMessage> messages;
	
	private final Session session;
	
	private WhomToSend whomToSend;
	
	private final MailServiceConfig serviceConfig;
	
	public MailServiceReactor(LinkedBlockingQueue<MailMessage> messages, Session session, MailServiceConfig serviceConfig) {
		super();
		setName("Mail Service Reactor");
		this.session = session;
		this.serviceConfig = serviceConfig;
		this.whomToSend = serviceConfig.getWhomtoSend();
		this.messages = messages;
	}

	public void addMessage(MailMessage message) {
		messages.add(message);
	}
	
	@Override
	public void run() {
		MailMessage message;
		try {
			while ((message = messages.take()) != null) {
				if (message instanceof StopMessage) {
					break;
				}
				sendMessage(message);
			}
		} catch (InterruptedException e) {
			LOG.warn("Interrupted...", e);
            Thread.currentThread().interrupt();
		}
	}

	private void sendMessage(MailMessage mailMessage) {
		MailMessageBuilder messageBuilder = MailMessageBuilderResolver.createBuilder(mailMessage, serviceConfig);
		LOG.info("Sending messages to {}", whomToSend);
		try {
			MimeMessage mimeMessage = messageBuilder.build(session, whomToSend);
			Transport.send(mimeMessage);
		} catch (Exception e) {
			LOG.error("Ooops...", e);
		}
	}

	public void stopReactor() {
		addMessage(new StopMessage());
	}
	
}
