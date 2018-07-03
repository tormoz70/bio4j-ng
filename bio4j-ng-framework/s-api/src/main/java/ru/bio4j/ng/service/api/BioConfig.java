package ru.bio4j.ng.service.api;


import ru.bio4j.ng.service.api.Prop;

public class BioConfig extends AnConfig {
    @Prop(name = "bio.debug")
    private boolean bioDebug = false;

    @Prop(name = "bio.error.handler")
    private String errorHandler = "std";

    @Prop(name = "bio.fcloud.api")
    private String fcloudApi = "fcloud-h2registry";

    @Prop(name = "ehcache.persistent.path")
    private String cachePersistentPath = "./ehcache-persistent";

    @Prop(name = "content.resolver.path")
    private String contentResolverPath = "./bio-content";

    @Prop(name = "tmp.path")
    private String tmpPath = "./bio-tmp";

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
    @Prop(name = "global.live-bio.content.path")
    private String liveBioContentPath = null;


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

    public String getLiveBioContentPath() {
        return liveBioContentPath;
    }

    public void setLiveBioContentPath(String liveBioContentPath) {
        this.liveBioContentPath = liveBioContentPath;
    }

    public String getCachePersistentPath() {
        return cachePersistentPath;
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

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public String getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(String errorHandler) {
        this.errorHandler = errorHandler;
    }

    public String getFcloudApi() {
        return fcloudApi;
    }

    public void setFcloudApi(String fcloudApi) {
        this.fcloudApi = fcloudApi;
    }
}
