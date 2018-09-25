package ru.bio4j.ng.database.pgsql.impl;

import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.*;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class PgSQLContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(PgSQLContext.class);

    public PgSQLContext(DataSource cpool, SQLConnectionPoolConfig config) throws Exception {
        super(cpool, config);

        if(this.config.getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, Attributes attrs) throws SQLException {
                            if(attrs.getConnection() != null) {
                                String curSchema = sender.getConfig().getCurrentSchema().toUpperCase();
                                LOG.debug("onAfterGetConnection - start setting current_schema="+curSchema);
//                                CallableStatement cs1 = attrs.getConnection().prepareCall( "SET search_path = "+curSchema);
//                                cs1.execute();
                                DbUtils.execSQL(attrs.getConnection(), "SET search_path = "+curSchema);
                                LOG.debug("onAfterGetConnection - OK. current_schema now is "+curSchema);
                            }
                        }
                    }
            );
        }

        wrappers = new PgSQLWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new PgSQLTypeConverterImpl(),
                new PgSQLUtilsImpl()
        );
    }

    @Override
    public String getDBMSName() {
        return "pgsql";
    }


}
