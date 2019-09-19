package ru.bio4j.ng.service.types;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
//import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.database.api.SQLActionScalar0;
import ru.bio4j.ng.database.api.SQLActionScalar1;
import ru.bio4j.ng.database.api.SQLActionVoid0;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.database.api.SQLDefinition;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


public class DefaultRestHelperMethods implements RestHelperMethods {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRestHelperMethods.class);

    private CacheService cacheService;
    public CacheService getCacheService() {
        synchronized (this) {
            if(cacheService == null)
                cacheService = Bundles4WAR.findBundleByInterface(RestHelper.getAppTypes().getCacheServiceClass());
        }
        return cacheService;
    }

    private OdacService odacService;
    public OdacService getOdacService() {
        synchronized (this) {
            if(odacService == null)
                odacService = Bundles4WAR.findBundleByInterface(RestHelper.getAppTypes().getOdacServiceClass());
        }
        return odacService;
    }

    private SecurityService securityService;
    public SecurityService getSecurityService() {
        synchronized (this) {
            if (securityService == null)
                securityService = Bundles4WAR.findBundleByInterface(RestHelper.getAppTypes().getSecurityServiceClass());
        }
        return securityService;
    }

    private FCloudApi fCloudApi;
    public FCloudApi getFCloudApi() {
        synchronized (this) {
            if (fCloudApi == null)
                fCloudApi = Utl.getService(ServletContextHolder.getServletContext(), RestHelper.getAppTypes().getFCloudApiClass());
        }
        return fCloudApi;
    }

    @Override
    public ABeanPage getListAll(String bioCode, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        ABeanPage rslt = RestApiAdapter.loadAll(bioCode, request, odacService);
        rslt.setMetadata(null);
        return rslt;
    }

    @Override
    public ABeanPage getList(String bioCode, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, odacService);
        rslt.setMetadata(null);
        return rslt;
    }

    @Override
    public HSSFWorkbook getExcel(String bioCode, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        HSSFWorkbook rslt = RestApiAdapter.loadToExcel(bioCode, request, odacService);
        return rslt;
    }

    @Override
    public ABeanPage getList(String bioCode, Object prms, FilterAndSorter filterAndSorter, boolean forceCalcCount) throws Exception {
        OdacService odacService = getOdacService();
        ABeanPage rslt = RestApiAdapter.loadPage(odacService, bioCode, prms, ServletContextHolder.getCurrentUser(), filterAndSorter, forceCalcCount);
        rslt.setMetadata(null);
        return rslt;
    }

    @Override
    public ABeanPage getListAll(String bioCode, Object prms, FilterAndSorter filterAndSorter, boolean forceCalcCount) throws Exception {
        OdacService odacService = getOdacService();
        ABeanPage rslt = RestApiAdapter.loadAll(odacService, bioCode, prms, ServletContextHolder.getCurrentUser(), filterAndSorter, forceCalcCount);
        rslt.setMetadata(null);
        return rslt;
    }

    @Override
    public ABeanPage getList(String bioCode, Object prms) throws Exception {
        OdacService odacService = getOdacService();
        ABeanPage rslt = RestApiAdapter.loadPage(odacService, bioCode, prms, ServletContextHolder.getCurrentUser());
        rslt.setMetadata(null);
        return rslt;
    }
    @Override
    public ABeanPage getListAll(String bioCode, Object prms) throws Exception {
        OdacService odacService = getOdacService();
        ABeanPage rslt = RestApiAdapter.loadAll(odacService, bioCode, prms, ServletContextHolder.getCurrentUser());
        rslt.setMetadata(null);
        return rslt;
    }

    @Override
    public <T> List<T> getList(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, odacService, calzz);
        return rslt;
    }
    @Override
    public <T> List<T> getListAll(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        List<T> rslt = RestApiAdapter.loadAllExt(bioCode, request, odacService, calzz);
        return rslt;
    }

    @Override
    public <T> List<T> getList(String bioCode, Object prms, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.loadPageExt(odacService, bioCode, prms, calzz);
    }


    @Override
    public <T> List<T> getListAll(String bioCode, Object prms, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.loadAllExt(odacService, bioCode, prms, calzz);
    }

    @Override
    public <T> T getFirst(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        List<T> rslt = RestApiAdapter.loadAllExt(bioCode, request, odacService, calzz);
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    @Override
    public <T> T getFirst(String bioCode, Object prms, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        List<T> rslt = RestApiAdapter.loadAllExt(odacService, bioCode, prms, calzz);
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    @Override
    public ABean getFirst(String bioCode, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, odacService);
        return rslt.getRows().size() > 0 ? rslt.getRows().get(0) : null;
    }

    @Override
    public <T> T getScalar(final String bioCode, final HttpServletRequest request, final Class<T> calzz, final T defaultValue) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.selectScalar(bioCode, request, odacService, calzz, defaultValue);
    }

    @Override
    public <T> T getScalar(final String bioCode, final Object params, Class<T> calzz, T defaultValue) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.selectScalar(bioCode, params, odacService, calzz, defaultValue, ServletContextHolder.getCurrentUser());
    }

    @Override
    public String getJson(String bioCode, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        StringBuilder rslt = RestApiAdapter.loadJson(bioCode, request, odacService);
        return rslt.toString();
    }

    @Override
    public ABean getTotalCount(String bioCode, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        ABean rslt = RestApiAdapter.calcTotalCount(bioCode, request, odacService);
        return rslt;
    }

    @Override
    public StoreMetadata getMetadataOld(String bioCode) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.getMetadataOld(bioCode, odacService);
    }

    @Override
    public ABean getMetadata(String bioCode) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.getMetadata(bioCode, odacService);
    }

    @Override
    public ABean getSingle(String bioCode, Object id, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.loadBean(bioCode, request, odacService, id);
    }

    @Override
    public ABean getSuccess() {
        ABean rslt = new ABean();
        rslt.put("success", true);
        return rslt;
    }

    @Override
    public List<ABean> exctarctBean(HttpServletRequest request) throws Exception {
        List<ABean> abeans = null;
        String abeanJson = null;
        try {
            abeanJson = ((WrappedRequest)request).getBioQueryParams().jsonData;
            abeans = Jecksons.getInstance().decodeABeans(abeanJson);
        } catch (Exception e) {
            throw new Exception(String.format("Cannot decode json to bean: %s", abeanJson));
        }
        return abeans;
    }

    @Override
    public <T> T exctarctBean(HttpServletRequest request, Class<T> clazz) throws Exception {
        T bean = null;
        String abeanJson = null;
        try {
            abeanJson = ((WrappedRequest)request).getBioQueryParams().jsonData;
            bean = Jecksons.getInstance().decode(abeanJson, clazz);
        } catch (Exception e) {
            throw new Exception(String.format("Cannot decode json to bean(%s): %s", clazz.getCanonicalName(), abeanJson));
        }
        return bean;
    }

    @Override
    public ABean setBean(ABean bean, String attrName, Object attrValue, Class<?> attrType) throws Exception {
        if(bean == null)
            bean = new ABean();
        if(bean.containsKey(attrName))
            bean.replace(attrName, Converter.toType(attrValue, attrType));
        else
            bean.put(attrName, Converter.toType(attrValue, attrType));
        return bean;
    }

    @Override
    public List<Param> setBeanToBioParams(ABean bean, List<Param> params) throws Exception {
        if(params == null)
            params = new ArrayList<>();
        if(bean == null)
            return params;
        for(String key : bean.keySet())
            Paramus.setParamValue(params, key, bean.get(key));
        return params;
    }

    @Override
    public <T> List<T> parsIdsTyped(String ids, Class<T> clazz) throws Exception {
        List<T> rslt = new ArrayList<>();
        String[] idsArrayOrig = Strings.split(ids, ",", ";", " ");
        for (String id : idsArrayOrig) {
            rslt.add(Converter.toType(id, clazz));
        }
        return rslt;
    }

    @Override
    public List<Object> parsIds(String ids, Class<?> clazz) throws Exception {
        List<Object> rslt = new ArrayList<>();
        String[] idsArrayOrig = Strings.split(ids, ",", ";", " ");
        for (String id : idsArrayOrig) {
            rslt.add(Converter.toType(id, clazz));
        }
        return rslt;
    }

    @Override
    public void setBioParamToRequest(String paramName, Object paramValue, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        Paramus.setParamValue(queryParams.bioParams, paramName, paramValue);
    }

    @Override
    public void setSorterToRequest(String fieldName, Sort.Direction direction, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
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

    @Override
    public void setBeanToRequest(ABean bean, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        setBeanToBioParams(bean, queryParams.bioParams);
    }

    @Override
    public boolean bioParamExistsInRequest(String paramName, HttpServletRequest request) throws Exception {
        return ((WrappedRequest)request).bioQueryParamExists(paramName);
    }

    @Override
    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType, T defaultValue) throws Exception {
        return ((WrappedRequest)request).getBioQueryParam(paramName, paramType, defaultValue);
    }

    @Override
    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType) throws Exception {
        return  getBioParamFromRequest(paramName, request, paramType, null);
    }

    @Override
    public ABean delete(String bioCode, List<Object> ids, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.deleteBeans(bioCode, request, odacService, ids);
    }

    @Override
    public void exec(String bioCode, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        RestApiAdapter.exec(bioCode, request, odacService);
    }

    @Override
    public void exec(String bioCode, Object params) throws Exception {
        OdacService odacService = getOdacService();
        RestApiAdapter.exec(bioCode, params, odacService, ServletContextHolder.getCurrentUser());
    }

    @Override
    public void execLocal(SQLContext context, SQLDefinition sqlDefinition, Object params) throws Exception {
        OdacService odacService = getOdacService();
        RestApiAdapter.execLocal(sqlDefinition, params, context);
    }

    @Override
    public <T> T selectScalar(final String bioCode, final HttpServletRequest request, final Class<T> clazz, final T defaultValue) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.selectScalar(bioCode, request, odacService, clazz, defaultValue, ((WrappedRequest) request).getUser());
    }

    @Override
    public List<ABean> save(String bioCode, List<ABean> abeans, HttpServletRequest request) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.saveBeans(bioCode, request, odacService, abeans);
    }

    @Override
    public <T> List<T> getList0(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        SQLDefinition sqldef = odacService.getSQLDefinition(bioCode);
        return RestApiAdapter.loadPage0Ext(sqldef, context, prms, calzz, filterAndSorter);
    }

    @Override
    public <T> List<T> getList0All(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, Class<T> calzz) throws Exception {
        OdacService odacService = getOdacService();
        SQLDefinition sqldef = odacService.getSQLDefinition(bioCode);
        return RestApiAdapter.loadAll0Ext(sqldef, context, prms, calzz, filterAndSorter);
    }
    @Override
    public void execBatch(final SQLActionVoid0 action) throws Exception {
        OdacService odacService = getOdacService();
        RestApiAdapter.execBatch(odacService.getSQLContext(), action, ServletContextHolder.getCurrentUser());
    }

    @Override
    public <T> T execBatch(final SQLActionScalar0<T> action) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.execBatch(odacService.getSQLContext(), action, ServletContextHolder.getCurrentUser());
    }

    @Override
    public <P, T> T execBatch(final SQLActionScalar1<P, T> action, P param) throws Exception {
        OdacService odacService = getOdacService();
        return RestApiAdapter.execBatch(odacService.getSQLContext(), action, param, ServletContextHolder.getCurrentUser());
    }

}
