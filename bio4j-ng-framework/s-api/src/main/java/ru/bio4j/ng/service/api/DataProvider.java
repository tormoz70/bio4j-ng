package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioRequest;

/**
 * Our business service. 
 */
public interface DataProvider extends BioService {

    BioRespBuilder.Builder processRequest(BioRoute route, final BioRequest request) throws Exception;
}
