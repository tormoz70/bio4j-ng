package ru.bio4j.service.message;

public interface AckMessageHandler extends MessageHandler {
	
	<T> void processMessage(T message, Object deliveryTag);

}
