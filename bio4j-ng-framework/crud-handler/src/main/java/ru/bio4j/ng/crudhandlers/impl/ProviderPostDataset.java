package ru.bio4j.ng.crudhandlers.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.api.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderPostDataset extends ProviderAn {

    private static final String STD_PARAM_PREFIX = "p_";

    private static void processUpDelRow(final StoreRow row, final SQLContext ctx, final Connection conn, final BioCursor cursor) throws Exception {
        SQLStoredProc cmd = ctx.createStoredProc();
        RowChangeType changeType = row.getChangeType();
        BioCursor.SQLDef sqlDef = (Arrays.asList(RowChangeType.create, RowChangeType.update).contains(changeType) ? cursor.getUpdateSqlDef() : cursor.getDeleteSqlDef());
        if(sqlDef == null && Arrays.asList(RowChangeType.create, RowChangeType.update).contains(changeType))
            throw new Exception(String.format("For bio \"%s\" must be defined \"create/update\" sql!", cursor.getBioCode()));
        if(sqlDef == null && Arrays.asList(RowChangeType.delete).contains(changeType))
            throw new Exception(String.format("For bio \"%s\" must be defined \"delete\" sql!", cursor.getBioCode()));
        try(Paramus paramus = Paramus.set(sqlDef.getParams())) {
//            for(Field field : cursor.getFields()) {
//                paramus.add(Param.builder()
//                        .name(STD_PARAM_PREFIX + field.getName().toLowerCase())
//                        .value(row.getValue(field.getName()))
//                        .type(field.getType())
//                        .build(), true);
//            }

            for(String key : row.getData().keySet()) {
                String paramName = (STD_PARAM_PREFIX + key).toLowerCase();
                Object paramValue = row.getData().get(key);
                Param p = paramus.getParam(paramName, true);
                if(p != null){
                    paramus.setValue(paramName, paramValue);
                } else {
                    paramus.add(Param.builder()
                            .name(paramName)
                            .value(paramValue)
                            .build(), true);
                }
            }
            cmd.init(conn, sqlDef.getPreparedSql(), paramus.get());
        }
        cmd.execSQL();
        try(Paramus paramus = Paramus.set(cmd.getParams())) {
            for(Param p : paramus.get()) {
                if(Arrays.asList(Param.Direction.INOUT, Param.Direction.OUT).contains(p.getDirection())){
                    String fieldName = p.getName().substring(STD_PARAM_PREFIX.length());
                    Field fld = cursor.findField(fieldName);
                    row.setValue(fld.getName().toLowerCase(), p.getValue());
                }
            }
        }
    }

    private void applyParentRowToChildren(final BioCursor parentCursorDef, final StoreRow parentRow, final BioCursor cursorDef, final StoreRow row) {
        if(parentCursorDef != null && parentRow != null)
            for(Field field : cursorDef.getFields()) {
                if(row.getValue(field.getName()) == null) {
                    Field parentField = parentCursorDef.findField(field.getName());
                    if(parentField != null) {
                        Object parentValue = parentRow.getValue(parentField.getName());
                        if (parentValue != null)
                            row.setValue(field.getName().toLowerCase(), parentValue);
                    }
                }
            }
    }

    private BioRespBuilder.DataBuilder processRequestPost(final BioRequestJStorePost request, final SQLContext ctx, final Connection conn, final BioCursor parentCursorDef, final StoreRow parentRow, final User rootUsr) throws Exception {
        final User usr = (rootUsr != null) ? rootUsr : request.getUser();
        final BioCursor cursorDef = module.getCursor(request);

        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
        result.bioCode(request.getBioCode());
        result.user(usr);

        StoreRow firstRow = null;
        for(StoreRow row : request.getModified()) {
            applyParentRowToChildren(parentCursorDef, parentRow, cursorDef, row);
            processUpDelRow(row, ctx, conn, cursorDef);
            if(firstRow == null)
                firstRow = row;
        }


        List<BioResponse> slaveResponses = new ArrayList<>();
        for(BioRequestJStorePost post : request.getSlavePostData()) {
            post.setModuleKey(request.getModuleKey()); // forward moduleKey
            BioResponse rsp = processRequestPost(post, ctx, conn, cursorDef, firstRow, usr).build();
            slaveResponses.add(rsp);
        }
        if(slaveResponses.size() > 0)
            result.slaveResponses(slaveResponses);

        StoreData data = new StoreData();
        data.setStoreId(request.getStoreId());
        data.setMetadata(new StoreMetadata());
        List<Field> cols = cursorDef.getFields();
        data.getMetadata().setFields(cols);
        data.setRows(request.getModified());

        result.packet(data);
        return result.exception(null);
    }

    @Override
    public void process(final BioRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process postDataSet for \"{}\" request...", request.getBioCode());
        try {
            final User usr = request.getUser();
            BioRespBuilder.DataBuilder responseBuilder = context.execBatch(new SQLAction<Object, BioRespBuilder.DataBuilder>() {
                @Override
                public BioRespBuilder.DataBuilder exec(SQLContext context, Connection conn, Object obj) throws Exception {
                    tryPrepareSessionContext(usr.getInnerUid(), conn);
                    return processRequestPost((BioRequestJStorePost)request, context, conn, null, null, null);
                }
            }, null, request.getUser());
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed postDataSet for \"{}\" - returning response...", request);
        }
    }

}
