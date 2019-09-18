package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.types.DelegateCheck;
import ru.bio4j.ng.commons.utils.Lists;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.api.BioSQLException;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.util.*;

public class SQLDefinitionImpl implements SQLDefinition {

    public String getExportTitle() {
        return exportTitle;
    }

    public void setExportTitle(String exportTitle) {
        this.exportTitle = exportTitle;
    }

    public Boolean getMultiSelection() {
        return multiSelection;
    }

    public void setMultiSelection(Boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }


    public static class SQLDefImpl implements SQLDef {
        private SQLDefinition owner;
        private final String sql;
        private String preparedSql;

        private List<Param> paramDeclaration = new ArrayList<>();

        public SQLDefImpl(String sql) {

            this.sql = sql;
            this.preparedSql = this.sql;
        }

        @Override
        public String toString() {
            return Utl.buildBeanStateInfo(this, this.getClass().getSimpleName(), "  ", "owner");
        }

        public void setOwner(SQLDefinition bioSQLDefinition){
            owner = bioSQLDefinition;
        }
        public List<Field> getFields() {
            return owner.getFields();
        }

        public Field findPk() {
            return owner.findPk();
        }

        public String getBioCode() {
            return owner.getBioCode();
        }

        public void setParamDeclaration(List<Param> paramDeclaration) {
            this.paramDeclaration = paramDeclaration;
        }

        public List<Param> getParamDeclaration() {
            return paramDeclaration;
        }

        public String getSql() {
            return sql;
        }

        public String getPreparedSql() {
            return preparedSql;
        }

        public void setPreparedSql(String preparedSql) {
            this.preparedSql = preparedSql;
        }

    }

    public static class SelectSQLDefImpl extends SQLDefImpl implements SelectSQLDef {

        private byte wrapMode = WrapMode.ALL.code();
        private String totalsSql;
        private String locateSql;
//        private Filter filter;
        private List<Sort> defaultSort;
//        private int offset;
//        private int pageSize;
//        private Object location;
        private boolean readonly;
        private boolean multySelection;

        public SelectSQLDefImpl(String sql) {
            super(sql);
        }

        public void setWrapMode(byte wrapMode) {
            this.wrapMode = wrapMode;
        }

        public byte getWrapMode() {
            return wrapMode;
        }

        public List<Sort> getDefaultSort() { return defaultSort; }

        public void setDefaultSort(List<Sort> defaultSort) { this.defaultSort = defaultSort; }

        public boolean isReadonly() { return readonly; }

        public void setReadonly(boolean readonly) { this.readonly = readonly; }

        public boolean isMultySelection() { return multySelection; }

        public void setMultySelection(boolean multySelection) { this.multySelection = multySelection; }

        public String getTotalsSql() {
            return totalsSql;
        }

        public void setTotalsSql(String totalsSql) {
            this.totalsSql = totalsSql;
        }

        public String getLocateSql() {
            return locateSql;
        }

        public void setLocateSql(String locateSql) {
            this.locateSql = locateSql;
        }

    }

    public static class UpdelexSQLDefImpl extends SQLDefImpl implements UpdelexSQLDef {
        private String signature;
        public UpdelexSQLDefImpl(String sql) {
            super(sql);
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    private final String bioCode;

    private String exportTitle;

    private Boolean multiSelection;
    private Boolean readOnly;

    private final List<Field> fields = new ArrayList<>();

    private final Map<SQLType, SQLDef> sqlDefs = new HashMap<>();

    public SQLDefinitionImpl(String bioCode) {
        this.bioCode = bioCode;
    }

    public Field findField(final String name) {
        return Lists.first(fields, item -> Strings.compare(name, item.getName(), true));
    }

    public Field findPk() {
        Field pkField = Lists.first(fields, new DelegateCheck<Field>() {
            @Override
            public Boolean callback(Field item) {
                return item.isPk();
            }
        });
        if(pkField == null)
            throw new BioSQLException(String.format("PK field not found in bio defenition \"%s\"", this.bioCode));
        return pkField;
    }

    public String getBioCode() { return bioCode; }

    public List<Field> getFields() {
        return fields;
    }

    public void setSqlDef(SQLType sqlType, SQLDef sqlDef) {
        sqlDefs.put(sqlType, sqlDef);
        sqlDef.setOwner(this);
    }

    public UpdelexSQLDef getUpdateSqlDef() {
        return (UpdelexSQLDef)sqlDefs.get(SQLType.UPDATE);
    }

    public UpdelexSQLDef getDeleteSqlDef() {
        return (UpdelexSQLDef)sqlDefs.get(SQLType.DELETE);
    }

    public UpdelexSQLDef getExecSqlDef() {
        return (UpdelexSQLDef)sqlDefs.get(SQLType.EXECUTE);
    }

    public SelectSQLDef getSelectSqlDef() {
        return (SelectSQLDef)sqlDefs.get(SQLType.SELECT);
    }

    public SQLDef getAfterSelectSqlDef() {
        return sqlDefs.get(SQLType.AFTERSELECT);
    }

    public Collection<SQLDef> sqlDefs(){
        return sqlDefs.values();
    }

    @Override
    public String toString() {
        final String attrFmt = " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        Class<?> type = this.getClass();
        String bnName = type.getName();
        out.append(String.format("%s {\n", bnName));
        out.append(String.format(attrFmt, "bioCode", bioCode));
        out.append("\tfields: [");
        for (Field fld : fields){
            out.append("\n"+Utl.buildBeanStateInfo(fld, fld.getName()+":", "\t\t"));
        }
        out.append("\n\t],");
        out.append(String.format("\n%s,", Utl.buildBeanStateInfo(getSelectSqlDef(), "sql-select:", "\t", "owner")));
        out.append(String.format("\n%s,", Utl.buildBeanStateInfo(getUpdateSqlDef(), "sql-update:", "\t", "owner")));
        out.append(String.format("\n%s,", Utl.buildBeanStateInfo(getDeleteSqlDef(), "sql-delete:", "\t", "owner")));
        out.append(String.format("\n%s", Utl.buildBeanStateInfo(getExecSqlDef(), "sql-exec:", "\t", "owner")));
        out.append("\n}");
        return out.toString();
    }

}
