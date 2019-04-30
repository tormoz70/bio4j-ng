package ru.bio4j.ng.service.api;

public interface HttpParamMap {
    String username();
    String password();

    String pageSize();
    String page();
    String offset();

    String securityToken();

    String pageSizeHeader();
    String pageHeader();
    String offsetHeader();
    String securityTokenHeader();
    String clientHeader();
    String clientVerHeader();

}
