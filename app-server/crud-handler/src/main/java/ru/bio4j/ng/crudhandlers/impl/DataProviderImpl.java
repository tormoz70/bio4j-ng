package ru.bio4j.ng.crudhandlers.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.WrapQueryType;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.Wrappers;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.api.BioRespBuilder;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.sql.Connection;


@Component
@Instantiate
@Provides(specifications = DataProvider.class)
public class DataProviderImpl extends BioServiceBase implements DataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderImpl.class);

    @Context
    private BundleContext bundleContext;
//    private SQLContext globalSQLContext;

    @Requires
    private ModuleProvider moduleProvider;
    @Requires
    private SQLContextProvider sqlContextProvider;

    private BioRespBuilder.Data processCursorAsSelectable(BioModule module, BioCursor cursor) throws Exception {
        LOG.debug("Try exec batch!!!");
        SQLContext ctx = sqlContextProvider.selectContext(module);
        BioRespBuilder.Data response = ctx.execBatch(new SQLAction<BioCursor, BioRespBuilder.Data>() {
            @Override
            public BioRespBuilder.Data exec(SQLContext context, Connection conn, BioCursor cur) throws Exception {
                final BioRespBuilder.Data result = BioRespBuilder.data();
                result.bioCode(cur.getBioCode());
                LOG.debug("Try open Cursor!!!");
                try(SQLCursor c = context.CreateCursor()
                        .init(conn, cur.getPreparedSql(), cur.getParams()).open();){
                    LOG.debug("Cursor opened!!!");
                    if(c.reader().read()){
                        LOG.debug("FirstRec readed!!!");
//                        dummysum += c.getValue("DM", Double.class);
                    }
                }
                return result.success(true);
            }
        }, cursor);
        return response;
    }

    private BioRespBuilder.Data processCursorAsExecutable(BioModule module, BioCursor cursor) {
        return BioRespBuilder.data();
    }

    public BioRespBuilder.Data processCursor(BioModule module, BioCursor cursor) throws Exception {
        if(cursor.getType() == BioCursor.Type.SELECT)
            return processCursorAsSelectable(module, cursor);
        if(cursor.getType() == BioCursor.Type.EXEC)
            return processCursorAsExecutable(module, cursor);
        return BioRespBuilder.data();
    }

    private void wrapCursor(BioCursor cursor) throws Exception {
        Wrappers.wrapRequest(cursor, WrapQueryType.FILTERING);
        Wrappers.wrapRequest(cursor, WrapQueryType.SORTING);
        Wrappers.wrapRequest(cursor, WrapQueryType.PAGING);
    }

    private BioRespBuilder.Data processRequest(BioRequestJStoreGet request) throws Exception {
        LOG.debug("Now processing request to module \"{}\"...", request.getBioModuleKey());
        BioModule module = moduleProvider.getModule(request.getBioModuleKey());
        BioCursor cursor = module.getCursor(request);
        wrapCursor(cursor);
        return processCursor(module, cursor);
    }

    @Override
    public BioRespBuilder.Data getData(final BioRequestJStoreGet bioRequest) throws Exception {
        LOG.debug("GetData...");
        try {
            return processRequest(bioRequest);
        } finally {
            LOG.debug("GetData - returning response...");
        }
    }

    @Override
    public String getDataTest() throws Exception {
        SQLContext globalSQLContext = sqlContextProvider.globalContext();
        return globalSQLContext.execBatch(new SQLActionScalar<String>() {
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

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
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
