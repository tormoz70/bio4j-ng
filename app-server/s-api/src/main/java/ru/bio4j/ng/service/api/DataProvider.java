package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;

/**
 * Our business service. 
 */
public interface DataProvider extends BioService {

    String getDataTest() throws Exception;
    BioResponse getData(final BioRequestJStoreGet bioRequest) throws Exception;
}
