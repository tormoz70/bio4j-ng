package ru.bio4j.ng.model.transport;

public class User {
    private String uid;
    private String login;
    private String fio;
    private String[] roles;
    private String[] grants;

    public String getUid() {
        return uid;
    }

    public String getLogin() {
        return login;
    }

    public String getFio() {
        return fio;
    }

    public String[] getRoles() {
        return roles;
    }

    public String[] getGrants() {
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

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public void setGrants(String[] grants) {
        this.grants = grants;
    }
}
