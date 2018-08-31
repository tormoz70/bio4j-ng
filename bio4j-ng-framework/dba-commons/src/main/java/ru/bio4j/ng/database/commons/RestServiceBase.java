package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.ABeanPage;
import ru.bio4j.ng.model.transport.FilterAndSorter;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.BioAppModule;
import ru.bio4j.ng.service.api.FCloudProvider;
import ru.bio4j.ng.service.api.ModuleProvider;
import ru.bio4j.ng.service.types.BioQueryParams;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public abstract class RestServiceBase {
    private static final Logger LOG = LoggerFactory.getLogger(RestServiceBase.class);

    protected abstract ServletContext getServletContext();

    private ModuleProvider mmoduleProvider;
    private ModuleProvider getModuleProvider() {
        if(mmoduleProvider == null)
            mmoduleProvider = Utl.getService(getServletContext(), ModuleProvider.class);
        return mmoduleProvider;
    }

    private FCloudProvider fcloudProvider;
    protected FCloudProvider getFCloudProvider() {
        if(fcloudProvider == null)
            fcloudProvider = Utl.getService(getServletContext(), FCloudProvider.class);
        return fcloudProvider;
    }

    protected BioAppModule getModule() throws Exception {
        ModuleProvider moduleProvider = getModuleProvider();
        return moduleProvider.getAppModule("efond2");
    }

    protected ABeanPage _getList(String bioCode, HttpServletRequest request) throws Exception {
        BioAppModule module = getModule();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, module);
        rslt.setMetadata(null);
        return rslt;
    }
    protected <T> List<T> _getList(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        BioAppModule module = getModule();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, module, calzz);
        return rslt;
    }

    protected <T> T _getFirst(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        BioAppModule module = getModule();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, module, calzz, true);
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    protected String _getJson(String bioCode, HttpServletRequest request) throws Exception {
        BioAppModule module = getModule();
        StringBuilder rslt = RestApiAdapter.loadJson(bioCode, request, module);
        return rslt.toString();
    }

    protected ABean _getTotalCount(String bioCode, HttpServletRequest request) throws Exception {
        BioAppModule module = getModule();
        ABean rslt = RestApiAdapter.calcTotalCount(bioCode, request, module);
        return rslt;
    }

    protected StoreMetadata _getMetadata(String bioCode) throws Exception {
        BioAppModule module = getModule();
        return RestApiAdapter.getMetadata(bioCode, module);
    }

    protected ABean _getSingle(String bioCode, Object id, HttpServletRequest request) throws Exception {
        BioAppModule module = getModule();
        return RestApiAdapter.loadBean(bioCode, request, module, id);
    }

    protected static ABean _getSuccess() {
        ABean rslt = new ABean();
        rslt.put("success", true);
        return rslt;
    }

    protected static List<ABean> _exctarctBean(HttpServletRequest request) throws Exception {
        List<ABean> abeans = null;
        String abeanJson = null;
        try {
            abeanJson = ((BioWrappedRequest)request).getBioQueryParams().jsonData;
            abeans = Jsons.decodeABeans(abeanJson);
        } catch (Exception e) {
            throw new Exception(String.format("Cannot decode json to bean: %s", abeanJson));
        }
        return abeans;
    }

    protected static ABean _setBean(ABean bean, String attrName, Object attrValue, Class<?> attrType) throws Exception {
        if(bean == null)
            bean = new ABean();
        if(bean.containsKey(attrName))
            bean.replace(attrName, Converter.toType(attrValue, attrType));
        else
            bean.put(attrName, Converter.toType(attrValue, attrType));
        return bean;
    }

    protected static List<Param> _setBeanToBioParams(ABean bean, List<Param> params) throws Exception {
        if(params == null)
            params = new ArrayList<>();
        if(bean == null)
            return params;
        for(String key : bean.keySet())
            Paramus.setParamValue(params, key, bean.get(key));
        return params;
    }

    protected static <T> List<T> _parsIdsTyped(String ids, Class<T> clazz) throws Exception {
        List<T> rslt = new ArrayList<>();
        String[] idsArrayOrig = Strings.split(ids, ",", ";", " ");
        for (String id : idsArrayOrig) {
            rslt.add(Converter.toType(id, clazz));
        }
        return rslt;
    }

    protected static List<Object> _parsIds(String ids, Class<?> clazz) throws Exception {
        List<Object> rslt = new ArrayList<>();
        String[] idsArrayOrig = Strings.split(ids, ",", ";", " ");
        for (String id : idsArrayOrig) {
            rslt.add(Converter.toType(id, clazz));
        }
        return rslt;
    }

    protected static void _setBioParamToRequest(String paramName, Object paramValue, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        Paramus.setParamValue(queryParams.bioParams, paramName, paramValue);
    }

    protected static void _setSorterToRequest(String fieldName, Sort.Direction direction, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        if(queryParams.sort == null)
            queryParams.sort = new ArrayList<>();
        queryParams.sort.clear();
        if(!Strings.isNullOrEmpty(fieldName)) {
            Sort newSort = new Sort();
            newSort.setFieldName(fieldName);
            newSort.setDirection(direction);
            queryParams.sort.add(newSort);
        }
    }

    protected static void _setBeanToRequest(ABean bean, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        _setBeanToBioParams(bean, queryParams.bioParams);
    }

    protected static <T> T _getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType) throws Exception {
        final BioQueryParams queryParams = ((BioWrappedRequest)request).getBioQueryParams();
        return Paramus.paramValue(queryParams.bioParams, paramName, paramType, null);
    }

    protected ABean _delete(String bioCode, List<Object> ids, HttpServletRequest request) throws Exception {
        BioAppModule module = getModule();
        return RestApiAdapter.deleteBeans(bioCode, request, module, ids);
    }

    protected void _exec(String bioCode, HttpServletRequest request) throws Exception {
        BioAppModule module = getModule();
        RestApiAdapter.exec(bioCode, request, module);
    }

    protected List<ABean> _save(String bioCode, List<ABean> abeans, HttpServletRequest request) throws Exception {
        BioAppModule module = getModule();
        return RestApiAdapter.saveBeans(bioCode, request, module, abeans);
    }

}
