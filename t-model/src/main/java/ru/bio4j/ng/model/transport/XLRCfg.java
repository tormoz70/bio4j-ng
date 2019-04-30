package ru.bio4j.ng.model.transport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.exts.XStreamCDATA;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Alignment;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("report")
public class XLRCfg {

    @XStreamAlias("field")
    public static class ColumnDefinition {
        @XStreamAsAttribute
        @XStreamAlias("name")
        private String fieldName;
        @XStreamAsAttribute
        private String type;
        @XStreamAsAttribute
        @XStreamAlias("header")
        private String title;
        @XStreamAsAttribute
        @XStreamAlias("expFormat")
        private String format;
        @XStreamAsAttribute
        private Alignment align;
        @XStreamAsAttribute
        @XStreamAlias("expWidth")
        private String width;

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

        public Alignment getAlign() {
            return align;
        }

        public void setAlign(Alignment align) {
            this.align = align;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @XStreamAlias("ds")
    public static class DataSource {
        @XStreamCDATA
        private String sql;
        @XStreamAlias("fields")
        private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        @XStreamAlias("sorts")
        private List<Sort> sorts = new ArrayList<>();
        @XStreamAsAttribute
        private Long maxRowsLimit;

        @XStreamAsAttribute
        private String range;

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

        public List<Sort> getSorts() {
            return sorts;
        }

        public void setSorts(List<Sort> sorts) {
            this.sorts = sorts;
        }

        public Long getMaxRowsLimit() {
            return maxRowsLimit;
        }

        public void setMaxRowsLimit(Long maxRowsLimit) {
            this.maxRowsLimit = maxRowsLimit;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }
    }

    @XStreamAlias("append")
    public static class Append {

        private String sessionID;
        private String userUID;
        private String userName;
        private String userOrgId;
        private String userRoles;
        private String remoteIP;

        private List<Param> inParams = new ArrayList<>();

        public String getSessionID() {
            return sessionID;
        }

        public void setSessionID(String sessionID) {
            this.sessionID = sessionID;
        }

        public String getUserUID() {
            return userUID;
        }

        public void setUserUID(String userUID) {
            this.userUID = userUID;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserOrgId() {
            return userOrgId;
        }

        public void setUserOrgId(String userOrgId) {
            this.userOrgId = userOrgId;
        }

        public String getUserRoles() {
            return userRoles;
        }

        public void setUserRoles(String userRoles) {
            this.userRoles = userRoles;
        }

        public String getRemoteIP() {
            return remoteIP;
        }

        public void setRemoteIP(String remoteIP) {
            this.remoteIP = remoteIP;
        }

        public List<Param> getInParams() {
            return inParams;
        }

        public void setInParams(List<Param> inParams) {
            this.inParams = inParams;
        }
    }

    @XStreamAsAttribute
    private String uid;
    @XStreamAsAttribute
    @XStreamAlias("full_code")
    private String fullCode;
    @XStreamAsAttribute
    private String started;
    @XStreamCDATA
    private String title;

    @XStreamAsAttribute
    private Boolean convertResultToPDF;

    private Append append;

    private List<DataSource> dss;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    public Boolean getConvertResultToPDF() {
        return convertResultToPDF;
    }

    public void setConvertResultToPDF(Boolean convertResultToPDF) {
        this.convertResultToPDF = convertResultToPDF;
    }

    public List<DataSource> getDss() {
        return dss;
    }

    public void setDss(List<DataSource> dss) {
        this.dss = dss;
    }

    public Append getAppend() {
        return append;
    }

    public void setAppend(Append append) {
        this.append = append;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
