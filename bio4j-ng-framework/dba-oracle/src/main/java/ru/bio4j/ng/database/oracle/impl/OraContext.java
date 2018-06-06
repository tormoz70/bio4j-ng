package ru.bio4j.ng.database.oracle.impl;

import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.*;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class OraContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(OraContext.class);

    public OraContext(final DataSource cpool, final SQLConnectionPoolConfig config) throws Exception {
        super(cpool, config);

        if(this.config.getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, Attributes attrs) throws SQLException {
                            if(attrs.getConnection() != null) {
                                String curSchema = sender.getConfig().getCurrentSchema().toUpperCase();
                                LOG.debug("onAfterGetConnection - start setting current_schema="+curSchema);
                                CallableStatement cs1 = attrs.getConnection().prepareCall( "alter session set current_schema="+curSchema);
                                cs1.execute();
                                LOG.debug("onAfterGetConnection - OK. current_schema now is "+curSchema);
                            }
                        }
                    }
            );
        }

        wrappers = new OraWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new OraTypeConverterImpl(),
                new OraUtilsImpl()
        );
    }

    @Override
    public String getDBMSName() {
        return "oracle";
    }


}
