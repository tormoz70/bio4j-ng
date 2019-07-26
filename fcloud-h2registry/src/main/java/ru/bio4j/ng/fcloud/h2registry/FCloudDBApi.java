package ru.bio4j.ng.fcloud.h2registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FCloudDBApi<T> {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudDBApi.class);

    private FCloudDBApi() { /* hidden constructor */ }

    public static FCloudDBApi getInstance() {
        return SingletonContainer.INSTANCE;
    }

    private static class SingletonContainer {
        public static final FCloudDBApi INSTANCE;

        static {
            INSTANCE = new FCloudDBApi();
        }
    }

    private static final String CS_FILEUUID_FLDNAME = "FILEUUID";
    private static final String CS_PARENTFILEUUID_FLDNAME = "PARENTFILEUUID";
    private static final String CS_TABLENAME = "FILEREGISTRY";
    private Class<T> tableType = null;
    public synchronized FCloudDBApi<T> initTableType(Class<T> tableType) {
        if(this.tableType == null)
            this.tableType = tableType;
        return this;
    }

    public static String encodeType(MetaType metaType) {
        if(metaType == MetaType.UNDEFINED || metaType == MetaType.STRING)
            return "VARCHAR";
        if(metaType == MetaType.INTEGER)
            return "BIGINT";
        if(metaType == MetaType.DECIMAL)
            return "DOUBLE";
        if(metaType == MetaType.DATE)
            return "TIMESTAMP";
        return "VARCHAR";
    }

    public static String encodeType(Class<?> type) {
        MetaType metaType = MetaTypeConverter.read(type);
        return encodeType(metaType);
    }

    public void dropDB(final Connection conn) throws Exception {
        H2Api.getInstance().execSql(conn,String.format("DROP TABLE IF EXISTS %s", CS_TABLENAME), null);
    }

    public void initDB(final Connection conn) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("CREATE TABLE IF NOT EXISTS %s(\n", CS_TABLENAME));
        boolean fileUUIDDefined = false;
        boolean parentFileUUIDDefined = false;
        for (java.lang.reflect.Field fld : Utl.getAllObjectFields(tableType)) {
            String fldName = fld.getName();
            Prop p = Utl.findAnnotation(Prop.class, fld);
            boolean caseInsensitive = Utl.findAnnotation(DbCaseInsensitive.class, fld) != null;
            if (p != null)
                fldName = p.name();
            fileUUIDDefined = fileUUIDDefined || Strings.compare(fldName, CS_FILEUUID_FLDNAME, true);
            parentFileUUIDDefined = parentFileUUIDDefined || Strings.compare(fldName, CS_PARENTFILEUUID_FLDNAME, true);
            String fldType = encodeType(fld.getType());
            if(Strings.compare(fldType, "VARCHAR", true) && caseInsensitive)
                fldType = "VARCHAR_IGNORECASE";
            sb.append(String.format("  %s  %s,\n", fldName.toUpperCase(), fldType.toUpperCase()));
        }
        if(!fileUUIDDefined)
            throw new Exception(String.format("Field %s not defined in type %s!", CS_FILEUUID_FLDNAME, tableType.getCanonicalName()));
        if(!parentFileUUIDDefined)
            throw new Exception(String.format("Field %s not defined in type %s!", CS_PARENTFILEUUID_FLDNAME, tableType.getCanonicalName()));

        sb.append(String.format("  CONSTRAINT PK_%s PRIMARY KEY (%s)\n", CS_TABLENAME, CS_FILEUUID_FLDNAME));
        sb.append(String.format(")", CS_FILEUUID_FLDNAME));
        H2Api.getInstance().execSql(conn, sb.toString(), null);
    }

    public void storeFileSpec(final Connection conn, final T obj) throws Exception {
        StringBuilder fieldssb = new StringBuilder();
        StringBuilder varssb = new StringBuilder();
        //sb.append(String.format("INSERT INTO %s(\n", tableName.toUpperCase()));
        List<Param> prms = new ArrayList<>();
        for (java.lang.reflect.Field fld : Utl.getAllObjectFields(tableType)) {
            boolean skip = Utl.findAnnotation(DbSkip.class, fld) != null;
            if(!skip) {
                String fldName = fld.getName();
                Prop p = Utl.findAnnotation(Prop.class, fld);
                if (p != null)
                    fldName = p.name();
                boolean toLower = Utl.findAnnotation(DbToLower.class, fld) != null;
                boolean toUpper = Utl.findAnnotation(DbToUpper.class, fld) != null;
                Strings.append(fieldssb, fldName.toUpperCase(), ",");
                String templ = ":%s";
                if(toLower)
                    templ = "lower(:%s)";
                if(toUpper)
                    templ = "upper(:%s)";
                Strings.append(varssb, String.format(templ, fldName.toUpperCase()), ",");
                if (Modifier.isPrivate(fld.getModifiers()))
                    fld.setAccessible(true);
                Paramus.setParamValue(prms, fldName.toUpperCase(), fld.get(obj));
            }
        }
        String sql = String.format("INSERT INTO %s(%s)\n VALUES(%s)", CS_TABLENAME, fieldssb, varssb);
        H2Api.getInstance().execSql(conn, sql, prms);
    }

    private static <T> T getColumnValue(final ResultSet resultSet, final String columnName, Class<T> type) throws Exception {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        int colCount = metaData.getColumnCount();
        String colName;
        for (int i=1; i<=colCount; i++) {
            colName = metaData.getColumnName(i);
            if (colName.equalsIgnoreCase(columnName))
                return Converter.toType(resultSet.getObject(i), type);
        }
        return null;
    }


    private T restoreFileSpec(final ResultSet resultSet) throws Exception {
        T rslt = tableType.newInstance();
        for (java.lang.reflect.Field fld : Utl.getAllObjectFields(tableType)) {
            boolean skip = Utl.findAnnotation(DbSkip.class, fld) != null;
            if(!skip) {
                String fldName = fld.getName();
                Prop p = Utl.findAnnotation(Prop.class, fld);
                if (p != null)
                    fldName = p.name();
                if (Modifier.isPrivate(fld.getModifiers()))
                    fld.setAccessible(true);
                fld.set(rslt, getColumnValue(resultSet, fldName, fld.getType()));
            }
        }
        return rslt;
    }

    public T readFileSpec(final Connection conn, final String fileUid) throws Exception {
        T rslt = null;
        StringBuilder fieldssb = new StringBuilder();
        for (java.lang.reflect.Field fld : Utl.getAllObjectFields(tableType)) {
            boolean skip = Utl.findAnnotation(DbSkip.class, fld) != null;
            if(!skip) {
                String fldName = fld.getName();
                Prop p = Utl.findAnnotation(Prop.class, fld);
                if (p != null)
                    fldName = p.name();
                Strings.append(fieldssb, fldName.toUpperCase(), ",");
            }
        }
        String sql = String.format("SELECT %s FROM %s \n WHERE %s = :%s", fieldssb, CS_TABLENAME, CS_FILEUUID_FLDNAME, CS_FILEUUID_FLDNAME);
        List<Param> prms = new ArrayList<>();
        Paramus.setParamValue(prms, CS_FILEUUID_FLDNAME, fileUid);
        try (ResultSet resultSet = H2Api.getInstance().openSql(conn, sql, prms)) {
            if (resultSet.next()) {
                rslt = restoreFileSpec(resultSet);
            }
        }
        //load children
        sql = String.format("SELECT %s FROM %s \n WHERE %s = :%s", fieldssb, CS_TABLENAME, CS_PARENTFILEUUID_FLDNAME, CS_FILEUUID_FLDNAME);
        try (ResultSet resultSet = H2Api.getInstance().openSql(conn, sql, prms)) {
            T child;
            while(resultSet.next()) {
                child = restoreFileSpec(resultSet);
            }
        }

        return rslt;
    }

    public boolean removeFileSpec(final Connection conn, final String fileUid) throws Exception {
        String sql = String.format("DELETE FROM %s WHERE %s = :%s", CS_TABLENAME, CS_FILEUUID_FLDNAME, CS_FILEUUID_FLDNAME);
        List<Param> prms = new ArrayList<>();
        Paramus.setParamValue(prms, CS_FILEUUID_FLDNAME, fileUid);
        return H2Api.getInstance().execSql(conn, sql, prms);
    }
}
