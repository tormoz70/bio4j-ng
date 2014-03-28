package ru.bio4j.service.processing;

import org.apache.felix.ipojo.annotations.*;
import ru.bio4j.collections.Parameter;
import ru.bio4j.model.transport.BioRequest;
import ru.bio4j.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.service.file.FileContentResolver;
import ru.bio4j.service.sql.Query;

import java.io.IOException;
import java.util.Map;

import static org.osgi.framework.Constants.SERVICE_RANKING;
import static ru.bio4j.service.ServiceConstants.PROCESSING_SERVICE_RANK_IPOJO;

@Component
@Instantiate
@Provides(properties = {@StaticServiceProperty(name = SERVICE_RANKING, value = PROCESSING_SERVICE_RANK_IPOJO, type = "java.lang.Integer")})
public class QueryProviderImpl implements QueryProvider {

    private FileContentResolver contentResolver;

    /**
     * @param bioRequest
     * @param context
     * @return Созданный запрос
     * @title Создание запроса
     */
    @Override
    public Query createQuery(BioRequest bioRequest, Map<String, Parameter> context) throws IOException {
        final String sql = contentResolver.getQueryContent(bioRequest.getBioCode());
        Query query = new Query(sql, bioRequest.getBioParams());
        if (bioRequest instanceof BioRequestJStoreGet) {
            BioRequestJStoreGet storeGet = (BioRequestJStoreGet) bioRequest;
            query.setCount(storeGet.getPagesize());
            query.setOffset(storeGet.getOffset());
            query.setFilter(storeGet.getFilter());
            query.setSort(storeGet.getSort());
        }
        query.setContext(context);
        return query;
    }

    @Bind
    public void setContentResolver(FileContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }
}
