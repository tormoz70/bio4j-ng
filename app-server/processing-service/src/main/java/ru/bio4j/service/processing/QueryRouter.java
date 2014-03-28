package ru.bio4j.service.processing;

import org.apache.felix.ipojo.annotations.*;
import ru.bio4j.collections.Parameter;
import ru.bio4j.model.transport.BioRequest;
import ru.bio4j.model.transport.jstore.BioRequestJStorePost;
import ru.bio4j.model.transport.jstore.StoreData;

import java.util.Map;

import static org.osgi.framework.Constants.SERVICE_RANKING;
import static ru.bio4j.service.ServiceConstants.PROCESSING_SERVICE_RANK_IPOJO;

@Component
@Instantiate
@Provides(properties = {@StaticServiceProperty(name = SERVICE_RANKING, value = PROCESSING_SERVICE_RANK_IPOJO, type = "java.lang.Integer")})
public class QueryRouter implements Router {

    private QueryProcessor queryProcessor;

    public StoreData route(BioRequest bioRequest, Map<String, Parameter> context) throws Exception {

        if (bioRequest instanceof BioRequestJStorePost) {
            return queryProcessor.write((BioRequestJStorePost)bioRequest, context);
        } else {
            return queryProcessor.read(bioRequest, context);
        }
    }

    @Bind
    public void setQueryProcessor(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
    }
}
