package ru.bio4j.service.message.common;

import ru.bio4j.service.message.Consumer;

public class ResourceConsumer implements Consumer {

	private final ServiceResource serviceResource; 
	
	public ResourceConsumer(ServiceResource serviceResource) {
		super();
		this.serviceResource = serviceResource;
	}

	@Override
	public void close() {
		if (serviceResource != null) {
			serviceResource.close();
		}
	}

}
