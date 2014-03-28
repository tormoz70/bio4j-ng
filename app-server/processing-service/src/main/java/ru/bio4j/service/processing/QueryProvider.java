package ru.bio4j.service.processing;

import ru.bio4j.collections.Parameter;
import ru.bio4j.model.transport.BioRequest;
import ru.bio4j.service.sql.Query;

import java.io.IOException;
import java.util.Map;

public interface QueryProvider {
    Query createQuery(BioRequest bioRequest, Map<String, Parameter> context) throws IOException;
}
