package ru.bio4j.ng.model.transport;

/**
 * Загрузка файлов на сервер
 */

public class BioRequestFCloud extends BioRequest {
    private FCloudCommand cmd;
    private String fileUid;
    private String uploadUid;
    private String uploadDesc;

    public FCloudCommand getCmd() {
        return cmd;
    }

    public void setCmd(FCloudCommand cmd) {
        this.cmd = cmd;
    }

    public String getFileUid() {
        return fileUid;
    }

    public void setFileUid(String fileUid) {
        this.fileUid = fileUid;
    }

    public String getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(String uploadUid) {
        this.uploadUid = uploadUid;
    }

    public String getUploadDesc() {
        return uploadDesc;
    }

    public void setUploadDesc(String uploadDesc) {
        this.uploadDesc = uploadDesc;
    }
}
