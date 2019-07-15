package ru.bio4j.ng.database.api;

public class SQLConnectionPoolConfig {

    public static class Builder {
        private String poolName;
        private String dbDriverName;
        private String dbConnectionUrl;
        private String dbConnectionUsr;
        private String dbConnectionPwd;
        private String minPoolSize;
        private String maxPoolSize;
        private String maxIdle;
        private String minIdle;
        private String connectionWaitTimeout;
        private String initialPoolSize;
        private String currentSchema;

        private String removeAbandoned;
        private String removeAbandonedTimeout;
        private String logAbandoned;
        private String defaultAutoCommit;
        private String testOnBorrow;
        private String validationInterval;
        private String validationQuery;
        private String commitOnReturn;
        private String timeBetweenEvictionRunsMillis;
        private String minEvictableIdleTimeMillis;

        public Builder poolName(String value) {
            poolName = value;
            return this;
        }
        public Builder dbDriverName(String value) {
            dbDriverName = value;
            return this;
        }

        public Builder dbConnectionUrl(String value) {
            dbConnectionUrl = value;
            return this;
        }

        public Builder dbConnectionUsr (String value) {
            dbConnectionUsr = value;
            return this;
        }
        public Builder dbConnectionPwd (String value) {
            dbConnectionPwd = value;
            return this;
        }
        public Builder minPoolSize (String value) {
            minPoolSize = value;
            return this;
        }
        public Builder maxPoolSize (String value) {
            maxPoolSize = value;
            return this;
        }
        public Builder connectionWaitTimeout (String value) {
            connectionWaitTimeout = value;
            return this;
        }
        public Builder initialPoolSize (String value) {
            initialPoolSize = value;
            return this;
        }

        public Builder currentSchema (String value) {
            currentSchema = value;
            return this;
        }

        public Builder removeAbandoned(String value) {
            removeAbandoned = value;
            return this;
        }

        public Builder removeAbandonedTimeout(String value) {
            removeAbandonedTimeout = value;
            return this;
        }

        public Builder logAbandoned(String value) {
            logAbandoned = value;
            return this;
        }

        public Builder defaultAutoCommit(String value) {
            defaultAutoCommit = value;
            return this;
        }

        public Builder testOnBorrow(String value) {
            testOnBorrow = value;
            return this;
        }

        public Builder validationInterval(String value) {
            validationInterval = value;
            return this;
        }

        public Builder validationQuery(String value) {
            validationQuery = value;
            return this;
        }

        public Builder commitOnReturn(String value) {
            commitOnReturn = value;
            return this;
        }

        public Builder timeBetweenEvictionRunsMillis(String value) {
            timeBetweenEvictionRunsMillis = value;
            return this;
        }

        public Builder minEvictableIdleTimeMillis(String value) {
            minEvictableIdleTimeMillis = value;
            return this;
        }

        public Builder maxIdle(String value) {
            maxIdle = value;
            return this;
        }
        public Builder minIdle(String value) {
            minIdle = value;
            return this;
        }

        public SQLConnectionPoolConfig build() {
            return new SQLConnectionPoolConfig(this);
        }


    }

    public static Builder builder() {
        return new Builder();
    }

    private String poolName;
    private String dbDriverName;
    private String dbConnectionUrl;
    private String dbConnectionUsr;
    private String dbConnectionPwd;
    private String minPoolSize;
    private String maxPoolSize;
    private String maxIdle;
    private String minIdle;
    private String connectionWaitTimeout;
    private String initialPoolSize;
    private String currentSchema;

    private String removeAbandoned;
    private String removeAbandonedTimeout;
    private String logAbandoned;
    private String defaultAutoCommit;
    private String testOnBorrow;
    private String validationInterval;
    private String validationQuery;
    private String commitOnReturn;
    private String timeBetweenEvictionRunsMillis;
    private String minEvictableIdleTimeMillis;

    private SQLConnectionPoolConfig(Builder builder) {
        this.poolName = builder.poolName;
        this.dbDriverName = builder.dbDriverName;
        this.dbConnectionUrl = builder.dbConnectionUrl;
        this.dbConnectionUsr = builder.dbConnectionUsr;
        this.dbConnectionPwd = builder.dbConnectionPwd;
        this.minPoolSize = builder.minPoolSize;
        this.maxPoolSize = builder.maxPoolSize;
        this.minIdle = builder.minIdle;
        this.maxIdle = builder.maxIdle;
        this.connectionWaitTimeout = builder.connectionWaitTimeout;
        this.initialPoolSize = builder.initialPoolSize;
        this.currentSchema = builder.currentSchema;
        this.removeAbandoned = builder.removeAbandoned;
        this.removeAbandonedTimeout = builder.removeAbandonedTimeout;
        this.logAbandoned = builder.logAbandoned;
        this.defaultAutoCommit = builder.defaultAutoCommit;
        this.testOnBorrow = builder.testOnBorrow;
        this.validationInterval = builder.validationInterval;
        this.validationQuery = builder.validationQuery;
        this.commitOnReturn = builder.commitOnReturn;
        this.timeBetweenEvictionRunsMillis = builder.timeBetweenEvictionRunsMillis;
        this.minEvictableIdleTimeMillis = builder.minEvictableIdleTimeMillis;

    }

    public String getDbDriverName() {
        return dbDriverName;
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

    public String getMinPoolSize() {
        return minPoolSize;
    }

    public String getMaxPoolSize() {
        return maxPoolSize;
    }

    public String getMaxIdle() {
        return maxIdle;
    }

    public String getMinIdle() {
        return minIdle;
    }

    public String getConnectionWaitTimeout() {
        return connectionWaitTimeout;
    }

    public String getInitialPoolSize() {
        return initialPoolSize;
    }

    public String getCurrentSchema() {
        return currentSchema;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getRemoveAbandoned() {
        return removeAbandoned;
    }

    public String getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public String getLogAbandoned() {
        return logAbandoned;
    }

    public String getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public String getTestOnBorrow() {
        return testOnBorrow;
    }

    public String getValidationInterval() {
        return validationInterval;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public String getCommitOnReturn() {
        return commitOnReturn;
    }

    public String getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public String getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }


}
