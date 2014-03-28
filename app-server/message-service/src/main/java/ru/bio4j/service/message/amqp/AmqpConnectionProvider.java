package ru.bio4j.service.message.amqp;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.message.AmqpDestinationDescriptor;
import ru.bio4j.service.message.AmqpDestinationType;
import ru.bio4j.service.message.DestinationDescriptor;
import ru.bio4j.service.message.MessageHandler;
import ru.bio4j.service.message.amqp.plugin.AmqpServiceManager;
import ru.bio4j.service.message.amqp.plugin.AmqpServiceMonitorPlugin;
import ru.bio4j.service.message.amqp.statistic.AmqpChannelsStatistic;
import ru.bio4j.service.message.amqp.statistic.AmqpConnectionInfo;
import ru.bio4j.service.message.common.AbstractConnectionProvider;
import ru.bio4j.service.ServiceConfig;
import ru.bio4j.service.message.EventUtil;
import ru.bio4j.service.monitor.plugin.AbstractPlugin;

import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AmqpConnectionProvider extends AbstractConnectionProvider<AmqpServiceResource> implements AmqpServiceManager {

	private static Logger LOG = LoggerFactory.getLogger(AmqpConnectionProvider.class);

	private final AmqpChannelsStatistic channelsStatistic;

	private Connection connection;

	public AmqpConnectionProvider() {
		super();
		channelsStatistic = new AmqpChannelsStatistic();
	}

	@Override
	public String pluginLabel() {
		return getClass().getName();
	}

	@Override
	public String pluginTitle() {
		return "AMQP Service";
	}

	@Override
	public AmqpConnectionInfo getConnectionInfo() {
		AmqpConnectionInfo connectionInfo = new AmqpConnectionInfo();
        if (connection == null) {
            return connectionInfo;
        }
		connectionInfo.setInetAddress(connection.getAddress());
		connectionInfo.setPort(connection.getPort());
		connectionInfo.setServerProperties(connection.getServerProperties());
		connectionInfo.setOpen(connection.isOpen());
		return connectionInfo;
	}

	@Override
	public AmqpChannelsStatistic getChannelsStatistic() {
		return channelsStatistic;
	}

	@Override
	public AbstractPlugin createPlugin() {
		return new AmqpServiceMonitorPlugin(this);
	}

	@Override
	public void connect(ServiceConfig serviceConfig) throws Exception {
		LOG.debug("Creating AMQP connection");
		AmqpServiceConfig amqpServiceConfig = (AmqpServiceConfig) serviceConfig;
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(amqpServiceConfig.getHost());
		connectionFactory.setVirtualHost(amqpServiceConfig.getVirtualHost());
		connectionFactory.setUsername(amqpServiceConfig.getUsername());
		connectionFactory.setPassword(amqpServiceConfig.getPassword());
		connectionFactory.setPort(amqpServiceConfig.getPort());
		connection = connectionFactory.newConnection();
		LOG.debug("AMQP connection established");
	}

	@Override
	public void disconnect() {
		closeResources();
		if (connection != null) {
			try {
				LOG.debug("Closing connection to AMQP server");
				connection.close();
				LOG.debug("Connection to AMQP server closed");
			} catch (IOException e) {
				LOG.error("Could not close AMQP connection", e);
			}
		}
	}

	@Override
	protected AmqpServiceResource createResource(DestinationDescriptor destination, MessageHandler handler, BundleContext context) throws Exception {
		checkArguments(destination, handler);
		LOG.debug("Creating AMQP Consumer for " + destination.getDestinationName());
		AmqpDestinationDescriptor destinationDescriptor = (AmqpDestinationDescriptor) destination;
		Channel channel = connection.createChannel();
		if (destinationDescriptor.getDestinationType() == AmqpDestinationType.QUEUE) {
			attachToQueue(destinationDescriptor, channel);
		} else {
			exchangeDeclare(destinationDescriptor, channel);
		}
		AmqpConsumer consumer = createConsumer(channel, destinationDescriptor, handler, context, destinationDescriptor.isAck());
		channel.basicConsume(destinationDescriptor.getDestinationName(), destinationDescriptor.isAck(), "", 
				false, false, null, consumer);
		channelsStatistic.increment(destinationDescriptor);
		LOG.debug("AMQP Consumer created");
		return new AmqpServiceResource(destinationDescriptor, channel);
	}

	private void checkArguments(DestinationDescriptor destination, MessageHandler handler) {
		if (destination == null) {
			throw new IllegalArgumentException("QueueDescriptor is not set");
		}
		if (handler == null) {
			throw new IllegalArgumentException("MessageHandler is not set");
		}
	}

	private AmqpConsumer createConsumer(Channel channel, AmqpDestinationDescriptor destinationDescriptor, 
			MessageHandler handler, BundleContext context, boolean isAutoAck) {
		AmqpConsumer consumer = new AmqpConsumer(channel, handler, isAutoAck);
		if (context != null && !isAutoAck) {
			context.registerService(EventHandler.class.getName(), consumer, 
					EventUtil.createEventHandlerProperties(ServiceConstants.ACK_CUR_TOPIC));
		}
		return consumer;
	}

	private void attachToQueue(AmqpDestinationDescriptor queueDescriptor, Channel channel) throws Exception {
		exchangeDeclare(queueDescriptor, channel);
		Queue.DeclareOk declareOk = channel.queueDeclare(queueDescriptor.getDestinationName(), true, false, false, null);
		channel.queueBind(declareOk.getQueue(), queueDescriptor.getExchangeName(), queueDescriptor.getRoutingKey(), queueDescriptor.getArguments());
		LOG.debug("Created connection for queue ", declareOk.getQueue());
	}

	private void exchangeDeclare(AmqpDestinationDescriptor queueDescriptor, Channel channel) throws Exception {
		channel.exchangeDeclare(queueDescriptor.getExchangeName(), queueDescriptor.getExchangeType());
	}

	@Override
	protected AmqpServiceResource createResource(DestinationDescriptor destination, BundleContext bundleContext) throws Exception {
		if (destination == null) {
			throw new IllegalArgumentException("QueueDescriptor is not set");
		}
		LOG.debug("Creating AMQP Producer for " + destination.getDestinationName());
		AmqpDestinationDescriptor destinationDescriptor = (AmqpDestinationDescriptor) destination;
		LOG.debug("Attempting to create publishing channel for " + destinationDescriptor.getDestinationName());
		Channel channel = connection.createChannel();
		if (destinationDescriptor.getDestinationType() == AmqpDestinationType.QUEUE) {
			attachToQueue(destinationDescriptor, channel);
		} else {
			exchangeDeclare(destinationDescriptor, channel);
		}
		channelsStatistic.increment(destinationDescriptor);
		LOG.debug("AMQP Producer created");
		return new AmqpServiceResource(destinationDescriptor, channel);
	}

}
