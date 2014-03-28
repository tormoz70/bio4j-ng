package ru.bio4j.service;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.BundleContext;

public abstract class ServiceConfig {
	
	public static final String DELIMITER = ";";

	private final BundleContext bundleContext;

	public ServiceConfig() {
		this(null);
	}

	public ServiceConfig(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	protected boolean getBooleanProperty(Dictionary<String, ?> props, String name, boolean defValue) {
		String value = getProperty(props, name, null);
		if (value != null) {
			return (value.equalsIgnoreCase("true") || value
					.equalsIgnoreCase("yes"));
		}

		return defValue;
	}

	protected int getIntProperty(Dictionary<String, ?> props, String name) {
		return getIntProperty(props, name, -1);
	}

	protected int getIntProperty(Dictionary<String, ?> props, String name, int defValue) {
		try {
			return Integer.parseInt(getProperty(props, name, null));
		} catch (Exception e) {
			return defValue;
		}
	}

	protected double getFloatProperty(Dictionary<String, ?> props, String name) {
		return getFloatProperty(props, name, -1);
	}

	protected double getFloatProperty(Dictionary<String, ?> props, String name, double defValue) {
		try {
			return Double.parseDouble(getProperty(props, name, null));
		} catch (Exception e) {
			return defValue;
		}
	}

	public List<String> getStringValues(Dictionary<String, ?> props, String parameterName) {
		String values = getProperty(props, parameterName, "");
		String[] array = values.split(DELIMITER);
		return Arrays.asList(array);
	}

	protected String getProperty(Dictionary<String, ?> props, String name) {
		return getProperty(props, name, null);
	}

	protected String getProperty(Dictionary<String, ?> props, String name, String defValue) {
		Object value = props.get(name);
		if (value == null && bundleContext != null) {
			value = bundleContext.getProperty(name);
		}
		return value != null ? String.valueOf(value) : defValue;
	}
	
	public abstract void config(Dictionary<String, ?> parameters);
}
