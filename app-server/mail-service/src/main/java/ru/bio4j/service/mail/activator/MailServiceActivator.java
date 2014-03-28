package ru.bio4j.service.mail.activator;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.DefaultBundleActivator;
import ru.bio4j.service.ServiceController;

public class MailServiceActivator extends DefaultBundleActivator {

	@Override
	protected ServiceController createServiceController(
			BundleContext bundleContext) {
		return new MailServiceController(bundleContext);
	}

}
