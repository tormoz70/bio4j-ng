package ru.bio4j.service.message.amqp.statistic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.message.AmqpDestinationDescriptor;

public class AmqpChannelsStatistic {

	private static Logger LOG = LoggerFactory.getLogger(AmqpChannelsStatistic.class);

	private ConcurrentHashMap<String, AtomicInteger> queueMap;

	public AmqpChannelsStatistic() {
		queueMap = new ConcurrentHashMap<>();
	}

	public void increment(AmqpDestinationDescriptor queueDescriptor) {
		AtomicInteger counter = queueMap.get(queueDescriptor.getDestinationName() == null ? queueDescriptor.getExchangeName() : queueDescriptor.getDestinationName());
		if (counter == null) {
			counter = new AtomicInteger(0);
		}
		counter.incrementAndGet();
		queueMap.putIfAbsent(queueDescriptor.getDestinationName() == null ? queueDescriptor.getExchangeName() : queueDescriptor.getDestinationName(), counter);
	}

	public void decremenet(AmqpDestinationDescriptor queueDescriptor) {
		AtomicInteger counter = queueMap.get(queueDescriptor.getDestinationName() == null ? queueDescriptor.getExchangeName() : queueDescriptor.getDestinationName());
		int value = counter.decrementAndGet();
		if (value == 0) {
			queueMap.remove(queueDescriptor);
		}
	}

	public JSONArray toJSON() {
		JSONArray queues = new JSONArray();
		try {
			for (Map.Entry<String, AtomicInteger> queueEntry : queueMap.entrySet()) {
				String queue = queueEntry.getKey();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("queue", queue);
				jsonObject.put("connections", queueEntry.getValue().get());
				queues.put(jsonObject);
			}
		} catch (JSONException e) {
			LOG.error("Could not build JSON", e);
		}
		return queues;
	}
}
