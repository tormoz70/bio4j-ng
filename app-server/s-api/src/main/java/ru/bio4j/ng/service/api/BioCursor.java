package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Column;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

import java.util.ArrayList;
import java.util.List;

public class BioCursor {

    public static enum Type {
        SELECT, EXEC
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

    private final String bioCode;
    private Type type = Type.SELECT;
    private byte wrapMode = WrapMode.ALL.code();
    private final String sql;
    private String preparedSql;
    private final StoreMetadata metadata = new StoreMetadata();
    private final List<Param> params = new ArrayList<>();
    private Expression filter;
    private Sort sort;
    private int offset;
    private int pageSize;
    private boolean readonly;
    private boolean multySelection;

    public BioCursor(String bioCode, String sql) {
        this.bioCode = bioCode;
        this.sql = sql;
    }

    public BioCursor setParamValue(String name, Object value) {
        try(Paramus paramus = Paramus.set(params)){
            paramus.setValue(name, value);
        }
        return this;
    }

    public BioCursor setParams(List<Param> params) {
        try(Paramus p = Paramus.set(this.params)){
            p.apply(params);
        }
        return this;
    }

    public Column getColumn(String columnName) {
        return null;
    }

    public String getBioCode() { return bioCode; }

    public String getSql() {
        return sql;
    }

    public StoreMetadata getMetadata() {
        return metadata;
    }

    public List<Param> getParams() {
        return params;
    }

    public String getPreparedSql() {
        return preparedSql;
    }

    public void setPreparedSql(String preparedSql) {
        this.preparedSql = preparedSql;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setWrapMode(byte wrapMode) {
        this.wrapMode = wrapMode;
    }

    public byte getWrapMode() {
        return wrapMode;
    }

    public Expression getFilter() { return filter; }

    public void setFilter(Expression filter) { this.filter = filter; }

    public Sort getSort() { return sort; }

    public void setSort(Sort sort) { this.sort = sort; }

    public int getOffset() { return offset; }

    public void setOffset(int offset) { this.offset = offset; }

    public int getPageSize() { return pageSize; }

    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public boolean isReadonly() { return readonly; }

    public void setReadonly(boolean readonly) { this.readonly = readonly; }

    public boolean isMultySelection() { return multySelection; }

    public void setMultySelection(boolean multySelection) { this.multySelection = multySelection; }

}
