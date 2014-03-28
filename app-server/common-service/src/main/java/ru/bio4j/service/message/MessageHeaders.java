package ru.bio4j.service.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MessageHeaders implements Map<String, Object>  {
	
	private final static String ID = "messageId";
	
	private final static String TIMESTAMP = "timestamp";

	private final HashMap<String, Object> headers;

	public MessageHeaders() {
		this(new HashMap<String, Object>());
	}
	
	public MessageHeaders(Map<String, Object> headers) {
		this.headers = (headers != null) ? new HashMap<>(headers) : new HashMap<String, Object>();
		this.headers.put(ID, UUID.randomUUID());
		this.headers.put(TIMESTAMP, System.currentTimeMillis());
	}
	
	@Override
	public int size() {
		return headers.size();
	}

	@Override
	public boolean isEmpty() {
		return headers.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return headers.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return headers.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return headers.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return headers.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return headers.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		headers.putAll(m);
	}

	@Override
	public void clear() {
		headers.clear();
	}

	@Override
	public Set<String> keySet() {
		return headers.keySet();
	}

	@Override
	public Collection<Object> values() {
		return headers.values();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return headers.entrySet();
	}
	
}
