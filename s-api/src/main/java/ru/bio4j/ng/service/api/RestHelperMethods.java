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

    ABeanPage getList(String bioCode, HttpServletRequest request) throws Exception;
    ABeanPage getListAll(String bioCode, HttpServletRequest request) throws Exception;
    HSSFWorkbook getExcel(String bioCode, HttpServletRequest request) throws Exception;
    ABeanPage getList(String bioCode, Object prms, FilterAndSorter filterAndSorter, boolean forceCalcCount) throws Exception;
    ABeanPage getListAll(String bioCode, Object prms, FilterAndSorter filterAndSorter, boolean forceCalcCount) throws Exception;
    ABeanPage getList(String bioCode, Object prms) throws Exception;
    ABeanPage getListAll(String bioCode, Object prms) throws Exception;
    <T> List<T> getList(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception;
    <T> List<T> getListAll(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception;
    <T> List<T> getList(String bioCode, Object prms, Class<T> calzz) throws Exception;
    <T> List<T> getListAll(String bioCode, Object prms, Class<T> calzz) throws Exception;
    <T> T getFirst(String bioCode, HttpServletRequest request, Class<T> calzz) throws Exception;
    <T> T getFirst(String bioCode, Object prms, Class<T> calzz) throws Exception;
    ABean getFirst(String bioCode, HttpServletRequest request) throws Exception;
    <T> T getScalar(final String bioCode, final HttpServletRequest request, final Class<T> calzz, final T defaultValue) throws Exception;
    <T> T getScalar(final String bioCode, final Object params, Class<T> calzz, T defaultValue) throws Exception;
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
    boolean bioParamExistsInRequest(String paramName, HttpServletRequest request) throws Exception;
    ABean delete(String bioCode, List<Object> ids, HttpServletRequest request) throws Exception;
    void exec(String bioCode, HttpServletRequest request) throws Exception;
    void exec(String bioCode, Object params) throws Exception;
    void execLocal(SQLContext context, SQLDefinition sqlDefinition, Object params) throws Exception;
    <T> T selectScalar(final String bioCode, final HttpServletRequest request, final Class<T> clazz, final T defaultValue) throws Exception;
    List<ABean> save(String bioCode, List<ABean> abeans, HttpServletRequest request) throws Exception;
    <T> List<T> getList0(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, Class<T> calzz) throws Exception;
    <T> List<T> getList0All(SQLContext context, String bioCode, FilterAndSorter filterAndSorter, Object prms, Class<T> calzz) throws Exception;
    void execBatch(final SQLActionVoid0 action) throws Exception;
    <T> T execBatch(final SQLActionScalar0<T> action) throws Exception;
    <P, T> T execBatch(final SQLActionScalar1<P, T> action, P param) throws Exception;
}
