package ru.bio4j.service.scheduler;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.ServiceConfig;

public class SchedulerServiceConfig extends ServiceConfig {
	
	private final static Logger LOG = LoggerFactory.getLogger(SchedulerServiceConfig.class);
	
	public final static String DEALY_IN_SEC_SHORT = "scheduler.delay.in.sec.short";
	
	public final static String DEALY_IN_SEC_LONG = "scheduler.delay.in.sec.long";
	
	private long sendDealyInSecShort;
	
	private long sendDealyInSecLong;

	public SchedulerServiceConfig(BundleContext bundleContext) {
		super(bundleContext);
	}
	
	@Override
	public void config(Dictionary<String, ?> parameters) {
		Dictionary<String, ?> props = parameters;
		if (props == null) {
			props = new Hashtable<String, Object>();
		}
		sendDealyInSecShort = getIntProperty(props, DEALY_IN_SEC_SHORT, 10);
		sendDealyInSecLong = getIntProperty(props, DEALY_IN_SEC_SHORT, 60);
		LOG.debug("Scgeduler parameters are: " + toString());
	}

	public long getSendDealyInSecShort() {
		return sendDealyInSecShort;
	}

	public void setSendDealyInSecShort(long sendDealyInSecShort) {
		this.sendDealyInSecShort = sendDealyInSecShort;
	}

	public long getSendDealyInSecLong() {
		return sendDealyInSecLong;
	}

	public void setSendDealyInSecLong(long sendDealyInSecLong) {
		this.sendDealyInSecLong = sendDealyInSecLong;
	}

	@Override
	public String toString() {
		return "SchedulerServiceConfig [sendDealyInSecShort="
				+ sendDealyInSecShort + ", sendDealyInSecLong="
				+ sendDealyInSecLong + "]";
	}
	
}
