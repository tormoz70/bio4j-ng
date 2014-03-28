package ru.bio4j.service.message.jms;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** default */ class ConsumerServiceResource extends JmsServiceResource {

	private final static Logger LOG = LoggerFactory.getLogger(ConsumerServiceResource.class);
	
	private final MessageConsumer messageConsumer;
	
	public ConsumerServiceResource(Session session, MessageConsumer messageConsumer) {
		super(session);
		this.messageConsumer = messageConsumer;
	}

	@Override
	protected void internalClose() {
		if (messageConsumer != null) {
			try {
				messageConsumer.close();
				LOG.info("Consumer closed");
			} catch (JMSException e) {
				LOG.warn("Couls not close consumer", e);
			}
		}
	}

}
