package ru.bio4j.ng.model.transport;

/**
 * Запрос на получение файла
 */
public class BioRequestGetFile extends BioRequest {

    /**
     * Код запрашиваемого файла
     */
    private String fileHashCode;

    public String getFileHashCode() {
        return fileHashCode;
    }

    public void setFileHashCode(String fileHashCode) {
        this.fileHashCode = fileHashCode;
    }

}

