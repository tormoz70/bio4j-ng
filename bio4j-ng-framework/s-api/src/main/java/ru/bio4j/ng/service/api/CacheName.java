package ru.bio4j.ng.service.api;

public enum CacheName {
	OBJECT_MAP("objectMap"),
    QUERY("query"),
    CONTENT("content");

	private final String cacheName;

	private CacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String cacheName() {
		return cacheName;
	}

	public static CacheName fromCode(String name) {
		CacheName cacheName = null;
		for (int i = 0; i < CacheName.values().length; i++) {
			if (CacheName.values()[i].cacheName.equals(name)) {
				cacheName = CacheName.values()[i];
			}
		}
		if (cacheName == null) {
			throw new IllegalArgumentException("Unknown cache name " + name);
		}
		return cacheName;
	}
	
}
