package ru.bio4j.ng.model.transport;

import java.util.Date;
import java.util.List;

public class FileSpec {
    private String uploadUID;       // уникальный идентификатор используемый системой на стороне клиента (может быть и null)
    private String fileUUID;        // уникальный идентификатор в хранилище определяет путь к файлу
    private Date creDatetime;       // дата/время сохранения в хранилище
    private Date regDatetime;       // дата/время регистрации в БД
    private String fileNameOrig;    // оригинальное имя файла
    private int fileSize;           // размер файла в байтах
    private Date fileDatetime;      // дата/время последнего изменения файла
    private String md5;             // md5
    private String contentType;     // тип контента
    private String remoteIpAddress; // IP с которого загружен файл
    private String uploadType;      // тип загрузки (зависит от реализации)
    private String adesc;           // описание файла (зависит от реализации)
    private String extParam;        // доп параметры JSON
    private String threadUID;       // ID потока, который обработал файл (зависит от реализации)
    private String ownerUserUid;    // UID пользователя, который загрузил файл
    private Long fileId;            // ID файла в БД (после регистрации в БД)
    private String parentFileUUID;  // уникальный идентификатор родительского файла в хранилище определяет путь к файлу
    private String parentFileNameOrig;    // оригинальное имя родительского файла
    private Long parentFileId;            // ID родительского файла в БД (после регистрации в БД)

    private List<FileSpec> innerFiles; // Вложенные файла (зависит от реализации)

    private List<Exception> applicationErrors; // Ошибки обработки файла (зависит от реализации)

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

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
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

    public String getOwnerUserUid() {
        return ownerUserUid;
    }

    public void setOwnerUserUid(String ownerUserUid) {
        this.ownerUserUid = ownerUserUid;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public List<FileSpec> getInnerFiles() {
        return innerFiles;
    }

    public void setInnerFiles(List<FileSpec> innerFiles) {
        this.innerFiles = innerFiles;
    }

    public Date getRegDatetime() {
        return regDatetime;
    }

    public void setRegDatetime(Date regDatetime) {
        this.regDatetime = regDatetime;
    }

    public List<Exception> getApplicationErrors() {
        return applicationErrors;
    }

    public void setApplicationErrors(List<Exception> applicationErrors) {
        this.applicationErrors = applicationErrors;
    }

    public String getParentFileUUID() {
        return parentFileUUID;
    }

    public void setParentFileUUID(String parentFileUUID) {
        this.parentFileUUID = parentFileUUID;
    }

    public String getParentFileNameOrig() {
        return parentFileNameOrig;
    }

    public void setParentFileNameOrig(String parentFileNameOrig) {
        this.parentFileNameOrig = parentFileNameOrig;
    }

    public Long getParentFileId() {
        return parentFileId;
    }

    public void setParentFileId(Long parentFileId) {
        this.parentFileId = parentFileId;
    }
}
