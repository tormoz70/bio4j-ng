package ru.bio4j.ng.crudhandlers.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.crudhandlers.impl.cursor.CursorParser;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.Wrappers;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.doa.SQLContextFactory;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;

import java.sql.Connection;



@Component
@Instantiate
@Provides(specifications = DataProvider.class)
public class DataProviderImpl extends BioServiceBase implements DataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderImpl.class);

    @Requires
    private FileContentResolver contentResolver;
    private SQLContext sqlContext;

    private BioResponse processCursorAsSelectable(Cursor cursor) throws Exception {
//        response.setBioCode(cursor.getBioCode());
        LOG.debug("Try exec batch!!!");
        BioResponse response = sqlContext.execBatch(new SQLAction<Cursor, BioResponse>() {
            @Override
            public BioResponse exec(SQLContext context, Connection conn, Cursor cur) throws Exception {
                final BioResponse result = new BioResponse();
                result.setBioCode(cur.getBioCode());
                LOG.debug("Try open Cursor!!!");
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, cur.getPreparedSql(), cur.getParams()).open();){
                    LOG.debug("Cursor opened!!!");
                    if(c.reader().read()){
                        LOG.debug("FirstRec readed!!!");
//                        dummysum += c.getValue("DM", Double.class);
                    }
                }
                return result;
            }
        }, cursor);
        return response;
    }

    private BioResponse processCursorAsExecutable(Cursor cursor) {
        return null;
    }

    public BioResponse processCursor(Cursor cursor) throws Exception {
        BioResponse response = null;
        if(cursor.getType() == Cursor.Type.SELECT)
            response = processCursorAsSelectable(cursor);
        if(cursor.getType() == Cursor.Type.EXEC)
            response = processCursorAsExecutable(cursor);
        if (response == null)
            response = new BioResponse();
        return response;
    }

    private void wrapCursor(Cursor cursor) throws Exception {
        Wrappers.wrapRequest(cursor, WrapQueryType.FILTERING);
        Wrappers.wrapRequest(cursor, WrapQueryType.SORTING);
        Wrappers.wrapRequest(cursor, WrapQueryType.PAGING);
    }

    private BioResponse processRequest(BioRequestJStoreGet request, String sql) throws Exception {
        LOG.debug("Now process sql: {}", sql);
        Cursor cursor = CursorParser.pars(request.getBioCode(), sql);
        cursor.setOffset(request.getOffset());
        cursor.setPageSize(request.getPagesize());
        cursor.setFilter(request.getFilter());
        cursor.setSort(request.getSort());
        cursor.setParams(request.getBioParams());
        wrapCursor(cursor);
        BioResponse response = processCursor(cursor);
        return response;
    }

    @Override
    public BioResponse getData(final BioRequestJStoreGet bioRequest) throws Exception {
        LOG.debug("GetData...");
        LOG.debug("Loading sql by code (bioCode:{})...", bioRequest.getBioCode());
        String sql = contentResolver.getQueryContent(bioRequest.getBioCode());
        LOG.debug("SQL loaded.");
        if(!Strings.isNullOrEmpty(sql)) {
            BioResponse response = processRequest(bioRequest, sql);
            LOG.debug("GetData - returning response...");
            return response;
        }
        return null;
    }

    @Override
    public String getDataTest() throws Exception {
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

    @Requires
    private ConfigProvider configProvider;

    @Validate
    public void doStart() throws Exception {
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
    public void doStop() throws Exception {
        LOG.debug("Stoping...");
        this.redy = false;
        LOG.debug("Stoped.");
    }

    @Subscriber(
            name="crud.handler.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
        doStop();
        doStart();
    }

}
