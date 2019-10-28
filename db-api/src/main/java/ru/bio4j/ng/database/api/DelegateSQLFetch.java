package ru.bio4j.ng.database.api;

import ru.bio4j.ng.database.api.SQLReader;

import java.sql.SQLException;

public interface DelegateSQLFetch {
    public boolean callback(final SQLReader reader);
}
