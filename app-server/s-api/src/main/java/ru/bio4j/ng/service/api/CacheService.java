package ru.bio4j.ng.service.api;

import java.io.Serializable;
import java.util.List;

public interface CacheService extends BioService {

	<Key extends Serializable, T extends Serializable> void put(CacheName cacheName, Key key, T value);
	<Key extends Serializable, T extends Serializable> void put(CacheName cacheName, Key key, T value, boolean notifyListeners);
	 //for not generating garbage (varagrs)
	<Key extends Serializable, T extends Serializable> void put(CacheName cacheName, Key key1, Key key2, T value);
	<Key extends Serializable, T extends Serializable> T get(CacheName cacheName, Key key1, Key key2);
	<Key extends Serializable, T extends Serializable> T remove(CacheName cacheName, Key key1, Key key2);
	
	<Key extends Serializable, T extends Serializable> T get(CacheName cacheName, Key objectKey); 
	<Key extends Serializable> List<Key> getKeys(CacheName cacheName);
	<Key extends Serializable, T extends Serializable> T remove(CacheName cacheName, Key objectKey);
	
	//works only with references which upload on start and don't changes!
	<Key extends Serializable, T extends Serializable> boolean isKeyInCache(CacheName cacheName, Key objectKey); //for not generating garbage
	
	void printTotalStatistics();
	void flush(CacheName cacheName);
	void clear(CacheName cacheName);
	void registerListener(CacheName cacheName, CacheEventListener listener);
	void removeListener(CacheName cacheName, CacheEventListener listener);
	void removeAllListeners();
}
