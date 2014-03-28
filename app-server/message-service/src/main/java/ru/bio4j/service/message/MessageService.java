package ru.bio4j.service.message;

public interface MessageService {

	Consumer createConsumer(DestinationDescriptor descriptor, MessageHandler messageHandler) throws Exception;
	
	Producer createProducer(DestinationDescriptor descriptor) throws Exception;

}
