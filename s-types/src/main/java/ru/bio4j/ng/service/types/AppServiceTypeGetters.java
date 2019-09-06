package ru.bio4j.ng.service.types;

import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.CacheService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.service.api.SecurityService;

public interface AppServiceTypeGetters {
    Class<? extends AppService> getAppServiceClass();
    Class<? extends FCloudApi> getFCloudApiClass();
    Class<? extends SecurityService> getSecurityServiceClass();
    Class<? extends CacheService> getCacheServiceClass();
}
