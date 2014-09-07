package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;

/**
 * Our business service. 
 */
public interface DataProvider extends BioService {

    String getDataTest() throws Exception;
    BioRespBuilder.Data getDataSet(final BioRequestJStoreGetDataSet request) throws Exception;
    BioRespBuilder.Data getRecord(final BioRequestJStoreGetRecord request) throws Exception;
}
