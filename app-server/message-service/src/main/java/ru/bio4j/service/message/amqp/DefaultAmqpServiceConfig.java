package ru.bio4j.service.message.amqp;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultAmqpServiceConfig extends AmqpServiceConfig {
	
	private final static Logger LOG = LoggerFactory.getLogger(AmqpServiceConfig.class);
	
	public static final String RABBIT_HOST = "rabbitmq.service.host";
	public static final String RABBIT_PORT = "rabbitmq.service.port";
	public static final String RABBIT_USER = "rabbitmq.service.username";
	public static final String RABBIT_PWD = "rabbitmq.service.password";

	private String username;
	private String password;
	private String host;
	private int port;

	public DefaultAmqpServiceConfig(BundleContext bundleContext) {
		super(bundleContext);
	}

	public String getHost() {
		return host;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void config(Dictionary<String, ?> parameters) {
		LOG.debug("Setting parameters for AMQP service");
		Dictionary<String, ?> props = parameters;
		if (props == null) {
			props = new Hashtable<String, Object>();
		}
		host = getProperty(props, RABBIT_HOST);
		port = getIntProperty(props, RABBIT_PORT);
		username = getProperty(props, RABBIT_USER);
		password = getProperty(props, RABBIT_PWD);
		LOG.debug("AMQP parameters are: " + toString());
	}

	@Override
	public String toString() {
		return "(username=" + username + ", password="
				+ password + ", host=" + host + ", port=" + port + ")";
	}
}
