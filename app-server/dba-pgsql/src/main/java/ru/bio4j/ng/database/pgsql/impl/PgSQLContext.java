package ru.bio4j.ng.database.pgsql.impl;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PgSQLContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(PgSQLContext.class);

    private final Wrappers wrappers;

    protected PgSQLContext(DataSource cpool, SQLConnectionPoolConfig config) throws Exception {
        super(cpool, config);
        wrappers = new PgSQLWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new PgSQLTypeConverterImpl(),
                new PgSQLUtils()
        );
    }

    @Override
    public Connection getConnection(String userName, String password) throws SQLException {
        LOG.debug("Getting connection from pool...");
        Connection conn = null;
        int connectionPass = 0;
        while(connectionPass < CONNECTION_TRY_COUNT) {
            connectionPass++;
            if (Strings.isNullOrEmpty(userName))
                conn = this.cpool.getConnection();
            else
                conn = this.cpool.getConnection(userName, password);
            if (conn.isClosed() || !conn.isValid(5)) {
                LOG.debug("Connection is not valid or closed...");
                conn = null;
                ((org.apache.tomcat.jdbc.pool.DataSource) this.cpool).close(true);
                LOG.debug("Waiting {} secs before next try connect to database...", CONNECTION_AFTER_FAIL_PAUSE);
                try {
                    Thread.sleep(CONNECTION_AFTER_FAIL_PAUSE * 1000);
                } catch (InterruptedException e) {}
            } else {
                LOG.debug("Connection is ok...");
                break;
            }
        }

        if(conn == null)
            LOG.debug("All trying of connecting to database ({}) failed...", CONNECTION_TRY_COUNT);

        this.doAfterConnect(SQLConnectionConnectedEvent.Attributes.build(conn));
        return conn;
    }

    public static SQLContext create(SQLConnectionPoolConfig config) throws Exception {
        LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(config, null, "\t"));
        final PoolProperties properties = new PoolProperties();
        properties.setMaxActive(config.getMaxPoolSize());
        properties.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        properties.setUrl(config.getDbConnectionUrl());
        properties.setUsername(config.getDbConnectionUsr());
        properties.setPassword(config.getDbConnectionPwd());
        properties.setCommitOnReturn(false);
        DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(properties);
        return new PgSQLContext(dataSource, config);
    }


    @Override
    public SQLCursor CreateCursor(){
        DbCursor cmd = new DbCursor(this);
        cmd.setParamSetter(new PgSQLSelectableParamSetter());
        return cmd;
    }

    @Override
    public SQLReader CreateReader(ResultSet resultSet) {
        return new PgSQLReader(resultSet);
    }

    @Override
    public String getDBMSName() {
        return "oracle";
    }

    @Override
    public Wrappers getWrappers() {
        return wrappers;
    }

}
