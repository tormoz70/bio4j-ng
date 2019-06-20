package ru.bio4j.ng.service.types;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLActionScalar0;
import ru.bio4j.ng.database.api.SQLActionScalar1;
import ru.bio4j.ng.database.api.SQLActionVoid0;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.service.api.SQLDefinition;
import ru.bio4j.ng.service.api.SecurityService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static ru.bio4j.ng.service.types.ServletContextHolder.getServletContext;

public class DefaultRestHelperMethods implements RestHelperMethods {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRestHelperMethods.class);

    private AppService appService;
    public AppService getAppService() {
        synchronized (this) {
            if(appService == null)
                appService = Utl.getService(getServletContext(), RestHelper.getAppTypes().getAppServiceClass());
        }
        return appService;
    }

    private SecurityService securityService;
    public SecurityService getSecurityService() {
        synchronized (this) {
            if (securityService == null)
                securityService = Utl.getService(getServletContext(), RestHelper.getAppTypes().getSecurityServiceClass());
        }
        return securityService;
    }

    private FCloudApi fCloudApi;
    public FCloudApi getFCloudApi() {
        synchronized (this) {
            if (fCloudApi == null)
                fCloudApi = Utl.getService(getServletContext(), RestHelper.getAppTypes().getFCloudApiClass());
        }
        return fCloudApi;
    }

    public ABeanPage getList(String bioCode, HttpServletRequest request, boolean forceAll) throws Exception {
        AppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, appService, forceAll);
        rslt.setMetadata(null);
        return rslt;
    }

    public ABeanPage getList(String bioCode, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, appService);
        rslt.setMetadata(null);
        return rslt;
    }

    public HSSFWorkbook getExcel(String bioCode, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        HSSFWorkbook rslt = RestApiAdapter.loadToExcel(bioCode, request, appService);
        return rslt;
    }

    public ABeanPage getList(String bioCode, Object prms, User user, boolean forceAll, FilterAndSorter filterAndSorter, boolean forceCalcCount) throws Exception {
        AppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(appService, bioCode, prms, user, forceAll, filterAndSorter, forceCalcCount);
        rslt.setMetadata(null);
        return rslt;
    }


    public ABeanPage getList(String bioCode, Object prms, User user, boolean forceAll) throws Exception {
        AppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(appService, bioCode, prms, user, forceAll);
        rslt.setMetadata(null);
        return rslt;
    }

    public <T> List<T> getList(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        AppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, appService, calzz);
        return rslt;
    }

    public <T> List<T> getList(String bioCode, HttpServletRequest request, Class<T> calzz, boolean forceAll) throws Exception {
        AppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, appService, calzz, forceAll);
        return rslt;
    }

    public <T> List<T> getList(String bioCode, Object prms, Class<T> calzz, boolean forceAll) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.loadPageExt(appService, bioCode, prms, calzz, forceAll);
    }

    public <T> List<T> getList(String bioCode, Object prms, Class<T> calzz) throws Exception {
        return getList(bioCode, prms, calzz, false);
    }

    public <T> T getFirst(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception {
        AppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(bioCode, request, appService, calzz, true);
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    public <T> T getFirst(String bioCode, Object prms, Class<T> calzz) throws Exception {
        AppService appService = getAppService();
        List<T> rslt = RestApiAdapter.loadPageExt(appService, bioCode, prms, calzz, true);
        return rslt.size() > 0 ? rslt.get(0) : null;
    }

    public ABean getFirst(String bioCode, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        ABeanPage rslt = RestApiAdapter.loadPage(bioCode, request, appService);
        return rslt.getRows().size() > 0 ? rslt.getRows().get(0) : null;
    }

    public <T> T getScalar(final String bioCode, final HttpServletRequest request, final Class<T> calzz, final T defaultValue) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.selectScalar(bioCode, request, appService, calzz, defaultValue);
    }

    public <T> T getScalar(final String bioCode, final Object params, Class<T> calzz, T defaultValue, User user) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.selectScalar(bioCode, params, appService, calzz, defaultValue, user);
    }

    public String getJson(String bioCode, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        StringBuilder rslt = RestApiAdapter.loadJson(bioCode, request, appService);
        return rslt.toString();
    }

    public ABean getTotalCount(String bioCode, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        ABean rslt = RestApiAdapter.calcTotalCount(bioCode, request, appService);
        return rslt;
    }

    public StoreMetadata getMetadataOld(String bioCode) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.getMetadataOld(bioCode, appService);
    }

    public ABean getMetadata(String bioCode) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.getMetadata(bioCode, appService);
    }

    public ABean getSingle(String bioCode, Object id, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.loadBean(bioCode, request, appService, id);
    }

    public ABean getSuccess() {
        ABean rslt = new ABean();
        rslt.put("success", true);
        return rslt;
    }

    public List<ABean> exctarctBean(HttpServletRequest request) throws Exception {
        List<ABean> abeans = null;
        String abeanJson = null;
        try {
            abeanJson = ((WrappedRequest)request).getBioQueryParams().jsonData;
            abeans = Jsons.decodeABeans(abeanJson);
        } catch (Exception e) {
            throw new Exception(String.format("Cannot decode json to bean: %s", abeanJson));
        }
        return abeans;
    }

    public <T> T exctarctBean(HttpServletRequest request, Class<T> clazz) throws Exception {
        T bean = null;
        String abeanJson = null;
        try {
            abeanJson = ((WrappedRequest)request).getBioQueryParams().jsonData;
            bean = Jsons.decode(abeanJson, clazz);
        } catch (Exception e) {
            throw new Exception(String.format("Cannot decode json to bean(%s): %s", clazz.getCanonicalName(), abeanJson));
        }
        return bean;
    }

    public ABean setBean(ABean bean, String attrName, Object attrValue, Class<?> attrType) throws Exception {
        if(bean == null)
            bean = new ABean();
        if(bean.containsKey(attrName))
            bean.replace(attrName, Converter.toType(attrValue, attrType));
        else
            bean.put(attrName, Converter.toType(attrValue, attrType));
        return bean;
    }

    public List<Param> setBeanToBioParams(ABean bean, List<Param> params) throws Exception {
        if(params == null)
            params = new ArrayList<>();
        if(bean == null)
            return params;
        for(String key : bean.keySet())
            Paramus.setParamValue(params, key, bean.get(key));
        return params;
    }

    public <T> List<T> parsIdsTyped(String ids, Class<T> clazz) throws Exception {
        List<T> rslt = new ArrayList<>();
        String[] idsArrayOrig = Strings.split(ids, ",", ";", " ");
        for (String id : idsArrayOrig) {
            rslt.add(Converter.toType(id, clazz));
        }
        return rslt;
    }

    public List<Object> parsIds(String ids, Class<?> clazz) throws Exception {
        List<Object> rslt = new ArrayList<>();
        String[] idsArrayOrig = Strings.split(ids, ",", ";", " ");
        for (String id : idsArrayOrig) {
            rslt.add(Converter.toType(id, clazz));
        }
        return rslt;
    }

    public void setBioParamToRequest(String paramName, Object paramValue, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        Paramus.setParamValue(queryParams.bioParams, paramName, paramValue);
    }

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

    public void setBeanToRequest(ABean bean, HttpServletRequest request) throws Exception {
        final BioQueryParams queryParams = ((WrappedRequest)request).getBioQueryParams();
        setBeanToBioParams(bean, queryParams.bioParams);
    }

    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType, T defaultValue) throws Exception {
        return ((WrappedRequest)request).getBioQueryParam(paramName, paramType, defaultValue);
    }

    public <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType) throws Exception {
        return  getBioParamFromRequest(paramName, request, paramType, null);
    }

    public ABean delete(String bioCode, List<Object> ids, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.deleteBeans(bioCode, request, appService, ids);
    }

    public void exec(String bioCode, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        RestApiAdapter.exec(bioCode, request, appService);
    }

    public void exec(String bioCode, Object params, User user) throws Exception {
        AppService appService = getAppService();
        RestApiAdapter.exec(bioCode, params, appService, user);
    }

    public <T> T selectScalar(final String bioCode, final HttpServletRequest request, final Class<T> clazz, final T defaultValue) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.selectScalar(bioCode, request, appService, clazz, defaultValue, ((WrappedRequest) request).getUser());
    }

    public List<ABean> save(String bioCode, List<ABean> abeans, HttpServletRequest request) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.saveBeans(bioCode, request, appService, abeans);
    }

    public <T> List<T> getList0(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, boolean forceAll, Class<T> calzz) throws Exception {
        AppService appService = getAppService();
        SQLDefinition sqldef = appService.getSQLDefinition(bioCode);
        return RestApiAdapter.loadPage0Ext(sqldef, context, prms, forceAll, calzz, filterAndSorter);
    }

    public void execBatch(final SQLActionVoid0 action, final User user) throws Exception {
        AppService appService = getAppService();
        RestApiAdapter.execBatch(appService.getSQLContext(), action, user);
    }

    public <T> T execBatch(final SQLActionScalar0<T> action, final User user) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.execBatch(appService.getSQLContext(), action, user);
    }

    public <P, T> T execBatch(final SQLActionScalar1<P, T> action, P param, final User user) throws Exception {
        AppService appService = getAppService();
        return RestApiAdapter.execBatch(appService.getSQLContext(), action, param, user);
    }

}
