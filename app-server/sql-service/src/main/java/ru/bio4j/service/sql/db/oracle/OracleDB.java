package ru.bio4j.service.sql.db.oracle;

import ru.bio4j.service.sql.db.DefaultDB;
import ru.bio4j.service.sql.db.IDTypeHandler;
import ru.bio4j.service.sql.db.WrapperInterpreter;
import ru.bio4j.util.Strings;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @title База данных oracle
 * @author rad
 */
class OracleDB extends DefaultDB {

    public OracleDB(DatabaseMetaData dbmd) throws SQLException {
        super(dbmd);
        this.setIdentifierConverter(Strings.UPPER_CASE_OP);
        this.typeMapper.register(new IDTypeHandler());
        this.wrapperInterpreter = new OracleWrapperInterpreter();

    }

}
