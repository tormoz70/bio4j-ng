package ru.bio4j.service.sql.db;

import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.db.oracle.OracleDBFactory;
import ru.bio4j.service.sql.util.DBTools;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class DBRegistry {
    private static final Map<String, UnsafeFunction<DatabaseMetaData, DB, SQLException>> dbs = new HashMap<>();
    private static final ReentrantReadWriteLock RRWL = new ReentrantReadWriteLock();

    static {
        register("oracle", new OracleDBFactory());
    }

    private DBRegistry() {
    }

    /**
     * Создает объект {@link DB } соответсвенно переданной {@link DatabaseMetaData }
     * @param dmd
     * @return База данных
     * @throws SQLException
     * @title Получение базы данных
     */
    public static DB getDB(DatabaseMetaData dmd) throws SQLException {
        String dbName = DBTools.escapeProductName(dmd.getDatabaseProductName());
        UnsafeFunction<DatabaseMetaData, DB, SQLException> factory = null;
        Lock lock = RRWL.readLock();
        lock.lock();
        try {
            factory = dbs.get(dbName);
        } finally {
            lock.unlock();
        }
        if(factory == null) {
            return new DefaultDB(dmd);
        }
        return factory.apply(dmd);
    }

    /**
     * @title Регистрация базы данных в реестре
     * @param dbName
     * @param factory
     */
    public static void register(String dbName, UnsafeFunction<DatabaseMetaData, DB, SQLException> factory) {
        Lock lock = RRWL.writeLock();
        lock.lock();
        try {
            if(!dbs.containsKey(dbName)) {
                dbs.put(dbName, factory);
            } else {
                throw new RuntimeException("DB with name '" + dbName + "' already registered.");
            }
        } finally {
            lock.unlock();
        }
    }

}
