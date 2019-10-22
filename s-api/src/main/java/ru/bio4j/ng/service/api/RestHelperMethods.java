package ru.bio4j.ng.service.api;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ru.bio4j.ng.database.api.SQLActionScalar0;
import ru.bio4j.ng.database.api.SQLActionScalar1;
import ru.bio4j.ng.database.api.SQLActionVoid0;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.database.api.SQLDefinition;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RestHelperMethods {
    OdacService getOdacService();
    SecurityService getSecurityService();
    FCloudApi getFCloudApi();
    CacheService getCacheService();

    ABeanPage getList(String bioCode, HttpServletRequest request);
    ABeanPage getListAll(String bioCode, HttpServletRequest request);
    HSSFWorkbook getExcel(String bioCode, HttpServletRequest request);
    ABeanPage getList(String bioCode, Object prms, FilterAndSorter filterAndSorter, boolean forceCalcCount);
    ABeanPage getListAll(String bioCode, Object prms, FilterAndSorter filterAndSorter, boolean forceCalcCount);
    ABeanPage getList(String bioCode, Object prms);
    ABeanPage getListAll(String bioCode, Object prms);
    <T> List<T> getList(String bioCode, HttpServletRequest request, Class<T> calzz);
    <T> List<T> getListAll(String bioCode, HttpServletRequest request, Class<T> calzz);
    <T> List<T> getList(String bioCode, Object prms, Class<T> calzz);
    <T> List<T> getListAll(String bioCode, Object prms, Class<T> calzz);
    <T> T getFirst(String bioCode, HttpServletRequest request, Class<T> calzz);
    <T> T getFirst(String bioCode, Object prms, Class<T> calzz);
    ABean getFirst(String bioCode, HttpServletRequest request);
    <T> T getScalar(final String bioCode, final HttpServletRequest request, final Class<T> calzz, final T defaultValue);
    <T> T getScalar(final String bioCode, final Object params, Class<T> calzz, T defaultValue);
    String getJson(String bioCode, HttpServletRequest request);
    ABean getTotalCount(String bioCode, HttpServletRequest request);
    StoreMetadata getMetadataOld(String bioCode);
    ABean getMetadata(String bioCode);
    ABean getSingle(String bioCode, Object id, HttpServletRequest request);
    ABean getSuccess();
    List<ABean> exctarctBean(HttpServletRequest request);
    <T> T exctarctBean(HttpServletRequest request, Class<T> clazz);
    ABean setBean(ABean bean, String attrName, Object attrValue, Class<?> attrType);
    List<Param> setBeanToBioParams(ABean bean, List<Param> params);
    <T> List<T> parsIdsTyped(String ids, Class<T> clazz);
    List<Object> parsIds(String ids, Class<?> clazz);
    void setBioParamToRequest(String paramName, Object paramValue, HttpServletRequest request);
    void setSorterToRequest(String fieldName, Sort.Direction direction, HttpServletRequest request);
    void setBeanToRequest(ABean bean, HttpServletRequest request);
    <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType, T defaultValue);
    <T> T getBioParamFromRequest(String paramName, HttpServletRequest request, Class<T> paramType);
    boolean bioParamExistsInRequest(String paramName, HttpServletRequest request);
    ABean delete(String bioCode, List<Object> ids, HttpServletRequest request);
    void exec(String bioCode, HttpServletRequest request);
    void exec(String bioCode, Object params);
    void execLocal(SQLContext context, SQLDefinition sqlDefinition, Object params);
    <T> T selectScalar(final String bioCode, final HttpServletRequest request, final Class<T> clazz, final T defaultValue);
    List<ABean> save(String bioCode, List<ABean> abeans, HttpServletRequest request);
    <T> List<T> getList0(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, Class<T> calzz);
    <T> List<T> getList0All(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, Class<T> calzz);
    void execBatch(final SQLActionVoid0 action);
    <T> T execBatch(final SQLActionScalar0<T> action);
    <P, T> T execBatch(final SQLActionScalar1<P, T> action, P param);
}
