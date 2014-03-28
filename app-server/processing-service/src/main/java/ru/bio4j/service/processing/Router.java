package ru.bio4j.service.processing;

import ru.bio4j.collections.Parameter;
import ru.bio4j.model.transport.BioRequest;
import ru.bio4j.model.transport.jstore.StoreData;

import java.util.Map;

public interface Router {

    StoreData route(BioRequest bioRequest, Map<String, Parameter> context) throws Exception;
}
