package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.jstore.BioResponseJStore;

/**
 * Created by ayrat on 25.04.14.
 */
public interface DbProxy {
    BioResponseJStore processCursor(Cursor cursor) throws Exception;
}
