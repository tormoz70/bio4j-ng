package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.types.DelegateAction;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Утилиты для работы с метаданными СУБД
 */
public class DbUtils {

    private SqlTypeConverter converter;
    private RDBMSUtils rdbmsUtils;

    private DbUtils() {
    }

    private static final DbUtils instance = new DbUtils();
    private static final Map<Integer, String> jdbcMappings = getAllJdbcTypeNames();

    public static DbUtils getInstance() {return instance;}

    private static final String INIT_ERRORS_TEMPL = "Instance of \"%s\" is not initiated!";
    public void init(SqlTypeConverter converter, RDBMSUtils rdbmsUtils) {
        this.converter = converter;
        this.rdbmsUtils = rdbmsUtils;
    }

    private static Map<Integer, String> getAllJdbcTypeNames() {

        Map<Integer, String> result = new HashMap<Integer, String>();

        for (Field field : Types.class.getFields()) {
            try {
                result.put((Integer) field.get(null), field.getName());
            } catch (IllegalAccessException ex) {}
        }

        return result;
    }

    public String getSqlTypeName(int type) {
        return jdbcMappings.get(type);
    }

    public int paramSqlType(Param param) {
        if(converter == null)
            throw new IllegalArgumentException(String.format(INIT_ERRORS_TEMPL, SqlTypeConverter.class.getSimpleName()));
        int stringSize = 0;
        if(param.getType() == MetaType.STRING){
            if(((param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.IN)) && (stringSize == 0))
                stringSize = Strings.isNullOrEmpty(Paramus.paramValueAsString(param)) ? 0 : Paramus.paramValueAsString(param).length();
        }
        boolean isCallable = (param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.OUT);
        return converter.read(param.getType(), stringSize, isCallable);
    }

    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> fixedParamsOverride) throws SQLException {
        if(rdbmsUtils == null)
            throw new IllegalArgumentException(String.format(INIT_ERRORS_TEMPL, RDBMSUtils.class.getSimpleName()));
        return rdbmsUtils.detectStoredProcParamsAuto(storedProcName, conn, fixedParamsOverride);
    }

    public static List<Param> processExec(final List<Param> params, final SQLContext ctx, final BioCursor cursor) throws Exception {
        final SQLStoredProc cmd = ctx.createStoredProc();
        final BioCursor.SQLDef sqlDef = cursor.getExecSqlDef();
        if(sqlDef == null)
            throw new IllegalArgumentException("Cursor definition has no Exec Sql definition!");
        List<Param> r = ctx.execBatch(new SQLActionScalar<List<Param>>() {
            @Override
            public List<Param> exec(SQLContext context, Connection conn) throws Exception {
                cmd.init(conn, sqlDef.getPreparedSql(), params);
                cmd.execSQL();
                return cmd.getParams();
            }
        });
        return r;
    }

    public static void processSelect(final List<Param> params, final SQLContext ctx, final BioCursor cursor, final DelegateAction<SQLReader, Integer> delegateAction) throws Exception {
        final BioCursor.SQLDef sqlDef = cursor.getSelectSqlDef();
        int r = ctx.execBatch(new SQLActionScalar<Integer>() {
            @Override
            public Integer exec(SQLContext context, Connection conn) throws Exception {
                try(SQLCursor c = context.createCursor()
                        .init(conn, sqlDef.getPreparedSql(), params).open();){
                    while(c.reader().next()){
                        delegateAction.callback(c.reader());
                    }
                }
                return 0;
            }
        });
    }

    public static <T> T processSelectScalar(final List<Param> params, final SQLContext ctx, final BioCursor cursor) throws Exception {
        final BioCursor.SQLDef sqlDef = cursor.getSelectSqlDef();
        T r = ctx.execBatch(new SQLActionScalar<T>() {
            @Override
            public T exec(SQLContext context, Connection conn) throws Exception {
                try(SQLCursor c = context.createCursor()
                        .init(conn, sqlDef.getPreparedSql(), params).open();){
                    if(c.reader().next()){
                        return (T)c.reader().getValue(1);
                    }
                }
                return null;
            }
        });
        return r;
    }

}
