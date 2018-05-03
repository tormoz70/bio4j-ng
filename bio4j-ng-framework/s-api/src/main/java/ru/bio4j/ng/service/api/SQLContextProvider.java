package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.service.api.BioService;

public interface SQLContextProvider extends BioService {
    SQLContext selectContext(BioAppModule module) throws Exception;
    //SQLContext globalContext() throws Exception;
}
