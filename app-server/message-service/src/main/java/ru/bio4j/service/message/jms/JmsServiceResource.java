package ru.bio4j.service.message.jms;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.message.common.ServiceResource;

public abstract class JmsServiceResource implements ServiceResource {

	private final static Logger LOG = LoggerFactory.getLogger(JmsServiceResource.class);

	private final String uuid;
	
	private final Session session;
	
	public JmsServiceResource(Session session) {
		super();
		this.session = session;
		this.uuid = UUID.randomUUID().toString();
	}

	public Session getSession() {
		return session;
	}

	@Override
	public void close() {
		internalClose();
		if (session != null) {
			try {
				session.close();
			} catch (JMSException e) {
				LOG.error("Could not close session ", e);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JmsServiceResource other = (JmsServiceResource) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	protected abstract void internalClose();
}
