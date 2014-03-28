package ru.bio4j.service.bootstrap.activator;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import ru.bio4j.service.bootstrap.BootstrapService;

public class BootstrapServiceManagedServiceFactory implements ServiceFactory<ManagedService> {

	private final BootstrapService monitorService;

	public BootstrapServiceManagedServiceFactory(BootstrapService monitorService) {
		super();
		this.monitorService = monitorService;
	}

	@Override
	public ManagedService getService(Bundle bundle,
			ServiceRegistration<ManagedService> registration) {
		return new ManagedService() {
			@SuppressWarnings("unchecked")
			@Override
			public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
				synchronized (monitorService) {
					monitorService.reconfigure(properties);
				}
			}
		};
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<ManagedService> registration,
			ManagedService service) {
	}

}
