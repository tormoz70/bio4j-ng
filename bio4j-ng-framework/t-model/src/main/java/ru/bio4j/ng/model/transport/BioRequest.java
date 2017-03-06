package ru.bio4j.ng.model.transport;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Базовый класс для всех запросов
 */
public class BioRequest {


    private HttpServletRequest httpRequest;

    private String origJson;

    private String remoteIP;
    private String remoteClient;

    private String moduleKey;

    /**
     * Тип запроса - используется для мапинга на router
     */
    private String requestType;

    /** UID пользователя
     * Это может быть либо реальный UID пользователя,
     * либо логин в формате <имя>/<пароль>
     */
    private User user;

    /** Логин в виде username/password */
    private String login;

    /**
     * Код запрашиваемого инф. объекта. Фактически - это путь к файлу описания метаданных запроса
     */
    private String bioCode;

    /**
     * id объекта Store на клиенте, который послал данный запрос
     */
    private String storeId;

    /**
     * параметры информационного объекта
     */
    private List<Param> bioParams;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getBioCode() {
        return bioCode;
    }

    public void setBioCode(String bioCode) {
        this.bioCode = bioCode;
    }

    public List<Param> getBioParams() {
        return bioParams;
    }

    public void setBioParams(List<Param> bioParams) {
        this.bioParams = bioParams;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getModuleKey() {
        return moduleKey;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getRemoteClient() {
        return remoteClient;
    }

    public void setRemoteClient(String remoteClient) {
        this.remoteClient = remoteClient;
    }

    public String getOrigJson() {
        return origJson;
    }

    public void setOrigJson(String origJson) {
        this.origJson = origJson;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

}
