package ru.bio4j.service.message.common;

import ru.bio4j.service.message.Producer;
import ru.bio4j.service.message.amqp.AmqpProducer;
import ru.bio4j.service.message.amqp.AmqpServiceResource;
import ru.bio4j.service.message.jms.JmsProducer;
import ru.bio4j.service.message.jms.JmsServiceResource;

public class ProducerFactory {

	private ProducerFactory() {}
	
	public static Producer createProducerFor(ServiceResource resource) {
		Producer producer;
		if (resource instanceof JmsServiceResource) {
			producer = new JmsProducer((JmsServiceResource) resource);
		} else if (resource instanceof AmqpServiceResource) {
			producer = new AmqpProducer((AmqpServiceResource) resource);
		} else {
			throw new IllegalArgumentException("Unknown resource" + resource);
		}
		return producer;
	}
	
}
