package ru.bio4j.ng.service.types;

import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.service.api.SecurityService;

public interface AppServiceTypes {
    Class<? extends AppService> getAppServiceClass();
    Class<? extends FCloudApi> getFCloudApiClass();
    Class<? extends SecurityService> getSecurityServiceClass();
}
