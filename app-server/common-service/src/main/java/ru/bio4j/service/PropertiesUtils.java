package ru.bio4j.service;

import java.io.Serializable;
import java.util.Dictionary;

public class PropertiesUtils {

	private PropertiesUtils() {}
	
	public static <T extends Serializable> T getProperty(String key, Dictionary<?, ?> properties) {
		return getProperty(key, properties, true);
	}
	
	public static <T extends Serializable> T getProperty(String key, Dictionary<?, ?> properties, boolean check) {
		@SuppressWarnings("unchecked")
		T property = (T) properties.get(key);
		if (check && (property == null)) {
			throw new IllegalArgumentException("Parameter " + key + " has not been set");
		}
		return property;
	}
}
