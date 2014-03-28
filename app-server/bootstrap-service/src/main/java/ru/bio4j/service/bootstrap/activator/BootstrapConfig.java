package ru.bio4j.service.bootstrap.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.ServiceConfig;

public class BootstrapConfig extends ServiceConfig {
	
	public BootstrapConfig(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	public void config(Dictionary<String, ?> parameters) {
		if (parameters == null) {
			parameters = new Hashtable<String, Object>();
		}
	}

}
