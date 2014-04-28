package ru.bio4j.ng.database.doa.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Strings;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Утилиты для работы с метаданными СУБД Oracle
 */
public class OraUtils {
	public static class PackageName {
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
    public static OraUtils.PackageName parsStoredProcName(String storedProcName) {
        //String pkgName = Regexs.find(storedProcName, "\\b[\\w$]+\\b(?=[.])", true);
        //String methodName = Regexs.find(storedProcName, "(?<=[.])\\b[\\w$]+\\b(?=\\s*[(;])", true);
        String pkgName = null;
        String methodName = null;
        String[] storedProcNameParts = Strings.split(storedProcName, ".");
        if(storedProcNameParts.length == 1)
            methodName = storedProcNameParts[0];
        if(storedProcNameParts.length == 2) {
            pkgName    = storedProcNameParts[0];
            methodName = storedProcNameParts[1];
        }
        PackageName pkg = new OraUtils.PackageName(pkgName, methodName);
    	return pkg;
    }


    /**
     * Вытаскивает из SQL все вызовы хранимых процедур
     * @param sql
     * @return
     */
    public static String[] detectExecsOfStoredProcs(String sql) {
        final String csDelimiter = "+|+";
        String resultStr = null;
        Matcher m = Regexs.match(sql, "\\b[\\w$]+\\b[.]\\b[\\w$]+\\b\\s*[(]\\s*[$]PRMLIST\\s*[)]", true);
        while(m.find())
            resultStr = Strings.append(resultStr, m.group(), csDelimiter);
        return !Strings.isNullOrEmpty(resultStr) ? Strings.split(resultStr, csDelimiter) : new String[0];
    }



    private static final String SQL_GET_PARAMS_FROM_DBMS = "select "+
            " a.argument_name, a.position, a.sequence, a.data_type, a.in_out, a.data_length" +
            " from ALL_ARGUMENTS a" +
            " where a.owner = sys_context('userenv', 'current_schema')" +
            " and (:package_name is null or a.package_name = upper(:package_name))" +
            " and a.object_name = upper(:method_name)" +
            " order by position";
    private static final String[] DEFAULT_PARAM_PREFIX = {"P_", "V_"};
    public static String detectStoredProcParamsAuto(String storedProcName, Connection conn) throws SQLException {
        StringBuilder args = new StringBuilder();
        OraUtils.PackageName pkg = OraUtils.parsStoredProcName(storedProcName);
        try (OraclePreparedStatement st = (OraclePreparedStatement)conn.prepareStatement(SQL_GET_PARAMS_FROM_DBMS, ResultSet.TYPE_FORWARD_ONLY)) {
            st.setStringAtName("package_name", pkg.pkgName);
            st.setStringAtName("method_name", pkg.methodName);
            try (OracleResultSet rs = (OracleResultSet)st.executeQuery()) {
                while(rs.next()) {
                    String parName = rs.getString("argument_name");
                    if(!(parName.startsWith(DEFAULT_PARAM_PREFIX[0]) || parName.startsWith(DEFAULT_PARAM_PREFIX[1])))
                        throw new IllegalArgumentException("Не верный формат наименования аргументов хранимой процедуры.\n" +
                                "Необходимо, чтобы все имена аргументов начинались с префикса \"" + DEFAULT_PARAM_PREFIX[0] + "\" или \"" + DEFAULT_PARAM_PREFIX[1] + "\" !");
                    //parName = parName.substring(2);
                    args.append(((args.length() == 0) ? ":" : ",:") + parName.toLowerCase());
                }
            }
        }
        String newExec = storedProcName + "(" + args + ")";
        return newExec;
    }

    public static List<String> extractParamNamesFromSQL(String sql) {
//        LOG.debug("extractParamNamesFromSQL - start");
//        LOG.debug("sql: " + sql);
        List<String> rslt = new ArrayList();
//        LOG.debug("Удаляем все строковые константы");
        sql = Regexs.replace(sql, "(['])(.*?)\\1", "", true);
//        LOG.debug("sql: " + sql);
//        LOG.debug("Удаляем все многострочные коментарии");
        sql = Regexs.replace(sql, "[/]\\*.*?\\*[/]", "", true);
//        LOG.debug("sql: " + sql);

//        LOG.debug("Удаляем все операторы присвоения");
        sql = Regexs.replace(sql, ":=", "", true);
//        LOG.debug("sql: " + sql);

//        LOG.debug("Находим все параметры вида :qwe_ad");
        Matcher m = Regexs.match(sql, "(?<=:)\\b[\\w\\#\\$]+", true);
        while(m.find()) {
            String parName = m.group();
//            LOG.debug(" - parName["+m.start()+"]: " + parName);
            if(rslt.indexOf(parName) == -1)
                rslt.add(parName);
        }
//        LOG.debug("Найдено: " + rslt.size() + " параметров");
        return rslt;
    }

    public static int paramSqlType(Param param) {
        int stringSize = 0;
        if(param.getType() == MetaType.STRING){
            if(((param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.IN)) && (stringSize == 0))
                stringSize = Strings.isNullOrEmpty(Paramus.paramValueAsString(param)) ? 0 : Paramus.paramValueAsString(param).length();
        }
        boolean isCallable = (param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.OUT);
        return SqlTypeConverter.read(param.getType(), stringSize, isCallable);
    }

}
