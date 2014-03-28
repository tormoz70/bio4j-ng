package ru.bio4j.service.message.amqp;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;

import ru.bio4j.service.message.AmqpDestinationDescriptor;
import ru.bio4j.service.message.common.ServiceResource;

public class AmqpServiceResource implements ServiceResource {

	private final static Logger LOG = LoggerFactory.getLogger(AmqpServiceResource.class); 
	
	private final String uuid;
	
	private final AmqpDestinationDescriptor amqpDestinationDescriptor;
	
	private final Channel channel;
	
	public AmqpServiceResource(AmqpDestinationDescriptor amqpDestinationDescriptor, Channel channel) {
		super();
		this.amqpDestinationDescriptor = amqpDestinationDescriptor;
		this.channel = channel;
		this.uuid = UUID.randomUUID().toString();
	}

	public void publish(byte[] body) {
		try {
			channel.basicPublish(amqpDestinationDescriptor.getExchangeName(), amqpDestinationDescriptor.getRoutingKey(), null, body);
		} catch (IOException e) {
			LOG.error("Could not send object", e);
		}
	}
	
	public void deleteQueue() {
		try {
			channel.queueDelete(amqpDestinationDescriptor.getDestinationName());
		} catch (IOException e) {
			LOG.error("Can't delete queue", e);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmqpServiceResource other = (AmqpServiceResource) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	@Override
	public void close() {
		if (channel != null) {
			try {
				if (channel.isOpen()) {
					channel.close();
					LOG.info("Channel {} closed", uuid);
				}
			} catch (IOException e) {
				LOG.error("Could not close channel", e);
			}
		}
	}

}
