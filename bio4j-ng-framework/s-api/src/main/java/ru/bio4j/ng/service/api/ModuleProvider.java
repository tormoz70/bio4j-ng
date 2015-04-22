package ru.bio4j.ng.service.api;

public interface ModuleProvider extends BioService {
    BioModule getModule(String key) throws Exception;
}
