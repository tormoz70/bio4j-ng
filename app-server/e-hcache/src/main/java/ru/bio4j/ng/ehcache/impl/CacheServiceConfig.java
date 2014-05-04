package ru.bio4j.ng.ehcache.impl;

import ru.bio4j.ng.commons.utils.Utl;

public class CacheServiceConfig {
    private String cachePersistentPath = null;

    public String getCachePersistentPath() {
        return Utl.normalizePath(cachePersistentPath);
    }

    public void setCachePersistentPath(String cachePersistentPath) {
        this.cachePersistentPath = cachePersistentPath;
    }
}
