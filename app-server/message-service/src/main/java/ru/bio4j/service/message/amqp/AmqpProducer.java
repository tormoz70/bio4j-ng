package ru.bio4j.service.message.amqp;

import ru.bio4j.service.message.Producer;

public class AmqpProducer implements Producer {

	private final AmqpServiceResource amqpServiceResource;
	
	public AmqpProducer(AmqpServiceResource amqpServiceResource) {
		super();
		this.amqpServiceResource = amqpServiceResource;
	}

	@Override
	public <T> void publish(T messageObject) {
		if (!byte[].class.equals(messageObject.getClass())) {
			throw new IllegalArgumentException("Unsupported object type " + messageObject.getClass());
		}
		amqpServiceResource.publish((byte[]) messageObject);
	}

}
