package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioRequestStoredProg;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStorePost;

/**
 * Our business service. 
 */
public interface DataProvider extends BioService {

//    String getDataTest() throws Exception;
//    BioRespBuilder.Data getDataSet(final BioRequestJStoreGetDataSet request) throws Exception;
//    BioRespBuilder.Data getRecord(final BioRequestJStoreGetRecord request) throws Exception;
//    BioRespBuilder.Data postDataSet(final BioRequestJStorePost request) throws Exception;
//    BioRespBuilder.Data exec(final BioRequestStoredProg request) throws Exception;

    BioRespBuilder.Data processRequest(BioRoute route, final BioRequest request) throws Exception;
}
