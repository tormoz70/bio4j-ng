package ru.bio4j.service.message;

import java.util.Map;

public class CustomAmqpDestinationDescriptor implements AmqpDestinationDescriptor {

	private final String destinationName;

	private final String exchageName;

	private final String routingKey;

	private final String exchageType;

	private final AmqpDestinationType destinationType;
	
	private final Map<String, Object> arguments;
	
	private final boolean ack;

	public CustomAmqpDestinationDescriptor(String exchageName, String routingKey, AmqpDestinationType destinationType, Map<String, Object> arguments) {
		this(null, exchageName, "direct", routingKey, destinationType, arguments, true);
	}
	
	public CustomAmqpDestinationDescriptor(String exchageName, String routingKey, AmqpDestinationType destinationType) {
		this(null, exchageName, "direct", routingKey, destinationType);
	}

	public CustomAmqpDestinationDescriptor(String destinationName, String exchageName, String routingKey, AmqpDestinationType destinationType) {
		this(destinationName, exchageName, "direct", routingKey, destinationType);
	}
	
	public CustomAmqpDestinationDescriptor(String destinationName, String exchageName, String routingKey, AmqpDestinationType destinationType, Map<String, Object> arguments, boolean ack) {
		this(destinationName, exchageName, "direct", routingKey, destinationType, arguments, ack);
	}

	public CustomAmqpDestinationDescriptor(String destinationName, String exchageName, String exchageType, String routingKey, AmqpDestinationType destinationType) {
		this(destinationName, exchageName, exchageType, routingKey, destinationType, null, true);
	}
	
	public CustomAmqpDestinationDescriptor(String destinationName, String exchageName, String exchageType, String routingKey, AmqpDestinationType destinationType, 
			Map<String, Object> arguments, boolean ack) {
		this.destinationName = destinationName;
		this.exchageName = exchageName;
		this.routingKey = routingKey;
		this.destinationType = destinationType;
		this.exchageType = exchageType;
		this.arguments = arguments;
		this.ack = ack;
	}

	@Override
	public String getDestinationName() {
		return destinationName;
	}

	@Override
	public String getExchangeName() {
		return exchageName;
	}

	@Override
	public String getRoutingKey() {
		return routingKey;
	}

	@Override
	public AmqpDestinationType getDestinationType() {
		return destinationType;
	}

	@Override
	public String getExchangeType() {
		return exchageType;
	}
	
	@Override
	public Map<String, Object> getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return "CustomAmqpDestinationDescriptor [destinationName="
				+ destinationName + ", exchageName=" + exchageName
				+ ", routingKey=" + routingKey + ", exchageType=" + exchageType
				+ ", destinationType=" + destinationType + ", arguments="
				+ arguments + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exchageName == null) ? 0 : exchageName.hashCode());
		result = prime * result + ((destinationName == null) ? 0 : destinationName.hashCode());
		result = prime * result + ((routingKey == null) ? 0 : routingKey.hashCode());
		result = prime * result + ((destinationType == null) ? 0 : destinationType.hashCode());
		return result;
	}

	public boolean isAck() {
		return ack;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomAmqpDestinationDescriptor other = (CustomAmqpDestinationDescriptor) obj;
		if (exchageName == null) {
			if (other.exchageName != null)
				return false;
		} else if (!exchageName.equals(other.exchageName))
			return false;
		if (routingKey == null) {
			if (other.routingKey != null)
				return false;
		} else if (!routingKey.equals(other.routingKey))
			return false;
		if (destinationType == null) {
			if (other.destinationType != null)
				return false;
		} else if (!destinationType.equals(other.destinationType))
			return false;
		return true;
	}

}
