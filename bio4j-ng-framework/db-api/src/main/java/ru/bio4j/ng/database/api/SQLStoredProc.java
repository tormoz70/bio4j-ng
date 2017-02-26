package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 17.12.13
 * Time: 0:26
 * To change this template use File | Settings | File Templates.
 */
public interface SQLStoredProc extends SQLCommand {
    SQLStoredProc init(Connection conn, String storedProcName, Object params, int timeout) throws Exception;
    SQLStoredProc init(Connection conn, String storedProcName, Object params) throws Exception;
    void execSQL(Object params) throws Exception;
    void execSQL() throws Exception;
}
