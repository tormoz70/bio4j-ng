package ru.bio4j.service.monitor.plugin;

import java.net.URL;

public class PluginUtils {
	
	private PluginUtils() {}
	
	public static URL loadResource(Class<?> clazz, String path) {
		return clazz.getResource(path);
	}
}
