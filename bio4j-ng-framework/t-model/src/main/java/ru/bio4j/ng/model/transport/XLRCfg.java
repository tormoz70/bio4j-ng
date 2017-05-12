package ru.bio4j.ng.model.transport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.exts.XStreamCDATA;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("report")
public class XLRCfg {

    @XStreamAlias("col")
    public static class ColumnDefinition {
        @XStreamAsAttribute
        private String fieldName;
        @XStreamAsAttribute
        private String title;
        @XStreamAsAttribute
        private String format;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    @XStreamAlias("ds")
    public static class DataSource {
        @XStreamCDATA
        private String sql;
        @XStreamAlias("cols")
        private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        @XStreamAlias("sort")
        private List<Sort> sort = new ArrayList<>();
        @XStreamAsAttribute
        private Long maxRowsLimit;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public List<ColumnDefinition> getColumnDefinitions() {
            return columnDefinitions;
        }

        public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
            this.columnDefinitions = columnDefinitions;
        }

        public List<Sort> getSort() {
            return sort;
        }

        public void setSort(List<Sort> sort) {
            this.sort = sort;
        }

        public Long getMaxRowsLimit() {
            return maxRowsLimit;
        }

        public void setMaxRowsLimit(Long maxRowsLimit) {
            this.maxRowsLimit = maxRowsLimit;
        }
    }

    @XStreamAsAttribute
    private Boolean convertResultToPDF;

    private DataSource dss;

    public Boolean getConvertResultToPDF() {
        return convertResultToPDF;
    }

    public void setConvertResultToPDF(Boolean convertResultToPDF) {
        this.convertResultToPDF = convertResultToPDF;
    }

    public DataSource getDss() {
        return dss;
    }

    public void setDss(DataSource dss) {
        this.dss = dss;
    }

}
