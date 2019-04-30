package ru.bio4j.ng.service.api;


import ru.bio4j.ng.model.transport.Prop;

public class SQLContextConfig extends AnConfig {

    @Prop(name = "context.pool.name")
    private String poolName;
    @Prop(name = "context.driver.name")
    private String driverName;
    @Prop(name = "context.connection.url")
    private String dbConnectionUrl;
    @Prop(name = "context.connection.usr")
    private String dbConnectionUsr;
    @Prop(name = "context.connection.pwd")
    private String dbConnectionPwd;
    @Prop(name = "context.min.pool.size")
    private int minPoolSize = 2;
    @Prop(name = "context.max.pool.size")
    private int maxPoolSize = 10;
    @Prop(name = "context.connection.wait.timeout")
    private int connectionWaitTimeout = 5;
    @Prop(name = "context.initial.pool.size")
    private int initialPoolSize = 5;
    @Prop(name = "context.current.schema")
    private String currentSchema = null;


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
}
