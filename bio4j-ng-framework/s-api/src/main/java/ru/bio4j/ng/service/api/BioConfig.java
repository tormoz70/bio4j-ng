package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.Utl;

public class BioConfig {
    @Prop(name = "bio.debug")
    private boolean bioDebug = false;

    @Prop(name = "ehcache.persistent.path")
    private String cachePersistentPath = null;

    @Prop(name = "content.resolver.path")
    private String contentResolverPath = null;

    @Prop(name = "files.cloud.path")
    private String filesCloudPath = null;

    @Prop(name = "global.pool.name")
    private String poolName;
    @Prop(name = "global.driver.name")
    private String driverName;
    @Prop(name = "global.connection.url")
    private String dbConnectionUrl;
    @Prop(name = "global.connection.usr")
    private String dbConnectionUsr;
    @Prop(name = "global.connection.pwd")
    private String dbConnectionPwd;
    @Prop(name = "global.min.pool.size")
    private int minPoolSize = 2;
    @Prop(name = "global.max.pool.size")
    private int maxPoolSize = 10;
    @Prop(name = "global.connection.wait.timeout")
    private int connectionWaitTimeout = 5;
    @Prop(name = "global.initial.pool.size")
    private int initialPoolSize = 5;
    @Prop(name = "global.current.schema")
    private String currentSchema = null;


    public String getCachePersistentPath() {
        return Utl.normalizePath(cachePersistentPath);
    }

    public void setCachePersistentPath(String cachePersistentPath) {
        this.cachePersistentPath = cachePersistentPath;
    }

    public String getContentResolverPath() {
        return contentResolverPath;
    }

    public void setContentResolverPath(String contentResolverPath) {
        this.contentResolverPath = contentResolverPath;
    }

    public String getDbConnectionUrl() {
        return dbConnectionUrl;
    }

    public String getDbConnectionUsr() {
        return dbConnectionUsr;
    }

    public String getDbConnectionPwd() {
        return dbConnectionPwd;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getConnectionWaitTimeout() {
        return connectionWaitTimeout;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public String getCurrentSchema() {
        return currentSchema;
    }

    public void setDbConnectionUrl(String dbConnectionUrl) {
        this.dbConnectionUrl = dbConnectionUrl;
    }

    public void setDbConnectionUsr(String dbConnectionUsr) {
        this.dbConnectionUsr = dbConnectionUsr;
    }

    public void setDbConnectionPwd(String dbConnectionPwd) {
        this.dbConnectionPwd = dbConnectionPwd;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setConnectionWaitTimeout(int connectionWaitTimeout) {
        this.connectionWaitTimeout = connectionWaitTimeout;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public void setCurrentSchema(String currentSchema) {
        this.currentSchema = currentSchema;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public boolean isBioDebug() {
        return bioDebug;
    }
}
