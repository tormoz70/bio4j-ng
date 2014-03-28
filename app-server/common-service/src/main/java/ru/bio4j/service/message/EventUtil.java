package ru.bio4j.service.message;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

public class EventUtil {
	
	public static <V> Map<String, V> buildSingletonMap(V value) {
		return Collections.singletonMap(toName(value.getClass()), value);
	}
	
	public static String toName(Class<?> clazz) {
		return clazz.getSimpleName();
	}
	
	public static <V> Event buildEvent(V value, String topic) {
		return new Event(topic, EventUtil.buildSingletonMap(value));
	}
	
	public static <V> Event buildEvent(String topic, V ... values) {
		Map<String, Object> map = new HashMap<>();
		for (V v : values) {
			map.put(toName(v.getClass()), v);
		}
		return new Event(topic, map);
	}
	
	public static Dictionary<String, ?> createEventHandlerProperties(String... topics) {
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(EventConstants.EVENT_TOPIC, topics);
		return props;
	}
	
	public static EventAdmin createEventAdmin(BundleContext bundleContext) {
        return createService(bundleContext, EventAdmin.class);
	}
	
	@SuppressWarnings("unchecked")
	public static <S> S createService(BundleContext bundleContext, Class<S> class1) {
		ServiceReference<S> ref = (ServiceReference<S>) bundleContext.getServiceReference(class1.getName());
        return bundleContext.getService(ref);
	}
}
