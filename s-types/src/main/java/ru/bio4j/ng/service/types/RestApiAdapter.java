package ru.bio4j.ng.service.types;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLActionScalar0;
import ru.bio4j.ng.database.api.SQLActionScalar1;
import ru.bio4j.ng.database.api.SQLActionVoid0;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.commons.CrudReaderApi;
import ru.bio4j.ng.database.commons.CrudWriterApi;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.RestParamNames;
import ru.bio4j.ng.model.transport.BioQueryParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class RestApiAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(RestApiAdapter.class);

    private static List<Param>_extractBioParams(final BioQueryParams queryParams) throws Exception {
        Paramus.setQueryParamsToBioParams(queryParams);
        return queryParams.bioParams;
    }

    private static List<Param>_extractBioParams(final HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        return _extractBioParams(queryParams);
    }

    public static ABeanPage loadPage(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user,
            final FilterAndSorter filterAndSorter,
            final boolean forceCalcCount
            ) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAll(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user);
        else
            return CrudReaderApi.loadPage(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, forceCalcCount, user);
    }
    public static ABeanPage loadAll(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user,
            final FilterAndSorter filterAndSorter,
            final boolean forceCalcCount
    ) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.loadAll(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user);
    }

    public static ABeanPage loadPage(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user) throws Exception {
        return loadPage(module, bioCode, params, user, null, false);
    }
    public static ABeanPage loadAll(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user) throws Exception {
        return loadAll(module, bioCode, params, user, null, false);
    }

    public static ABeanPage loadPage(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
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
        return loadPage(module, bioCode, params, user, fs, forceCalcCount);
    }
    public static ABeanPage loadAll(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
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
        return loadAll(module, bioCode, params, user, fs, forceCalcCount);
    }

    public static ABean calcTotalCount(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
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
        sqlDefinition.getSelectSqlDef().setPreparedSql(context.getWrappers().getFilteringWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql(), filter, sqlDefinition.getSelectSqlDef().getFields()));
        sqlDefinition.getSelectSqlDef().setTotalsSql(context.getWrappers().getTotalsWrapper().wrap(sqlDefinition.getSelectSqlDef().getPreparedSql()));
        long totalCount = CrudReaderApi.calcTotalCount(params, context, sqlDefinition, user);
        rslt.put("totalCount", totalCount);
        return rslt;
    }

    public static <T> List<T> loadPageExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
        else
            return CrudReaderApi.loadPageExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }
    public static <T> List<T> loadAllExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.loadAllExt(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, user, beanType);
    }

    public static <T> List<T> loadPageExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        User user = null;
        Param usrParam  = Paramus.getParam(prms, "p_userbean");
        if(usrParam != null)
            user = (User)usrParam.getValue();

        return loadPageExt(module, bioCode, params, user, beanType, filterAndSorter);
    }
    public static <T> List<T> loadAllExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        User user = null;
        Param usrParam  = Paramus.getParam(prms, "p_userbean");
        if(usrParam != null)
            user = (User)usrParam.getValue();

        return loadAllExt(module, bioCode, params, user, beanType, filterAndSorter);
    }

    public static <T> List<T> loadPageExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) throws Exception {
        return loadPageExt(module, bioCode, params, user, beanType, null);
    }
    public static <T> List<T> loadAllExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final User user,
            final Class<T> beanType) throws Exception {
        return loadAllExt(module, bioCode, params, user, beanType, null);
    }

    public static <T> List<T> loadPageExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType) throws Exception {
        return loadPageExt(module, bioCode, params, beanType, null);
    }
    public static <T> List<T> loadAllExt(
            final AppService module,
            final String bioCode,
            final Object params,
            final Class<T> beanType) throws Exception {
        return loadAllExt(module, bioCode, params, beanType, null);
    }

    public static HSSFWorkbook loadToExcel(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDef = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        return context.execBatch((ctx) -> {
            FilterAndSorter fs = null;
            if(!Strings.isNullOrEmpty(queryParams.jsonData))
                fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
            if(fs == null) {
                fs = new FilterAndSorter();
                fs.setSorter(queryParams.sort);
                fs.setFilter(queryParams.filter);
            }
            return CrudReaderApi.toExcel(params, fs.getFilter(), fs.getSorter(), ctx, sqlDef);
        }, user);

    }

    public static <T> List<T> loadPageExt(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module,
            final Class<T> beanType) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData))
            fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
        if(fs == null) {
            fs = new FilterAndSorter();
            fs.setSorter(queryParams.sort);
            fs.setFilter(queryParams.filter);
        }
        return loadPageExt(module, bioCode, params, user, beanType, fs);
    }
    public static <T> List<T> loadAllExt(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module,
            final Class<T> beanType) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        final List<Param> params = _extractBioParams(queryParams);
        final User user = ((WrappedRequest)request).getUser();
        FilterAndSorter fs = null;
        if(!Strings.isNullOrEmpty(queryParams.jsonData))
            fs = Jsons.decodeFilterAndSorter(queryParams.jsonData);
        if(fs == null) {
            fs = new FilterAndSorter();
            fs.setSorter(queryParams.sort);
            fs.setFilter(queryParams.filter);
        }
        return loadAllExt(module, bioCode, params, user, beanType, fs);
    }

    public static ABean getMetadata(
            final String bioCode,
            AppService module) throws Exception {
        ABean rslt = new ABean();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
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
            AppService module) throws Exception {

        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
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
            final AppService module,
            final Object id) throws Exception {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
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
            final AppService module) throws Exception {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        StringBuilder rslt = CrudReaderApi.loadJson(params, context, sqlDefinition, user);
        return rslt;
    }

    public static List<ABean> saveBeans(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module,
            final List<ABean> rows) throws Exception {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        List<ABean> rslt = CrudWriterApi.saveRecords(params, rows, context, sqlDefinition, user);
        return rslt;
    }
    public static ABean deleteBeans(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module,
            final List<Object> ids) throws Exception {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        int affected = CrudWriterApi.deleteRecords(params, ids, context, sqlDefinition, user);
        ABean rslt = new ABean();
        rslt.put("deleted", affected);
        return rslt;
    }

    public static void exec(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module) throws Exception {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public static void exec(
            final String bioCode,
            final Object params,
            final AppService module,
            final User user) throws Exception {
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

    public static <T> T selectScalar(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module,
            final Class<T> clazz,
            final T defaultValue) throws Exception {
        final List<Param> params = _extractBioParams(request);
        final User user = ((WrappedRequest)request).getUser();
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.selectScalar(params, context, sqlDefinition, clazz, defaultValue, user);
    }

    public static <T> T selectScalar(
            final String bioCode,
            final Object params,
            final AppService module,
            final Class<T> clazz,
            final T defaultValue,
            final User user) throws Exception {
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        return CrudReaderApi.selectScalar(params, context, sqlDefinition, clazz, defaultValue, user);
    }

    public static void execBatch(final SQLContext context, final SQLActionVoid0 action, final User user) throws Exception {
        context.execBatch(action, user);
    }

    public static <T> T execBatch(final SQLContext context, final SQLActionScalar0<T> action, final User user) throws Exception {
        return context.execBatch(action, user);
    }

    public static <P, T> T execBatch(final SQLContext context, final SQLActionScalar1<P, T> action, P param, final User user) throws Exception {
        return context.execBatch(action, param, user);
    }

    public static void execLocal(
            final SQLDefinition sqlDefinition,
            final Object params,
            final SQLContext context) throws Exception {
        CrudWriterApi.execSQL0(params, context, sqlDefinition);
    }

    public static <T> List<T> loadPage0Ext(
            final SQLDefinition sqlDefinition,
            final SQLContext context,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        int pageSize = Paramus.paramValue(prms, RestParamNames.PAGINATION_PARAM_PAGESIZE, int.class, 0);
        if(pageSize == 0)
            return CrudReaderApi.loadAll0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
        else
            return CrudReaderApi.loadPage0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
    }
    public static <T> List<T> loadAll0Ext(
            final SQLDefinition sqlDefinition,
            final SQLContext context,
            final Object params,
            final Class<T> beanType,
            final FilterAndSorter filterAndSorter) throws Exception {
        final List<Param> prms = DbUtils.decodeParams(params);
        return CrudReaderApi.loadAll0Ext(prms, filterAndSorter != null ? filterAndSorter.getFilter() : null, filterAndSorter != null ? filterAndSorter.getSorter() : null, context, sqlDefinition, beanType);
    }


    public static void execForEach(
            final String bioCode,
            final HttpServletRequest request,
            final AppService module) throws Exception {
        final List<Param> params = _extractBioParams(request);
        final SQLContext context = module.getSQLContext();
        final SQLDefinition sqlDefinition = module.getSQLDefinition(bioCode);
        final User user = ((WrappedRequest)request).getUser();
        CrudWriterApi.execSQL(params, context, sqlDefinition, user);
    }

}
