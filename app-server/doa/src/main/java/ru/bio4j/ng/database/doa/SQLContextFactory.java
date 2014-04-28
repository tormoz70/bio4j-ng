package ru.bio4j.ng.database.doa;

import ru.bio4j.ng.database.api.SQLConnectionPoolConfig;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.doa.impl.OraContext;

/**
 * Created by ayrat on 25.04.14.
 */
public class SQLContextFactory {
    public static SQLContext create(SQLContextConfig config) throws Exception {
//        "ru.bio4j.ng.doa.connectionPool.main"
        return OraContext.create(config.getPoolName(), SQLConnectionPoolConfig.builder()
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
