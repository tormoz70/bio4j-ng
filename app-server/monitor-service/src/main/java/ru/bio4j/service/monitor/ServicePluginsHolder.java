package ru.bio4j.service.monitor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.monitor.plugin.AbstractPlugin;

public class ServicePluginsHolder implements ServiceListener {

	private final static Logger LOG = LoggerFactory.getLogger(ServicePluginsHolder.class);
	
	protected static class Plugin {
		
		private AbstractPlugin plugin;
		
		private ServiceReference<Monitor> serviceReference;
		
		private BundleContext bundleContext;

		public Plugin(ServiceReference<Monitor> serviceReference,
				BundleContext bundleContext) {
			super();
			this.serviceReference = serviceReference;
			this.bundleContext = bundleContext;
		}

		public AbstractPlugin getPlugin() {
			if (plugin == null) {
				Monitor monitor = bundleContext.getService(serviceReference);
				if (monitor != null) {
					plugin = monitor.createPlugin();
				}
			}
			return plugin;
		}
	}
	
	private BundleContext bundleContext;
	
	private ConcurrentHashMap<String, Plugin> plugins;
	
	public ServicePluginsHolder(BundleContext bundleContext) {
		super();
		this.bundleContext = bundleContext;
		this.plugins = new ConcurrentHashMap<>();
	}
	
	public void create() {
		try {
			bundleContext.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + Monitor.class.getName() + ")");
			Collection<ServiceReference<Monitor>> serviceReferences = bundleContext.getServiceReferences(Monitor.class, null);
			if (serviceReferences != null) {
				LOG.debug("Load registered services");
				for (ServiceReference<Monitor> serviceReference : serviceReferences) {
					registerService(serviceReference);
				}
			}
		} catch (InvalidSyntaxException e) {
            throw new InternalError( "Failed getting existing Servlet services: " + e.getMessage() );
		}
	}

	private void registerService(ServiceReference<Monitor> serviceReference) {
		String label = getLabel(serviceReference);
		LOG.debug("Load service with label " + label);
		plugins.put(label, new Plugin(serviceReference, bundleContext));
	}
	
	private String getLabel(ServiceReference<Monitor> serviceReference) {
		Monitor monitor = bundleContext.getService(serviceReference);
		return monitor.pluginLabel();
	}
	
	private void unregisterService(ServiceReference<Monitor> serviceReference) {
		String label = getLabel(serviceReference);
		plugins.remove(label);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void serviceChanged(ServiceEvent event) {
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			registerService((ServiceReference<Monitor>) event.getServiceReference());
			break;
		case ServiceEvent.UNREGISTERING:
			unregisterService((ServiceReference<Monitor>) event.getServiceReference());
			break;
		}
	}

	public Set<Map.Entry<String, Plugin>> getPlugins() {
		return plugins.entrySet();
	}

	public Plugin getPlugin(String pluginLabel) {
		return plugins.get(pluginLabel);
	}
	
	public void dispose() {
		bundleContext.removeServiceListener(this);
		plugins.clear();
	}

}
