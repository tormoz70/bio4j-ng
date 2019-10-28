package ru.bio4j.ng.service.api;

public interface HttpParamMap {
    String username();
    String password();

    String pageSize();
    String page();
    String offset();

    String securityToken();
    String deviceuuid();

    String pageSizeHeader();
    String pageHeader();
    String offsetHeader();
    String deviceuuidHeader();
    String securityTokenHeader();
    String clientHeader();
    String clientVerHeader();

}
