package ru.bio4j.service.message.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.message.DestinationDescriptor;
import ru.bio4j.service.message.MessageHandler;
import ru.bio4j.service.message.Producer;
import ru.bio4j.service.ServiceConfig;

public abstract class AbstractConnectionProvider<R extends ServiceResource> {
	
	private final Map<DestinationDescriptor, R> resources;

	private final static Logger LOG = LoggerFactory.getLogger(AbstractConnectionProvider.class);
	
	protected AbstractConnectionProvider() {
		super();
		this.resources = new ConcurrentHashMap<>();
	}

	public ResourceConsumer createConsumerFor(DestinationDescriptor destination, MessageHandler handler, BundleContext bundleContext) throws Exception {
		R resource = createResource(destination, handler, bundleContext);
		resources.put(destination, resource);
		return new ResourceConsumer(resource);
	}
	
	public Producer createProducerFor(DestinationDescriptor destination, BundleContext bundleContext) throws Exception {
		R resource = createResource(destination, bundleContext);
		resources.put(destination, resource);
		return ProducerFactory.createProducerFor(resource);
	}

	public void closeResources() {
		for (ServiceResource serviceResource : resources.values()) {
			serviceResource.close();
		}
		LOG.info("Resources closed");
		resources.clear();
	}
	
	public R getResource(DestinationDescriptor desc) {
		return resources.get(desc);
	}
	
	public abstract void connect(ServiceConfig serviceConfig) throws Exception;
	
	public abstract void disconnect();
	
	protected abstract R createResource(DestinationDescriptor destination, MessageHandler handler, BundleContext bundleContext) throws Exception;
	
	protected abstract R createResource(DestinationDescriptor destination, BundleContext bundleContext) throws Exception;
}
