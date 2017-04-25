package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.service.api.SrvcUtils;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class BioWrappedRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> modParameters;
    private final HashMap<String, String> modHeaders;

    private SrvcUtils.BioQueryParams bioQueryParams;

    public SrvcUtils.BioQueryParams getBioQueryParams() {
        return bioQueryParams;
    }

    public static class LoginParamObj {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }

    public static class SortAndFilterObj {
        private List<Sort> sort;
        private Filter filter;

        public List<Sort> getSort() {
            return sort;
        }

        public void setSort(List<Sort> sort) {
            this.sort = sort;
        }

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }
    }

    private static final String[] ACS_SYS_PAR_NAMES = {"_dc"};
    private static List<String> extractSysParamNames() {
        List<String> rslt = new ArrayList<>();
        for(java.lang.reflect.Field fld : Utl.getAllObjectFields(SrvcUtils.BioQueryParams.class)) {
            String fldName = fld.getName();
            Prop p = Utl.findAnnotation(Prop.class, fld);
            if(p != null) {
                fldName = p.name();
                rslt.add(fldName);
            }
        }
        for (String s : ACS_SYS_PAR_NAMES)
            rslt.add(s);
        return rslt;
    }

    private static void extractBioParamsFromQuery(SrvcUtils.BioQueryParams qparams) {
        List<String> sysParamNames = extractSysParamNames();
        qparams.bioParams = new ArrayList<>();
        Enumeration<String> paramNames = qparams.request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String paramName = paramNames.nextElement();
            String val = qparams.request.getParameter(paramName);
            if(sysParamNames.indexOf(paramName) == -1){
                qparams.bioParams.add(Param.builder().name(paramName).type(MetaType.STRING).direction(Param.Direction.IN).value(val).build());
            }
        }
//        if(!Strings.isNullOrEmpty(qparams.jsonData)) {
//            BioParamObj bioParamObj = null;
//            try {
//                bioParamObj = Jsons.decode(qparams.jsonData, BioParamObj.class);
//            } catch (Exception e) {
//            }
//            if (bioParamObj != null && bioParamObj.getBioParams() != null)
//                qparams.bioParams = Paramus.set(qparams.bioParams).merge(bioParamObj.getBioParams(), true).pop();
//        }

    }

    public static SrvcUtils.BioQueryParams decodeBioQueryParams(HttpServletRequest request) throws Exception {
        SrvcUtils.BioQueryParams result = Httpc.createBeanFromHttpRequest(request, SrvcUtils.BioQueryParams.class);
        result.request = request;
        result.method = request.getMethod();
        result.remoteIP = Httpc.extractRealRemoteAddr(request);
        result.remoteClient = Httpc.extractRealRemoteClient(request);

        if(result.method == "POST"){
            String usrname = request.getParameter("usrname");
            String passwd = request.getParameter("passwd");
            if(!Strings.isNullOrEmpty(usrname) && !Strings.isNullOrEmpty(passwd)) {
                result.login = usrname+"/"+passwd;
            }
        }

        if(Strings.isNullOrEmpty(result.login) && !Strings.isNullOrEmpty(result.jsonData)) {
            LoginParamObj obj = null;
            try {
                obj = Jsons.decode(result.jsonData, LoginParamObj.class);
            } catch (Exception e) {
            }
            if (obj != null && !Strings.isNullOrEmpty(obj.getLogin()))
                result.login = obj.getLogin();
        }

        result.fcloudCmd = FCloudCommand.decode(result.fcloudCmdOrig);
        result.rmtCommand = RmtCommand.decode(result.rmtCommandOrig);

        if((result.sort == null || result.filter == null) && !Strings.isNullOrEmpty(result.jsonData)) {
            SortAndFilterObj obj = null;
            try {
                obj = Jsons.decode(result.jsonData, SortAndFilterObj.class);
            } catch (Exception e) {
            }
            if (obj != null && result.sort == null)
                result.sort = obj.getSort();
            if (obj != null && result.filter == null)
                result.filter = obj.getFilter();
        }

        result.page = Converter.toType(result.pageOrig, Integer.class);
        result.offset = Converter.toType(result.offsetOrig, Integer.class);
        result.pageSize = Converter.toType(result.pageSizeOrig, Integer.class);

        extractBioParamsFromQuery(result);

        return result;
    }

    public BioWrappedRequest(final HttpServletRequest request) throws Exception {
        super(request);
        modParameters = new TreeMap<>();
        modHeaders = new HashMap();
        bioQueryParams = decodeBioQueryParams(request);
    }

    public void appendParams(final Map<String, String[]> params) {
        if(params != null)
            modParameters.putAll(params);
    }

    public void putHeader(String name, String value){
        this.modHeaders.put(name, value);
    }

    @Override
    public String getParameter(final String name) {
        String[] strings = getParameterMap().get(name);
        if (strings != null)
            return strings[0];
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        //Return an unmodifiable collection because we need to uphold the interface contract.
        return Collections.unmodifiableMap(modParameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(final String name) {
        return getParameterMap().get(name);
    }


    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (modHeaders.containsKey(name)) {
            headerValue = modHeaders.get(name);
        }
        return headerValue;
    }

    /**
     * get the Header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        for (String name : modHeaders.keySet()) {
            names.add(name);
        }
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (modHeaders.containsKey(name)) {
            values.add(modHeaders.get(name));
        }
        return Collections.enumeration(values);
    }


}
