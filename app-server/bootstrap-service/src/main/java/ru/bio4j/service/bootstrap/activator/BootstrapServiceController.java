package ru.bio4j.service.bootstrap.activator;

import org.apache.felix.shell.Command;
import org.osgi.framework.*;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.ServiceController;
import ru.bio4j.service.ServiceLifecycle;
import ru.bio4j.service.bootstrap.Bootstrap;
import ru.bio4j.service.bootstrap.BootstrapService;
import ru.bio4j.service.bootstrap.command.*;
import ru.bio4j.service.mail.MailService;
import ru.bio4j.service.message.EventUtil;

import java.util.Hashtable;

public class BootstrapServiceController implements ServiceController, FrameworkListener, ServiceListener, EventHandler {

	private final static Logger LOG = LoggerFactory.getLogger(BootstrapServiceController.class);
	
	private final static String LISTENER_FILTER = "(|(" + Constants.OBJECTCLASS + "=" + ServiceLifecycle.class.getName() + ")(" + Constants.OBJECTCLASS + "=" + MailService.class.getName() + "))";
	private final BundleContext bundleContext;
	private BootstrapService bootstrapService;
	
	public BootstrapServiceController(BundleContext bundleContext) {
		super();
		this.bundleContext = bundleContext;
	}

	@Override
	public String getServiceName() {
		return "Bootstrap Monitor";
	}

	@Override
	public void start() throws Exception {
		BootstrapConfig monitorConfig = new BootstrapConfig(bundleContext);
		monitorConfig.config(null);
		bootstrapService = new BootstrapService(monitorConfig);
		bundleContext.addServiceListener(this, LISTENER_FILTER);
		bundleContext.addFrameworkListener(this);
		bundleContext.registerService(Bootstrap.class, bootstrapService, null);
		bundleContext.registerService(ManagedService.class.getName(), new BootstrapServiceManagedServiceFactory(bootstrapService), createManagedServiceProperties());
		bundleContext.registerService(EventHandler.class.getName(), this, 
				EventUtil.createEventHandlerProperties(ServiceConstants.COMMAND_TOPIC));
		addShellCommands();
		LOG.info("Bootstrap Service Started. Parameters {}", monitorConfig);
	}
	
	private void addShellCommands() {
		bundleContext.registerService(Command.class, new ListServicesCommand(bootstrapService), null);
		bundleContext.registerService(Command.class, new StartServiceCommand(bootstrapService), null);
		bundleContext.registerService(Command.class, new StopServiceCommand(bootstrapService), null);
		bundleContext.registerService(Command.class, new RestartServiceCommand(bootstrapService), null);
		bundleContext.registerService(Command.class, new ErrorsQueueListenerCommand(), null);
	}
	
	private Hashtable<String, ?> createManagedServiceProperties() {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_PID, "ru.bio4j.services.monitor.config");
		props.put(Constants.SERVICE_RANKING, ServiceConstants.MONITOR_SERVICE_RANK);
		return props;
	}
	
	@Override
	public void stop() throws Exception {
		bundleContext.removeServiceListener(this);
		bundleContext.removeFrameworkListener(this);
		bootstrapService.stopServices();
		LOG.info("Bootstrap Service Stoped");
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		Object someService = bundleContext.getService(event.getServiceReference());
		if (isServiceManageable(someService)) {
			ServiceLifecycle serviceLifecycle = (ServiceLifecycle) someService;
			switch (event.getType()) {
			case ServiceEvent.REGISTERED:
				bootstrapService.addService(serviceLifecycle);
				break;
			case ServiceEvent.UNREGISTERING:
				bootstrapService.removeService(serviceLifecycle);
				break;
			}
		}
	}

	private boolean isServiceManageable(Object someService) {
		return someService instanceof ServiceLifecycle;
	}

	@Override
	public void frameworkEvent(FrameworkEvent event) {
		if (event.getType() == FrameworkEvent.STARTED) {
			bootstrapService.startServices();
		}
	}

	@Override
	public void handleEvent(Event event) {
	}

}
