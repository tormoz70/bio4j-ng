package ru.bio4j.ng.database.pgsql.impl;

import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.*;

import javax.sql.DataSource;

public class PgSQLContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(PgSQLContext.class);

    public PgSQLContext(DataSource cpool, SQLConnectionPoolConfig config) throws Exception {
        super(cpool, config);
        wrappers = new PgSQLWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new PgSQLTypeConverterImpl(),
                new PgSQLUtils()
        );
    }

    @Override
    public String getDBMSName() {
        return "pgsql";
    }


}
