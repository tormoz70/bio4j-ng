package ru.bio4j.service.bootstrap.activator;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.DefaultBundleActivator;
import ru.bio4j.service.ServiceController;

public class BootstrapServiceActivator extends DefaultBundleActivator {

	@Override
	protected ServiceController createServiceController(
			BundleContext bundleContext) {
		return new BootstrapServiceController(bundleContext);
	}

}
