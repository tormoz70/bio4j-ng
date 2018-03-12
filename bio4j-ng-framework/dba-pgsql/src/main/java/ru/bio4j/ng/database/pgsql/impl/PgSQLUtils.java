package ru.bio4j.ng.database.pgsql.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;
import ru.bio4j.ng.database.api.RDBMSUtils;
import ru.bio4j.ng.database.api.StoredProgMetadata;
import ru.bio4j.ng.database.commons.DbNamedParametersStatement;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилиты для работы с метаданными СУБД PostgreSQL
 */
public class PgSQLUtils implements RDBMSUtils {
	private static class PackageName {
		public final String pkgName;
		public final String methodName;
		public PackageName(String pkgName, String methodName) {
			this.pkgName = pkgName;
			this.methodName = methodName;
		}
	}

    /**
     * Вытаскивает из SQL имя пакета и метода
     * @param storedProcName  - имя процедуры в виде [methodName] или [packageName].[methodName]
     * @return
     */
    private PgSQLUtils.PackageName parsStoredProcName(String storedProcName) {
        //String pkgName = null;
        //String methodName = null;
        //String[] storedProcNameParts = Strings.split(storedProcName, ".");
        //if(storedProcNameParts.length == 1)
        //    methodName = storedProcNameParts[0];
        //if(storedProcNameParts.length == 2) {
        //    pkgName    = storedProcNameParts[0];
        //    methodName = storedProcNameParts[1];
        //}
        PackageName pkg = new PgSQLUtils.PackageName(null, storedProcName);
    	return pkg;
    }


    /**
     * Вытаскивает из SQL все вызовы хранимых процедур
     * @param sql
     * @return
     */
    private String[] detectExecsOfStoredProcs(String sql) {
        final String csDelimiter = "+|+";
        String resultStr = null;
        Matcher m = Regexs.match(sql, "\\b[\\w$]+\\b[.]\\b[\\w$]+\\b\\s*[(]\\s*[$]PRMLIST\\s*[)]", Pattern.CASE_INSENSITIVE);
        while(m.find())
            resultStr = Strings.append(resultStr, m.group(), csDelimiter);
        return !Strings.isNullOrEmpty(resultStr) ? Strings.split(resultStr, csDelimiter) : new String[0];
    }

    private static MetaType decodeType(String typeName) {
        if(Strings.isNullOrEmpty(typeName))
            return MetaType.UNDEFINED;
        typeName = typeName.toUpperCase();
        if(Arrays.asList("CHARACTER", "CHARACTER VARYING", "TEXT").contains(typeName))
            return MetaType.STRING;
        if(Arrays.asList("SMALLINT  ", "INTEGER", "BIGINT", "SMALLSERIAL", "SERIAL", "BIGSERIAL").contains(typeName))
            return MetaType.INTEGER;
        if(Arrays.asList("DECIMAL", "NUMERIC", "REAL", "DOUBLE PRECISION").contains(typeName))
            return MetaType.DECIMAL;
        if(Arrays.asList("DATE", "TIMESTAMP", "TIME", "TIMESTAMP WITH TIME ZONE", "TIME WITH TIME ZONE").contains(typeName))
            return MetaType.DATE;
        if(Arrays.asList("BYTEA").contains(typeName))
            return MetaType.BLOB;
        if(Arrays.asList("REFCURSOR").contains(typeName))
            return MetaType.CURSOR;
        return MetaType.UNDEFINED;
    }

    public static final String DIRECTION_NAME_IN = "IN";
    public static final String DIRECTION_NAME_OUT = "OUT";
    public static final String DIRECTION_NAME_INOUT = "INOUT";
    private static Param.Direction decodeDirection(String dirName) {
        if(dirName.equals(DIRECTION_NAME_IN))
            return Param.Direction.IN;
        if(dirName.equals(DIRECTION_NAME_OUT))
            return Param.Direction.OUT;
        if(dirName.equals(DIRECTION_NAME_INOUT))
            return Param.Direction.INOUT;
        return Param.Direction.IN;
    }

    private static String cutDirName(String paramDesc, String dirName){
        if(paramDesc.toUpperCase().startsWith(dirName.toUpperCase()+" "))
            paramDesc = paramDesc.substring(dirName.length());
        return paramDesc.trim();
    }

    public static String cutDirNames(String paramDesc){
        paramDesc = cutDirName(paramDesc, DIRECTION_NAME_IN);
        paramDesc = cutDirName(paramDesc, DIRECTION_NAME_OUT);
        paramDesc = cutDirName(paramDesc, DIRECTION_NAME_INOUT);
        return paramDesc;
    }

    private static boolean checkDirName(String paramDesc, String dirName){
        if(!Strings.isNullOrEmpty(paramDesc) && !Strings.isNullOrEmpty(dirName) && paramDesc.toUpperCase().startsWith(dirName+" ")) {
            return true;
        }
        return false;
    }

