package ru.bio4j.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public abstract class DefaultBundleActivator implements BundleActivator {

	private ServiceController serviceController; 
	
	@Override
	public void start(BundleContext context) throws Exception {
		serviceController = createServiceController(context);
		serviceController.start();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (serviceController != null) {
			serviceController.stop();
		}
	}

	protected abstract ServiceController createServiceController(BundleContext bundleContext);

}
