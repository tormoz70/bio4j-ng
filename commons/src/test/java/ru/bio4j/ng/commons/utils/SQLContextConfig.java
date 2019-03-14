package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.model.transport.Prop;

/**
 * Created by ayrat on 22.04.14.
 */
public class SQLContextConfig {

    @Prop(name = "crud.pool.name")
    private String poolName;
    @Prop(name = "crud.driver.name")
    private String driverName;
    @Prop(name = "crud.connection.url")
    private String dbConnectionUrl;
    @Prop(name = "crud.connection.usr")
    private String dbConnectionUsr;
    @Prop(name = "crud.connection.pwd")
    private String dbConnectionPwd;
    @Prop(name = "crud.min.pool.size")
    private int minPoolSize = 2;
    @Prop(name = "crud.max.pool.size")
    private int maxPoolSize = 10;
    @Prop(name = "crud.connection.wait.timeout")
    private int connectionWaitTimeout = 5;
    @Prop(name = "crud.initial.pool.size")
    private int initialPoolSize = 5;
    @Prop(name = "crud.current.schema")
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