    public static String extractDirName(String paramDesc){
        if(checkDirName(paramDesc, DIRECTION_NAME_IN))
            return DIRECTION_NAME_IN;
        if(checkDirName(paramDesc, DIRECTION_NAME_OUT))
            return DIRECTION_NAME_OUT;
        if(checkDirName(paramDesc, DIRECTION_NAME_INOUT))
            return DIRECTION_NAME_INOUT;
        return DIRECTION_NAME_IN;
    }

    private static final String SQL_GET_DOMINE_TYPE_DBMS =
            "select data_type from information_schema.domains a\n" +
                    "where a.domain_schema = 'public'\n" +
                    "and a.domain_name = :domain_name";
    private static String detectDomineType(String type, Connection conn) throws SQLException {
        try (SQLNamedParametersStatement st = DbNamedParametersStatement.prepareStatement(conn, SQL_GET_DOMINE_TYPE_DBMS)) {
            st.setStringAtName("domain_name", type);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private static void parsParam(String paramDesc, Paramus p, StringBuilder args, Param fixedParam) {
        String dirName = extractDirName(paramDesc);
        paramDesc = cutDirNames(paramDesc);
        String paramNameFromDesc = paramDesc.substring(0, paramDesc.indexOf(" ")).trim().toLowerCase();
        String paramNameFixed = fixedParam != null ? fixedParam.getName().toLowerCase() : null;
        if(paramNameFromDesc.equalsIgnoreCase(paramNameFixed) ||
                paramNameFromDesc.equals(DEFAULT_PARAM_PREFIX[0].toLowerCase() + paramNameFixed)
                || paramNameFromDesc.equals(DEFAULT_PARAM_PREFIX[1].toLowerCase() + paramNameFixed)) {
            paramNameFixed = paramNameFromDesc;
            fixedParam.setName(paramNameFixed);
        }
        if (fixedParam != null && !paramNameFromDesc.equalsIgnoreCase(paramNameFixed))
            throw new IllegalArgumentException("Параметру хранимой процедуры \"" + paramNameFromDesc +
                    "\" не соответствует параметр на входе \"" + paramNameFixed + "\"!");


        paramDesc = cutDirName(paramDesc, paramNameFromDesc);
        String paramNameUpper = paramNameFromDesc.toUpperCase();
        String typeName = paramDesc;

        if (!(paramNameUpper.startsWith(DEFAULT_PARAM_PREFIX[0]) || paramNameUpper.startsWith(DEFAULT_PARAM_PREFIX[1])))
            throw new IllegalArgumentException("Не верный формат наименования аргументов хранимой процедуры.\n" +
                    "Необходимо, чтобы все имена аргументов начинались с префикса \"" + DEFAULT_PARAM_PREFIX[0] + "\" или \"" + DEFAULT_PARAM_PREFIX[1] + "\" !");
        args.append(((args.length() == 0) ? ":" : ",:") + paramNameFromDesc);
        MetaType type = decodeType(typeName);

        p.add(Param.builder()
                .name(paramNameFromDesc)
                .type(fixedParam != null ? fixedParam.getType() : type)
                .direction(fixedParam != null ? fixedParam.getDirection() : decodeDirection(dirName))
                .innerObject(typeName)
                .build());

    }

    //"p_param1 character varying, OUT p_param2 integer"
    public static void parsParams(String paramsList, Paramus p, StringBuilder args, List<Param> paramsOverride) {
        String[] substrs = Strings.split(paramsList, ",");
        int i = 0;
        for (String prmDesc : substrs) {
            Param overrideParam = null;
            if(paramsOverride != null && paramsOverride.size() > i)
                overrideParam = paramsOverride.get(i).getOverride() ? paramsOverride.get(i) : null;
            parsParam(prmDesc.trim(), p, args, overrideParam);
            i++;
        }
    }

    private static final String SQL_GET_PARAMS_FROM_DBMS = "SELECT pg_get_function_identity_arguments(:method_name::regproc) as rslt";

    private static final String[] DEFAULT_PARAM_PREFIX = {"P_", "V_"};
    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> paramsOverride) throws SQLException {
        StringBuilder args = new StringBuilder();
        PgSQLUtils.PackageName pkg = this.parsStoredProcName(storedProcName);
        List<Param> params = new ArrayList<>();
        try (SQLNamedParametersStatement st = DbNamedParametersStatement.prepareStatement(conn, SQL_GET_PARAMS_FROM_DBMS)) {
            st.setStringAtName("method_name", pkg.methodName);
            try (ResultSet rs = st.executeQuery()) {
                try(Paramus p = Paramus.set(params)) {
                    if (rs.next()) {
                        String pars = rs.getString("rslt");
                        parsParams(pars, p, args, paramsOverride);
                    }
                }
            }
        }
        try(Paramus pp = Paramus.set(params)) {
            for(Param p : pp.get()){
                if(p.getType() == MetaType.UNDEFINED){
                    String typeName = detectDomineType((String)p.getInnerObject(), conn);
                    p.setType(decodeType(typeName));
                }
            }
        }

        String newExec = storedProcName + "(" + args + ")";
        return new StoredProgMetadata(newExec, params);
    }

}
