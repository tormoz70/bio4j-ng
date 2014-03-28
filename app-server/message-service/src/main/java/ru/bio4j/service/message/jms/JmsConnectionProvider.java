package ru.bio4j.service.message.jms;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.message.AcknowledgeType;
import ru.bio4j.service.message.DestinationDescriptor;
import ru.bio4j.service.message.JmsDestinationDescriptor;
import ru.bio4j.service.message.JmsDestinationType;
import ru.bio4j.service.message.MessageHandler;
import ru.bio4j.service.message.common.AbstractConnectionProvider;
import ru.bio4j.service.ServiceConfig;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

public class JmsConnectionProvider extends AbstractConnectionProvider<JmsServiceResource> {

	private final static Logger LOG = LoggerFactory.getLogger(JmsConnectionProvider.class);

	private final Lock consumerLock;

	private final Lock publisherLock;

	private Connection connection;
	
	private final ServiceConfig serviceConfig;

	public JmsConnectionProvider(ServiceConfig serviceConfig) {
		super();
		consumerLock = new ReentrantLock();
		publisherLock = new ReentrantLock();
		this.serviceConfig = serviceConfig;
	}

	@Override
	public void connect(ServiceConfig serviceConfig) throws Exception {
		JmsServiceConfig jmsServiceConfig = (JmsServiceConfig) serviceConfig;
		LOG.debug("Creating JMS connection with parameters: {} ", jmsServiceConfig);
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, jmsServiceConfig.getAddress());
		if (jmsServiceConfig.isAuthRequired()) {
			connection = connectionFactory.createConnection(jmsServiceConfig.getUsername(), jmsServiceConfig.getPassword());
		} else {
			connection = connectionFactory.createConnection();
		}
		connection.start();
		LOG.debug("JMS connection established");
	}

	@Override
	public void disconnect() {
		closeResources();
		if (connection != null) {
			try {
				connection.close();
				LOG.debug("JMS Connection closed");
			} catch (JMSException e) {
				LOG.error("Could not close connection to server", e);
			}
		}
	}

	@Override
	protected JmsServiceResource createResource(DestinationDescriptor destinationDescriptor, MessageHandler handler, BundleContext bundleContext) throws Exception {
		if (destinationDescriptor == null) {
			throw new IllegalArgumentException("DestinationDescriptor is not set");
		}
		if (handler == null) {
			throw new IllegalArgumentException("MessageHandler is not set");
		}
		LOG.debug("Creating JMS Consumer for descriptor {}", destinationDescriptor.toString());
		JmsDestinationDescriptor jmsDestinationDescriptor = (JmsDestinationDescriptor) destinationDescriptor;
		consumerLock.lockInterruptibly();
		Session session;
		try {
			session = createSession(jmsDestinationDescriptor);
		} finally {
			consumerLock.unlock();
		}
		Destination destination = createDestination(jmsDestinationDescriptor, session);
		MessageConsumer messageConsumer;
		if (jmsDestinationDescriptor.getSelectorString() != null) {
			messageConsumer = session.createConsumer(destination, jmsDestinationDescriptor.getSelectorString());
		} else {
			messageConsumer = session.createConsumer(destination);
		}
		messageConsumer.setMessageListener(new JmsConsumer(handler));
		LOG.debug("JMS Consumer created");
		return new ConsumerServiceResource(session, messageConsumer);
	}

	@Override
	protected JmsServiceResource createResource(DestinationDescriptor destinationDescriptor, BundleContext bundleContext) throws Exception {
		if (destinationDescriptor == null) {
			throw new IllegalArgumentException("DestinationDescriptor is not set");
		}
		LOG.debug("Creating JMS Producer for descriptor {}", destinationDescriptor);
		JmsDestinationDescriptor jmsDestinationDescriptor = (JmsDestinationDescriptor) destinationDescriptor;
		publisherLock.lockInterruptibly();
		Session session;
		try {
			session = createSession(jmsDestinationDescriptor);
		} finally {
			publisherLock.unlock();
		}
		Destination destination = createDestination(jmsDestinationDescriptor, session);
		MessageProducer messageProducer = session.createProducer(destination);
		LOG.debug("JMS Producer created");
		return new ProducerServiceResource(session, messageProducer, serviceConfig, bundleContext);
	}

	private Destination createDestination(JmsDestinationDescriptor descriptor, Session session) throws Exception {
		Destination destination;
		if (descriptor.getDestinationType() == JmsDestinationType.QUEUE) {
			LOG.debug("Creating queue for {}", descriptor);
			destination = session.createQueue(descriptor.getDestinationName());
		} else {
			LOG.debug("Creating topic for {}", descriptor);
			destination = session.createTopic(descriptor.getDestinationName());
		}
		return destination;
	}

	private Session createSession(JmsDestinationDescriptor descriptor) throws Exception {
		LOG.debug("Creating session for {}", descriptor);
		Session session;
		if (descriptor.getAcknowledgeType() == AcknowledgeType.AUTO) {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} else {
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		}
		return session;
	}

}
