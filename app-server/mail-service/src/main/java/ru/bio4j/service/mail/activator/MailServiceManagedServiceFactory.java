package ru.bio4j.service.mail.activator;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class MailServiceManagedServiceFactory implements ServiceFactory<ManagedService> {

	private final MailServiceFactory mailServiceFactory;
	
	public MailServiceManagedServiceFactory(
			MailServiceFactory mailServiceFactory) {
		super();
		this.mailServiceFactory = mailServiceFactory;
	}

	@Override
	public ManagedService getService(Bundle bundle,
			ServiceRegistration<ManagedService> registration) {
		return new ManagedService() {
			@SuppressWarnings("unchecked")
			@Override
			public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
				mailServiceFactory.updateConfig((Dictionary<String, ?>) properties);
			}
		};
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<ManagedService> registration,
			ManagedService service) {
	}

}
