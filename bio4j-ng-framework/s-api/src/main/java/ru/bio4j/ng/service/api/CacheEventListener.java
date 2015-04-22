package ru.bio4j.ng.service.api;

public interface CacheEventListener {
	
    void notifyElementRemoved(CacheName cacheName, Object value);

    void notifyElementPut(CacheName cacheName, Object value);

    void notifyElementUpdated(CacheName cacheName, Object value);

    void notifyElementExpired(CacheName cacheName, Object value);

    void notifyElementEvicted(CacheName cacheName, Object value);

    void notifyRemoveAll(CacheName cacheName);

}
