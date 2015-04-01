package ru.bio4j.ng.database.oracle.impl;

import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.*;

import javax.sql.DataSource;

public class OraContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(OraContext.class);


    public OraContext(DataSource cpool, SQLConnectionPoolConfig config) throws Exception {
        super(cpool, config);
        wrappers = new OraWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new OraTypeConverterImpl(),
                new OraUtils()
        );
    }

    @Override
    public String getDBMSName() {
        return "oracle";
    }


}
