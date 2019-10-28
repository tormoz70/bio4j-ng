package ru.bio4j.ng.model.transport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.Principal;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class User implements Principal {

    private String innerUid;
    private String stoken;
    private String login;
    private String fio;
    private String email;
    private String phone;
    private String orgId;
    private String orgName;
    private String orgDesc;

    private String roles;
    private String grants;

    private String remoteIP;
    private String remoteClient;

    private Boolean anonymouse;

    private String deviceuuid;

    public String getStoken() {
        return stoken;
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

    public void setStoken(String stoken) {
        this.stoken = stoken;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    @JsonIgnore
    public String getInnerUid() {
        return innerUid;
    }

    public void setInnerUid(String innerUid) {
        this.innerUid = innerUid;
    }

    @Override
    public String getName() {
        return login;
    }

    public String getRemoteClient() {
        return remoteClient;
    }

    public void setRemoteClient(String remoteClient) {
        this.remoteClient = remoteClient;
    }

    public String getDeviceuuid() {
        return deviceuuid;
    }

    public void setDeviceuuid(String deviceuuid) {
        this.deviceuuid = deviceuuid;
    }

    public Boolean getAnonymouse() {
        return anonymouse;
    }

    public void setAnonymouse(Boolean anonymouse) {
        this.anonymouse = anonymouse;
    }
}
