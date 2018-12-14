package ru.bio4j.ng.database.api;

import java.sql.SQLException;

public interface DelegateSQLScalar<T> {
    public T callback(SQLReader reader) throws Exception;
}
