package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.BioAppService;
import ru.bio4j.ng.service.api.BioSecurityService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public abstract class RestServiceBase {
    private static final Logger LOG = LoggerFactory.getLogger(RestServiceBase.class);

    protected abstract ServletContext getServletContext();

    protected Class<? extends BioAppService> getAppServiceClass() {
        return BioAppService.class;
    }
    protected Class<? extends FCloudApi> getFCloudApiClass() {
        return FCloudApi.class;
    }
    protected Class<? extends BioSecurityService> getSecurityServiceClass() {
        return BioSecurityService.class;
    }

    private BioAppService appService;
    protected BioAppService getAppService() {
        if(appService == null)
            appService = Utl.getService(getServletContext(), getAppServiceClass());
        return appService;
    }

    private BioSecurityService securityService;
    protected BioSecurityService getSecurityService() {
        if(securityService == null)
            securityService = Utl.getService(getServletContext(), getSecurityServiceClass());
        return securityService;
    }

    private FCloudApi fCloudApi;
    protected FCloudApi getFCloudApi() {
        if(fCloudApi == null)
            fCloudApi = Utl.getService(getServletContext(), getFCloudApiClass());
        return fCloudApi;
    }

    protected ABeanPage _getList(String bioCode, HttpServletRequest request, boolean forceAll) throws Exception {
        BioAppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, appService, forceAll);
        rslt.setMetadata(null);
        return rslt;
    }

    protected ABeanPage _getList(String bioCode, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, appService);
        rslt.setMetadata(null);
        return rslt;
    }

    protected ABeanPage _getList(String bioCode, Object prms, User user, boolean forceAll, FilterAndSorter filterAndSorter, boolean forceCalcCount) throws Exception {
        BioAppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(appService, bioCode, prms, user, forceAll, filterAndSorter, forceCalcCount);
        rslt.setMetadata(null);
        return rslt;
    }


    protected ABeanPage _getList(String bioCode, Object prms, User user, boolean forceAll) throws Exception {
        BioAppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(appService, bioCode, prms, user, forceAll);
        rslt.setMetadata(null);
        return rslt;
    }

    protected <T> List<T> _getList(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        BioAppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, appService, calzz);
        return rslt;
    }

    protected <T> List<T> _getList(String bioCode, HttpServletRequest request, Class<T> calzz, boolean forceAll) throws Exception {
        BioAppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, appService, calzz, forceAll);
        return rslt;
    }

    protected <T> List<T> _getList(String bioCode, Object prms, Class<T> calzz, boolean forceAll) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.loadPageExt(appService, bioCode, prms, calzz, forceAll);
    }

    protected <T> List<T> _getList(String bioCode, Object prms, Class<T> calzz) throws Exception {
        return _getList(bioCode, prms, calzz, false);
    }

    protected <T> T _getFirst(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        BioAppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, appService, calzz, true);
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    protected <T> T _getFirst(String bioCode, Object prms, Class<T> calzz) throws Exception {
        BioAppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(appService, bioCode, prms, calzz);
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    protected ABean _getFirst(String bioCode, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, appService);
        return rslt.getRows().size() > 0 ? rslt.getRows().get(0) : null;
    }

    protected <T> T _getScalar(final String bioCode, final HttpServletRequest request, final Class<T> calzz, final T defaultValue) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.selectScalar(bioCode, request, appService, calzz, defaultValue);
    }

    protected <T> T _getScalar(final String bioCode, final Object params, Class<T> calzz, T defaultValue, User user) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.selectScalar(bioCode, params, appService, calzz, defaultValue, user);
    }

    protected String _getJson(String bioCode, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        StringBuilder rslt = RestApiAdapter.loadJson(bioCode, request, appService);
        return rslt.toString();
    }

    protected ABean _getTotalCount(String bioCode, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        ABean rslt = RestApiAdapter.calcTotalCount(bioCode, request, appService);
        return rslt;
    }

    protected StoreMetadata _getMetadataOld(String bioCode) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.getMetadataOld(bioCode, appService);
    }

    protected ABean _getMetadata(String bioCode) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.getMetadata(bioCode, appService);
    }

    protected ABean _getSingle(String bioCode, Object id, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.loadBean(bioCode, request, appService, id);
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

    protected static <T> T _exctarctBean(HttpServletRequest request, Class<T> clazz) throws Exception {
        T bean = null;
        String abeanJson = null;
        try {
            abeanJson = ((BioWrappedRequest)request).getBioQueryParams().jsonData;
            bean = Jsons.decode(abeanJson, clazz);
        } catch (Exception e) {
            throw new Exception(String.format("Cannot decode json to bean(%s): %s", clazz.getCanonicalName(), abeanJson));
        }
        return bean;
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

    protected static <T> T _getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType, T defaultValue) throws Exception {
        return ((BioWrappedRequest)request).getBioQueryParam(paramName, paramType, defaultValue);
    }

    protected static <T> T _getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType) throws Exception {
        return  _getBioParamFromRequest(paramName, request, paramType, null);
    }

    protected ABean _delete(String bioCode, List<Object> ids, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.deleteBeans(bioCode, request, appService, ids);
    }

    protected void _exec(String bioCode, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        RestApiAdapter.exec(bioCode, request, appService);
    }

    protected void _exec(String bioCode, Object params, User user) throws Exception {
        BioAppService appService = getAppService();
        RestApiAdapter.exec(bioCode, params, appService, user);
    }

    protected <T> T _selectScalar(final String bioCode, final HttpServletRequest request, final Class<T> clazz, final T defaultValue) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.selectScalar(bioCode, request, appService, clazz, defaultValue, ((BioWrappedRequest) request).getUser());
    }

    protected List<ABean> _save(String bioCode, List<ABean> abeans, HttpServletRequest request) throws Exception {
        BioAppService appService = getAppService();
        return RestApiAdapter.saveBeans(bioCode, request, appService, abeans);
    }

}
