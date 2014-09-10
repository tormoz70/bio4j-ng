package ru.bio4j.ng.database.api;

import ru.bio4j.ng.commons.types.DelegateCheck;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Lists;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioCursor {

    public static enum Type {
        SELECT, UPDATE, DELETE, EXEC
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
        private String sql;
        private String preparedSql;

        private final List<Param> params = new ArrayList<>();

        public SQLDef setParamValue(String name, Object value, boolean addIfNotExists) {
            try(Paramus paramus = Paramus.set(params)) {
                paramus.setValue(name, value, addIfNotExists);
            }
            return this;
        }

        public SQLDef setParamValue(String name, Object value) {
            return setParamValue(name, value, true);
        }

        public SQLDef setParams(List<Param> params) {
            try(Paramus p = Paramus.set(this.params)) {
                p.apply(params);
            }
            return this;
        }
        public List<Param> getParams() {
            return params;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public String getPreparedSql() {
            return preparedSql;
        }

        public void setPreparedSql(String preparedSql) {
            this.preparedSql = preparedSql;
        }

    }

    public static class SelectSQLDef extends SQLDef {
        private byte wrapMode = WrapMode.ALL.code();
        private String totalsSql;
        private String locateSql;
        private Expression filter;
        private List<Sort> sort;
        private int offset;
        private int pageSize;
        private Object location;
        private boolean readonly;
        private boolean multySelection;

        public void setWrapMode(byte wrapMode) {
            this.wrapMode = wrapMode;
        }

        public byte getWrapMode() {
            return wrapMode;
        }

        public Expression getFilter() { return filter; }

        public void setFilter(Expression filter) { this.filter = filter; }

        public List<Sort> getSort() { return sort; }

        public void setSort(List<Sort> sort) { this.sort = sort; }

        public int getOffset() { return offset; }

        public void setOffset(int offset) { this.offset = offset; }

        public int getPageSize() { return pageSize; }

        public void setPageSize(int pageSize) { this.pageSize = pageSize; }

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

    public static class UpdelexSQLDef extends SQLDef { }

    private final String bioCode;

    private final List<Field> fields = new ArrayList<>();

    private final Map<Type, SQLDef> sqlDefs = new HashMap<>();

    public BioCursor(String bioCode) {
        this.bioCode = bioCode;
    }

    public Field findField(final String name) {
        return Lists.first(fields, new DelegateCheck<Field>() {
            @Override
            public Boolean callback(Field item) {
                return Strings.compare(name, item.getName(), true);
            }
        });
    }

    public Field findPk() {
        return Lists.first(fields, new DelegateCheck<Field>() {
            @Override
            public Boolean callback(Field item) {
                return item.isPk();
            }
        });
    }

    public String getBioCode() { return bioCode; }

    public List<Field> getFields() {
        return fields;
    }

    public void setSqlDef(Type sqlType, SQLDef sqlDef) {
        sqlDefs.put(sqlType, sqlDef);
    }

    public SQLDef getSqlDef(Type sqlType) {
        return sqlDefs.get(sqlType);
    }


}
