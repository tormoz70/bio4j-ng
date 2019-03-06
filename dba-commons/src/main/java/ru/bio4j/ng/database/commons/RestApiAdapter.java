package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLActionVoid0;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.BioAppService;
import ru.bio4j.ng.service.api.BioSQLDefinition;
import ru.bio4j.ng.service.api.RestParamNames;
import ru.bio4j.ng.service.types.BioQueryParams;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class RestApiAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(RestApiAdapter.class);

    public static ABeanPage loadPage(
            final BioAppService module,
            final String bioCode,
            final Object params,
            final User user,
            final boolean forceAll,
            final FilterAndSorter filterAndSorter,
            final boolean forceCalcCount
            ) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0 || forceAll)
            return CrudReaderApi.loadAll(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user);
        else
            return CrudReaderApi.loadPage(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, forceCalcCount, user);
    }

    public static ABeanPage loadPage(
            final BioAppService module,
            final String bioCode,
            final Object params,
            final User user,
            final boolean forceAll) throws Exception {
        return loadPage(module, bioCode, params, user, forceAll, null, false);
    }

    public static ABeanPage loadPage(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module,
            final boolean forceAll) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        final List<Param> params = queryParams.bioParams;
        final User user = ((BioWrappedRequest)request).getUser();
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData)) {
            try {
                fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
            } catch (Exception e) {
                LOG.error(String.format("Ошибка при восстановлении объекта %s. Json: %s", FilterAndSorter.class.getSimpleName(), queryParams.jsonData), e);
            }
        }
        if(fs == null) {
            fs = new FilterAndSorter();
            fs.setSorter(queryParams.sort);
            fs.setFilter(queryParams.filter);
        }
        boolean forceCalcCount = Converter.toType(queryParams.gcount, boolean.class);
        return loadPage(module, bioCode, params, user, forceAll, fs, forceCalcCount);
    }


    public static ABeanPage loadPage(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module) throws Exception {
        return loadPage(bioCode, request, module, false);
    }

    public static ABean calcTotalCount(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        final List<Param> params = queryParams.bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        int pageSize = Paramus.paramValue(params, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData))
            fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
        if(fs == null) {
            fs = new FilterAndSorter();
            fs.setSorter(queryParams.sort);
            fs.setFilter(queryParams.filter);
        }
        ABean rslt = new ABean();
        ru.bio4j.ng.model.transport.jstore.filter.Filter filter = fs != null ? fs.getFilter() : null;
        sqlDefinition.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql(), filter));
        sqlDefinition.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql()));
        long totalCount = CrudReaderApi.calcTotalCount(params, context, sqlDefinition, user);
        rslt.put("totalCount", totalCount);
        return rslt;
    }

    public static <T> List<T> loadPageExt(
            final BioAppService module,
            final String bioCode,
            final Object params,
            final User user,
            final boolean forceAll,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0 || forceAll)
            return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
        else
            return CrudReaderApi.loadPageExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }

    public static <T> List<T> loadPageExt(
            final BioAppService module,
            final String bioCode,
            final Object params,
            final boolean forceAll,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        User user = null;
        Param usrParam  = Paramus.getParam(prms, "p_userbean");
        if(usrParam != null)
            user = (User)usrParam.getValue();

        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0 || forceAll)
            return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
        else
            return CrudReaderApi.loadPageExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }

    public static <T> List<T> loadPageExt(
            final BioAppService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final boolean forceAll) throws Exception {
        return loadPageExt(module, bioCode, params, user, forceAll, beanType, null);
    }

    public static <T> List<T> loadPageExt(
            final BioAppService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType,
            final boolean forceAll) throws Exception {
        return loadPageExt(module, bioCode, params, forceAll, beanType, null);
    }

    public static <T> List<T> loadPageExt(
            final BioAppService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType) throws Exception {
        return loadPageExt(module, bioCode, params, beanType, false);
    }

    public static <T> List<T> loadPageExt(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module,
            final Class<T> beanType,
            final boolean forceAll) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        final List<Param> params = queryParams.bioParams;
        final User user = ((BioWrappedRequest)request).getUser();
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData))
            fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
        if(fs == null) {
            fs = new FilterAndSorter();
            fs.setSorter(queryParams.sort);
            fs.setFilter(queryParams.filter);
        }
        return loadPageExt(module, bioCode, params, user, forceAll, beanType, fs);
    }
    public static <T> List<T> loadPageExt(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module,
            final Class<T> beanType) throws Exception {
        return loadPageExt(bioCode, request, module, beanType, false);
    }

    public static ABean getMetadata(
            final String bioCode,
            BioAppService module) throws Exception {
        ABean rslt = new ABean();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        StoreMetadata metadata = new StoreMetadata();
        metadata.setReadonly(sqlDefinition.getReadOnly());
        metadata.setMultiSelection(sqlDefinition.getMultiSelection());
        List<Field> fields = sqlDefinition.getFields();
        metadata.setFields(fields);
        rslt.put("dataset", metadata);
        if(sqlDefinition.getUpdateSqlDef() != null) {
            ABean createUpdateObject = new ABean();
            for(Param p : sqlDefinition.getUpdateSqlDef().getParamDeclaration()){
                createUpdateObject.put(DbUtils.cutParamPrefix(p.getName()), p.getType().name());
            }
            rslt.put("createUpdateObject", createUpdateObject);
        }
        return rslt;
    }

    public static StoreMetadata getMetadataOld(
            final String bioCode,
            BioAppService module) throws Exception {

        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        StoreMetadata metadata = new StoreMetadata();
        metadata.setReadonly(sqlDefinition.getReadOnly());
        metadata.setMultiSelection(sqlDefinition.getMultiSelection());
        List<Field> fields = sqlDefinition.getFields();
        metadata.setFields(fields);
        return metadata;
    }

    public static ABean loadBean(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module,
            final Object id) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        if(id != null) {
            Paramus.setParamValue(params, RestParamNames.GETROW_PARAM_PKVAL, id, MetaTypeConverter.read(id.getClass()));
            ABeanPage rslt = CrudReaderApi.loadRecord(params, context, sqlDefinition, user);
            if (rslt.getRows() != null && rslt.getRows().size() > 0)
                return rslt.getRows().get(0);
        }
        return null;
    }

    public static StringBuilder loadJson(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        StringBuilder rslt = CrudReaderApi.loadJson(params, context, sqlDefinition, user);
        return rslt;
    }

    public static List<ABean> saveBeans(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module,
            final List<ABean> rows) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        List<ABean> rslt = CrudWriterApi.saveRecords(params, rows, context, sqlDefinition, user);
        return rslt;
    }
    public static ABean deleteBeans(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module,
            final List<Object> ids) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        int affected = CrudWriterApi.deleteRecords(params, ids, context, sqlDefinition, user);
        ABean rslt = new ABean();
        rslt.put("deleted", affected);
        return rslt;
    }

    public static void exec(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public static void exec(
            final String bioCode,
            final Object params,
            final BioAppService module,
            final User user) throws Exception {
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public static <T> T selectScalar(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module,
            final Class<T> clazz,
            final T defaultValue,
            final User user) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudWriterApi.selectScalar(params, context, sqlDefinition, clazz, defaultValue, user);
    }

    public static void execBatch(final SQLContext context, final SQLActionVoid0 action, final User user) throws Exception {
        context.execBatch(action, user);

    }

    public static void execLocal(
            final BioSQLDefinition sqlDefinition,
            final Object params,
            final SQLContext context) throws Exception {
        CrudWriterApi.execSQLLocal(params, context, sqlDefinition);
    }

    public static void execForEach(
            final String bioCode,
            final HttpServletRequest request,
            final BioAppService module) throws Exception {
        final List<Param> params = ((BioWrappedRequest)request).getBioQueryParams().bioParams;
        final SQLContext context = module.getSQLContext();
        final BioSQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((BioWrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

}
