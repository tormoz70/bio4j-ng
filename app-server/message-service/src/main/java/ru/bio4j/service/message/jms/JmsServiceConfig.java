package ru.bio4j.service.message.jms;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.ServiceConfig;

public class JmsServiceConfig extends ServiceConfig {
	
	private final static Logger LOG = LoggerFactory.getLogger(JmsServiceConfig.class);

	public final static String HOST_ID = "jms.host";
	public final static String PORT_ID = "jms.port";
	public final static String USERNAME_ID = "jms.username";
	public final static String PASSWORD_ID = "jms.password";
	public final static String PRIORITY = "jms.priority";
	public final static String ACCUMULATOR_CAPACITY = "jms.accumulator.capacity";
	public final static String UPPER_MESSAGE_FETCH_LIMIT_COUNT = "jms.fetch.limit";
	public final static String REAL_TIMEP_RIORITY = "jms.real.time.priority";
	public final static String DEF_PRIORITY = "jms.def.priority";
	public final static String IS_DIRECT_SEND = "jms.is.direct.send";

	private String host;
	private int port;
	private String username;
	private String password;
	private int accumulatorCapacity;
	private int upperMessageFetchLimitcount;
	private int realTimePriority;
	private int defPriority;
	private boolean isDirectSend;
	private Map<String, Integer> priority = new HashMap<>();
	
	public JmsServiceConfig(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	public void config(Dictionary<String, ?> parameters) {
		LOG.debug("Setting parameters for JMS service");
		Dictionary<String, ?> props = parameters;
		if (props == null) {
			props = new Hashtable<String, Object>();
		}
		host = getProperty(props, HOST_ID);
		port = getIntProperty(props, PORT_ID);
		username = getProperty(props, USERNAME_ID);
		password = getProperty(props, PASSWORD_ID);
		accumulatorCapacity = getIntProperty(props, ACCUMULATOR_CAPACITY, 1_000_000);
		upperMessageFetchLimitcount = getIntProperty(props, UPPER_MESSAGE_FETCH_LIMIT_COUNT, 1_000_000);
		realTimePriority = getIntProperty(props, REAL_TIMEP_RIORITY, 5);
		defPriority = getIntProperty(props, DEF_PRIORITY, 0);
		isDirectSend = getBooleanProperty(props, IS_DIRECT_SEND, false);
		String priorityString = getProperty(props, PRIORITY);
		buildMap(priorityString, priority);
		
		if (host == null || "".equals(host)) {
			throw new IllegalArgumentException("Host is not set");
		}
		if (port <= 0) {
			throw new IllegalArgumentException("Port is not set");
		}
		
		username = "".equals(username) ? null : username;
		password = "".equals(password) ? null : password;
		LOG.debug("JMS parameters are: " + toString());
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getAddress() {
		return getHost() + ":" + getPort();
	}
	
	public boolean isAuthRequired() {
		return (username != null && password != null);
	}
	
	public Map<String, Integer> getPriority() {
		return priority;
	}
	
	public int getAccumulatorCapacity() {
		return accumulatorCapacity;
	}

	public int getUpperMessageFetchLimitcount() {
		return upperMessageFetchLimitcount;
	}

	private void buildMap(String priorityString, Map<String, Integer> priority) {
		if (priorityString == null || priorityString.isEmpty()) {
			priorityString = "def:0,Deal:4,Trade:4,DocumentAdditionalCommission:5,Command:5,RecalculationCommand:0,LifeCycleCommand:9,SyncCommand:9";
		}
		String[] pair = priorityString.split(",");
		
		for (String string : pair) {
			String[] keyValue = string.split(":");
			assertPair(keyValue);
			priority.put(keyValue[0].trim(), Integer.valueOf(keyValue[1]));
		}
	}

	private void assertPair(String[] keyValue) {
		if (keyValue.length != 2) {
			throw new IllegalArgumentException("Property set wrong - keyValue" + keyValue);
		}
	}

	public int getRealTimePriority() {
		return realTimePriority;
	}
	
	public int getDefPriority() {
		return defPriority;
	}

	public boolean isDirectSend() {
		return isDirectSend;
	}

	@Override
	public String toString() {
		return "JmsServiceConfig [host=" + host + ", port=" + port
				+ ", username=" + username + ", password=" + password
				+ ", accumulatorCapacity=" + accumulatorCapacity
				+ ", upperMessageFetchLimitcount="
				+ upperMessageFetchLimitcount + ", realTimePriority="
				+ realTimePriority + ", defPriority=" + defPriority
				+ ", isDirectSend=" + isDirectSend + ", priority=" + priority
				+ "]";
	}
}
