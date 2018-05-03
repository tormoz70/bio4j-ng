package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;

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
    SQLStoredProc init(Connection conn, String storedProcName) throws Exception;
//    SQLStoredProc init(Connection conn, UpdelexSQLDef sqlDef) throws Exception;
    void execSQL(Object params, User usr, boolean stayOpened) throws Exception;
    void execSQL(Object params, User usr) throws Exception;
    void execSQL(User usr) throws Exception;
}
