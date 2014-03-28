package ru.bio4j.service.mail.activator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.mail.MailService;
import ru.bio4j.service.mail.impl.MailServiceConfig;
import ru.bio4j.service.ServiceController;

public class MailServiceController implements ServiceController {

	private final static Logger LOG = LoggerFactory.getLogger(MailServiceController.class); 
	
	private final BundleContext bundleContext;

	public MailServiceController(BundleContext bundleContext) {
		super();
		this.bundleContext = bundleContext;
	}

	@Override
	public String getServiceName() {
		return "Mail Service";
	}

	@Override
	public void start() throws Exception {
		MailServiceConfig serviceConfig = new MailServiceConfig(bundleContext);

		MailServiceFactory mailServiceFactory = new MailServiceFactory(serviceConfig);

		bundleContext.registerService(MailService.class.getName(), mailServiceFactory, null);
		bundleContext.registerService(ManagedService.class.getName(), new MailServiceManagedServiceFactory(mailServiceFactory), createProperty());
		LOG.info("Mail Service Started. Parameters {}", serviceConfig);
	}

	private Dictionary<String, ?> createProperty() {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_PID, "mail.service.config");
		props.put(Constants.SERVICE_RANKING, ServiceConstants.MAIL_SERVICE_RANK);
		return props;
	}
	
	@Override
	public void stop() throws Exception {
		LOG.info("Mail Service Stoped");
	}

}
