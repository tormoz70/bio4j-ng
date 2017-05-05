package ru.bio4j.ng.model.transport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.exts.XStreamCDATA;

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
        @XStreamAlias("range")
        @XStreamAsAttribute
        private String rangeName;
        @XStreamCDATA
        private String sql;
        @XStreamAlias("cols")
        private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        @XStreamAsAttribute
        private Boolean singleRow;
        @XStreamAsAttribute
        private Long maxRowsLimit;

        public String getRangeName() {
            return rangeName;
        }

        public void setRangeName(String rangeName) {
            this.rangeName = rangeName;
        }

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

        public Boolean getSingleRow() {
            return singleRow;
        }

        public void setSingleRow(Boolean singleRow) {
            this.singleRow = singleRow;
        }

        public Long getMaxRowsLimit() {
            return maxRowsLimit;
        }

        public void setMaxRowsLimit(Long maxRowsLimit) {
            this.maxRowsLimit = maxRowsLimit;
        }
    }

    @XStreamAsAttribute
    private String bioCode;

    private String title;
    private String subject;
    private String autor;

    @XStreamAsAttribute
    private Boolean convertResultToPDF;
    @XStreamOmitField
    private List<Param> inPrms;
    @XStreamAlias("params")
    private List<Param> rptPrms;

    private List<DataSource> dss = new ArrayList<>();

    public String getBioCode() {
        return bioCode;
    }

    public void setBioCode(String bioCode) {
        this.bioCode = bioCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Boolean getConvertResultToPDF() {
        return convertResultToPDF;
    }

    public void setConvertResultToPDF(Boolean convertResultToPDF) {
        this.convertResultToPDF = convertResultToPDF;
    }

    public List<Param> getInPrms() {
        return inPrms;
    }

    public void setInPrms(List<Param> inPrms) {
        this.inPrms = inPrms;
    }

    public List<Param> getRptPrms() {
        return rptPrms;
    }

    public void setRptPrms(List<Param> rptPrms) {
        this.rptPrms = rptPrms;
    }

    public List<DataSource> getDss() {
        return dss;
    }

    public void setDss(List<DataSource> dss) {
        this.dss = dss;
    }

}
