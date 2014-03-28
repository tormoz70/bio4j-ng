package ru.bio4j.service.message;

import ru.bio4j.service.ServiceLifecycle;

public interface AmqpMessageService extends MessageService, ServiceLifecycle {
	public void deleteQueue(DestinationDescriptor desc);
}
