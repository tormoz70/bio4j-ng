package ru.bio4j.service.monitor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.apache.felix.webconsole.WebConsoleConstants.PLUGIN_LABEL;
import static org.apache.felix.webconsole.WebConsoleConstants.SERVICE_NAME;

public class ServicePluginActivator implements BundleActivator {

	private final static Logger LOG = LoggerFactory.getLogger(ServicePluginActivator.class);
	
	public final static String SERVLET_PATH = "servicesmonitor";
	
	private ServicePluginsHolder pluginHolder;
	
	public void start(BundleContext context) throws Exception {
		LOG.debug("Attempting to start service ... ");
		
		pluginHolder = new ServicePluginsHolder(context);
		pluginHolder.create();
		
		Dictionary<String, Object> pluginProps = new Hashtable<>();
		pluginProps.put(PLUGIN_LABEL, SERVLET_PATH);
		pluginProps.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);
		
		new AbstractServiceFactory<ServicesMonitorConsolePanel>(context,
				pluginProps,
				SERVICE_NAME,
				ServicesMonitorConsolePanel.class.getName()) {
			@Override
			public ServicesMonitorConsolePanel createObject() {
				LOG.debug("Create object for service");
				return new ServicesMonitorConsolePanel(pluginHolder);
			}
		};
		LOG.info("Monitor Service Started");
	}

	public void stop(BundleContext context) throws Exception {
		pluginHolder.dispose();
	}

}
