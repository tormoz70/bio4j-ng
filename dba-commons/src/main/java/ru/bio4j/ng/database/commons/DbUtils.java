package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.Prop;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.StoreRow;
import ru.bio4j.ng.database.api.SelectSQLDef;
import ru.bio4j.ng.database.api.UpdelexSQLDef;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void processExec(final User usr, final Object params, final SQLContext ctx, final SQLDefinition cursor) throws Exception {
        final SQLStoredProc cmd = ctx.createStoredProc();
        final UpdelexSQLDef sqlDef = cursor.getExecSqlDef();
        if(sqlDef == null)
            throw new IllegalArgumentException("Cursor definition has no Exec Sql definition!");
        ctx.execBatch((context) -> {
            cmd.init(context.getCurrentConnection(), sqlDef.getPreparedSql());
            cmd.execSQL(params, context.getCurrentUser());
        }, usr);
    }

    public static void processSelect(final User usr, final Object params, final SQLContext ctx, final SQLDefinition cursor, final DelegateSQLFetch action) throws Exception {
        final List<Param> prms = params != null ? decodeParams(params) : new ArrayList<>();
        final SelectSQLDef sqlDef = cursor.getSelectSqlDef();
        int r = ctx.execBatch((context) -> {
            context.createCursor()
                    .init(context.getCurrentConnection(), sqlDef)
                    .fetch(prms, context.getCurrentUser(), action);
            return 0;
        }, usr);
    }

    public static <T> T processSelectScalar0(final Object params, final SQLContext context, final SQLDefinition sqlDefinition, Class<T> clazz, T defaultValue) throws Exception {
        final List<Param> prms = params != null ? decodeParams(params) : new ArrayList<>();
        final SelectSQLDef sqlDef = sqlDefinition.getSelectSqlDef();
        return context.createCursor()
                    .init(context.getCurrentConnection(), sqlDef).scalar(prms, context.getCurrentUser(), clazz, defaultValue);
    }

    public static <T> T processSelectScalar(final User usr, final Object params, final SQLContext ctx, final SQLDefinition sqlDefinition, Class<T> clazz, T defaultValue) throws Exception {
        return ctx.execBatch((context) -> {
            return processSelectScalar0(params, ctx, sqlDefinition, clazz, defaultValue);
        }, usr);
    }

    public static <T> T processSelectScalar0(final Object params, final SQLContext context, final String sql, Class<T> clazz, T defaultValue) throws Exception {
        final List<Param> prms = params != null ? decodeParams(params) : new ArrayList<>();
        return context.createCursor()
                    .init(context.getCurrentConnection(), sql).scalar(prms, context.getCurrentUser(), clazz, defaultValue);
    }

    public static <T> T processSelectScalar(final User usr, final Object params, final SQLContext ctx, final String sql, Class<T> clazz, T defaultValue) throws Exception {
        return ctx.execBatch((SQLActionScalar0<T>) (context) -> {
            return processSelectScalar0(params, ctx, sql, clazz, defaultValue);
        }, usr);
    }

    public static ABean createABeanFromReader0(SQLReader reader) throws Exception {
        ABean bean = new ABean();
        for (DBField dbField : reader.getFields()) {
            String attrName = dbField.getName();
            Object val = reader.getValue(dbField.getId());
            bean.put(attrName.toLowerCase(), val);
        }
        return bean;
    }

    public static ABean createABeanFromReader(List<ru.bio4j.ng.model.transport.jstore.Field> metaData, SQLReader reader) throws Exception {
        if(metaData != null && metaData.size() > 0) {
            ABean bean = new ABean();
            for (ru.bio4j.ng.model.transport.jstore.Field field : metaData) {
                String attrName = field.getAttrName();
                if (Strings.isNullOrEmpty(attrName))
                    attrName = field.getName();
                DBField f = reader.getField(field.getName());
                if (f != null) {
                    Object val = reader.getValue(f.getId());
                    Class<?> clazz = MetaTypeConverter.write(field.getMetaType());
                    Object valTyped = Converter.toType(val, clazz);
                    bean.put(attrName, valTyped);
                } else
                    bean.put(attrName, null);
            }
            return bean;
        }
        return createABeanFromReader0(reader);
    }

    private static String findFieldName(List<ru.bio4j.ng.model.transport.jstore.Field> metaData, String attrName) {
        for (ru.bio4j.ng.model.transport.jstore.Field fld : metaData){
            if(Strings.compare(fld.getName(), attrName, true) || Strings.compare(fld.getAttrName(), attrName, true))
                return fld.getName();
        }
        return null;
    }

    public static <T> T createBeanFromReader(List<ru.bio4j.ng.model.transport.jstore.Field> metaData, SQLReader reader, Class<T> clazz) throws Exception {
        if(reader == null)
            throw new IllegalArgumentException("Argument \"reader\" cannot be null!");
        if(clazz == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        T result = clazz.newInstance();
        for(java.lang.reflect.Field fld : Utl.getAllObjectFields(clazz)) {
            String attrName = fld.getName();
            Prop p = Utl.findAnnotation(Prop.class, fld);
            if(p != null)
                attrName = p.name();
            String fldName = metaData != null ? findFieldName(metaData, attrName) : attrName;
            if(Strings.isNullOrEmpty(fldName))
                fldName = attrName;
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
                    throw new ApplyValuesToBeanException(attrName, String.format("Can't set value %s to field %s(%s). Msg: %s", valObj, fld.getName(), fld.getType(), e.getMessage()));
                }
            }
        }
        return result;
    }

    public static <T> T createBeanFromReader(SQLReader reader, Class<T> clazz) throws Exception {
        return createBeanFromReader(null, reader, clazz);
    }

    public static List<Param> decodeParams(Object params) throws Exception {
        List<Param> rslt = null;
        if(params != null){
            if(params instanceof List)
                rslt = (List<Param>)params;
            else if(params instanceof ABean)
                rslt = Utl.abeanToParams((ABean) params);
            else if(params instanceof HashMap)
                rslt = Utl.hashmapToParams((HashMap) params);
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

    public static String cutParamPrefix(String paramName) {
        if(Strings.isNullOrEmpty(paramName))
            return paramName;
        if(paramName.toUpperCase().startsWith(DEFAULT_PARAM_PREFIX[0]))
            return paramName.substring(DEFAULT_PARAM_PREFIX[0].length());
        if(paramName.toUpperCase().startsWith(DEFAULT_PARAM_PREFIX[1]))
            return paramName.substring(DEFAULT_PARAM_PREFIX[1].length());
        return paramName;
    }

    public static Param findParamIgnorePrefix(String paramName, List<Param> params) {
        String paramName2Find = cutParamPrefix(paramName);
        for (Param param : params) {
            String prmName = cutParamPrefix(param.getName());
            if(prmName.equalsIgnoreCase(paramName2Find)) {
                return param;
            }
        }
        return null;
    }

    public static String findKeyIgnorePrefix(String paramName, ABean bean) {
        String paramName2Find = cutParamPrefix(paramName);
        for (String key : bean.keySet()) {
            String prmName = cutParamPrefix(key);
            if(prmName.equalsIgnoreCase(paramName2Find)) {
                return key;
            }
        }
        return null;
    }

    public static String findKeyIgnorePrefix(String paramName, HashMap<String, Object> bean) {
        String paramName2Find = cutParamPrefix(paramName);
        for (String key : bean.keySet()) {
            String prmName = cutParamPrefix(key);
            if(prmName.equalsIgnoreCase(paramName2Find)) {
                return key;
            }
        }
        return null;
    }

    private static void applyParamsToParams0(List<Param> src, List<Param> dst, boolean normalizeName, boolean addIfNotExists, boolean overwriteTypes) throws Exception {
        if(src != null && dst != null) {
            for(Param p : src){
                Param exists = findParamIgnorePrefix(p.getName(), dst);
                if(exists != null && exists != p) {
                    MetaType srcType = p.getType();
                    MetaType dstType = overwriteTypes || exists.getType() == MetaType.UNDEFINED ? srcType : exists.getType();

                    if (srcType != null && srcType != MetaType.UNDEFINED && srcType != exists.getType()) {
                        exists.setValue(Converter.toType(p.getValue(), MetaTypeConverter.write(dstType)));
                        exists.setType(dstType);
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

    private static void applyParamsToABean(List<Param> src, ABean dst, boolean normalizeName, boolean addIfNotExists, boolean overwriteTypes) throws Exception {
        if(src != null && dst != null) {
            for(Param p : src){
                String existsKey = findKeyIgnorePrefix(p.getName(), dst);
                if(existsKey != null) {
                    if(normalizeName) {
                        String newName = normalizeParamName(existsKey);
                        dst.remove(existsKey);
                        dst.put(newName, p.getValue());
                    } else
                        dst.put(existsKey, p.getValue());
                } else {
                    if(addIfNotExists) {
                        if(normalizeName) {
                            String newName = normalizeParamName(p.getName());
                            dst.put(newName, p.getValue());
                        } else
                            dst.put(p.getName(), p.getValue());
                    }
                }
            }
        }
    }

    private static void applyParamsToHashMap(List<Param> src, HashMap<String, Object> dst, boolean normalizeName, boolean addIfNotExists, boolean overwriteTypes) throws Exception {
        if(src != null && dst != null) {
            for(Param p : src){
                String existsKey = findKeyIgnorePrefix(p.getName(), dst);
                if(existsKey != null) {
                    if(normalizeName) {
                        String newName = normalizeParamName(existsKey);
                        dst.remove(existsKey);
                        dst.put(newName, p.getValue());
                    } else
                        dst.put(existsKey, p.getValue());
                } else {
                    if(addIfNotExists) {
                        if(normalizeName) {
                            String newName = normalizeParamName(p.getName());
                            dst.put(newName, p.getValue());
                        } else
                            dst.put(p.getName(), p.getValue());
                    }
                }
            }
        }
    }

    public static void applyParamsToObject(List<Param> src, Object dst) throws Exception {
        if (src == null || src.size() == 0 || dst == null)
            return;
        Class<?> dstType = dst.getClass();
        for (java.lang.reflect.Field fld : Utl.getAllObjectFields(dstType)) {
            String param2find = fld.getName();
            Prop prp = fld.getAnnotation(Prop.class);
            if (prp != null && !Strings.isNullOrEmpty(prp.name()))
                param2find = prp.name().toLowerCase();
            Param param = findParamIgnorePrefix(param2find, src);
            if (param != null) {
                fld.setAccessible(true);
                Object valObj = Converter.toType(param.getValue(), fld.getType());
                fld.set(dst, valObj);
            }

        }
    }

    public static List<Param> findOUTParams(List<Param> prms) {
        List<Param> rslt = new ArrayList<>();
        for (Param p : prms) {
            if (Arrays.asList(Param.Direction.INOUT, Param.Direction.OUT).indexOf(p.getDirection()) >= 0)
                rslt.add(p);
        }
        return rslt;
    }

    public static void applyParamsToParams(List<Param> src, Object dst, boolean normalizeName, boolean addIfNotExists, boolean overwriteTypes) throws Exception {
        if(src != null && dst != null) {

            if(dst instanceof List) {
                applyParamsToParams0(src, (List<Param>) dst, normalizeName, addIfNotExists, overwriteTypes);
            } else if(dst instanceof ABean) {
                applyParamsToABean(src, (ABean) dst, normalizeName, addIfNotExists, overwriteTypes);
            } else if(dst instanceof HashMap) {
                applyParamsToHashMap(src, (HashMap<String, Object>) dst, normalizeName, addIfNotExists, overwriteTypes);
            } else {
                applyParamsToObject(src, dst);
            }
        }
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

    public static boolean execSQL(Connection conn, String sql, List<Param> params) throws SQLException {
        try(CallableStatement cs = conn.prepareCall(sql)) {
            return cs.execute();
        }
    }
    public static boolean execSQL(Connection conn, String sql) throws SQLException {
        return execSQL(conn, sql, null);
    }

    private static final String CS_EMPTYCASE = "/*empty*/";
    private static final String CS_FIND_PARAM = ":\\w+";
    private static final String CS_CHECKPRMS_REGEX1 = "(?<=\\/\\*\\[).*(?=\\]\\*\\/)";

    private static boolean checkParamsIsEmpty(String sqlPart, List<Param> prms) {
        boolean isEmpty = true;
        String prmsList = Regexs.find(sqlPart, CS_CHECKPRMS_REGEX1, Pattern.CASE_INSENSITIVE);
        if (!Strings.isNullOrEmpty(prmsList)) {
            String[] prms2check = Strings.split(prmsList, ",");
            for (String prmName : prms2check) {
                Param prm = findParamIgnorePrefix(prmName, prms);
                isEmpty = isEmpty && (prm == null || prm.isEmpty());
            }
        } else {
            Matcher m = Regexs.match(sqlPart, CS_FIND_PARAM, Pattern.CASE_INSENSITIVE);
            while (m.find()) {
                String paramName = m.group(0).substring(1);
                Param prm = findParamIgnorePrefix(paramName, prms);
                isEmpty = isEmpty && (prm == null || prm.isEmpty());
            }
        }
        return isEmpty;
    }

    private static final String CS_CUTEMPTY_PLACEHOLDER_BGN = "/*@{cutempty}*/";
    private static final String CS_CUTEMPTY_PLACEHOLDER_END = "/*{cutempty}@*/";
    private static String cutEmptyFilterConditions(String sql, List<Param> prms) throws Exception {
        return Strings.findRoundedStr(sql, CS_CUTEMPTY_PLACEHOLDER_BGN, CS_CUTEMPTY_PLACEHOLDER_END, new Strings.IRoundedStrProcessor() {
            @Override
            public String process(String found) throws Exception {
                boolean isEmpty = checkParamsIsEmpty(found, prms);
                if(isEmpty)
                    return CS_EMPTYCASE;
                return found;
            }
        });
    }

    private static final String CS_CUTNOTEMPTY_PLACEHOLDER_BGN = "/*@{cutnotempty}*/";
    private static final String CS_CUTNOTEMPTY_PLACEHOLDER_END = "/*{cutnotempty}@*/";

    private static String cutNotEmptyFilterConditions(String sql, List<Param> prms) throws Exception {
        return Strings.findRoundedStr(sql, CS_CUTNOTEMPTY_PLACEHOLDER_BGN, CS_CUTNOTEMPTY_PLACEHOLDER_END, new Strings.IRoundedStrProcessor() {
            @Override
            public String process(String found) throws Exception {
                boolean isEmpty = checkParamsIsEmpty(found, prms);
                if(!isEmpty)
                    return CS_EMPTYCASE;
                return found;
            }
        });
    }

    private static final String CS_CUTIIF_PLACEHOLDER_BGN = "/*@{cutiif}*/";
    private static final String CS_CUTIIF_PLACEHOLDER_END = "/*{cutiif}@*/";
    private static final String CS_CUTIIF_REGEX1 = "(?<=\\/\\*\\[).*(?=\\]\\*\\/)";

    private static String cutIIFConditions(final String sql, final List<Param> prms) throws Exception {
        return Strings.findRoundedStr(sql, CS_CUTIIF_PLACEHOLDER_BGN, CS_CUTIIF_PLACEHOLDER_END, new Strings.IRoundedStrProcessor() {
            @Override
            public String process(String found) throws Exception {
                String rslt = found;
                String js = Regexs.find(rslt, CS_CUTIIF_REGEX1, Pattern.CASE_INSENSITIVE);
                if(Evals.getInstance().runCondition(js, prms))
                    rslt = CS_EMPTYCASE;
                return rslt;
            }
        });
    }

    public static String cutFilterConditions(String sql, List<Param> prms) throws Exception {
        String rslt = cutEmptyFilterConditions( sql, prms);
        rslt = cutNotEmptyFilterConditions( rslt, prms);
        //rslt = cutIIFConditions( rslt, prms);
        return rslt;
    }

    public String extractStoredProcAppError(Exception e) {
        return rdbmsUtils.extractStoredProcAppError(e);
    }

}
