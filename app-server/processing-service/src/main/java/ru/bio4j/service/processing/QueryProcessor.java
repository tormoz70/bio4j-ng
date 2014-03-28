package ru.bio4j.service.processing;

import ru.bio4j.collections.Parameter;
import ru.bio4j.model.transport.BioRequest;
import ru.bio4j.model.transport.jstore.BioRequestJStorePost;
import ru.bio4j.model.transport.jstore.StoreData;

import java.util.Map;

public interface QueryProcessor {

    StoreData read(BioRequest bioRequest, Map<String, Parameter> context) throws Exception;

    StoreData write(final BioRequestJStorePost bioRequest, Map<String, Parameter> context) throws Exception;
}
