package ru.bio4j.service.ehcache.activator;

import org.osgi.framework.BundleContext;
import ru.bio4j.service.DefaultBundleActivator;
import ru.bio4j.service.ServiceController;

public class CacheServiceBundleActivator extends DefaultBundleActivator {

	@Override
	protected ServiceController createServiceController(
			BundleContext bundleContext) {
		return new CacheServiceController(bundleContext);
	}

}
