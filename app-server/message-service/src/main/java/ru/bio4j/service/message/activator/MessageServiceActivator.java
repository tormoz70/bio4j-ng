package ru.bio4j.service.message.activator;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.DefaultBundleActivator;
import ru.bio4j.service.ServiceController;

public class MessageServiceActivator extends DefaultBundleActivator {

	@Override
	protected ServiceController createServiceController(
			BundleContext bundleContext) {
		return new MessageServiceController(bundleContext);
	}

}
