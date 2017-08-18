package ru.bio4j.service.ehcache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import ru.bio4j.service.ehcache.CacheServiceManagement;
import ru.bio4j.service.ehcache.EhCacheServiceMonitorPlugin;
import ru.bio4j.service.monitor.plugin.AbstractPlugin;

public class CacheServiceManagementImpl implements CacheServiceManagement {

	private final CacheManager cacheManager;

	public CacheServiceManagementImpl(CacheManager cacheManager) {
		super();
		this.cacheManager = cacheManager;
	}

	@Override
	public String pluginLabel() {
		return CacheServiceManagement.class.getName();
	}

	@Override
	public String pluginTitle() {
		return "EhCache Service";
	}

	@Override
	public AbstractPlugin createPlugin() {
		return new EhCacheServiceMonitorPlugin(this);
	}

	@Override
	public String[] getCacheNames() {
		return cacheManager.getCacheNames();
	}

	@Override
	public Cache getCache(String name) {
		return cacheManager.getCache(name);
	}

	@Override
	public void clean(String name) {
		Cache cache = cacheManager.getCache(name);
		if (cache != null) {
			cache.removeAll();
		}
	}
}
