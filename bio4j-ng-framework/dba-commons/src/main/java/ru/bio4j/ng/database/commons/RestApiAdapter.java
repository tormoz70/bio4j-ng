package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.commons.CrudReaderApi;
import ru.bio4j.ng.database.commons.CrudWriterApi;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.BioAppModule;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.api.RestParamNames;
import ru.bio4j.ng.service.types.BioQueryParams;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class RestApiAdapter {


    public static ABeanPage loadPage(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        final List<Param> params = queryParams.bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData))
            fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
        if(pageSize > 0) {
            boolean forceCalcCount = Converter.toType(queryParams.gcount, boolean.class);
            return CrudReaderApi.loadPage(params, fs != null ? fs.getFilter() : null, fs != null ? fs.getSorter() : null, context, cursor, forceCalcCount, user);
        } else
            return CrudReaderApi.loadAll(params, fs != null ? fs.getFilter() : null, fs != null ? fs.getSorter() : null, context, cursor, user);
    }

    public static ABean calcTotalCount(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        final List<Param> params = queryParams.bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData))
            fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
        ABean rslt = new ABean();
        ru.bio4j.ng.model.transport.jstore.filter.Filter filter = fs != null ? fs.getFilter() : null;
        cursor.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql(), filter));
        cursor.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(cursor.getSelectSqlDef().getPreparedSql()));
        long totalCount = CrudReaderApi.calcTotalCount(params, context, cursor, user);
        rslt.put("totalCount", totalCount);
        return rslt;
    }

    public static <T> List<T> loadPageExt(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module,
            final Class<T> beanType) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        final List<Param> params = queryParams.bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData))
            fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
        if(pageSize > 0)
            return CrudReaderApi.loadPageExt(params, fs != null ? fs.getFilter() : null, fs != null ? fs.getSorter() : null, context, cursor, user, beanType);
        else
            return CrudReaderApi.loadAllExt(params, fs != null ? fs.getFilter() : null, fs != null ? fs.getSorter() : null, context, cursor, user, beanType);
    }

    public static StoreMetadata getMetadata(
            final String bioCode,
            BioAppModule module) throws Exception {

        final BioCursor cursor = module.getCursor(bioCode);
        StoreMetadata metadata = new StoreMetadata();
        metadata.setReadonly(cursor.getReadOnly());
        metadata.setMultiSelection(cursor.getMultiSelection());
        List<Field> fields = cursor.getFields();
        metadata.setFields(fields);
        return metadata;
    }

    public static ABean loadBean(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module,
            final Object id) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        Paramus.setParamValue(params, RestParamNames.GETROW_PARAM_PKVAL, id, MetaTypeConverter.read(id.getClass()));
        ABeanPage rslt = CrudReaderApi.loadRecord(params, context, cursor, user);
        if(rslt.getRows() != null && rslt.getRows().size() > 0)
            return rslt.getRows().get(0);
        return null;
    }

    public static StringBuilder loadJson(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        StringBuilder rslt = CrudReaderApi.loadJson(params, context, cursor, user);
        return rslt;
    }

    public static List<ABean> saveBeans(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module,
            final List<ABean> rows) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        List<ABean> rslt = CrudWriterApi.saveRecords(params, rows, context, cursor, user);
        return rslt;
    }
    public static ABean deleteBeans(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module,
            final List<Object> ids) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        int affected = CrudWriterApi.deleteRecords(params, ids, context, cursor, user);
        ABean rslt = new ABean();
        rslt.put("deleted", affected);
        return rslt;
    }

    public static void exec(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, cursor, user);
    }

    public static void execForEach(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppModule module) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioCursor cursor = module.getCursor(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, cursor, user);
    }

}
