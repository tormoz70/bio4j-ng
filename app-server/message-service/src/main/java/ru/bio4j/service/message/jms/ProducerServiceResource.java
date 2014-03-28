package ru.bio4j.service.message.jms;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ServiceConfig;
import ru.bio4j.service.ServiceConstants;
import ru.bio4j.service.message.*;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** default */ class ProducerServiceResource extends JmsServiceResource implements EventHandler {

	private final static Logger LOG = LoggerFactory.getLogger(ProducerServiceResource.class);
	
	private final MessageProducer messageProducer;
	
	private final PriorityResolver priorityResolver;
	
	private MessageAccumulator messageAccumulator;
	
	private final Lock lock;
	
	private final boolean isDirectSend;
	
	private final int realTimePriority;
	
	private int[] priorities;
	
	private final AtomicBoolean isActive = new AtomicBoolean(true);
	
	private final JmsServiceConfig config;
	
	private final BundleContext bundleContext;
	
	public ProducerServiceResource(Session session, MessageProducer messageProducer, ServiceConfig serviceConfig, BundleContext bundleContext) {
		super(session);
		config = (JmsServiceConfig)serviceConfig;
		this.messageProducer = messageProducer;
		this.bundleContext = bundleContext;
		lock = new ReentrantLock();
		priorityResolver = new PriorityResolverImpl(config);
		realTimePriority = config.getRealTimePriority();
		isDirectSend = config.isDirectSend();
		if (!isDirectSend) {
			prepareScheduler(config);
		}
	}

	private void prepareScheduler(JmsServiceConfig config) {
		subscribeToSchedulerEvents();
		
		priorities = priorityResolver.getPrioritiesInDescLessThen(realTimePriority);
		messageAccumulator = new MessageAccumulatorImpl(realTimePriority, 
				config.getUpperMessageFetchLimitcount(), 
				config.getAccumulatorCapacity());
	}

	public void prepareToSendMessage(Serializable serializable) {
		int priorty = getPriority(serializable);
		if (isDirectSend || priorty >= realTimePriority) {
			sendMessages(priorty, serializable);
		} else {
			messageAccumulator.storeMessage(priorty, serializable);
		}
	}
	
	public void sendAllMessages() {
		for (int i = 0; i < priorities.length; i++) {
			int key = priorities[i];
			Serializable messages = messageAccumulator.drain(key);
			if (messages != null) {
				sendMessages(key, messages);
			}
		}
	}
	
	public void sendMessages(int priority, Serializable messages) {
		if (messageProducer != null) {
			try {
				ObjectMessage message = getSession().createObjectMessage(messages);
				updatePriority(priority);
				messageProducer.send(message);
			} catch (JMSException e) {
				LOG.error("Could not send message", e);
			}
		}
	}
	
	private void updatePriority(int priority) throws JMSException {
		if (priority != messageProducer.getPriority()) {
			messageProducer.setPriority(priority);
		}
	}

	@Override
	protected void internalClose() {
		sendAllMessages();
		isActive.set(false);
		if (messageProducer != null) {
			try {
				try {
					lock.lockInterruptibly();
					try {
						messageProducer.close();
					} finally {
						lock.unlock();
					}
				} catch (InterruptedException e) {
					LOG.error("Oops...", e);
                    Thread.currentThread().interrupt();
				}
				LOG.info("Produce closed");
			} catch (JMSException e) {
				LOG.warn("Could not close MessageProducer", e);
				
			}
		}
	}

	private int getPriority(Serializable serializable) {
		return priorityResolver.getPriority(serializable);
	}

	@Override
	public void handleEvent(Event event) {
		if (isActive.get()) {
			sendAllMessages();
		}
	}
	
	private void subscribeToSchedulerEvents() {
		bundleContext.registerService(EventHandler.class.getName(), this, 
				EventUtil.createEventHandlerProperties(ServiceConstants.SCHEDULER_TOPIC_SHORT));
	}
}
