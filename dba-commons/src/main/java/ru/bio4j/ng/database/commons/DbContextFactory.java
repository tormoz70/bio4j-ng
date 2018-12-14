package ru.bio4j.ng.database.commons;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;

public class DbContextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DbContextAbstract.class);

    public static <T extends DbContextAbstract> SQLContext createApache(SQLConnectionPoolConfig config, Class<T> clazz) throws Exception {
        LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(config, null, "\t"));
        final PoolProperties properties = new PoolProperties();
        properties.setMaxActive(config.getMaxPoolSize());
        properties.setMaxIdle(config.getMaxPoolSize());
        properties.setInitialSize(config.getInitialPoolSize());
        properties.setMaxWait(config.getConnectionWaitTimeout() == 0 ? 30000 : config.getConnectionWaitTimeout());
        properties.setRemoveAbandoned(true);
        properties.setRemoveAbandonedTimeout(60);
        properties.setLogAbandoned(true);


        properties.setDriverClassName(config.getDbDriverName());
        properties.setUrl(config.getDbConnectionUrl());
        properties.setUsername(config.getDbConnectionUsr());
        properties.setPassword(config.getDbConnectionPwd());
        properties.setCommitOnReturn(false);
        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(properties);
        Constructor<T> constructor = clazz.getConstructor(DataSource.class, SQLConnectionPoolConfig.class);
        return constructor.newInstance(new Object[]{dataSource, config});
    }

}
