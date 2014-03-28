package ru.bio4j.service.message.jms;

import java.io.Serializable;

import ru.bio4j.service.message.Producer;

public class JmsProducer implements Producer {

	private final ProducerServiceResource producerServiceResource;
	
	public JmsProducer(JmsServiceResource serviceResource) {
		super();
		producerServiceResource = (ProducerServiceResource) serviceResource;
	}

	@Override
	public <T> void publish(T messageObject) {
		if (!(messageObject instanceof Serializable)) {
			throw new IllegalArgumentException("Unsupported object type " + messageObject.getClass());
		}
		producerServiceResource.prepareToSendMessage((Serializable) messageObject);
	}

}
