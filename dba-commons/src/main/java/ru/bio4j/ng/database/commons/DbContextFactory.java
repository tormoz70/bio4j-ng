package ru.bio4j.ng.database.commons;

import com.zaxxer.hikari.HikariConfig;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioError;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.Properties;

public class DbContextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DbContextFactory.class);

    private static <T> T getValFromCfg(String str, final String defaultStr, final Class<T> type) throws ConvertValueException {
        if(Strings.isNullOrEmpty(str)) str = defaultStr;
        return Converter.toType(str, type);
    }

    public static <T extends DbContextAbstract> SQLContext createApache(SQLConnectionPoolConfig config, Class<T> clazz) {
        if(LOG.isDebugEnabled())
            LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(config, null, "\t"));
        final PoolProperties properties = new PoolProperties();
        properties.setMaxActive(getValFromCfg(config.getMaxPoolSize(), "10", int.class));
        properties.setMaxIdle(getValFromCfg(config.getMaxIdle(), config.getMaxPoolSize(), int.class));
        properties.setMinIdle(getValFromCfg(config.getMinIdle(), config.getMinPoolSize(), int.class));
        properties.setInitialSize(getValFromCfg(config.getInitialPoolSize(), ""+(properties.getMaxActive() / 2), int.class));
        properties.setMaxWait(getValFromCfg(config.getConnectionWaitTimeout(), "60000", int.class));
        properties.setRemoveAbandoned(getValFromCfg(config.getRemoveAbandoned(), "true", boolean.class));
        properties.setRemoveAbandonedTimeout(getValFromCfg(config.getRemoveAbandonedTimeout(), "60", int.class));
        properties.setLogAbandoned(getValFromCfg(config.getLogAbandoned(), "true", boolean.class));
        properties.setDefaultAutoCommit(getValFromCfg(config.getDefaultAutoCommit(), "false", boolean.class));
        properties.setTestOnBorrow(getValFromCfg(config.getTestOnBorrow(), "false", boolean.class));
        if(!Strings.isNullOrEmpty(config.getValidationInterval()))
            properties.setValidationInterval(getValFromCfg(config.getValidationInterval(), null, int.class));
        if(!Strings.isNullOrEmpty(config.getValidationQuery()))
            properties.setValidationQuery(config.getValidationQuery());
        properties.setCommitOnReturn(getValFromCfg(config.getCommitOnReturn(), "true", boolean.class));

        properties.setDriverClassName(config.getDbDriverName());
        properties.setUrl(config.getDbConnectionUrl());
        properties.setUsername(config.getDbConnectionUsr());
        properties.setPassword(config.getDbConnectionPwd());
        properties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(properties);
        try {
            Constructor<T> constructor = clazz.getConstructor(DataSource.class, SQLConnectionPoolConfig.class);
            return constructor.newInstance(new Object[]{dataSource, config});
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }


    public static <T extends DbContextAbstract> SQLContext createHikariCP(SQLConnectionPoolConfig config, Class<T> clazz) {
        if(LOG.isDebugEnabled())
            LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(config, null, "\t"));
        final Properties properties = new Properties();
        properties.setProperty("dataSource.cachePrepStmts", "true");
        properties.setProperty("dataSource.prepStmtCacheSize", "250");
        properties.setProperty("dataSource.prepStmtCacheSqlLimit", "2048");

        HikariConfig cfg = new HikariConfig();
        cfg.setPoolName(config.getPoolName());
        cfg.setAutoCommit(false);
        cfg.setDriverClassName(config.getDbDriverName());
        cfg.setJdbcUrl(config.getDbConnectionUrl());
        cfg.setUsername(config.getDbConnectionUsr());
        cfg.setPassword(config.getDbConnectionPwd());
        cfg.setMaximumPoolSize(getValFromCfg(config.getMaxPoolSize(), "10", int.class));
        cfg.setMinimumIdle(getValFromCfg(config.getMinIdle(), "2", int.class));
        cfg.setDataSourceProperties(properties);

        DataSource dataSource = new com.zaxxer.hikari.HikariDataSource(cfg);
        try {
            Constructor<T> constructor = clazz.getConstructor(DataSource.class, SQLConnectionPoolConfig.class);
            return constructor.newInstance(new Object[]{dataSource, config});
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }

}
