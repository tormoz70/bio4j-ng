package ru.bio4j.service.scheduler;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.AbstractDependencyService;
import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.ServiceLifecycle;
import ru.bio4j.service.message.EventUtil;

@Component
@Provides(specifications = ServiceLifecycle.class)
@Instantiate
public class SchedulerService extends AbstractDependencyService implements ServiceLifecycle {
	
	private final Scheduler schedulerShort = new Scheduler();
	
	private final Scheduler schedulerLong = new Scheduler();

	private BundleContext bundleContext;
	
	private volatile Status status = Status.STOPPED;
	
	private SchedulerServiceConfig config;

    private final static Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    public SchedulerService(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

	@Validate
	@Override
	protected void start() throws Exception {
		config = new SchedulerServiceConfig(bundleContext);
		config.config(null);
	}

    @Invalidate
	@Override
	protected void stop() throws Exception {
	}

	@Override
	public String getName() {
		return "Scheduler Service";
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Integer getOrder() {
		return ServiceConstants.SCHEDULER_SERVICE_ORDER;
	}

	@Override
	public void startWork() {
		try {
            final EventAdmin eventAdmin = EventUtil.createEventAdmin(bundleContext);
			schedulerShort.start(config.getSendDealyInSecShort(), eventAdmin, ServiceConstants.SCHEDULER_TOPIC_SHORT);
			schedulerLong.start(config.getSendDealyInSecLong(), eventAdmin, ServiceConstants.SCHEDULER_TOPIC_LONG);
			status = Status.STARTED;
		} catch (Exception e) {
			LOG.error("Ooops can't start scheduler", e);
			status = Status.ERROR;
		}
	}

	@Override
	public void finishWork() {
		schedulerLong.stop();
		schedulerShort.stop();
		status = Status.STOPPED;
	}
}
