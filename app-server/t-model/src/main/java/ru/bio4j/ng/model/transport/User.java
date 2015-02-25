package ru.bio4j.ng.model.transport;

public class User {

    public static final String BIO_ANONYMOUS_USER_LOGIN = "$bio-anonymous$";
    private String moduleKey;
    private String uid;
    private String login;
    private String fio;
    private String roles;
    private String grants;

    public String getUid() {
        return uid;
    }

    public String getLogin() {
        return login;
    }

    public String getFio() {
        return fio;
    }

    public String getRoles() {
        return roles;
    }

    public String getGrants() {
        return grants;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setGrants(String grants) {
        this.grants = grants;
    }

    public String getModuleKey() {
        return moduleKey;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    public Boolean isAnonymous() {
        return BIO_ANONYMOUS_USER_LOGIN.equals(login);
    }
}
