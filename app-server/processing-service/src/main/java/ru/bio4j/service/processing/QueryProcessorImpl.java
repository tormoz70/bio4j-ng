package ru.bio4j.service.processing;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.collections.Parameter;
import ru.bio4j.model.transport.BioRequest;
import ru.bio4j.model.transport.jstore.*;
import ru.bio4j.service.processing.config.ProcessingConfig;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.query.ConnectionFactoryImpl;
import ru.bio4j.service.sql.query.QueryHelper;

import java.sql.SQLException;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import static org.osgi.framework.Constants.SERVICE_RANKING;
import static ru.bio4j.service.ServiceConstants.PROCESSING_SERVICE_RANK_IPOJO;
import static ru.bio4j.service.sql.QueryContext.create;
import static ru.bio4j.service.sql.QueryContext.remove;

@Component(managedservice="processing.service.config")
@Instantiate
@Provides(properties = {@StaticServiceProperty(name = SERVICE_RANKING,
        value = PROCESSING_SERVICE_RANK_IPOJO, type = "java.lang.Integer")})
public class QueryProcessorImpl implements QueryProcessor, ManagedService {

    private static final Logger LOG = LoggerFactory.getLogger(QueryProcessorImpl.class);

    private final ProcessingConfig processingConf = new ProcessingConfig();

    private QueryProvider queryProvider;
    private volatile ConnectionFactoryImpl connectionFactory;

    public StoreData read(BioRequest bioRequest, Map<String, Parameter> context) throws Exception {
        try {
            create(connectionFactory);
            final Query query = queryProvider.createQuery(bioRequest, context);
            final StoreData result = query(query);
            return result;
        } finally {
            remove();
        }
    }

    public StoreData write(final BioRequestJStorePost bioRequest, Map<String, Parameter> context) throws Exception {
        try {
            create(connectionFactory);
            final StoreData packet = bioRequest.getPacket();
            final List<StoreRow> rows = packet.getRows();
            final StoreMetadata metadata = packet.getMetadata();
            final Map<String, Integer> fieldToIndex = packet.getFieldToIndex();
            final List<ColumnMetadata> fields = metadata.getFields();
            final Query query = queryProvider.createQuery(bioRequest, context);
            for (final StoreRow row : rows) {
                if (row.getChangeType() != RowChangeType.UNCHANGED) {
                    for (ColumnMetadata field : fields) {
                        final Object value = row.getValue(fieldToIndex.get(field.getName()));
                        query.setContextParam(field.getName(), new Parameter(value, null));
                    }
                    query(query);
                }
            }
        } finally {
            remove();
        }
        return bioRequest.getPacket();
    }

    private StoreData query(Query query) throws SQLException {
        final StoreData result = QueryHelper.query(query, new DataResultHandler(new StoreDataBuilder()));
        return result;
    }

    @Updated
    public synchronized void updated(Dictionary conf) {
        processingConf.config(conf);
        if (processingConf.isFilled()) {
            try {
                LOG.info("service updated new values are {}", processingConf);
                if (connectionFactory != null) {
                    connectionFactory.closeFactory();
                }
                connectionFactory = new ConnectionFactoryImpl(processingConf.getDriverClassname(),
                        processingConf.getConnectURI(),
                        processingConf.getUsername(),
                        processingConf.getPassword(),
                        processingConf.getMaxPoolSize());
            } catch (Exception e) {
                LOG.error("failed to update service", e);
            }
        }
    }

    @Invalidate
    public void stop() {
        if (connectionFactory != null) {
            connectionFactory.closeFactory();
        }
    }

    @Bind
    public void setQueryProvider(QueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

}
