package ru.bio4j.service.message.activator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ServiceConfig;
import ru.bio4j.service.message.MessageService;
import ru.bio4j.service.message.common.AbstractConnectionProvider;
import ru.bio4j.service.message.common.Closable;
import ru.bio4j.service.message.common.ServiceResource;

import java.util.Dictionary;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class AbstractMessageServiceFactory<T extends MessageService, R extends ServiceResource> implements ServiceFactory<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageServiceFactory.class);

	private final ServiceConfig serviceConfig;

	private final ArrayBlockingQueue<T> services;

	private AbstractConnectionProvider<R> connectionProvider;

	public AbstractMessageServiceFactory(ServiceConfig serviceConfig) {
		super();
		this.serviceConfig = serviceConfig;
		this.services = new ArrayBlockingQueue<>(100);
	}

	@Override
	public T getService(Bundle bundle, ServiceRegistration<T> registration) {
		return getService(bundle.getBundleContext(), registration);
	}
	
	public T getService(BundleContext bundleContext, ServiceRegistration<T> registration) {
		T service = createService(connectionProvider, bundleContext);
		services.add(service);
		return service;
	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration<T> registration, T service) {
		if (service != null) {
			services.remove(service);
			((Closable) service).close();
		}
	}

	public void start() throws Exception {
		connectionProvider = createConnectionProvider();
	}

	public void updateConfig(Dictionary<String, ?> parameters) throws Exception {
		serviceConfig.config(parameters);
        LOG.info("service updated new values are {}", serviceConfig);
		if (connectionProvider == null) {
			connectionProvider = createConnectionProvider();
		}
		disconnect();
		//connect(serviceConfig);
	}

	public void stop() throws Exception {
	}

	public void connect() throws Exception {
		if (connectionProvider != null) {
			connectionProvider.connect(serviceConfig);
		}
	}

	public void disconnect() throws Exception {
		if (connectionProvider != null) {
			connectionProvider.disconnect();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serviceConfig == null) ? 0 : serviceConfig.hashCode());
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
		AbstractMessageServiceFactory<?, ?> other = (AbstractMessageServiceFactory<?, ?>) obj;
		if (serviceConfig == null) {
			if (other.serviceConfig != null)
				return false;
		} else if (!serviceConfig.equals(other.serviceConfig))
			return false;
		return true;
	}

	//FIXME i will remove this code in the nearest future
	protected AbstractConnectionProvider<R> getConnectionProvider() {
		return connectionProvider;
	}

	protected abstract T createService(AbstractConnectionProvider<R> connectionProvider, BundleContext bundleContext);

	protected abstract AbstractConnectionProvider<R> createConnectionProvider();

	public ServiceConfig getServiceConfig() {
		return serviceConfig;
	}
	
}
