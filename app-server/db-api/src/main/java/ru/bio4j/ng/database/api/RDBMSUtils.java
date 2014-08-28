package ru.bio4j.ng.database.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface RDBMSUtils {
    String detectStoredProcParamsAuto(String storedProcName, Connection conn) throws SQLException;
}
