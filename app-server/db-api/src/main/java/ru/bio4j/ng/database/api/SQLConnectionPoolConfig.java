package ru.bio4j.ng.database.api;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 28.11.13
 * Time: 1:38
 * To change this template use File | Settings | File Templates.
 */
public class SQLConnectionPoolConfig {

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public static class Builder {
        private String poolName;
        private String dbDriverName;
        private String dbConnectionUrl;
        private String dbConnectionUsr;
        private String dbConnectionPwd;
        private int minPoolSize = 2;
        private int maxPoolSize = 10;
        private int connectionWaitTimeout = 5;
        private int initialPoolSize = 5;
        private String currentSchema = null;

        public Builder poolName(String poolName) {
            this.poolName = poolName;
            return this;
        }
        public Builder dbDriverName(String dbDriverName) {
            this.dbDriverName = dbDriverName;
            return this;
        }

        public Builder dbConnectionUrl(String value) {
            this.dbConnectionUrl = value;
            return this;
        }

        public Builder dbConnectionUsr (String value) {
            this.dbConnectionUsr = value;
            return this;
        }
        public Builder dbConnectionPwd (String value) {
            this.dbConnectionPwd = value;
            return this;
        }
        public Builder minPoolSize (int value) {
            this.minPoolSize = value;
            return this;
        }
        public Builder maxPoolSize (int value) {
            this.maxPoolSize = value;
            return this;
        }
        public Builder connectionWaitTimeout (int value) {
            this.connectionWaitTimeout = value;
            return this;
        }
        public Builder initialPoolSize (int value) {
            this.initialPoolSize = value;
            return this;
        }

        public Builder currentSchema (String value) {
            this.currentSchema = value;
            return this;
        }

        public SQLConnectionPoolConfig build() {
            return new SQLConnectionPoolConfig(this);
        }

        private String getDbConnectionUrl() {
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

        public String getDbDriverName() {
            return dbDriverName;
        }

        public String getPoolName() {
            return poolName;
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
    private int minPoolSize = 2;
    private int maxPoolSize = 10;
    private int connectionWaitTimeout = 5;
    private int initialPoolSize = 5;
    private String currentSchema = null;

    private SQLConnectionPoolConfig(Builder builder) {
        this.poolName = builder.getPoolName();
        this.dbDriverName = builder.getDbDriverName();
        this.dbConnectionUrl = builder.getDbConnectionUrl();
        this.dbConnectionUsr = builder.getDbConnectionUsr();
        this.dbConnectionPwd = builder.getDbConnectionPwd();
        this.minPoolSize = builder.getMinPoolSize();
        this.maxPoolSize = builder.getMaxPoolSize();
        this.connectionWaitTimeout = builder.getConnectionWaitTimeout();
        this.initialPoolSize = builder.getInitialPoolSize();
        this.currentSchema = builder.getCurrentSchema();
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

    public void setDbDriverName(String dbDriverName) {
        this.dbDriverName = dbDriverName;
    }

}
