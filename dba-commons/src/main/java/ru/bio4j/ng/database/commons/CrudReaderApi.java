package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.DBField;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLCursor;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.service.api.BioSQLDefinition;
import ru.bio4j.ng.service.api.RestParamNames;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CrudReaderApi {
    protected final static Logger LOG = LoggerFactory.getLogger(CrudReaderApi.class);

    //TODO Перенести в настройки
    private static final int MAX_RECORDS_FETCH_LIMIT = 250000;

    private static void preparePkParamValue(final List<Param> params, final Field pkField) throws Exception {
        Param pkParam = Paramus.getParam(params, RestParamNames.GETROW_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setType(pkField.getMetaType());
            pkParam.setValue(newValue);
        }
        pkParam = Paramus.getParam(params, RestParamNames.LOCATE_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setValue(newValue);
        }
        pkParam = Paramus.getParam(params, RestParamNames.DELETE_PARAM_PKVAL);
        if(pkParam != null) {
            Object curValue = pkParam.getValue();
            Object newValue = Converter.toType(curValue, MetaTypeConverter.write(pkField.getMetaType()));
            pkParam.setValue(newValue);
        }
    }

    private static ABeanPage readStoreData(
            final List<Param> params,
            final SQLContext context,
            final Connection conn,
            final BioSQLDefinition cursorDef,
            final User usr) throws Exception {
        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        ABeanPage result = new ABeanPage();
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        result.setTotalCount(Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, int.class, 0));
        result.setPaginationOffset(Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0));
        result.setRows(new ArrayList<>());

        long startTime = System.currentTimeMillis();
        result.setMetadata(cursorDef.getFields());

        List<Param> prms = params;
        context.createCursor()
                .init(conn, cursorDef.getSelectSqlDef().getPreparedSql(), cursorDef.getSelectSqlDef().getParamDeclaration())
                .fetch(prms, usr, rs -> {
                    if(rs.isFirstRow()) {
                        long estimatedTime = System.currentTimeMillis() - startTime;
                        LOG.debug("Cursor \"{}\" opened in {} secs!!!", cursorDef.getBioCode(), Double.toString(estimatedTime / 1000));
                    }
                    ABean bean = new ABean();
                    for (Field field : result.getMetadata()) {
                        String attrName = field.getAttrName();
                        if(Strings.isNullOrEmpty(attrName))
                            attrName = field.getName();
                        DBField f = rs.getField(field.getName());
                        if (f != null) {
                            Object val = rs.getValue(f.getId());
                            Class<?> clazz = MetaTypeConverter.write(field.getMetaType());
                            Object valTyped = Converter.toType(val, clazz);
                            bean.put(attrName, valTyped);
                        } else
                            bean.put(attrName, null);
                    }
                    result.getRows().add(bean);
                    if(result.getRows().size() >= MAX_RECORDS_FETCH_LIMIT)
                        return false;
                    return true;
                });
        LOG.debug("Cursor \"{}\" fetched! {} - records loaded.", cursorDef.getBioCode(), result.getRows().size());
        result.setPaginationCount(result.getRows().size());
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        result.setPaginationPage(pageSize > 0 ? (int)Math.floor(result.getPaginationOffset() / pageSize) + 1 : 0);
        if(result.getRows().size() < paginationPagesize) {
            result.setTotalCount(result.getPaginationOffset() + result.getRows().size());
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, result.getTotalCount());
        }
        return result;
    }

    private static long calcOffset(int locatedPos, int pageSize){
        long pg = ((long)((double)(locatedPos - 1) / (double)pageSize) + 1);
        return (pg - 1) * pageSize;
    }

    public static long calcTotalCount(
            final List<Param> params,
            final SQLContext context,
            final BioSQLDefinition cursor,
            final User user) throws Exception {
        long result = context.execBatch((ctx) -> {
            SQLCursor c = ctx.createCursor();
            c.init(ctx.getCurrentConnection(), cursor.getSelectSqlDef().getTotalsSql(), cursor.getSelectSqlDef().getParamDeclaration());
            return c.scalar(params, user, long.class, 0L);
        }, user);
        return result;
    }

    public static ABeanPage loadPage(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final BioSQLDefinition cursor,
            final boolean forceCalcCount,
            final User user) throws Exception {
        final Object location = Paramus.paramValue(params, RestParamNames.LOCATE_PARAM_PKVAL, java.lang.Object.class, null);
        final int paginationOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0);
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);

        final String paginationTotalcountStr = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, String.class, null);
        final int paginationTotalcount = Strings.isNullOrEmpty(paginationTotalcountStr) ? Sqls.UNKNOWN_RECS_TOTAL : Converter.toType(paginationTotalcountStr, int.class);

        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter));
        cursor.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), sort, cursor.getSelectSqlDef().getFields()));
        if(location != null) {
            Field pkField = cursor.getSelectSqlDef().findPk();
            if(pkField == null)
                throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
            cursor.getSelectSqlDef().setLocateSql(context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), pkField.getName()));
            preparePkParamValue(params, pkField);
        }
        if(paginationPagesize > 0)
            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));

        long factOffset = paginationOffset;
        long totalCount = paginationTotalcount;
        if(forceCalcCount || paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1))
            totalCount = calcTotalCount(params, context, cursor, user);
        if(paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1)) {
            factOffset = (int)Math.floor(totalCount / paginationPagesize) * paginationPagesize;
            LOG.debug("Count of records of cursor \"{}\" - {}!!!", cursor.getBioCode(), totalCount);
        }
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, factOffset);
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, totalCount);
        final ABeanPage result = context.execBatch((ctx) -> {
            long locFactOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, long.class, 0L);
            if(location != null) {
                LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cursor.getBioCode(), location);
                int locatedPos = ctx.createCursor()
                        .init(ctx.getCurrentConnection(), cursor.getSelectSqlDef().getLocateSql(), cursor.getSelectSqlDef().getParamDeclaration())
                        .scalar(params, ctx.getCurrentUser(), int.class, -1);
                if(locatedPos >= 0){
                    locFactOffset = calcOffset(locatedPos, paginationPagesize);
                    LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cursor.getBioCode(), location, locatedPos, locFactOffset);
                } else {
                    LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cursor.getBioCode(), location);
                }
            }
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, locFactOffset);
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_LIMIT, paginationPagesize);
            return readStoreData(params, ctx, ctx.getCurrentConnection(), cursor, ctx.getCurrentUser());
        }, user);
        return result;
    }

    public static ABeanPage loadAll(final List<Param> params, final Filter filter, final List<Sort> sort, final SQLContext context, final BioSQLDefinition cursor, User user) throws Exception {
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), sort, cursor.getSelectSqlDef().getFields()));
        ABeanPage result = context.execBatch((ctx) -> {
            return readStoreData(params, ctx, ctx.getCurrentConnection(), cursor, ctx.getCurrentUser());
        }, user);
        return result;
    }

    public static ABeanPage loadRecord(final List<Param> params, final SQLContext context, final BioSQLDefinition cursor, final User user) throws Exception {
        ABeanPage result = context.execBatch((ctx) -> {
            return loadRecordLocal(params, ctx, cursor);
        }, user);
        return result;
    }

    public static ABeanPage loadRecordLocal(final List<Param> params, final SQLContext context, final BioSQLDefinition cursor) throws Exception {
        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        preparePkParamValue(params, pkField);
        return readStoreData(params, context, context.getCurrentConnection(), cursor, context.getCurrentUser());
    }

    private static <T> List<T> readStoreDataExt(
            final List<Param> params,
            final SQLContext context,
            final BioSQLDefinition cursorDef,
            final Class<T> beanType) throws Exception {

        if (context.getCurrentConnection() == null)
            throw new Exception(String.format("This methon can be useded only in SQLAction of execBatch!", cursorDef.getBioCode()));

        LOG.debug("Opening Cursor \"{}\"...", cursorDef.getBioCode());
        List<T> result = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        List<Param> prms = params;
        context.createCursor()
                .init(context.getCurrentConnection(), cursorDef.getSelectSqlDef().getPreparedSql(), cursorDef.getSelectSqlDef().getParamDeclaration())
                .fetch(prms, context.getCurrentUser(), rs -> {
                    if (rs.isFirstRow()) {
                        long estimatedTime = System.currentTimeMillis() - startTime;
                        LOG.debug("Cursor \"{}\" opened in {} secs!!!", cursorDef.getBioCode(), Double.toString(estimatedTime / 1000));
                    }
                    T bean = DbUtils.createBeanFromReader(rs, beanType);
                    result.add(bean);
                    if (result.size() >= MAX_RECORDS_FETCH_LIMIT)
                        return false;
                    return true;
                });
        LOG.debug("Cursor \"{}\" fetched! {} - records loaded.", cursorDef.getBioCode(), result.size());
        return result;
    }

    public static <T> List<T> loadPageExt(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final BioSQLDefinition cursor,
            final User user,
            final Class<T> beanType) throws Exception {
        final Object location = Paramus.paramValue(params, RestParamNames.LOCATE_PARAM_PKVAL, java.lang.Object.class, null);
        final int paginationOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, int.class, 0);
        final int paginationPagesize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);

        final String paginationTotalcountStr = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, String.class, null);
        final int paginationTotalcount = Strings.isNullOrEmpty(paginationTotalcountStr) ? Sqls.UNKNOWN_RECS_TOTAL : Converter.toType(paginationTotalcountStr, int.class);

        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter));
        cursor.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), sort, cursor.getSelectSqlDef().getFields()));
        if (location != null) {
            Field pkField = cursor.getSelectSqlDef().findPk();
            if (pkField == null)
                throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
            cursor.getSelectSqlDef().setLocateSql(context.getWrappers().getLocateWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
            preparePkParamValue(params, pkField);
        }
        if (paginationPagesize > 0)
            cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getPaginationWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));

        long factOffset = paginationOffset;
        long totalCount = paginationTotalcount;
        if (paginationOffset == (Sqls.UNKNOWN_RECS_TOTAL - paginationPagesize + 1)) {
            totalCount = calcTotalCount(params, context, cursor, user);
            factOffset = (int) Math.floor(totalCount / paginationPagesize) * paginationPagesize;
            LOG.debug("Count of records of cursor \"{}\" - {}!!!", cursor.getBioCode(), totalCount);
        }
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, factOffset);
        Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, totalCount);
        List<T> result = context.execBatch((ctx) -> {
            long locFactOffset = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, long.class, 0L);
            if (location != null) {
                LOG.debug("Try locate cursor \"{}\" to [{}] record by pk!!!", cursor.getBioCode(), location);
                int locatedPos = ctx.createCursor()
                        .init(ctx.getCurrentConnection(), cursor.getSelectSqlDef().getLocateSql(), cursor.getSelectSqlDef().getParamDeclaration())
                        .scalar(params, ctx.getCurrentUser(), int.class, -1);
                if (locatedPos >= 0) {
                    locFactOffset = calcOffset(locatedPos, paginationPagesize);
                    LOG.debug("Cursor \"{}\" successfully located to [{}] record by pk. Position: [{}], New offset: [{}].", cursor.getBioCode(), location, locatedPos, locFactOffset);
                } else {
                    LOG.debug("Cursor \"{}\" failed location to [{}] record by pk!!!", cursor.getBioCode(), location);
                }
            }
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_OFFSET, locFactOffset);
            Paramus.setParamValue(params, RestParamNames.PAGINATION_PARAM_LIMIT, paginationPagesize);
            return readStoreDataExt(params, ctx, cursor, beanType);
        }, user);
        return result;
    }

    public static <T> List<T> loadAllExt(
            final List<Param> params,
            final Filter filter,
            final List<Sort> sort,
            final SQLContext context,
            final BioSQLDefinition cursor,
            final User user,
            final Class<T> beanType) throws Exception {
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getSql(), filter));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getSortingWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), sort, cursor.getSelectSqlDef().getFields()));
        List<T> result = context.execBatch((ctx) -> {
            return readStoreDataExt(params, ctx, cursor, beanType);
        }, user);
        return result;
    }

    public static <T> List<T> loadRecordExt(
            final List<Param> params,
            final SQLContext context, final BioSQLDefinition cursor,
            final User user,
            final Class<T> beanType) throws Exception {
        Field pkField = cursor.getSelectSqlDef().findPk();
        if(pkField == null)
            throw new BioError.BadIODescriptor(String.format("PK column not fount in \"%s\" object!", cursor.getSelectSqlDef().getBioCode()));
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getGetrowWrapper().wrap(cursor.getSelectSqlDef().getSql(), pkField.getName()));
        preparePkParamValue(params, pkField);
        List<T> result = context.execBatch((ctx) -> {
            return readStoreDataExt(params, ctx, cursor, beanType);
        }, user);
        return result;
    }

    public static StringBuilder loadJson(
            final List<Param> params,
            final SQLContext context,
            final BioSQLDefinition cursor,
            final User user) throws Exception {
        StringBuilder result = context.execBatch((ctx) -> {
            final StringBuilder r = new StringBuilder();

            ctx.createCursor()
                    .init(ctx.getCurrentConnection(), cursor.getSelectSqlDef().getSql(), cursor.getSelectSqlDef().getParamDeclaration())
                    .fetch(params, user, rs -> {
                        List<Object> values = rs.getValues();
                        for (Object val : values)
                            r.append(val);
                        return true;
                    });
            return r;
        }, user);
        return result;
    }
}
