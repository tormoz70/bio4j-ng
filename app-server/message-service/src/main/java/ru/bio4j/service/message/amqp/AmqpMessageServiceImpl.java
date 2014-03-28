package ru.bio4j.service.message.amqp;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.message.AmqpMessageService;
import ru.bio4j.service.message.DestinationDescriptor;
import ru.bio4j.service.message.common.AbstractConnectionProvider;
import ru.bio4j.service.message.common.AbstractMessageService;

public class AmqpMessageServiceImpl extends AbstractMessageService<AmqpServiceResource> implements AmqpMessageService {

	public AmqpMessageServiceImpl(
			AbstractConnectionProvider<AmqpServiceResource> connectionProvider, BundleContext bundleContext) {
		super(connectionProvider, bundleContext);
	}

	@Override
	public String getName() {
		return "AMQP Connections Provider Service";
	}
	
	public void deleteQueue(DestinationDescriptor desc) {
		connectionProvider.getResource(desc).deleteQueue();
	}

}
