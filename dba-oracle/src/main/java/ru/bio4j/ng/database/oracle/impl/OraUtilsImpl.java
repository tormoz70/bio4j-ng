package ru.bio4j.ng.database.oracle.impl;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.DbNamedParametersStatement;
import ru.bio4j.ng.database.commons.DbUtils;
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
 * Утилиты для работы с метаданными СУБД Oracle
 */
public class OraUtilsImpl implements RDBMSUtils {
	private static class PackageName {
        public final String schemaName;
		public final String pkgName;
		public final String methodName;
		public PackageName(String schemaName, String pkgName, String methodName) {
            this.schemaName = schemaName;
			this.pkgName = pkgName;
			this.methodName = methodName;
		}
	}

    /**
     * Вытаскивает из SQL имя пакета и метода
     * @param storedProcName  - имя процедуры в виде [methodName] или [packageName].[methodName]
     * @return
     */
    public static OraUtilsImpl.PackageName parsStoredProcName(String storedProcName) {
        String schemaName = null;
        String pkgName = null;
        String methodName = null;
        String[] storedProcNameParts = Strings.split(storedProcName, ".");
        if (storedProcNameParts.length == 1) {
            methodName = storedProcNameParts[0];
        } else if(storedProcNameParts.length == 2) {
            pkgName    = storedProcNameParts[0];
            methodName = storedProcNameParts[1];
        } else if(storedProcNameParts.length == 3) {
            schemaName    = storedProcNameParts[0];
            pkgName    = storedProcNameParts[1];
            methodName = storedProcNameParts[2];
        }
    	return new OraUtilsImpl.PackageName(schemaName, pkgName, methodName);
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


    private static MetaType decodeType(String oraTypeName) {
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

    private static Param.Direction decodeDirection(String oraDirName) {
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
            " where a.owner = coalesce(:schema_name, sys_context('userenv', 'current_schema'))" +
            " and (:package_name is null or a.package_name = upper(:package_name))" +
            " and a.object_name = upper(:method_name)" +
            " order by position";
    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> paramsOverride) {
        try {
            OraUtilsImpl.PackageName pkg = this.parsStoredProcName(storedProcName);
            List<Param> params = new ArrayList<>();
            try (SQLNamedParametersStatement st = DbNamedParametersStatement.prepareStatement(conn, SQL_GET_PARAMS_FROM_DBMS)) {
                st.setStringAtName("schema_name", pkg.schemaName);
                st.setStringAtName("package_name", pkg.pkgName);
                st.setStringAtName("method_name", pkg.methodName);
                try (ResultSet rs = st.executeQuery()) {
                    try (Paramus p = Paramus.set(params)) {
                        int i = 0;
                        while (rs.next()) {
                            String parName = rs.getString("argument_name");
                            if (!Strings.isNullOrEmpty(parName)) {
                                String parType = rs.getString("data_type");
                                String parDir = rs.getString("in_out");
                                Param newParam = Param.builder()
                                        .name(parName.toLowerCase())
                                        .type(decodeType(parType))
                                        .direction(decodeDirection(parDir))
                                        .build();
                                Param overrideParam = null;
                                if (paramsOverride != null && paramsOverride.size() > i)
                                    overrideParam = paramsOverride.get(i).getOverride() ? paramsOverride.get(i) : null;
                                if (overrideParam != null) {
                                    if (overrideParam.getOverride())
                                        newParam.setName(DbUtils.normalizeParamName(overrideParam.getName()));
                                    if (overrideParam.getValue() != null)
                                        newParam.setValue(Converter.toType(overrideParam.getValue(), MetaTypeConverter.write(newParam.getType())));
                                }
                                DbUtils.checkParamName(newParam.getName());
                                p.add(newParam);
                                i++;
                            }
                        }
                    }
                }
            }
            String newExec = DbUtils.generateSignature(storedProcName, params);
            return new StoredProgMetadata(newExec, params);
        } catch(SQLException e) {
            throw BioSQLException.create(e);
        }
    }

    @Override
    public BioSQLApplicationError extractStoredProcAppError(Exception e) {
        if(e == null)
            return null;
        if(e instanceof BioSQLApplicationError)
            return (BioSQLApplicationError)e;
        String appErrorCodeStr = Regexs.find(e.getMessage(), "ORA-2\\d{4}", Pattern.CASE_INSENSITIVE);
        List<String> appErrorMessages = Regexs.findAll(e.getMessage(), "(?<=ORA-2\\d{4}:).+", Pattern.CASE_INSENSITIVE);
        String appErrorMessage = appErrorMessages != null && appErrorMessages.size() > 0 ? appErrorMessages.get(appErrorMessages.size()-1).trim() : null;
        if (!Strings.isNullOrEmpty(appErrorMessage) && !Strings.isNullOrEmpty(appErrorCodeStr)) {
            int code = Converter.toType(appErrorCodeStr.substring(4), int.class);
            return new BioSQLApplicationError(code, appErrorMessage);
        }
        return null;
    }


}
