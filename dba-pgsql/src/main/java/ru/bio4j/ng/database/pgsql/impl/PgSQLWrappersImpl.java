package ru.bio4j.ng.database.pgsql.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.WrappersBaseImpl;

public class PgSQLWrappersImpl extends WrappersBaseImpl {
    private static final Logger LOG = LoggerFactory.getLogger(PgSQLWrappersImpl.class);
    public PgSQLWrappersImpl(String dbmsName) throws Exception {
        super(dbmsName, new PgSQLWrapperInterpreter());
        LOG.debug("Wrappers for \"{}\" database initialized.", dbmsName);
    }
}
