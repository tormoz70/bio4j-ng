package ru.bio4j.ng.database.api;

import org.omg.CORBA.PUBLIC_MEMBER;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.DelegateCheck;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Lists;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import javax.management.Query;
import java.io.Serializable;
import java.util.*;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class BioCursor implements Serializable {

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

    public static enum Type {
        SELECT, UPDATE, DELETE, EXECUTE, AFTERSELECT
    }
    public static enum WrapMode {
        NONE((byte)0), FILTER((byte)1), SORT((byte)2), PAGING((byte)4), ALL((byte)7);
        private byte code;
        private WrapMode(byte code) {
            this.code = code;
        }
        public byte code() {
            return code;
        }
    }

    public static class SQLDef {
        private BioCursor owner;
        private final String sql;
        private String preparedSql;

        private List<Param> params = new ArrayList<>();

        public SQLDef(String sql) {

            this.sql = sql;
            this.preparedSql = this.sql;
        }

        @Override
        public String toString() {
            return Utl.buildBeanStateInfo(this, this.getClass().getSimpleName(), "  ", "owner");
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

        public SQLDef setParamValue(String name, Object value, Param.Direction direction, boolean addIfNotExists) {
            try(Paramus paramus = Paramus.set(params)) {
                paramus.setValue(name, value, direction, addIfNotExists);
            }
            return this;
        }

        public SQLDef setParamValue(String name, Object value, boolean addIfNotExists) {
            return setParamValue(name, value, Param.Direction.IN, true);
        }

        public SQLDef setParamValue(String name, Object value) {
            return setParamValue(name, value, true);
        }

        public void setParams(List<Param> params) {
            this.params = params;
        }

//        public SQLDef setParams(List<Param> params) throws Exception {
//            try(Paramus paramus = Paramus.set(this.params)) {
//               for (Param pa : params) {
//                   Param existsParam = paramus.getParam(pa.getName());
//                   Object val = pa.getValue();
//                   if(existsParam != null) {
//                       if(existsParam.getType() == MetaType.UNDEFINED) {
//                           MetaType inType = pa.getType();
//                           if(inType == MetaType.UNDEFINED && val != null)
//                               inType = MetaTypeConverter.read(val.getClass());
//                           existsParam.setType(inType);
//                       }
//                       if(existsParam.getDirection() == Param.Direction.UNDEFINED)
//                           existsParam.setDirection(pa.getDirection());
//
//                       if(existsParam.getType() != MetaType.UNDEFINED){
//                           Class<?> toType = MetaTypeConverter.write(existsParam.getType());
//                           val = Converter.toType(val, toType, true);
//                       }
//                       existsParam.setValue(val);
//                   } else
//                       paramus.add((Param) Utl.cloneBean(pa));
//                }
//            }
//            return this;
//        }
        public List<Param> getParams() {
            return params;
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

    public static class SelectSQLDef extends SQLDef {

        public static final String PAGING_PARAM_OFFSET = "paging$offset";
        public static final String PAGING_PARAM_LAST = "paging$last";

        private byte wrapMode = WrapMode.ALL.code();
        private String totalsSql;
        private String locateSql;
        private Filter filter;
        private List<Sort> sort;
        private int offset;
        private int pageSize;
        private Object location;
        private boolean readonly;
        private boolean multySelection;

        public SelectSQLDef(String sql) {
            super(sql);
        }

        public void setWrapMode(byte wrapMode) {
            this.wrapMode = wrapMode;
        }

        public byte getWrapMode() {
            return wrapMode;
        }

        public Filter getFilter() { return filter; }

        public void setFilter(Filter filter) { this.filter = filter; }

        public List<Sort> getSort() { return sort; }

        public void setSort(List<Sort> sort) { this.sort = sort; }

        public int getOffset() { return offset; }

        public void setOffset(Integer offset) {
            this.offset = offset == null ? 0 : offset;
            this.setParamValue(PAGING_PARAM_OFFSET, this.offset);
            this.setParamValue(PAGING_PARAM_LAST, this.offset + this.pageSize);
        }

        public int getPageSize() { return pageSize; }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize == null || pageSize.intValue() == 0 ? 30 : pageSize;
            this.setParamValue(PAGING_PARAM_LAST, this.offset + this.pageSize);
        }

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

        public Object getLocation() {
            return location;
        }

        public void setLocation(Object location) {
            this.location = location;
        }

    }

    public static class UpdelexSQLDef extends SQLDef {
        private String signature;
        public UpdelexSQLDef(String sql) {
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

    private final Map<Type, SQLDef> sqlDefs = new HashMap<>();

    public BioCursor(String bioCode) {
        this.bioCode = bioCode;
    }

    public Field findField(final String name) throws Exception {
        return Lists.first(fields, item -> Strings.compare(name, item.getName(), true));
    }

    public Field findPk() {
        try {
            return Lists.first(fields, new DelegateCheck<Field>() {
                @Override
                public Boolean callback(Field item) {
                    return item.isPk();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getBioCode() { return bioCode; }

    public List<Field> getFields() {
        return fields;
    }

    public void setSqlDef(Type sqlType, SQLDef sqlDef) {
        sqlDefs.put(sqlType, sqlDef);
        sqlDef.owner = this;
    }

    public UpdelexSQLDef getUpdateSqlDef() {
        return (UpdelexSQLDef)sqlDefs.get(Type.UPDATE);
    }

    public UpdelexSQLDef getDeleteSqlDef() {
        return (UpdelexSQLDef)sqlDefs.get(Type.DELETE);
    }

    public UpdelexSQLDef getExecSqlDef() {
        return (UpdelexSQLDef)sqlDefs.get(Type.EXECUTE);
    }

    public SelectSQLDef getSelectSqlDef() {
        return (SelectSQLDef)sqlDefs.get(Type.SELECT);
    }

    public SQLDef getAfterselectSqlDef() {
        return sqlDefs.get(Type.AFTERSELECT);
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
