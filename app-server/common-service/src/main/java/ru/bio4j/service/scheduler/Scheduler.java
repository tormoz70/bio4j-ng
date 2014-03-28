package ru.bio4j.service.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.message.EventUtil;
import ru.bio4j.service.message.SchedulerEvent;
import ru.bio4j.util.NamedThreadFactory;

public class Scheduler {
	
	private final static Logger LOG = LoggerFactory.getLogger(Scheduler.class);

	private volatile ScheduledExecutorService service;
	private EventAdmin eventAdmin;
	private String topicName;
	
	private final class EventNotifier implements Runnable {
		private final long periodCheckInSec;
		public EventNotifier(long periodCheckInSec) {
			this.periodCheckInSec = periodCheckInSec;
		}

		@Override
		public void run() {
			LOG.debug("Period event happened " + periodCheckInSec +" seconds past");
			eventAdmin.postEvent(EventUtil.buildEvent(new SchedulerEvent(periodCheckInSec), topicName));
		}
	}

	public void start(long periodCheckInSec, EventAdmin eventAdmin, String topicName) {
		this.eventAdmin = eventAdmin;
		this.topicName = topicName;
		service = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(periodCheckInSec + " seconds Scheduler"));
		service.scheduleAtFixedRate(new EventNotifier(periodCheckInSec), 1, periodCheckInSec, TimeUnit.SECONDS);
		LOG.debug("ScheduledExecutorService started");
	}
	
	public void stop() {
		if (service != null) {
			service.shutdown();
			service = null;
		}
		LOG.debug("ScheduledExecutorService stopped");
	}
}
