package ru.bio4j.ng.service.api;

public interface HttpParamMap {
    default String username() {
        return null;
    }
    default String password() {
        return null;
    }

    default String pageSize() {
        return null;
    }
    default String page() {
        return null;
    }
    default String offset() {
        return null;
    }

    default String securityToken() {
        return null;
    }
    default String deviceuuid() {
        return null;
    }

    default String pageSizeHeader() {
        return null;
    }
    default String pageHeader() {
        return null;
    }
    default String offsetHeader() {
        return null;
    }
    default String deviceuuidHeader() {
        return null;
    }
    default String securityTokenHeader() {
        return null;
    }
    default String clientHeader() {
        return null;
    }
    default String clientVerHeader() {
        return null;
    }

}
