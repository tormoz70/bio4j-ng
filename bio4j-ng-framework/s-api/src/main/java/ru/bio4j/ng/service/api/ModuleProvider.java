package ru.bio4j.ng.service.api;

public interface ModuleProvider extends BioService {
    BioAppModule getAppModule(String key) throws Exception;
    BioSecurityModule getSecurityModule(String key) throws Exception;

}
