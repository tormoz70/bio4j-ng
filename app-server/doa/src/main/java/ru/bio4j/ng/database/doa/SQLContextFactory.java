package ru.bio4j.ng.database.doa;

import ru.bio4j.ng.database.api.SQLConnectionPoolConfig;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.doa.impl.OraContext;

/**
 * Created by ayrat on 25.04.14.
 */
public class SQLContextFactory {
    public static SQLContext create(SQLConnectionPoolConfig config) throws Exception {
        return OraContext.create(config);
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
