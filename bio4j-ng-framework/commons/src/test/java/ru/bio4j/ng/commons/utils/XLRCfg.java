package ru.bio4j.ng.commons.utils;

import com.sun.xml.internal.txw2.annotation.XmlCDATA;
import ru.bio4j.ng.commons.types.CDATAAdapter;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "report")
public class XLRCfg {

    public static class ColumnDefinition {
        private String fieldName;
        private String title;
        private String format;

        public String getFieldName() {
            return fieldName;
        }

        @XmlAttribute
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getTitle() {
            return title;
        }

        @XmlAttribute
        public void setTitle(String title) {
            this.title = title;
        }

        public String getFormat() {
            return format;
        }

        @XmlAttribute
        public void setFormat(String format) {
            this.format = format;
        }
    }

    public static class DataSource {
        private String rangeName;
        private String sql;
        private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        private Boolean singleRow;
        private Long maxRowsLimit;

        public String getRangeName() {
            return rangeName;
        }

        @XmlAttribute
        public void setRangeName(String rangeName) {
            this.rangeName = rangeName;
        }

        public String getSql() {
            return sql;
        }

        @XmlElement
        @XmlJavaTypeAdapter(value=CDATAAdapter.class)
        public void setSql(String sql) {
            this.sql = sql;
        }

        public List<ColumnDefinition> getColumnDefinitions() {
            return columnDefinitions;
        }

        @XmlElement(name = "col")
        @XmlElementWrapper(name = "cols")
        public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
            this.columnDefinitions = columnDefinitions;
        }

        public Boolean getSingleRow() {
            return singleRow;
        }

        @XmlAttribute
        public void setSingleRow(Boolean singleRow) {
            this.singleRow = singleRow;
        }

        public Long getMaxRowsLimit() {
            return maxRowsLimit;
        }

        @XmlAttribute
        public void setMaxRowsLimit(Long maxRowsLimit) {
            this.maxRowsLimit = maxRowsLimit;
        }
    }

    private String bioCode;

    private String title;
    private String subject;
    private String autor;

    private Boolean convertResultToPDF;
    private List<Param> inPrms;
    private List<Param> rptPrms;

    private List<DataSource> dss = new ArrayList<>();

    public String getBioCode() {
        return bioCode;
    }

    @XmlAttribute
    public void setBioCode(String bioCode) {
        this.bioCode = bioCode;
    }

    public String getTitle() {
        return title;
    }

    @XmlElement
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    @XmlElement
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAutor() {
        return autor;
    }

    @XmlElement
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Boolean getConvertResultToPDF() {
        return convertResultToPDF;
    }

    @XmlAttribute
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

    @XmlElement
    public void setRptPrms(List<Param> rptPrms) {
        this.rptPrms = rptPrms;
    }

    public List<DataSource> getDss() {
        return dss;
    }

    @XmlElement(name = "ds")
    @XmlElementWrapper
    public void setDss(List<DataSource> dss) {
        this.dss = dss;
    }

}
