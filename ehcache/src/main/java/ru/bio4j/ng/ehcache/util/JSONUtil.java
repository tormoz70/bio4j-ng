package ru.bio4j.ng.ehcache.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Statistics;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.statistics.LiveCacheStatistics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
	
	private JSONUtil() {}

	public static JSONArray cacheNames(String[] cacheNames) {
		JSONArray caches = new JSONArray();
		for (String cacheName : cacheNames) {
			caches.put(cacheName);
		}
		return caches;
	}
	
	public static JSONObject cacheToJSON(Cache cache) {
		try {
			JSONObject cacheObject = new JSONObject();
			cacheObject.put("name", cache.getName());
			cacheObject.put("size", cache.getSize());
			cacheObject.put("configuration", JSONUtil.cacheConfigToJSON(cache.getCacheConfiguration()));
			cacheObject.put("statistics", JSONUtil.cacheStatisticsToJSON(cache.getStatistics()));
			cacheObject.put("live_statistics", JSONUtil.cacheLiveStatisticsToJSON(cache.getLiveCacheStatistics()));
			JSONObject fullCacheObject = new JSONObject();
			fullCacheObject.put("cache", cacheObject);
			return fullCacheObject;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static JSONObject cacheLiveStatisticsToJSON(LiveCacheStatistics liveCacheStatistics) {
		try {
			JSONObject statisticObject = new JSONObject();
			statisticObject.put("hit_count", liveCacheStatistics.getCacheHitCount());
			statisticObject.put("miss_count", liveCacheStatistics.getCacheMissCount());
			statisticObject.put("miss_count_expired", liveCacheStatistics.getCacheMissCountExpired());
			statisticObject.put("evicted_count", liveCacheStatistics.getEvictedCount());
			statisticObject.put("expired_count", liveCacheStatistics.getExpiredCount());
			statisticObject.put("in_memory_hit_count", liveCacheStatistics.getInMemoryHitCount());
			statisticObject.put("in_memory_miss_count", liveCacheStatistics.getInMemoryMissCount());
			statisticObject.put("in_memory_size", liveCacheStatistics.getLocalHeapSize());
			statisticObject.put("max_get_time_millis", liveCacheStatistics.getMaxGetTimeMillis());
			statisticObject.put("min_get_time_millis", liveCacheStatistics.getMinGetTimeMillis());
			statisticObject.put("off_heap_hit_count", liveCacheStatistics.getOffHeapHitCount());
			statisticObject.put("off_heap_miss_count", liveCacheStatistics.getOffHeapMissCount());
			statisticObject.put("off_heap_size", liveCacheStatistics.getLocalOffHeapSize());
			statisticObject.put("put_count", liveCacheStatistics.getPutCount());
			statisticObject.put("removed_count", liveCacheStatistics.getRemovedCount());
			statisticObject.put("size", liveCacheStatistics.getSize());
			statisticObject.put("update_count", liveCacheStatistics.getUpdateCount());
			statisticObject.put("writer_queue_length", liveCacheStatistics.getWriterQueueLength());
			statisticObject.put("average_get_time_millis", liveCacheStatistics.getAverageGetTimeMillis());
			return statisticObject;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static JSONObject cacheStatisticsToJSON(Statistics cacheStatistics) {
		try {
			JSONObject statisticObject = new JSONObject();
			statisticObject.put("average_get_time", cacheStatistics.getAverageGetTime());
			statisticObject.put("average_search_time", cacheStatistics.getAverageSearchTime());
			statisticObject.put("cache_hits", cacheStatistics.getCacheHits());
			statisticObject.put("cache_misses", cacheStatistics.getCacheMisses());
			statisticObject.put("eviction_count", cacheStatistics.getEvictionCount());
			statisticObject.put("object_count", cacheStatistics.getObjectCount());
			statisticObject.put("off_heap_hits", cacheStatistics.getOffHeapHits());
			statisticObject.put("off_heap_misses", cacheStatistics.getOffHeapMisses());
			statisticObject.put("searches_per_second", cacheStatistics.getSearchesPerSecond());
			statisticObject.put("writer_queue_size", cacheStatistics.getWriterQueueSize());
			return statisticObject;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static JSONObject cacheConfigToJSON(CacheConfiguration cacheConfiguration) {
		try {
			JSONObject configObject = new JSONObject();
			configObject.put("cache_loader_timeout_millis", cacheConfiguration.getCacheLoaderTimeoutMillis());
			configObject.put("max_elements_in_memory", cacheConfiguration.getMaxEntriesLocalHeap());
			configObject.put("max_memory_off_heap_in_bytes", cacheConfiguration.getMaxMemoryOffHeapInBytes());
			configObject.put("time_to_idle_seconds", cacheConfiguration.getTimeToIdleSeconds());
			configObject.put("time_to_live_seconds", cacheConfiguration.getTimeToLiveSeconds());
			configObject.put("max_memory_off_heap", cacheConfiguration.getMaxMemoryOffHeap());
			return configObject;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
