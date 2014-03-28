package ru.bio4j.service.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ServiceLifecycle;
import ru.bio4j.service.ServiceLifecycle.Status;
import ru.bio4j.service.bootstrap.activator.BootstrapConfig;

import java.io.PrintStream;
import java.util.Dictionary;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.bio4j.service.ServiceLifecycle.Status.*;

public class BootstrapService implements Bootstrap {

	private final static Logger LOG = LoggerFactory.getLogger(BootstrapService.class);

	private final List<ServiceLifecycle> services;
	private volatile ServiceLifecycle.Status status = STOPPED;
    private final BootstrapConfig bootstrapConfig;

	public BootstrapService(BootstrapConfig bootstrapConfig) {
		services = new CopyOnWriteArrayList<>();
        this.bootstrapConfig = bootstrapConfig;
	}
	
	public void reconfigure(Dictionary<String, ?> parameters) {
        bootstrapConfig.config(parameters);
	}

	public void addService(ServiceLifecycle serviceLifecycle) {
		LOG.debug("Added service {} {}", serviceLifecycle, serviceLifecycle.getName());
		services.add(serviceLifecycle);
		if (checkStatus(STARTED)) {
			startService(serviceLifecycle);
		}
	}

	public void removeService(ServiceLifecycle serviceLifecycle) {
		LOG.debug("Service removed {} {} ", serviceLifecycle, serviceLifecycle.getName());
		stopService(serviceLifecycle);
		services.remove(serviceLifecycle);
	}

	@Override
	public void startServices() {
		startServices(null);
	}

	public void startServices(PrintStream out) {
		LOG.info("Called start");
		OutWriter writer = new OutWriter(out);
		startServices(writer, getServices());
	}

	private void startServices(OutWriter writer, List<ServiceLifecycle> srvcs) {
		
		if (!checkAndSetStatus(STARTED, STARTING, STARTING)) {
			writer.write("In process {0}", status);
			return;
		}
		writer.write("Starting services:");
		for (ServiceLifecycle serviceLifecycle : srvcs) {
			writer.write("Starting {0}", serviceLifecycle.getName());
			
			if (checkStatus(STOPPED, STOPPING)) {
				return;
			}
			if (Status.ERROR.equals(startService(serviceLifecycle))) {
				writer.write("Could not start service {0}", serviceLifecycle.getName());
				setStatus(Status.ERROR);
				break;
			}
			writer.write("{0} Started", serviceLifecycle.getName());
		}
		if (checkStatus(Status.ERROR)) {
			stopServices(writer, srvcs);
			return;
		}
		setStatus(STARTED);
	}

	private synchronized Status startService(ServiceLifecycle serviceLifecycle) {
		try {
			if (!EnumSet.of(STARTED, STARTING).contains(serviceLifecycle.getStatus())) {
				serviceLifecycle.startWork();
			}
			return serviceLifecycle.getStatus();
		} catch (Throwable e) {
			LOG.info("Could not start", e);
			return Status.ERROR;
		}
	}

	@Override
	public void stopServices() {
		stopServices(null);
	}
	
	public void stopServices(PrintStream out) {
		LOG.info("Called stop");
		OutWriter writer = new OutWriter(out);
		stopServices(writer, getServices());
	}

	private void stopServices(OutWriter writer, List<ServiceLifecycle> srvcs) {
		if (!checkAndSetStatus(STOPPED, STOPPING, STOPPING)) {
			writer.write("Already called stop");
			return;
		}
		writer.write("Stopping services:");
		for (ServiceLifecycle serviceLifecycle : srvcs) {
			stopService(serviceLifecycle);
			writer.write("{0} Stopped", serviceLifecycle.getName());
		}
		setStatus(STOPPED);
	}

	private synchronized void stopService(ServiceLifecycle serviceLifecycle) {
		try {
            if (!EnumSet.of(STOPPED, STOPPING).contains(serviceLifecycle.getStatus())) {
                serviceLifecycle.finishWork();
            }
		} catch (Exception e) {
			LOG.error("Error while Stopping " + serviceLifecycle.getName(), e);
		}
	}

	@Override
	public void restart(PrintStream out) {
		stopServices(out);
		startServices(out);
	}


	public List<ServiceLifecycle> getServices() {
		return services;
	}

	@Override
	public Status getStatus() {
		return status;
	}
	
	private Status setStatus(Status status) {
		return this.status = status;
	}
	
	private synchronized boolean checkStatus(Status checkedStatus, Status ... checkedStatuses) {
		return EnumSet.of(checkedStatus, checkedStatuses).contains(status);
	}

    private synchronized boolean checkAndSetStatus(Status checkedStatus1, Status checkedStatus2, Status newStatus) {
        if (!checkStatus(checkedStatus1, checkedStatus2)) {
              setStatus(newStatus);
            return true;
        }
        return false;
    }

}
