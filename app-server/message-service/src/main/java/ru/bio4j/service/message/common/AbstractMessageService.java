package ru.bio4j.service.message.common;

import org.osgi.framework.BundleContext;
import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.ServiceLifecycle;
import ru.bio4j.service.message.*;

import java.util.UUID;

public abstract class AbstractMessageService<R extends ServiceResource> implements MessageService, ServiceLifecycle, Closable {

	private final String serviceId;
	private Status status = Status.STOPPED;
	private final BundleContext bundleContext;
	protected AbstractConnectionProvider<R> connectionProvider;

	protected AbstractMessageService(AbstractConnectionProvider<R> connectionProvider, BundleContext bundleContext) {
		this.connectionProvider = connectionProvider;
		this.serviceId = UUID.randomUUID().toString();
		this.bundleContext = bundleContext;
	}

	@Override
	public Consumer createConsumer(DestinationDescriptor destination, MessageHandler handler) throws Exception {
		return connectionProvider.createConsumerFor(destination, handler, bundleContext);
	}

	@Override
	public Producer createProducer(DestinationDescriptor destination) throws Exception {
		return connectionProvider.createProducerFor(destination, bundleContext);
	}

	@Override
	public void close() {
		if (connectionProvider != null) {
			connectionProvider.closeResources();
		}
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Integer getOrder() {
		return ServiceConstants.MESSAGE_SERVICE_ORDER;
	}

	@Override
	public void startWork() {
		status = Status.STARTED;
	}

	@Override
	public void finishWork() {
		close();
		status = Status.STOPPED;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
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
		@SuppressWarnings("unchecked")
		AbstractMessageService<R> other = (AbstractMessageService<R>) obj;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		return true;
	}
}
