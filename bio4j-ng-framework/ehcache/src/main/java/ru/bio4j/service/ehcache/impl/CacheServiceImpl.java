package ru.bio4j.service.ehcache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ehcache.CacheName;
import ru.bio4j.service.ehcache.CacheService;
import ru.bio4j.service.ehcache.util.CacheEventListener;
import ru.bio4j.service.ehcache.util.CacheEventListenerWrapper;
import ru.bio4j.service.ehcache.util.CacheUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheServiceImpl implements CacheService {

	private static Logger LOG = LoggerFactory.getLogger(CacheService.class);

	private final CacheManager cacheManager;
	
	private final Map<String, CacheConfiguration> configuration;
	
	private final Map<CacheEventListener, CacheEventListenerWrapper> listeners = new ConcurrentHashMap<>();
	
	public CacheServiceImpl(CacheManager cacheManager) {
		if (cacheManager == null) {
			throw new IllegalArgumentException("CacheManager is not set");
		}
		this.cacheManager = cacheManager;
		configuration = cacheManager.getConfiguration().getCacheConfigurations();
	}

	@Override
	public <Key extends Serializable, T extends Serializable> void put(CacheName cacheName, Key key, T value) {
		this.put(cacheName, key, value, false);
	}

	@Override
	public <Key extends Serializable, T extends Serializable> void put(CacheName cacheName, Key key1, Key key2, T value) {
		this.put(cacheName, CacheUtil.createKeyFromObjects(key1, key2), value, false);
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> void put(CacheName cacheName, Key key, T value, boolean notifyListeners) {
		checkForNull(key, value);
		LOG.trace("Attempting to put object {} into cache with key {}", key, value);
		Cache cache = getCache(cacheName);
		cache.put(new Element(key, value), !notifyListeners);
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> T get(CacheName cacheName, Key key) {
		Cache cache = getCache(cacheName);
		Element element = cache.get(key);
		T result = getValue(element);
		LOG.trace("got from cahe {} by key {}", result, key);
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <Key extends Serializable> List<Key> getKeys(CacheName cacheName) {
		Cache cache = getCache(cacheName);
        return cache.getKeys();
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> boolean isKeyInCache(CacheName cacheName, Key key) {
		return getCache(cacheName).isKeyInCache(key);
	}

	@Override
	public <Key extends Serializable, T extends Serializable> T get(CacheName cacheName, Key key1, Key key2) {
		return get(cacheName, CacheUtil.createKeyFromObjects(key1, key2));
		
	}

	@Override
	public <Key extends Serializable, T extends Serializable> T remove(CacheName cacheName, Key key1, Key key2) {
		return remove(cacheName, CacheUtil.createKeyFromObjects(key1, key2));
	}
	
	@Override
	public <Key extends Serializable, T extends Serializable> T remove(CacheName cacheName, Key key) {
		Cache cache = getCache(cacheName);
		Element element = cache.removeAndReturnElement(key);
		T result = getValue(element);
		LOG.trace("removed from cahe {} by key {}", result, key);
		return result;
	}

	@Override
	public void flush(CacheName cacheName) {
		try {
			if (isDiskPersistence(cacheName)) {
				Cache cache = getCache(cacheName);
				cache.flush();
			}
		} catch (Exception e) {
			LOG.error("Ooops.. can't flush cache " + cacheName.name(), e);
		}
	}

	@Override
	public void clear(CacheName cacheName) {
		Cache cache = getCache(cacheName);
		cache.removeAll();
	}

	public void registerListener(CacheName cacheName, CacheEventListener listener) {
		CacheEventListenerWrapper wrapper = listeners.get(listener);
		if (wrapper == null) {
			wrapper = new CacheEventListenerWrapper(listener);
		}
		Cache cache = getCache(cacheName);
		cache.getCacheEventNotificationService().registerListener(wrapper);
	}
	
	public void removeListener(CacheName cacheName, CacheEventListener listener) {
		CacheEventListenerWrapper wrapper = listeners.get(listener);
		if (wrapper == null) {
			return;
		}
		Cache cache = getCache(cacheName);
		cache.getCacheEventNotificationService().unregisterListener(wrapper);
		listeners.remove(listener);
	}

	@Override
	public void removeAllListeners() {
		for (int i = 0; i < CacheName.values().length; i++) {
			Cache cache = getCache(CacheName.values()[i]);
			if (cache != null) {
				cache.getCacheEventNotificationService().getCacheEventListeners().clear();
			}
		}
		listeners.clear();
	}
	
	private boolean isDiskPersistence(CacheName cacheName) {
		return configuration.get(cacheName.cacheName()).isDiskPersistent();
	}
	
	@SuppressWarnings("unchecked")
	private <Key extends Serializable, T extends Serializable> T getValue(Element element) {
		if (element != null) {
            return (T) element.getObjectValue();
		}
		return null;
	}
	
	private void checkForNull(Object value1, Object value2) {
		if (value1 == null || value2 == null) {
			throw new IllegalArgumentException("Object cannot be null");
		}
	}
	
	private Cache getCache(CacheName cacheName) throws IllegalArgumentException, IllegalStateException {
		Cache cache = cacheManager.getCache(cacheName.cacheName());
		if (cache == null) {
			throw new IllegalArgumentException("Unknown cache " + cacheName.cacheName());
		}
		return cache;
	}

	@Override
	public void printTotalStatistics() {
		long inMemSizeInBytes = 0l;
		long onDiskSizeInBytes = 0l;
		long inMemCount = 0;
		int onDiskCount = 0;
		for (CacheName cacheName : CacheName.values()) {
			Cache cache = getCache(cacheName);
			inMemSizeInBytes += cache.calculateInMemorySize();
			inMemCount += cache.getMemoryStoreSize();
			if (isDiskPersistence(cacheName)) {
				onDiskSizeInBytes += cache.calculateOnDiskSize();
				onDiskCount += cache.getDiskStoreSize();
				LOG.info(
						"cacheName {}, number of elements in the disk store is {}, " +
								"size of the on-disk store for this cache in bytes is {}",
						new Object[] { cacheName, cache.getDiskStoreSize(),
								cache.calculateOnDiskSize() });
			}
			LOG.info(
					"cacheName {}, number of elements in the memory store is {}, " +
							"size of the memory store for this cache in bytes is {}",
					new Object[] { cacheName, cache.getMemoryStoreSize(),
							cache.calculateInMemorySize() });
		}
		LOG.info(
				"Total number of elements in the disk store is {}, " +
						"Total size of the on-disk store for this in bytes is {}",
				onDiskCount, onDiskSizeInBytes);
		LOG.info(
				"Total number of elements in the memory store is {}, " +
						"Total size of the memory store for this in bytes is {}",
				inMemCount, inMemSizeInBytes);
	}
}
