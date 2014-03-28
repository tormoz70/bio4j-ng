package ru.bio4j.service.message.activator;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.message.AmqpMessageService;
import ru.bio4j.service.message.amqp.AmqpConnectionProvider;
import ru.bio4j.service.message.amqp.AmqpMessageServiceImpl;
import ru.bio4j.service.message.amqp.AmqpServiceResource;
import ru.bio4j.service.message.amqp.plugin.AmqpServiceManager;
import ru.bio4j.service.message.common.AbstractConnectionProvider;
import ru.bio4j.service.ServiceConfig;

public class AmqpMessageServiceFactory extends AbstractMessageServiceFactory<AmqpMessageService, AmqpServiceResource> {

	public AmqpMessageServiceFactory(ServiceConfig serviceConfig) {
		super(serviceConfig);
	}

	@Override
	protected AmqpMessageService createService(AbstractConnectionProvider<AmqpServiceResource> connectionProvider, 
			BundleContext bundleContext) {
		return new AmqpMessageServiceImpl(connectionProvider, bundleContext);
	}

	@Override
	protected AbstractConnectionProvider<AmqpServiceResource> createConnectionProvider() {
		return new AmqpConnectionProvider();
	}

	public AmqpServiceManager getAmqpServiceManager() {
		return (AmqpServiceManager) getConnectionProvider();
	}
	
}
