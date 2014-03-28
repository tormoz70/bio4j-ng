package ru.bio4j.service.message;

import java.util.Map;

public interface AmqpDestinationDescriptor extends DestinationDescriptor {

	@Override
	String getDestinationName();

	String getExchangeName();

	String getRoutingKey();

	AmqpDestinationType getDestinationType();

	String getExchangeType();
	
	boolean isAck();
	
	Map<String, Object> getArguments();
	
}