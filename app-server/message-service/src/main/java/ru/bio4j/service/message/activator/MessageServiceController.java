package ru.bio4j.service.message.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.message.AmqpMessageService;
import ru.bio4j.service.message.JmsMessageService;
import ru.bio4j.service.message.amqp.AmqpServiceConfig;
import ru.bio4j.service.message.amqp.DefaultAmqpServiceConfig;
import ru.bio4j.service.message.jms.JmsServiceConfig;
import ru.bio4j.service.ServiceController;
import ru.bio4j.service.ServiceLifecycle;
import ru.bio4j.service.monitor.Monitor;

public class MessageServiceController implements ServiceController {

	private final static String SERVICE_NAME = "Open Message Service";

	private final static Logger LOG = LoggerFactory.getLogger(MessageServiceController.class);

	private JmsMessageServiceFactory jmsMessageServiceFactory;

	private AmqpMessageServiceFactory amqpMessageServiceFactory;

	private final BundleContext bundleContext;

	public MessageServiceController(BundleContext bundleContext) {
		super();
		this.bundleContext = bundleContext;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public void start() throws Exception {
		JmsServiceConfig jmsServiceConfig = new JmsServiceConfig(bundleContext);
		AmqpServiceConfig amqpServiceConfig = new DefaultAmqpServiceConfig(bundleContext);
		amqpMessageServiceFactory = new AmqpMessageServiceFactory(amqpServiceConfig);
		jmsMessageServiceFactory = new JmsMessageServiceFactory(jmsServiceConfig);
        amqpMessageServiceFactory.start();
        jmsMessageServiceFactory.start();
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_RANKING, ServiceConstants.MESSAGING_SERVICE_RANK);
		bundleContext.registerService(ServiceLifecycle.class.getName(), new MessagingServiceLifecycle(jmsMessageServiceFactory, amqpMessageServiceFactory), props);
		bundleContext.registerService(JmsMessageService.class.getName(), jmsMessageServiceFactory, props);
		bundleContext.registerService(AmqpMessageService.class.getName(), amqpMessageServiceFactory, props);
		bundleContext.registerService(ManagedService.class.getName(), new MessageServiceManagedFactory(amqpMessageServiceFactory, jmsMessageServiceFactory), createProperties());
		bundleContext.registerService(Monitor.class, amqpMessageServiceFactory.getAmqpServiceManager(), null);
		LOG.info("Message Service Has Been Configured. AMQP Parameters {}", amqpServiceConfig);
        LOG.info("Message Service Has Been Configured. JMS Parameters {}", jmsServiceConfig);
	}

	private Dictionary<String, ?> createProperties() {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_PID, "message.service.config");
		props.put(Constants.SERVICE_RANKING, ServiceConstants.MESSAGING_SERVICE_RANK);
		return props;
	}

	@Override
	public void stop() throws Exception {
		if (amqpMessageServiceFactory != null) {
			amqpMessageServiceFactory.stop();
		}
		if (jmsMessageServiceFactory != null) {
			jmsMessageServiceFactory.stop();
		}
		LOG.info("Message Service Stoped");
	}

}
