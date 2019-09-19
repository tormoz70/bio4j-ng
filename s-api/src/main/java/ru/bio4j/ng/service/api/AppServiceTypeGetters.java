package ru.bio4j.ng.service.api;

public interface AppServiceTypeGetters {
    Class<? extends OdacService> getOdacServiceClass();
    Class<? extends FCloudApi> getFCloudApiClass();
    Class<? extends SecurityService> getSecurityServiceClass();
    Class<? extends CacheService> getCacheServiceClass();

}
