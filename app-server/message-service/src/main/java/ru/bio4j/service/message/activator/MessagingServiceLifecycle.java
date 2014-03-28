package ru.bio4j.service.message.activator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.ServiceLifecycle;

public class MessagingServiceLifecycle implements ServiceLifecycle {

	private Status status = Status.STOPPED;

	private final static Logger LOG = LoggerFactory.getLogger(MessagingServiceLifecycle.class);

	private final JmsMessageServiceFactory jmsMessageServiceFactory;

	private final AmqpMessageServiceFactory amqpMessageServiceFactory;

	public MessagingServiceLifecycle(
			JmsMessageServiceFactory jmsMessageServiceFactory,
			AmqpMessageServiceFactory amqpMessageServiceFactory) {
		super();
		this.jmsMessageServiceFactory = jmsMessageServiceFactory;
		this.amqpMessageServiceFactory = amqpMessageServiceFactory;
	}

	@Override
	public String getName() {
		return "Messaging Connection Provider";
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Integer getOrder() {
		return ServiceConstants.MESSAGE_SERVICE_ORDER;
	}

	@Override
	public void startWork() {
		status = Status.ERROR;
		try {
			LOG.info("Creating Connection To JMS Server");
			//jmsMessageServiceFactory.connect();
			LOG.info("Creating Creating Connection To AMQP Server");
			//amqpMessageServiceFactory.connect();
			status = Status.STARTED;
		} catch (Exception e) {
			LOG.error("Ooops ... ", e);
		}
	}

	@Override
	public void finishWork() {
		try {
			LOG.info("Closing Connection To JMS Server");
			//jmsMessageServiceFactory.disconnect();
			LOG.info("Closing Connection To AMQP Server");
			amqpMessageServiceFactory.disconnect();
		} catch (Exception e) {
			LOG.error("Ooops ... ", e);
		}
		status = Status.STOPPED;
	}

}
