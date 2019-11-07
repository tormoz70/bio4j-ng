package ru.bio4j.ng.service.api;

public interface HttpParamMap {
    default String username() {
        return "login";
    }
    default String password() {
        return "passwd";
    }

    default String pageSize() {
        return "limit";
    }
    default String page() {
        return "page";
    }
    default String offset() {
        return "offset";
    }

    default String securityToken() {
        return "stoken";
    }
    default String deviceuuid() {
        return "deviceuuid";
    }

    default String pageSizeHeader() {
        return "X-Pagination-Pagesize";
    }
    default String pageHeader() {
        return "X-Pagination-Page";
    }
    default String offsetHeader() {
        return "X-Pagination-Offset";
    }
    default String deviceuuidHeader() {
        return "X-DEVICEUUID";
    }
    default String securityTokenHeader() {
        return "X-SToken";
    }
    default String clientHeader() {
        return "X-Client";
    }
    default String clientVerHeader() {
        return "X-Client-Ver";
    }

}
