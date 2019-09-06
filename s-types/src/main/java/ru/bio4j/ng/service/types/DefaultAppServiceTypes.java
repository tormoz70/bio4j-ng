package ru.bio4j.ng.service.types;

import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.CacheService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.service.api.SecurityService;

public class DefaultAppServiceTypes implements AppServiceTypeGetters {

    @Override
    public Class<? extends AppService> getAppServiceClass() {
        return AppService.class;
    }
    @Override
    public Class<? extends FCloudApi> getFCloudApiClass() {
        return FCloudApi.class;
    }
    @Override
    public Class<? extends SecurityService> getSecurityServiceClass() {
        return SecurityService.class;
    }
    @Override
    public Class<? extends CacheService> getCacheServiceClass() {
        return CacheService.class;
    }

}
