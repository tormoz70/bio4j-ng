package ru.bio4j.service.ehcache.util;

import ru.bio4j.service.ehcache.CacheName;

import java.io.Serializable;

public class CacheUtil {
	
	public static Serializable createKeyFromObjects(Serializable key1, Serializable key2) {
		return new KeyPair(key1, key2);
	}
	
	public static CacheName getCacheByCode(String name) {
		return CacheName.fromCode(name);
	}
}
