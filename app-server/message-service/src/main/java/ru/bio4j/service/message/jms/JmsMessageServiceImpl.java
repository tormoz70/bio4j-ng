package ru.bio4j.service.message.jms;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.message.JmsMessageService;
import ru.bio4j.service.message.common.AbstractConnectionProvider;
import ru.bio4j.service.message.common.AbstractMessageService;

public class JmsMessageServiceImpl extends AbstractMessageService<JmsServiceResource> implements JmsMessageService {

	public JmsMessageServiceImpl(AbstractConnectionProvider<JmsServiceResource> connectionProvider, BundleContext bundleContext) {
		super(connectionProvider, bundleContext);
	}

	@Override
	public String getName() {
		return "JMS Connections Provider Service";
	}

}
