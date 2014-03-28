package ru.bio4j.service.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageBuilder<T> {
	
	private final T payload;
	
	private final HashMap<String, Object> headers;
	
	private MessageBuilder(T payload) {
		this.payload = payload;
		this.headers = new HashMap<>();
	}
	
	public static <T> MessageBuilder<T> create(T payload) {
        return new MessageBuilder<>(payload);
	}

	public MessageBuilder<T> copyHeaders(Map<String, ?> headersToCopy) {
		Set<String> keys = headersToCopy.keySet();
		for (String key : keys) {
			this.setHeader(key, headersToCopy.get(key));
		}
		return this;
	}

	public MessageBuilder<T> setHeader(String headerName, Object headerValue) {
		this.headers.put(headerName, headerValue);
		return this;
	}
	
	public static <T> MessageBuilder<T> fromMessage(Message<T> message) {
		return create(message.getPayload()).copyHeaders(message.getMessageHeaders());
	}
	
	public Message<T> build() {
		return new Message<>(this.payload, new MessageHeaders(headers));
	}
	
}
