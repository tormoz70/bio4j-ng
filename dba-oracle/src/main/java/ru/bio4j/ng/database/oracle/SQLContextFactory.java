package ru.bio4j.ng.database.oracle;

import ru.bio4j.ng.database.api.SQLConnectionPoolConfig;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.commons.DbContextFactory;
import ru.bio4j.ng.database.oracle.impl.OraContext;
import ru.bio4j.ng.model.transport.SQLContextConfig;

public class SQLContextFactory {
    public static SQLContext create(SQLConnectionPoolConfig config) {
        return DbContextFactory.createHikariCP(config, OraContext.class);
    }
    public static SQLContext create(SQLContextConfig config) {
        return create(SQLConnectionPoolConfig.builder()
                .poolName(config.getPoolName())
                .dbDriverName(config.getDriverName())
                .dbConnectionUrl(config.getDbConnectionUrl())
                .dbConnectionUsr(config.getDbConnectionUsr())
                .dbConnectionPwd(config.getDbConnectionPwd())
                .connectionWaitTimeout(config.getConnectionWaitTimeout())
                .currentSchema(config.getCurrentSchema())
                .minPoolSize(config.getMinPoolSize())
                .maxPoolSize(config.getMaxPoolSize())
                .initialPoolSize(config.getInitialPoolSize())
                .build()
        );
    }
}
