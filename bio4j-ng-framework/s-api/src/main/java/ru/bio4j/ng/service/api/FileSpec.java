package ru.bio4j.ng.service.api;

import java.util.Date;

public class FileSpec {
    private String uploadUID;
    private String fileUUID;
    private Date creDatetime;
    private String fileNameOrig;
    private Long fileSize;
    private Date fileDatetime;
    private String md5;
    private String contentType;
    private String remoteIpAddress;
    private String adesc;
    private String extParam;
    private String threadUID;

    public String getFileUUID() {
        return fileUUID;
    }

    public void setFileUUID(String fileUUID) {
        this.fileUUID = fileUUID;
    }

    public String getFileNameOrig() {
        return fileNameOrig;
    }

    public void setFileNameOrig(String fileNameOrig) {
        this.fileNameOrig = fileNameOrig;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    public void setRemoteIpAddress(String remoteIpAddress) {
        this.remoteIpAddress = remoteIpAddress;
    }

    public String getAdesc() {
        return adesc;
    }

    public void setAdesc(String adesc) {
        this.adesc = adesc;
    }

    public String getUploadUID() {
        return uploadUID;
    }

    public void setUploadUID(String uploadUID) {
        this.uploadUID = uploadUID;
    }

    public String getThreadUID() {
        return threadUID;
    }

    public void setThreadUID(String threadUID) {
        this.threadUID = threadUID;
    }

    public String getExtParam() {
        return extParam;
    }

    public void setExtParam(String extParam) {
        this.extParam = extParam;
    }

    public Date getFileDatetime() {
        return fileDatetime;
    }

    public void setFileDatetime(Date fileDatetime) {
        this.fileDatetime = fileDatetime;
    }

    public Date getCreDatetime() {
        return creDatetime;
    }

    public void setCreDatetime(Date creDatetime) {
        this.creDatetime = creDatetime;
    }
}
