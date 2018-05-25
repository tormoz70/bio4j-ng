package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.DelegateAction1;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.api.Prop;
import ru.bio4j.ng.commons.utils.ApplyValuesToBeanException;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.StoreRow;
import ru.bio4j.ng.service.api.SelectSQLDef;
import ru.bio4j.ng.service.api.UpdelexSQLDef;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Types;
import java.util.*;

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

    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> fixedParamsOverride) throws Exception {
        if(rdbmsUtils == null)
            throw new IllegalArgumentException(String.format(INIT_ERRORS_TEMPL, RDBMSUtils.class.getSimpleName()));
        return rdbmsUtils.detectStoredProcParamsAuto(storedProcName, conn, fixedParamsOverride);
    }

    public static List<Param> processExec(final User usr, final List<Param> params, final SQLContext ctx, final BioCursor cursor) throws Exception {
        final SQLStoredProc cmd = ctx.createStoredProc();
        final UpdelexSQLDef sqlDef = cursor.getExecSqlDef();
        if(sqlDef == null)
            throw new IllegalArgumentException("Cursor definition has no Exec Sql definition!");
        List<Param> r = ctx.execBatch(new SQLActionScalar<List<Param>>() {
            @Override
            public List<Param> exec(SQLContext context, Connection conn, User u) throws Exception {
                cmd.init(conn, sqlDef.getPreparedSql());
                cmd.execSQL(params, u);
                for (Param p : cmd.getParams()) {
                    Param foundPrm = Paramus.getParam(params, p.getName());
                    if(foundPrm != null)
                        foundPrm.setValue(p.getValue());
                }
                return cmd.getParams();
            }
        }, usr);
        return r;
    }

    public static void processSelect(final User usr, final List<Param> params, final SQLContext ctx, final BioCursor cursor, final DelegateAction1<SQLReader, Integer> delegateAction) throws Exception {
        final SelectSQLDef sqlDef = cursor.getSelectSqlDef();
        int r = ctx.execBatch(new SQLActionScalar<Integer>() {
            @Override
            public Integer exec(SQLContext context, Connection conn, User u) throws Exception {
                try(SQLCursor c = context.createCursor()
                        .init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration()).open(params, u);){
                    while(c.reader().next()){
                        delegateAction.callback(c.reader());
                    }
                }
                return 0;
            }
        }, usr);
    }

    public static <T> T processSelectScalar(final User usr, final List<Param> params, final SQLContext ctx, final BioCursor cursor, Class<T> clazz) throws Exception {
        final SelectSQLDef sqlDef = cursor.getSelectSqlDef();
        T r = ctx.execBatch(new SQLActionScalar<T>() {
            @Override
            public T exec(SQLContext context, Connection conn, User u) throws Exception {
                try(SQLCursor c = context.createCursor()
                        .init(conn, sqlDef.getPreparedSql(), sqlDef.getParamDeclaration()).open(params, u);){
                    if(c.reader().next()){
                        return Converter.toType(c.reader().getValue(1), clazz);
                    }
                }
                return null;
            }
        }, usr);
        return r;
    }

    public static <T> T createBeanFromReader(SQLReader reader, Class<T> clazz) throws Exception {
        if(reader == null)
            throw new IllegalArgumentException("Argument \"reader\" cannot be null!");
        if(clazz == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        T result = (T)clazz.newInstance();
        for(java.lang.reflect.Field fld : Utl.getAllObjectFields(clazz)) {
            String fldName = fld.getName();
            Prop p = Utl.findAnnotation(Prop.class, fld);
            if(p != null)
                fldName = p.name();
            Object valObj = null;
            DBField f = reader.getField(fldName);
            if (f != null)
                valObj = reader.getValue(f.getId());

            if(valObj != null){
                try {
                    Object val = (fld.getType() == Object.class) ? valObj : Converter.toType(valObj, fld.getType());
                    fld.setAccessible(true);
                    fld.set(result, val);
                } catch (Exception e) {
                    throw new ApplyValuesToBeanException(fldName, String.format("Can't set value %s to field. Msg: %s", valObj, e.getMessage()));
                }
            }
        }
        return result;
    }

    public static List<Param> decodeParams(Object params) throws Exception {
        List<Param> rslt = null;
        if(params != null){
            if(params instanceof List)
                rslt = (List<Param>)params;
            else
                rslt = Utl.beanToParams(params);
        }
        return rslt;
    }

    public static String generateSignature(String procName, List<Param> params) throws Exception {
        StringBuilder args = new StringBuilder();
        try(Paramus pp = Paramus.set(params)) {
            for(Param p : pp.get()){
                args.append(((args.length() == 0) ? ":" : ",:") + p.getName().toLowerCase());
            }
        }
        return procName + "(" + args + ")";
    }

    private static final String[] DEFAULT_PARAM_PREFIX = {"P_", "V_"};

    public static void checkParamName(String parName) {
        if (!(parName.toUpperCase().startsWith(DEFAULT_PARAM_PREFIX[0]) || parName.toUpperCase().startsWith(DEFAULT_PARAM_PREFIX[1])))
            throw new IllegalArgumentException(String.format("Не верный формат наименования аргументов хранимой процедуры, \"%s\".\n" +
                    "Необходимо, чтобы все имена аргументов начинались с префикса \"%s\" или \"%s\" !", parName.toUpperCase(), DEFAULT_PARAM_PREFIX[0], DEFAULT_PARAM_PREFIX[1]));
    }

    public static String normalizeParamName(String paramName) {
        if(!Strings.isNullOrEmpty(paramName)) {
            paramName = paramName.toUpperCase().startsWith(DEFAULT_PARAM_PREFIX[0])||
                    paramName.toUpperCase().startsWith(DEFAULT_PARAM_PREFIX[1]) ? paramName :
                    DEFAULT_PARAM_PREFIX[0] + paramName;
        }
        return paramName.toLowerCase();
    }

    public static String trimParamNam(String paramName) {
        if(!Strings.isNullOrEmpty(paramName)) {
            for (String prfx : DEFAULT_PARAM_PREFIX) {
                if(paramName.toUpperCase().startsWith(prfx))
                    return paramName.substring(prfx.length());
            }
            return paramName;
        }
        return paramName.toLowerCase();
    }

    public static Param findParamIgnorePrefix(String paramName, List<Param> params) {
        for (Param param : params) {
            if(param.getName().equalsIgnoreCase(paramName.toLowerCase()) ||
                    param.getName().equalsIgnoreCase(DEFAULT_PARAM_PREFIX[0].toLowerCase() + paramName.toLowerCase()) ||
                    param.getName().equalsIgnoreCase(DEFAULT_PARAM_PREFIX[0].toLowerCase() + paramName.toLowerCase())) {
                return param;
            }
        }
        return null;
    }

    public static void applyParamsToParams(List<Param> src, List<Param> dst, boolean normalizeName, boolean addIfNotExists) throws Exception {
        if(src != null && dst != null) {
            for(Param p : src){
                Param exists = findParamIgnorePrefix(p.getName(), dst);
                if(exists != null && exists != p) {
                    if (p.getType() != null && p.getType() != MetaType.UNDEFINED && p.getType() != exists.getType()) {
                        exists.setValue(Converter.toType(p.getValue(), MetaTypeConverter.write(p.getType())));
                        exists.setType(p.getType());
                    } else
                        exists.setValue(p.getValue());
                    if(normalizeName)
                        exists.setName(normalizeParamName(exists.getName()));
                } else {
                    if(addIfNotExists) {
                        Paramus.setParam(dst, p, false, false);
                        if (normalizeName) {
                            Param newParam = findParamIgnorePrefix(p.getName(), dst);
                            newParam.setName(normalizeParamName(newParam.getName()));
                        }
                    }
                }
            }
        }
    }

    public static void applyParamsToParams(List<Param> src, List<Param> dst, boolean normalizeName) throws Exception {
        applyParamsToParams(src, dst, normalizeName, true);
    }

    public static void applayRowToParams(StoreRow row, List<Param> params){
        try(Paramus paramus = Paramus.set(params)) {
            for(String key : row.getData().keySet()) {
                String paramName = DbUtils.normalizeParamName(key).toLowerCase();
                Object paramValue = row.getData().get(key);
                Param p = paramus.getParam(paramName, true);
                if(p != null){
                    paramus.setValue(paramName, paramValue);
                } else {
                    paramus.add(Param.builder()
                            .name(paramName)
                            .value(paramValue)
                            .build(), true);
                }
            }
        }

    }

    public static void applayRowToParams(ABean row, List<Param> params){
        try(Paramus paramus = Paramus.set(params)) {
            for(String key : row.keySet()) {
                String paramName = DbUtils.normalizeParamName(key).toLowerCase();
                Object paramValue = row.get(key);
                Param p = paramus.getParam(paramName, true);
                if(p != null){
                    paramus.setValue(paramName, paramValue);
                } else {
                    paramus.add(Param.builder()
                            .name(paramName)
                            .value(paramValue)
                            .build(), true);
                }
            }
        }

    }
}
