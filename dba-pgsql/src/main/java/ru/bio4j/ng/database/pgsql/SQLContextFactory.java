package ru.bio4j.ng.database.pgsql;

import ru.bio4j.ng.database.api.SQLConnectionPoolConfig;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.commons.DbContextFactory;
import ru.bio4j.ng.model.transport.SQLContextConfig;
import ru.bio4j.ng.database.pgsql.impl.PgSQLContext;

public class SQLContextFactory {
    public static SQLContext create(SQLConnectionPoolConfig config) throws Exception {
        return DbContextFactory.createHikariCP(config, PgSQLContext.class);
    }
    public static SQLContext create(SQLContextConfig config) throws Exception {
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
