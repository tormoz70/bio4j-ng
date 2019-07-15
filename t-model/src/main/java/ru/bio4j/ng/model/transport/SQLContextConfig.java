package ru.bio4j.ng.model.transport;


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
    private String minPoolSize = "2";
    @Prop(name = "context.max.pool.size")
    private String maxPoolSize = "10";

    @Prop(name = "context.min.idle")
    private String minIdle;
    @Prop(name = "context.max.idle")
    private String maxIdle;

    @Prop(name = "context.connection.wait.timeout")
    private String connectionWaitTimeout = "5";
    @Prop(name = "context.initial.pool.size")
    private String initialPoolSize;
    @Prop(name = "context.current.schema")
    private String currentSchema = null;

    @Prop(name = "context.pool.removeAbandoned")
    private String removeAbandoned = "true";
    @Prop(name = "context.pool.removeAbandonedTimeout")
    private String removeAbandonedTimeout = "60";
    @Prop(name = "context.pool.logAbandoned")
    private String logAbandoned = "false";
    @Prop(name = "context.pool.defaultAutoCommit")
    private String defaultAutoCommit = "false";
    @Prop(name = "context.pool.testOnBorrow")
    private String testOnBorrow = "false";
    @Prop(name = "context.pool.validationInterval")
    private String validationInterval = "35000";
    @Prop(name = "context.pool.validationQuery")
    private String validationQuery = null;
    @Prop(name = "context.pool.commitOnReturn")
    private String commitOnReturn = "true";
    @Prop(name = "context.pool.timeBetweenEvictionRunsMillis")
    private String timeBetweenEvictionRunsMillis = null;
    @Prop(name = "context.pool.minEvictableIdleTimeMillis")
    private String minEvictableIdleTimeMillis = null;

    public String getDbConnectionUrl() {
        return dbConnectionUrl;
    }

    public String getDbConnectionUsr() {
        return dbConnectionUsr;
    }

    public String getDbConnectionPwd() {
        return dbConnectionPwd;
    }

    public String  getMinPoolSize() {
        return minPoolSize;
    }

    public String  getMaxPoolSize() {
        return maxPoolSize;
    }

    public String  getConnectionWaitTimeout() {
        return connectionWaitTimeout;
    }

    public String  getInitialPoolSize() {
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

    public void setMinPoolSize(String  minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public void setMaxPoolSize(String  maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setConnectionWaitTimeout(String  connectionWaitTimeout) {
        this.connectionWaitTimeout = connectionWaitTimeout;
    }

    public void setInitialPoolSize(String  initialPoolSize) {
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

    public String getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(String minIdle) {
        this.minIdle = minIdle;
    }

    public String getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    public String getRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(String removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public String getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(String removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public String getLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(String logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public String getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(String defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public String getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(String testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public String getValidationInterval() {
        return validationInterval;
    }

    public void setValidationInterval(String validationInterval) {
        this.validationInterval = validationInterval;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public String getCommitOnReturn() {
        return commitOnReturn;
    }

    public void setCommitOnReturn(String commitOnReturn) {
        this.commitOnReturn = commitOnReturn;
    }

    public String getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(String timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public String getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(String minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
}
