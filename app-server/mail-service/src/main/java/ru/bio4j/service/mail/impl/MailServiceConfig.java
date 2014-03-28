package ru.bio4j.service.mail.impl;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ServiceConfig;

import java.util.Dictionary;
import java.util.Hashtable;

public class MailServiceConfig extends ServiceConfig {

	private final static Logger LOG = LoggerFactory.getLogger(MailServiceConfig.class);

	public final static String HOST = "mail.service.config.host";
	public final static String PORT = "mail.service.config.port";
	public final static String USER = "mail.service.config.user";
	public final static String PWD = "mail.service.config.password";
	public final static String SEND_TO = "mail.service.config.send.to";
	public final static String SEND_CC = "mail.service.config.send.cc";
	public final static String FROM = "mail.service.config.send.from";
	public final static String DEFAULT_E_MAILS_SEPARATOR = ";"; 
	
	private String host;
	private Integer port;
	private String username;
	private String password;
	private WhomToSend whomtoSend;
	private String from;
	
	public MailServiceConfig(BundleContext bundleContext) {
		super(bundleContext);
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public WhomToSend getWhomtoSend() {
		return whomtoSend;
	}

	public boolean isAuthRequired() {
		return getUsername() != null && getPassword() != null;
	}
	
	@Override
	public void config(Dictionary<String, ?> props) {
		LOG.debug("Attempting to load properties for Mail Notification Service");
		if (props == null) {
			props = new Hashtable<String, Object>();
		}
		host = getProperty(props, HOST);
		port = getIntProperty(props, PORT);
		username = getProperty(props, USER);
		password = getProperty(props, PWD);
		from = getProperty(props, FROM);
		whomtoSend = new WhomToSend(getProperty(props, SEND_TO, null), splitCC(getProperty(props, SEND_CC, null)));
		LOG.debug("Mail service parameter {}", toString());
	}

	private String[] splitCC(String cc) {
		return cc != null ? cc.split(DEFAULT_E_MAILS_SEPARATOR) : null;
	}

    @Override
    public String toString() {
        return "MailServiceConfig{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", whomtoSend=" + whomtoSend +
            ", from='" + from + '\'' +
            "} " + super.toString();
    }

    public String getFrom() {
		return from;
	}

}
