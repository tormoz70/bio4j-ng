package ru.bio4j.service.message.activator;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.message.JmsMessageService;
import ru.bio4j.service.message.common.AbstractConnectionProvider;
import ru.bio4j.service.message.jms.JmsConnectionProvider;
import ru.bio4j.service.message.jms.JmsMessageServiceImpl;
import ru.bio4j.service.message.jms.JmsServiceResource;
import ru.bio4j.service.ServiceConfig;

public class JmsMessageServiceFactory extends AbstractMessageServiceFactory<JmsMessageService, JmsServiceResource> {

	public JmsMessageServiceFactory(ServiceConfig serviceConfig) {
		super(serviceConfig);
	}

	@Override
	protected JmsMessageService createService(AbstractConnectionProvider<JmsServiceResource> connectionProvider, BundleContext bundleContext) {
        return new JmsMessageServiceImpl(connectionProvider, bundleContext);
	}

	@Override
	protected AbstractConnectionProvider<JmsServiceResource> createConnectionProvider() {
		return new JmsConnectionProvider(getServiceConfig());
	}
}
