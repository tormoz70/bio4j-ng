package ru.bio4j.service.monitor;

import ru.bio4j.service.monitor.plugin.AbstractPlugin;

public interface Monitor {

	String pluginLabel();
	
	String pluginTitle();

	AbstractPlugin createPlugin();

}
