package ru.bio4j.ng.crudhandlers.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;
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
//import static org.osgi.framework.Constants.SERVICE_RANKING;
//import static ru.bio4j.ng.service.api.ServiceConstants.PROCESSING_SERVICE_RANK_IPOJO;



@Component
@Instantiate
@Provides(specifications = DataProvider.class)
public class DataProviderImpl extends BioServiceBase implements DataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderImpl.class);

    @Requires
    private FileContentResolver contentResolver;
    private DbProxy dbProxy;
    private SQLContext sqlContext;

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

//    @Updated
//    public void updated(Dictionary<String, ?> confDictionary) throws ConfigurationException {
//        LOG.debug("Updating config...");
//
//        configurator.update(confDictionary);
//
////        if(isNullOrEmpty(configurator.getConfig().getPoolName())){
////            LOG.debug("SQLContextConfig bp empty. Wating...");
////            return;
////        }
//        if(!configurator.isUpdated()) {
//            LOG.info("Config is not loaded! Waiting...");
//            return;
//        }
//
//        if(sqlContext == null) {
//            LOG.debug("Creating SQLContext (poolName:{})...", configurator.getConfig().getPoolName());
//            try {
//                sqlContext = SQLContextFactory.create(configurator.getConfig());
//            } catch (Exception e) {
//                LOG.error("Error while creating SQLContext!", e);
//            }
//        } else {
//
//        }
//
//    }

//    @Bind
//    public void setContentResolver(FileContentResolver contentResolver) {
//        this.contentResolver = contentResolver;
//    }

    @Requires
    private ConfigProvider configProvider;

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");

        if(!configProvider.configIsRedy()) {
            LOG.info("Config is not redy! Waiting...");
            return;
        }

        if(sqlContext == null) {
            LOG.debug("Creating SQLContext (poolName:{})...", configProvider.getConfig().getPoolName());
            try {
                SQLContextConfig cfg = new SQLContextConfig();
                Utl.applyValuesToBean(configProvider.getConfig(), cfg);
                sqlContext = SQLContextFactory.create(cfg);
            } catch (Exception e) {
                LOG.error("Error while creating SQLContext!", e);
            }
        } else {

        }

        Wrappers.getInstance().init("oracle");
        this.redy = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void stop() throws Exception {
        this.redy = false;
        LOG.debug("Stoping...");
        LOG.debug("Stoped.");
    }

    @Subscriber(
            name="crud.handler.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
        stop();
        start();
    }

}
