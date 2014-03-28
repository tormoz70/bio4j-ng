package ru.bio4j.service.mail.activator;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.mail.MailMessage;
import ru.bio4j.service.mail.MailService;
import ru.bio4j.service.mail.impl.MailServiceConfig;
import ru.bio4j.service.mail.impl.MailServiceImpl;
import ru.bio4j.service.mail.impl.MailServiceReactor;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Dictionary;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

public class MailServiceFactory implements ServiceFactory<MailService> {

	private final static Logger LOG = LoggerFactory.getLogger(MailServiceFactory.class);
	
	private final MailServiceConfig serviceConfig;
	
	private final LinkedBlockingQueue<MailMessage> messages;

	private MailServiceReactor mailServiceReactor;
	
	public MailServiceFactory(MailServiceConfig serviceConfig) {
		super();
		this.serviceConfig = serviceConfig;
		this.messages = new LinkedBlockingQueue<>();
	}

	@Override
	public MailService getService(Bundle bundle,
			ServiceRegistration<MailService> registration) {
		return new MailServiceImpl(mailServiceReactor);
	}

	@Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<MailService> registration, MailService service) {
	}

	public void updateConfig(Dictionary<String, ?> properties) {
		synchronized (this) {
            if (properties != null) {
                serviceConfig.config(properties);
                LOG.info("service updated new values are {}", serviceConfig);
                disconnect();
                connect();
            }
		}
	}
	
	public void connect() {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", serviceConfig.getHost());
		if (serviceConfig.getPort() > 0) {
			properties.put("mail.imap.port", serviceConfig.getPort());
		}
		
		Session session = Session.getDefaultInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return serviceConfig.isAuthRequired() ?
					new PasswordAuthentication(serviceConfig.getUsername(), serviceConfig.getUsername()) :
					null;
			}
		});
		mailServiceReactor = new MailServiceReactor(messages, session, serviceConfig);
		mailServiceReactor.setContextClassLoader(getClass().getClassLoader());
		mailServiceReactor.setDaemon(false);
		mailServiceReactor.start();
	}
	
	public void disconnect() {
        if (mailServiceReactor != null) {
            mailServiceReactor.stopReactor();
            try {
                mailServiceReactor.join();
            } catch (InterruptedException e) {
                LOG.warn("Interrupted...", e);
                Thread.currentThread().interrupt();
            }
        }
	}
	
}
