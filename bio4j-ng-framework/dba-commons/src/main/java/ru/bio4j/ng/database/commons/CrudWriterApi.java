package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.api.RestParamNames;
import ru.bio4j.ng.service.api.UpdelexSQLDef;

import java.util.Arrays;
import java.util.List;

public class CrudWriterApi {


    public static List<ABean> saveRecords(
            final List<Param> params,
            final List<ABean> rows,
            final SQLContext context,
            final BioCursor cursor,
            final User user) throws Exception {
        UpdelexSQLDef sqlDef = cursor.getUpdateSqlDef();
        if(sqlDef == null)
            throw new Exception(String.format("For bio \"%s\" must be defined \"create/update\" sql!", cursor.getBioCode()));
        int affected = context.execBatch((context1, conn, cur, usr) -> {
            SQLStoredProc cmd = context1.createStoredProc();
            cmd.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration());
            for (ABean row : rows) {
                DbUtils.applayRowToParams(row, params);
                cmd.execSQL(params, null);
                try (Paramus paramus = Paramus.set(cmd.getParams())) {
                    for (Param p : paramus.get()) {
                        if (Arrays.asList(Param.Direction.INOUT, Param.Direction.OUT).contains(p.getDirection())) {
                            Field fld = cur.findField(DbUtils.trimParamNam(p.getName()));
                            row.put(fld.getName().toLowerCase(), p.getValue());
                        }
                    }
                }
            }
            return 0;
        }, cursor, user);
        return rows;
    }

    public static void deleteRecords(
            final List<Param> params,
            final List<Object> ids,
            final SQLContext context,
            final BioCursor cursor,
            final User user) throws Exception {
        UpdelexSQLDef sqlDef = cursor.getDeleteSqlDef();
        if (sqlDef == null)
            throw new Exception(String.format("For bio \"%s\" must be defined \"delete\" sql!", cursor.getBioCode()));
        int affected = context.execBatch((context1, conn, cur, usr) -> {
            SQLStoredProc cmd = context1.createStoredProc();
            cmd.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration());
            for (Object id : ids) {
                Paramus.setParamValue(params, RestParamNames.DELETE_PARAM_PKVAL, id, MetaTypeConverter.read(id.getClass()));
                cmd.execSQL(params, user, true);
            }
            return 0;
        }, cursor, user);
    }

    public static void execSQL(
            final List<Param> params,
            final SQLContext context,
            final BioCursor cursor,
            final User user) throws Exception {
        UpdelexSQLDef sqlDef = cursor.getExecSqlDef();
        if (sqlDef == null)
            throw new Exception(String.format("For bio \"%s\" must be defined \"exec\" sql!", cursor.getBioCode()));
        context.execBatch((context1, conn, cur, usr) -> {
            SQLStoredProc cmd = context1.createStoredProc();
            cmd.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration());
            cmd.execSQL(params, user, true);
            return 0;
        }, cursor, user);
    }


}
