package ru.bio4j.ng.model.transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileSpec<T extends FileSpec> {
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
    private String serviceUID;      // ID сервиса, который обработал/принял файл (зависит от реализации)
    private String ownerUserUid;    // UID пользователя, который загрузил файл
    private Long fileId;            // ID файла в БД (после регистрации в БД)
    private String parentFileUUID;  // уникальный идентификатор родительского файла в хранилище определяет путь к файлу
    private String parentFileNameOrig;    // оригинальное имя родительского файла
    private Long parentFileId;            // ID родительского файла в БД (после регистрации в БД)
    private List<T> innerFiles; // Вложенные файла (зависит от реализации)

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

    public String getServiceUID() {
        return serviceUID;
    }

    public void setServiceUID(String serviceUID) {
        this.serviceUID = serviceUID;
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

    public List<T> getInnerFiles() {
        return innerFiles;
    }

    public void setInnerFiles(List<T> innerFiles) {
        this.innerFiles = innerFiles;
    }

    public Date getRegDatetime() {
        return regDatetime;
    }

    public void setRegDatetime(Date regDatetime) {
        this.regDatetime = regDatetime;
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

    public static <T extends FileSpec> Builder<T> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<T extends FileSpec> {
        protected String uploadUID;
        protected String fileUUID;
        protected Date regDatetime;
        protected String fileNameOrig;
        protected int fileSize;
        protected Date fileDatetime;
        protected String md5;
        protected String contentType;
        protected String remoteIpAddress;
        protected String uploadType;
        protected String adesc;
        protected String extParam;
        protected String serviceUID;
        protected String ownerUserUid;
        protected Long fileId;
        protected String parentFileUUID;
        protected String parentFileNameOrig;
        protected Long parentFileId;
        protected List<T> innerFiles;

        protected Builder(){}

        public Builder fileUUID(String fileUUID) {
            this.fileUUID = fileUUID;
            return this;
        }

        public Builder fileNameOrig(String fileNameOrig) {
            this.fileNameOrig = fileNameOrig;
            return this;
        }

        public Builder fileSize(int fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder md5(String md5) {
            this.md5 = md5;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder remoteIpAddress(String remoteIpAddress) {
            this.remoteIpAddress = remoteIpAddress;
            return this;
        }

        public Builder adesc(String adesc) {
            this.adesc = adesc;
            return this;
        }

        public Builder uploadUID(String uploadUID) {
            this.uploadUID = uploadUID;
            return this;
        }

        public Builder serviceUID(String serviceUID) {
            this.serviceUID = serviceUID;
            return this;
        }

        public Builder extParam(String extParam) {
            this.extParam = extParam;
            return this;
        }

        public Builder fileDatetime(Date fileDatetime) {
            this.fileDatetime = fileDatetime;
            return this;
        }

        public Builder ownerUserUid(String ownerUserUid) {
            this.ownerUserUid = ownerUserUid;
            return this;
        }

        public Builder uploadType(String uploadType) {
            this.uploadType = uploadType;
            return this;
        }

        public Builder fileId(Long fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder addInnerFile(T innerFile) {
            if(this.innerFiles == null)
                this.innerFiles = new ArrayList<>();
            this.innerFiles.add(innerFile);
            return this;
        }

        public Builder regDatetime(Date regDatetime) {
            this.regDatetime = regDatetime;
            return this;
        }

        public Builder parentFileUUID(String parentFileUUID) {
            this.parentFileUUID = parentFileUUID;
            return this;
        }

        public Builder parentFileNameOrig(String parentFileNameOrig) {
            this.parentFileNameOrig = parentFileNameOrig;
            return this;
        }

        public Builder parentFileId(Long parentFileId) {
            this.parentFileId = parentFileId;
            return this;
        }

        public T build(Class<T> clazz) throws Exception {
            T result = clazz.newInstance();
            result.setUploadUID(this.uploadUID);
            result.setFileUUID(fileUUID);
            result.setCreDatetime(new Date());
            result.setRegDatetime(regDatetime);
            result.setFileNameOrig(fileNameOrig);
            result.setFileSize(fileSize);
            result.setFileDatetime(fileDatetime);
            result.setMd5(md5);
            result.setContentType(contentType);
            result.setRemoteIpAddress(remoteIpAddress);
            result.setUploadType(uploadType);
            result.setAdesc(adesc);
            result.setExtParam(extParam);
            result.setServiceUID(serviceUID);
            result.setOwnerUserUid(ownerUserUid);
            result.setFileId(fileId);
            result.setParentFileUUID(parentFileUUID);
            result.setParentFileNameOrig(parentFileNameOrig);
            result.setParentFileId(parentFileId);
            result.setInnerFiles(innerFiles);
            return result;
        }
    }

}
