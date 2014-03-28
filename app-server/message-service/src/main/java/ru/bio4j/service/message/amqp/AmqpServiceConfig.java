package ru.bio4j.service.message.amqp;

import org.osgi.framework.BundleContext;

import ru.bio4j.service.ServiceConfig;

import com.rabbitmq.client.ConnectionFactory;

public abstract class AmqpServiceConfig extends ServiceConfig {

	public AmqpServiceConfig(BundleContext bundleContext) {
		super(bundleContext);
	}

	public abstract String getHost();


	public String getUsername() {
		return ConnectionFactory.DEFAULT_USER;
	}

	public String getPassword() {
		return ConnectionFactory.DEFAULT_PASS;
	}

	public int getPort() {
		return ConnectionFactory.DEFAULT_AMQP_PORT;
	}

	public String getVirtualHost() {
		return ConnectionFactory.DEFAULT_VHOST;
	}

}
