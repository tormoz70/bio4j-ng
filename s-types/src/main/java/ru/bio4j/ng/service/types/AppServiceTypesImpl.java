package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Bundles4WAR;
import ru.bio4j.ng.model.transport.BioConfig;
import ru.bio4j.ng.service.api.*;

public class AppServiceTypesImpl implements AppServiceTypeGetters {

    private final Class<? extends OdacService> odacServiceType;
    private Class<? extends FCloudApi> fcloudApiType;
    private Class<? extends SecurityService> securityService;
    private Class<? extends CacheService> cacheService;

    private AppServiceTypesImpl(
            Class<? extends OdacService> odacServiceType,
            Class<? extends FCloudApi> fcloudApiType,
            Class<? extends SecurityService> securityService,
            Class<? extends CacheService> cacheService) {
        this.odacServiceType = odacServiceType;
        this.fcloudApiType = fcloudApiType;
        this.securityService = securityService;
        this.cacheService = cacheService;
    }

    @Override
    public Class<? extends OdacService> getOdacServiceClass() {
        return odacServiceType;
    }

    @Override
    public Class<? extends FCloudApi> getFCloudApiClass() {
        return fcloudApiType;
    }

    @Override
    public Class<? extends SecurityService> getSecurityServiceClass() {
        return securityService;
    }

    @Override
    public Class<? extends CacheService> getCacheServiceClass() {
        return cacheService;
    }

    public static class Builder {
        public AppServiceTypeGetters build(BioConfig config) {
            Class<? extends OdacService> odacServiceType = Bundles4WAR.createLocalServiceByName(config.getServiceNameOdac());
            Class<? extends FCloudApi> fcloudApiType = Bundles4WAR.createLocalServiceByName(config.getServiceNameFCloud());
            Class<? extends SecurityService> securityService = Bundles4WAR.createLocalServiceByName(config.getServiceNameSecurity());
            Class<? extends CacheService> cacheService = Bundles4WAR.createLocalServiceByName(config.getServiceNameCache());
            AppServiceTypeGetters rslt = new AppServiceTypesImpl(odacServiceType, fcloudApiType, securityService, cacheService);
            return rslt;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
