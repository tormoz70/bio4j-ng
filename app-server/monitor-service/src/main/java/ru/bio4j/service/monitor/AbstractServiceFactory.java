package ru.bio4j.service.monitor;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractServiceFactory<T> implements ServiceFactory<T> {
	
	private final static String BIO4J_SERVICES = "Bio4j Services (${0})";
	
	private final static String VENDOR_NAME  = "Bio4j Broker";
	
	private final AtomicInteger counter;

	private T service;

	public AbstractServiceFactory(BundleContext context, Dictionary<String, Object> properties, String ... serviceNames) {
		// ensure properties
		if (properties == null) {
			properties = new Hashtable<>();
		}
		// default settings
		properties.put(Constants.SERVICE_DESCRIPTION, String.format(BIO4J_SERVICES, serviceNames[0]));
		properties.put(Constants.SERVICE_VENDOR, VENDOR_NAME);

		context.registerService(serviceNames, this, properties);

		counter = new AtomicInteger(0);
	}

	@Override
	public T getService(Bundle bundle, ServiceRegistration<T> registration) {
		counter.incrementAndGet();
		if (service == null) {
			service = createObject();
		}
		return service;
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<T> registration, T service) {
		int value = counter.decrementAndGet();
		if (value <= 0) {
			service = null;
		}
	}
	
	protected abstract T createObject();

}
