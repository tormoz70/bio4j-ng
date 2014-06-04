package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;

/**
 * Our business service. 
 */
public interface DataProvider extends BioService {

    String getDataTest() throws Exception;
    BioRespBuilder.Data getData(final BioRequestJStoreGet bioRequest) throws Exception;
}
