package ru.bio4j.service.ehcache;

import net.sf.ehcache.Cache;
import ru.bio4j.service.monitor.Monitor;


public interface CacheServiceManagement extends Monitor {
	
	String[] getCacheNames();

	Cache getCache(String name);

	void clean(String name);
}
