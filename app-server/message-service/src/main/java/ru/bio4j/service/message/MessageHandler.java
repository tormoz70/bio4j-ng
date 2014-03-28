package ru.bio4j.service.message;

public interface MessageHandler {
	
	<T> void processMessage(T message);

}
