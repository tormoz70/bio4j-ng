package ru.bio4j.ng.service.types;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ru.bio4j.ng.database.api.SQLActionScalar0;
import ru.bio4j.ng.database.api.SQLActionScalar1;
import ru.bio4j.ng.database.api.SQLActionVoid0;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.FCloudApi;
import ru.bio4j.ng.service.api.SecurityService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RestHelperMethods {
    AppService getAppService();
    SecurityService getSecurityService();
    FCloudApi getFCloudApi();

    ABeanPage getList(String bioCode, HttpServletRequest request, boolean forceAll) throws Exception;
    ABeanPage getList(String bioCode, HttpServletRequest request) throws Exception;
    HSSFWorkbook getExcel(String bioCode, HttpServletRequest request) throws Exception;
    ABeanPage getList(String bioCode, Object prms, User user, boolean forceAll, FilterAndSorter filterAndSorter, boolean forceCalcCount) throws Exception;
    ABeanPage getList(String bioCode, Object prms, User user, boolean forceAll) throws Exception;
    <T> List<T> getList(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception;
    <T> List<T> getList(String bioCode, HttpServletRequest request, Class<T> calzz, boolean forceAll) throws Exception;
    <T> List<T> getList(String bioCode, Object prms, Class<T> calzz, boolean forceAll) throws Exception;
    <T> List<T> getList(String bioCode, Object prms, Class<T> calzz) throws Exception;
    <T> T getFirst(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception;
    <T> T getFirst(String bioCode, Object prms, Class<T> calzz) throws Exception;
    ABean getFirst(String bioCode, HttpServletRequest request) throws Exception;
    <T> T getScalar(final String bioCode, final HttpServletRequest request, final Class<T> calzz, final T defaultValue) throws Exception;
    <T> T getScalar(final String bioCode, final Object params, Class<T> calzz, T defaultValue, User user) throws Exception;
    String getJson(String bioCode, HttpServletRequest request) throws Exception;
    ABean getTotalCount(String bioCode, HttpServletRequest request) throws Exception;
    StoreMetadata getMetadataOld(String bioCode) throws Exception;
    ABean getMetadata(String bioCode) throws Exception;
    ABean getSingle(String bioCode, Object id, HttpServletRequest request) throws Exception;
    ABean getSuccess();
    List<ABean> exctarctBean(HttpServletRequest request) throws Exception;
    <T> T exctarctBean(HttpServletRequest request, Class<T> clazz) throws Exception;
    ABean setBean(ABean bean, String attrName, Object attrValue, Class<?> attrType) throws Exception;
    List<Param> setBeanToBioParams(ABean bean, List<Param> params) throws Exception;
    <T> List<T> parsIdsTyped(String ids, Class<T> clazz) throws Exception;
    List<Object> parsIds(String ids, Class<?> clazz) throws Exception;
    void setBioParamToRequest(String paramName, Object paramValue, HttpServletRequest request) throws Exception;
    void setSorterToRequest(String fieldName, Sort.Direction direction, HttpServletRequest request) throws Exception;
    void setBeanToRequest(ABean bean, HttpServletRequest request) throws Exception;
    <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType, T defaultValue) throws Exception;
    <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType) throws Exception;
    ABean delete(String bioCode, List<Object> ids, HttpServletRequest request) throws Exception;
    void exec(String bioCode, HttpServletRequest request) throws Exception;
    void exec(String bioCode, Object params, User user) throws Exception;
    <T> T selectScalar(final String bioCode, final HttpServletRequest request, final Class<T> clazz, final T defaultValue) throws Exception;
    List<ABean> save(String bioCode, List<ABean> abeans, HttpServletRequest request) throws Exception;
    <T> List<T> getList0(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, boolean forceAll, Class<T> calzz) throws Exception;
    void execBatch(final SQLActionVoid0 action, final User user) throws Exception;
    <T> T execBatch(final SQLActionScalar0<T> action, final User user) throws Exception;
    <P, T> T execBatch(final SQLActionScalar1<P, T> action, P param, final User user) throws Exception;
}