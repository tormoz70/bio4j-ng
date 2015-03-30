package ru.bio4j.ng.database.pgsql.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.NamedParametersStatement;
import ru.bio4j.ng.database.api.RDBMSUtils;
import ru.bio4j.ng.database.api.StoredProgMetadata;
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
        String pkgName = null;
        String methodName = null;
        String[] storedProcNameParts = Strings.split(storedProcName, ".");
        if(storedProcNameParts.length == 1)
            methodName = storedProcNameParts[0];
        if(storedProcNameParts.length == 2) {
            pkgName    = storedProcNameParts[0];
            methodName = storedProcNameParts[1];
        }
        PackageName pkg = new PgSQLUtils.PackageName(pkgName, methodName);
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


    private static MetaType decodeOraType(String oraTypeName) {
        if(Arrays.asList("CHAR", "VARCHAR", "VARCHAR2", "CLOB").contains(oraTypeName))
            return MetaType.STRING;
        if(Arrays.asList("NUMBER", "INTEGER", "SMALLINT", "FLOAT", "DECIMAL", "DOUBLE PRECISION", "BINARY_DOUBLE", "BINARY_FLOAT").contains(oraTypeName))
            return MetaType.DECIMAL;
        if(Arrays.asList("DATE", "TIMESTAMP", "TIME", "TIME WITH TZ", "TIMESTAMP WITH LOCAL TZ", "TIMESTAMP WITH TZ").contains(oraTypeName))
            return MetaType.DATE;
        if(Arrays.asList("BLOB").contains(oraTypeName))
            return MetaType.BLOB;
        if(Arrays.asList("REF").contains(oraTypeName))
            return MetaType.CURSOR;
        return MetaType.UNDEFINED;
    }

    private static Param.Direction decodeOraDirection(String oraDirName) {
        if(oraDirName.equals("IN"))
            return Param.Direction.IN;
        if(oraDirName.equals("OUT"))
            return Param.Direction.OUT;
        if(oraDirName.equals("IN/OUT"))
            return Param.Direction.INOUT;
        return Param.Direction.IN;
    }

    private static final String SQL_GET_PARAMS_FROM_DBMS = "select "+
            " a.argument_name, a.position, a.sequence, a.data_type, a.in_out, a.data_length" +
            " from ALL_ARGUMENTS a" +
            " where a.owner = sys_context('userenv', 'current_schema')" +
            " and (:package_name is null or a.package_name = upper(:package_name))" +
            " and a.object_name = upper(:method_name)" +
            " order by position";
    private static final String[] DEFAULT_PARAM_PREFIX = {"P_", "V_"};
    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn) throws SQLException {
        StringBuilder args = new StringBuilder();
        PgSQLUtils.PackageName pkg = this.parsStoredProcName(storedProcName);
        List<Param> params = new ArrayList<>();
        try (NamedParametersStatement st = NamedParametersStatement.prepareStatement(conn, SQL_GET_PARAMS_FROM_DBMS, ResultSet.TYPE_FORWARD_ONLY)) {
            st.setStringAtName("package_name", pkg.pkgName);
            st.setStringAtName("method_name", pkg.methodName);
            try (ResultSet rs = st.executeQuery()) {
                try(Paramus p = Paramus.set(params)) {
                    while (rs.next()) {
                        String parName = rs.getString("argument_name");
                        String parType = rs.getString("data_type");
                        String parDir = rs.getString("in_out");
                        if (!(parName.startsWith(DEFAULT_PARAM_PREFIX[0]) || parName.startsWith(DEFAULT_PARAM_PREFIX[1])))
                            throw new IllegalArgumentException("Не верный формат наименования аргументов хранимой процедуры.\n" +
                                    "Необходимо, чтобы все имена аргументов начинались с префикса \"" + DEFAULT_PARAM_PREFIX[0] + "\" или \"" + DEFAULT_PARAM_PREFIX[1] + "\" !");
                        args.append(((args.length() == 0) ? ":" : ",:") + parName.toLowerCase());
                        p.add(Param.builder()
                                .name(parName.toLowerCase())
                                .type(decodeOraType(parType))
                                .direction(decodeOraDirection(parDir))
                                .build());
                    }
                }
            }
        }
        String newExec = storedProcName + "(" + args + ")";
        return new StoredProgMetadata(newExec, params);
    }

}
