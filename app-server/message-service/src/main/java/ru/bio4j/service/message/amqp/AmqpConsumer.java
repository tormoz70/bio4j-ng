package ru.bio4j.service.message.amqp;

import java.io.IOException;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.message.AckMessageHandler;
import ru.bio4j.service.message.MessageHandler;
import ru.bio4j.service.message.Message;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class AmqpConsumer extends DefaultConsumer implements EventHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(AmqpConsumer.class);

	private final MessageHandler handler;
	private final boolean isAutoAck;
	
	public AmqpConsumer(Channel channel, MessageHandler handler, boolean isAutoAck) {
		super(channel);
		if (handler == null) {
			throw new IllegalArgumentException("MessageHandler is not set");
		}
		this.isAutoAck = isAutoAck;
		this.handler = handler;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		if (!isAutoAck) {
			long deliveryTag = envelope.getDeliveryTag();
			if (handler instanceof AckMessageHandler) {
				((AckMessageHandler)handler).processMessage(body, deliveryTag);
			} else {
				getChannel().basicAck(deliveryTag, false);
			}
		}
		handler.processMessage(body);
	}
	
	@Override
	public void handleEvent(Event event) {
		try {
			Long ackTag = (Long) event.getProperty(Message.ACK_TAG);
			getChannel().basicAck(ackTag, true);
		} catch (IOException e) {
			LOG.error("Ooops... can't ack", e);
		}
	}
}
