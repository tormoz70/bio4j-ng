package ru.bio4j.service.message;

public class Message<T> {
	
	private final T payload;
	
	private final MessageHeaders messageHeaders;
	
	public final static String ACK_TAG = "delivery-tag";
	
	public Message(T payload) {
		this(payload, new MessageHeaders());
	}
	
	public Message(T payload, MessageHeaders messageHeaders) {
		this.messageHeaders = messageHeaders;
		this.payload = payload;
	}
	
	public MessageHeaders getMessageHeaders() {
		return messageHeaders;
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getHeaderValue(String key) {
		return (V) messageHeaders.get(key);
	} 
	
	public <V> void setHeaderValue(String key, V value) {
		messageHeaders.put(key, value);
	} 
	
	@SuppressWarnings("unchecked")
	public <V> V removeHeaderValue(String key) {
		return (V) messageHeaders.remove(key);
	} 
	
	public T getPayload() {
		return payload;
	}
	
	public Object getAckTag() {
		return messageHeaders.get(ACK_TAG);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
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
		Message<?> other = (Message<?>) obj;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [payload=" + payload + "]";
	}
}
