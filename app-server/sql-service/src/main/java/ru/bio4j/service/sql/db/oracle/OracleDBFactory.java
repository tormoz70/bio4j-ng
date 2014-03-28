package ru.bio4j.service.sql.db.oracle;

import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.db.DB;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Фабрика представления для субд oracle
 * @title Фабрика представления для субд oracle
 * @author rad
 */
public class OracleDBFactory implements UnsafeFunction<DatabaseMetaData, DB, SQLException> {

    /**
     * @title Выполнение функции создания нового экземпляра базы данных oracle
     * @param dbmd
     * @return Экземпляр базы данных Oracle
     * @throws SQLException
     */
    @Override
    public DB apply(DatabaseMetaData dbmd) throws SQLException {
        return new OracleDB(dbmd);
    }
}
