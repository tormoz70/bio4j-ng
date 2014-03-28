package ru.bio4j.service.mail.impl;

import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.MailService;

public class MailServiceImpl implements MailService {

	private final MailServiceReactor reactor;
	
	public MailServiceImpl(MailServiceReactor reactor) {
		super();
		this.reactor = reactor;
	}

	@Override
	public void sendMessage(MailMessage message) {
		reactor.addMessage(message);
	}

}
