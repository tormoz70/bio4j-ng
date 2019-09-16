package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 17.12.13
 * Time: 0:26
 * To change this template use File | Settings | File Templates.
 */
public interface SQLStoredProc extends SQLCommand {
    SQLStoredProc init(final Connection conn, final UpdelexSQLDef sqlDef, final int timeout);
    SQLStoredProc init(final Connection conn, final UpdelexSQLDef sqlDef);
    SQLStoredProc init(final Connection conn, final String storedProcName, final List<Param> paramDeclaration);
    SQLStoredProc init(final Connection conn, final String storedProcName);
    void execSQL(final Object params, final User usr, final boolean stayOpened);
    void execSQL(final Object params, final User usr);
    void execSQL(final User usr);
    void close();
}
