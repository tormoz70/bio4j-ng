package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Bundles4WAR;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.BioConfig;
import ru.bio4j.ng.service.api.*;

public class AppServiceTypesImpl implements AppServiceTypeGetters {

    private final Class<? extends OdacService> odacServiceType;
    private Class<? extends FCloudApi> fcloudApiType;
    private Class<? extends SecurityService> securityServiceType;
    private Class<? extends CacheService> cacheServiceType;

    private AppServiceTypesImpl(
            Class<? extends OdacService> odacServiceType,
            Class<? extends FCloudApi> fcloudApiType,
            Class<? extends SecurityService> securityServiceType,
            Class<? extends CacheService> cacheServiceType) {

        this.odacServiceType = Utl.nvl(odacServiceType, ru.bio4j.ng.service.api.OdacService.class);
        this.fcloudApiType = Utl.nvl(fcloudApiType, ru.bio4j.ng.service.api.FCloudApi.class);
        this.securityServiceType = Utl.nvl(securityServiceType, ru.bio4j.ng.service.api.SecurityService.class);
        this.cacheServiceType = Utl.nvl(cacheServiceType, ru.bio4j.ng.service.api.CacheService.class);
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
        return securityServiceType;
    }

    @Override
    public Class<? extends CacheService> getCacheServiceClass() {
        return cacheServiceType;
    }

    public static class Builder {
        public AppServiceTypeGetters build(BioConfig config) {
            Class<? extends OdacService> odacServiceType = Bundles4WAR.findServiceTypeByName(config.getServiceNameOdac());
            Class<? extends FCloudApi> fcloudApiType = Bundles4WAR.findServiceTypeByName(config.getServiceNameFCloud());
            Class<? extends SecurityService> securityServiceType = Bundles4WAR.findServiceTypeByName(config.getServiceNameSecurity());
            Class<? extends CacheService> cacheServiceType = Bundles4WAR.findServiceTypeByName(config.getServiceNameCache());
            AppServiceTypeGetters rslt = new AppServiceTypesImpl(odacServiceType, fcloudApiType, securityServiceType, cacheServiceType);
            return rslt;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
