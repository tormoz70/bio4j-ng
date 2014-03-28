package ru.bio4j.service.message.jms;

import javax.jms.Message;
import javax.jms.MessageListener;

import ru.bio4j.service.message.MessageHandler;

/** default */ class JmsConsumer implements MessageListener {
	
	private final MessageHandler handler;

	public JmsConsumer(MessageHandler handler) {
		super();
		if (handler == null) {
			throw new IllegalArgumentException("MessageHandler is not set");
		}
		this.handler = handler;
	}

	@Override
	public final void onMessage(Message message) {
		handler.processMessage(message);
	}
}
