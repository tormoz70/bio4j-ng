package ru.bio4j.ng.crudhandlers.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.service.types.BioCursorDeclaration;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.service.types.BioRespBuilder;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ayrat on 07.03.2016.
 */
public class ProviderPostDataset extends ProviderAn<BioRequestJStorePost> {


    private static void processUpDelRow(final BioRequest request, final StoreRow row, final SQLContext ctx, final Connection conn, final BioCursorDeclaration cursor) throws Exception {
        SQLStoredProc cmd = ctx.createStoredProc();
        RowChangeType changeType = row.getChangeType();
        BioCursorDeclaration.UpdelexSQLDef sqlDef = (Arrays.asList(RowChangeType.create, RowChangeType.update).contains(changeType) ? cursor.getUpdateSqlDef() : cursor.getDeleteSqlDef());
        if(sqlDef == null && Arrays.asList(RowChangeType.create, RowChangeType.update).contains(changeType))
            throw new Exception(String.format("For bio \"%s\" must be defined \"create/update\" sql!", cursor.getBioCode()));
        if(sqlDef == null && Arrays.asList(RowChangeType.delete).contains(changeType))
            throw new Exception(String.format("For bio \"%s\" must be defined \"delete\" sql!", cursor.getBioCode()));
        cmd.init(conn, sqlDef);
        DbUtils.applayRowToParams(row, request.getBioParams());
        cmd.execSQL(request.getBioParams(), null);
        try(Paramus paramus = Paramus.set(cmd.getParams())) {
            for(Param p : paramus.get()) {
                if(Arrays.asList(Param.Direction.INOUT, Param.Direction.OUT).contains(p.getDirection())){
                    Field fld = cursor.findField(DbUtils.trimParamNam(p.getName()));
                    row.setValue(fld.getName().toLowerCase(), p.getValue());
                }
            }
        }
    }

//    private void applyParentRowToChildren(final BioCursorDeclaration parentCursorDef, final StoreRow parentRow, final BioCursorDeclaration cursorDef, final StoreRow row) throws Exception {
//        if(parentCursorDef != null && parentRow != null)
//            for(Field field : cursorDef.getFields()) {
//                Field parentField = parentCursorDef.findField(field.getName());
//                if(parentField != null) {
//                    Object parentValue = parentRow.getValue(parentField.getName());
//                    if (parentValue != null)
//                        row.setValue(field.getName().toLowerCase(), parentValue);
//                }
//            }
//    }

//    private static void applyParentRowToChildrenParams(final BioCursorDeclaration parentCursorDef, final StoreRow parentRow, final BioCursorDeclaration cursorDef, final StoreRow row) throws Exception {
//        if(parentCursorDef != null && parentRow != null)
//            for(BioCursorDeclaration.SQLDef sqlDef : cursorDef.sqlDefs()) {
//                for(Param p : sqlDef.getParamDeclaration()) {
//                    String paramName = p.getName();
//                    if(paramName.toLowerCase().startsWith("p_"))
//                        paramName = paramName.toLowerCase().substring(2);
//                    Field parentField = parentCursorDef.findField(paramName);
//                    if (parentField != null) {
//                        Object parentValue = parentRow.getValue(parentField.getName());
//                        if (parentValue != null)
//                            p.setValue(parentValue);
//                    }
//                }
//            }
//    }

    private BioRespBuilder.DataBuilder processRequestPost(final BioRequestJStorePost request, final SQLContext ctx, final Connection conn, final User rootUsr) throws Exception {
        final User usr = (rootUsr != null) ? rootUsr : request.getUser();
//        final BioCursorDeclaration cursor = contentResolver.getCursor(module.getKey(), request);
        final BioCursorDeclaration cursor = module.getCursor(request.getBioCode());

        final BioRespBuilder.DataBuilder result = BioRespBuilder.dataBuilder();
        result.bioCode(request.getBioCode());
        result.user(usr);

        StoreRow firstRow = null;
        for(StoreRow row : request.getModified()) {
            //applyParentRowToChildren(parentCursorDef, parentRow, cursor, row);
            //applyParentRowToChildrenParams(parentCursorDef, parentRow, cursor, row);
            processUpDelRow(request, row, ctx, conn, cursor);
            if(firstRow == null)
                firstRow = row;
        }


        List<BioResponse> slaveResponses = new ArrayList<>();
        for(BioRequestJStorePost post : request.getSlavePostData()) {
            post.setModuleKey(request.getModuleKey()); // forward moduleKey
            Paramus.applyParams(post.getBioParams(), request.getBioParams(), false, false);
            BioResponse rsp = processRequestPost(post, ctx, conn, rootUsr).build();
            slaveResponses.add(rsp);
        }
        if(slaveResponses.size() > 0)
            result.slaveResponses(slaveResponses);

        StoreData data = new StoreData();
        data.setStoreId(request.getStoreId());
        data.setMetadata(new StoreMetadata());
        List<Field> cols = cursor.getFields();
        data.getMetadata().setFields(cols);
        data.setRows(request.getModified());

        result.packet(data);
        result.bioParams(request.getBioParams());
        return result.exception(null);
    }

    @Override
    public void process(final BioRequestJStorePost request, final HttpServletResponse response) throws Exception {
        LOG.debug("Process postDataSet for \"{}\" request...", request.getBioCode());
        try {
            final User usr = request.getUser();
            BioRespBuilder.DataBuilder responseBuilder = context.execBatch(new SQLAction<Object, BioRespBuilder.DataBuilder>() {
                @Override
                public BioRespBuilder.DataBuilder exec(SQLContext context, Connection conn, Object obj, User usr) throws Exception {
//                    tryPrepareSessionContext(usr.getInnerUid(), conn);
                    return processRequestPost(request, context, conn, request.getUser());
                }
            }, null, request.getUser());
            response.getWriter().append(responseBuilder.json());
        } finally {
            LOG.debug("Processed postDataSet for \"{}\" - returning response...", request);
        }
    }

}
