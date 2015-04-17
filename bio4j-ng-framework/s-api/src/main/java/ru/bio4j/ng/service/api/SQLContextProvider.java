package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.SQLContext;

public interface SQLContextProvider extends BioService {
    SQLContext selectContext(BioModule module) throws Exception;
    //SQLContext globalContext() throws Exception;
}
