package ru.bio4j.ng.crudhandlers.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.crudhandlers.impl.cursor.CursorParser;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.Wrappers;
import ru.bio4j.ng.database.api.SQLActionScalar;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.database.doa.SQLContextFactory;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.ng.model.transport.jstore.BioResponseJStore;

import java.sql.Connection;
import java.util.Dictionary;
import static ru.bio4j.ng.commons.utils.Strings.*;
import static org.osgi.framework.Constants.SERVICE_RANKING;
import static ru.bio4j.ng.service.api.ServiceConstants.PROCESSING_SERVICE_RANK_IPOJO;



@Component(managedservice="bio4j.crud.handler.config")
@Instantiate
@Provides(specifications = DataProvider.class)
//        properties = {@StaticServiceProperty(name = SERVICE_RANKING, value = PROCESSING_SERVICE_RANK_IPOJO, type = "java.lang.Integer")})
public class DataProviderImpl implements DataProvider, ManagedService {
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderImpl.class);

    private FileContentResolver contentResolver;
    private DbProxy dbProxy;
//    private SQLContextConfig sqlContextConfig;
    private SQLContext sqlContext;
    private Configurator<SQLContextConfig> configurator = new Configurator<>(SQLContextConfig.class);

    private void wrapCursor(Cursor cursor) throws Exception {
        Wrappers.wrapRequest(cursor, WrapQueryType.FILTERING);
        Wrappers.wrapRequest(cursor, WrapQueryType.SORTING);
        Wrappers.wrapRequest(cursor, WrapQueryType.PAGING);
    }

    private BioResponseJStore processRequest(BioRequestJStoreGet request, String sql) throws Exception {
        LOG.debug("Now process sql: {}", sql);
        Cursor cursor = CursorParser.pars(request.getBioCode(), sql);
        cursor.setOffset(request.getOffset());
        cursor.setPageSize(request.getPagesize());
        cursor.setFilter(request.getFilter());
        cursor.setSort(request.getSort());
        cursor.setParams(request.getBioParams());
        wrapCursor(cursor);
        BioResponseJStore response = dbProxy.processCursor(cursor);
        return response;
    }

//    public String getData(final String json) throws Exception {
//        LOG.debug("GetData...");
//        BioRequestJStoreGet bioRequest = Jsons.decode(json, BioRequestJStoreGet.class);
//        if(bioRequest != null){
//            String sql = contentResolver.getQueryContent(bioRequest.getBioCode());
//            if(!Strings.isNullOrEmpty(sql)) {
//                BioResponseJStore response = processRequest(bioRequest, sql);
//                LOG.debug("GetData - returning response...");
//                return Jsons.encode(response);
//            }
//        }
//        return null;
//    }

    public String getData(final String json) throws Exception {
        return sqlContext.execBatch(new SQLActionScalar<String>() {
            @Override
            public String exec(SQLContext context, Connection conn) throws Exception {
                StringBuilder rslt = new StringBuilder();
                LOG.debug("Opening cursor...");
                try(SQLCursor c = context.CreateCursor()
                    .init(conn, "select username from user_users", null)
                    .open()) {
                    LOG.debug("Cursor opened...");
                    while (c.reader().read()){
                        LOG.debug("Reading field USERNAME...");
                        String s = c.reader().getValue("USERNAME", String.class);
                        rslt.append(s+";");
                    }
                }
                return rslt.toString();
            }
        });
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws ConfigurationException {
        LOG.debug("Updating config...");

        configurator.update(conf);

        if(isNullOrEmpty(configurator.getConfig().getPoolName())){
            LOG.debug("SQLContextConfig bp empty. Wating...");
            return;
        }

        if(sqlContext == null) {
            LOG.debug("Creating SQLContext (poolName:{})...", configurator.getConfig().getPoolName());
            try {
                sqlContext = SQLContextFactory.create(configurator.getConfig());
            } catch (Exception e) {
                LOG.error("Error while creating SQLContext!", e);
            }
        } else {

        }

    }

    @Bind
    public void setContentResolver(FileContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        Wrappers.getInstance().init("oracle");
        LOG.debug("Started");
    }

    @Invalidate
    public void stop() throws Exception {
    }

}
