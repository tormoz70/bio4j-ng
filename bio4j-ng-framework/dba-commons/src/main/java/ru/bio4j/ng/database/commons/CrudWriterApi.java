package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLStoredProc;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.api.RestParamNames;
import ru.bio4j.ng.service.api.UpdelexSQLDef;

import java.util.ArrayList;
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
            try {
                cmd.init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration());
                List<Param> prms = new ArrayList<>();
                for (ABean row : rows) {
                    prms.clear();
                    Paramus.setParams(prms, params);
                    DbUtils.applayRowToParams(row, prms);
                    cmd.execSQL(prms, usr, true);
                    try (Paramus paramus = Paramus.set(cmd.getParams())) {
                        for (Param p : paramus.get()) {
                            if (Arrays.asList(Param.Direction.INOUT, Param.Direction.OUT).contains(p.getDirection())) {
                                Field fld = cur.findField(DbUtils.trimParamNam(p.getName()));
                                row.put(fld.getName().toLowerCase(), p.getValue());
                            }
                        }
                    }
                }
            } finally {
                cmd.getStatement().close();
            }

            return 0;
        }, cursor, user);
        return rows;
    }

    public static int deleteRecords(
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
            int r = 0;
            for (Object id : ids) {
                Param prm = Paramus.getParam(cmd.getParams(), RestParamNames.DELETE_PARAM_PKVAL);
                if(prm == null)
                    prm = cmd.getParams().size() > 0 ? cmd.getParams().get(0) : null;
                if(prm != null) {
                    Paramus.setParamValue(params, prm.getName(), id, MetaTypeConverter.read(id.getClass()));
                    cmd.execSQL(params, user, true);
                    r++;
                } else
                    throw new Exception(String.format("ID Param not found in Delete sql of \"%s\"!", cur.getBioCode()));
            }
            return r;
        }, cursor, user);
        return affected;
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
