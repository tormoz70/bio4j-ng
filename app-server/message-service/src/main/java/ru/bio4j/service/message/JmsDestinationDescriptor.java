package ru.bio4j.service.message;

public class JmsDestinationDescriptor implements DestinationDescriptor {

	private final String destinationName;
	
	private final JmsDestinationType destinationType;
	
	private final AcknowledgeType acknowledgeType;
	
	private final String selectorString;
	
	public JmsDestinationDescriptor(String destinationName) {
		this(destinationName, JmsDestinationType.QUEUE, AcknowledgeType.AUTO, null);
	}
	
	public JmsDestinationDescriptor(String destinationName, JmsDestinationType destinationType) {
		this(destinationName, destinationType, AcknowledgeType.AUTO, null);
	}
	
	public JmsDestinationDescriptor(String destinationName, String selectorString) {
		this(destinationName, JmsDestinationType.QUEUE, AcknowledgeType.AUTO, selectorString);
	}

	public JmsDestinationDescriptor(String destinationName,
			JmsDestinationType destinationType, AcknowledgeType acknowledgeType, String selectorString) {
		super();
		this.destinationName = destinationName;
		this.destinationType = destinationType;
		this.acknowledgeType = acknowledgeType;
		this.selectorString = selectorString;
	}
	
	@Override
	public String getDestinationName() {
		return destinationName;
	}

	public JmsDestinationType getDestinationType() {
		return destinationType;
	}

	public AcknowledgeType getAcknowledgeType() {
		return acknowledgeType;
	}

	@Override
	public String toString() {
		return "JmsDestinationDescriptor [destinationName=" + destinationName
				+ ", destinationType=" + destinationType + ", acknowledgeType="
				+ acknowledgeType + "]";
	}

	public String getSelectorString() {
		return selectorString;
	}
}
