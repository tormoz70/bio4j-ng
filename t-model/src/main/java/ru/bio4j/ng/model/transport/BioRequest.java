package ru.bio4j.ng.model.transport;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
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

    /**
     * Объект пользователя присваивается на входе, после удачной аутентификации
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
    private Object bioParams;
    private List<Param> _bioParams;

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
        if(_bioParams == null) {
            if(bioParams instanceof ArrayList && ((ArrayList)bioParams).size() > 0) {
                Object item = ((ArrayList)bioParams).get(0);
                if (item instanceof Param)
                    _bioParams = (List<Param>) bioParams;
                else {
                    _bioParams = new ArrayList<>();
                    HashMap<String, Object> prms = (HashMap)item;
                    for(String paramName : prms.keySet()) {
                        Object val = prms.get(paramName);
                        _bioParams.add(Param.builder().name(paramName).value(val).build());
                    }
                }
            } else
                _bioParams = new ArrayList<>();
        }
        return _bioParams;
    }

    public void setBioParams(List<Param> bioParams) {
        this.bioParams = bioParams;
        if(bioParams instanceof ArrayList && ((ArrayList)bioParams).size() > 0) {
            Object item = ((ArrayList)bioParams).get(0);
            if (item instanceof Param)
                _bioParams = (List<Param>) bioParams;
            else {
                _bioParams = new ArrayList<>();
                HashMap<String, Object> prms = (HashMap)item;
                for(String paramName : prms.keySet()) {
                    Object val = prms.get(paramName);
                    _bioParams.add(Param.builder().name(paramName).value(val).build());
                }
            }
        } else
            _bioParams = new ArrayList<>();
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
