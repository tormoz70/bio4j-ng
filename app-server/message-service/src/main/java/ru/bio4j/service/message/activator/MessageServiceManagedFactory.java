package ru.bio4j.service.message.activator;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class MessageServiceManagedFactory implements
		ServiceFactory<ManagedService> {

	private final AmqpMessageServiceFactory amqpMessageServiceFactory;
	
	private final JmsMessageServiceFactory jmsMessageServiceFactory;
	
	public MessageServiceManagedFactory(
			AmqpMessageServiceFactory amqpMessageServiceFactory,
			JmsMessageServiceFactory jmsMessageServiceFactory) {
		super();
		this.amqpMessageServiceFactory = amqpMessageServiceFactory;
		this.jmsMessageServiceFactory = jmsMessageServiceFactory;
	}

	@Override
	public ManagedService getService(Bundle bundle,
			ServiceRegistration<ManagedService> registration) {
		return new ManagedService() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
				if (properties != null) {
					try {
						jmsMessageServiceFactory.updateConfig(properties);
						amqpMessageServiceFactory.updateConfig(properties);
					} catch (Exception e) {
						throw new ConfigurationException("Bad parameters", e.getMessage(), e);
					}
				}
			}
		};
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<ManagedService> registration,
			ManagedService service) {
	}

}
