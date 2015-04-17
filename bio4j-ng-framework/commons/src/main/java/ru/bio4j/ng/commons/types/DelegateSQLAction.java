package ru.bio4j.ng.commons.types;

import java.sql.SQLException;

public interface DelegateSQLAction {
    public void execute() throws SQLException;
}
