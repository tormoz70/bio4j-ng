package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.Prop;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.service.api.AppService;
import ru.bio4j.ng.service.api.HttpParamMap;
import ru.bio4j.ng.service.api.SecurityService;
import ru.bio4j.ng.model.transport.RestParamNames;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WrappedRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> modParameters;
    private final HashMap<String, String> modHeaders;

    private BioQueryParams bioQueryParams;

    public BioQueryParams getBioQueryParams() {
        return bioQueryParams;
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
        for(java.lang.reflect.Field fld : Utl.getAllObjectFields(BioQueryParams.class)) {
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

    private static void extractBioParamsFromQuery(BioQueryParams qparams) throws Exception {
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
        if(!Strings.isNullOrEmpty(qparams.jsonData)) {
            List<Param> bioParams = Utl.anjsonToParams(qparams.jsonData);
            if (bioParams != null && bioParams.size() > 0) {
                qparams.bioParams = Paramus.set(qparams.bioParams).merge(bioParams, true).pop();
            }
        }

    }

    private static class BasicAutenticationLogin {
        public String username;
        public String password;
    }

    private static BasicAutenticationLogin detectBasicAutentication(HttpServletRequest request) {
        BasicAutenticationLogin rslt = new BasicAutenticationLogin();

        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            rslt.username = values[0];
            rslt.password = values[1];
        }
        return rslt;
    }
    private static void setQueryParamsToBioParams(BioQueryParams qprms) throws Exception {
        if(qprms.bioParams == null)
            qprms.bioParams = new ArrayList<>();
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_PAGE, qprms.page, MetaType.INTEGER);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_PAGESIZE, qprms.pageSize, MetaType.INTEGER);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_OFFSET, qprms.offset, MetaType.INTEGER);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, qprms.totalCount);
//        Paramus.setParamValue(qprms.bioParams, RestParamNames.GETROW_PARAM_PKVAL, qprms.id);
//        Paramus.setParamValue(qprms.bioParams, RestParamNames.RAPI_PARAM_FILEHASHCODE, qprms.fileHashCode);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.QUERY_PARAM_VALUE, qprms.query);
        Object location = qprms.location;
        if (location != null && location instanceof String) {
            if (((String) location).startsWith("1||"))
                location = null;
            if (((String) location).startsWith("0||")) {
                location = Regexs.find((String) location, "(?<=0\\|\\|)(\\w|\\d|-|\\+)+", Pattern.CASE_INSENSITIVE);
            }
            Paramus.setParamValue(qprms.bioParams, RestParamNames.LOCATE_PARAM_PKVAL, location);
        }
        Paramus.setParamValue(qprms.bioParams, RestParamNames.LOCATE_PARAM_STARTFROM, qprms.offset);
    }

    private static boolean isMultypartRequest(HttpServletRequest request){
        String contentType = request.getHeader("Content-Type");
        return !Strings.isNullOrEmpty(contentType) && contentType.startsWith("multipart/form-data");
    }

    private static boolean isUrlencodedFormRequest(HttpServletRequest request){
        String contentType = request.getHeader("Content-Type");
        return !Strings.isNullOrEmpty(contentType) && contentType.startsWith("application/x-www-form-urlencoded");
    }


    public static BioQueryParams decodeBioQueryParams(HttpServletRequest request) throws Exception {
        if(request.getMethod() == "OPTIONS") return null;
        StringBuilder sb = new StringBuilder();

        ServletContext servletContext = request.getServletContext();
        SecurityService securityService = Utl.getService(servletContext, SecurityService.class);
        AppService appService = Utl.getService(servletContext, AppService.class);
        HttpParamMap httpParamMap =  appService.getHttpParamMap();

        String uploadedJson = null;
        if (!isMultypartRequest(request) && !isUrlencodedFormRequest(request)) {
            Httpc.readDataFromRequest(request, sb);
            uploadedJson = sb.toString();
        }

        BioQueryParams result = Httpc.createBeanFromHttpRequest(request, BioQueryParams.class);
        result.jsonData = uploadedJson;

        result.request = request;
        result.method = request.getMethod();
        result.remoteIP = Httpc.extractRealRemoteAddr(request);
        result.remoteClient = Httpc.extractRealRemoteClient(request);

//        if(Strings.isNullOrEmpty(result.moduleKey)) {
//            final String bioHeaderModuleKey = request.getHeader("X-Module");
//            if (!Strings.isNullOrEmpty(bioHeaderModuleKey))
//                result.moduleKey = bioHeaderModuleKey;
//        }
        final String bioHeaderClientName = request.getHeader(httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.clientHeader()) ? httpParamMap.clientHeader() : "X-Client");
        if(!Strings.isNullOrEmpty(bioHeaderClientName)) {
            result.remoteClient = bioHeaderClientName;
        }
        if(Strings.isNullOrEmpty(result.remoteClientVersion)) {
            final String bioHeaderClientVersion = request.getHeader(httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.clientVerHeader()) ? httpParamMap.clientVerHeader() : "X-Client-Ver");
            if (!Strings.isNullOrEmpty(bioHeaderClientVersion))
                result.remoteClientVersion = bioHeaderClientVersion;
        }
        if(Strings.isNullOrEmpty(result.stoken)) {
            final String securityTokenHeader = httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.securityTokenHeader()) ? httpParamMap.securityTokenHeader() : "X-SToken";
            final String bioHeaderSToken = request.getHeader(securityTokenHeader);
            if (!Strings.isNullOrEmpty(bioHeaderSToken))
                result.stoken = bioHeaderSToken;
            final String securityTokenParam = httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.securityToken()) ? httpParamMap.securityToken() : "stoken";
            if(Strings.isNullOrEmpty(result.stoken) && !Strings.isNullOrEmpty(securityTokenParam)){
                result.stoken = request.getParameter(securityTokenParam);
            }

        }
//        if(Strings.isNullOrEmpty(result.stoken)) result.stoken = "anonymouse";

        if(Strings.isNullOrEmpty(result.pageOrig) && httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.page())) {
            result.pageOrig = request.getParameter(httpParamMap.page());
        }
        if(Strings.isNullOrEmpty(result.pageSizeOrig) && httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.pageSize())) {
            result.pageSizeOrig = request.getParameter(httpParamMap.pageSize());
        }
        if(Strings.isNullOrEmpty(result.offsetOrig) && httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.offset())) {
            result.offsetOrig = request.getParameter(httpParamMap.offset());
        }

        if(Strings.isNullOrEmpty(result.pageOrig)) {
            final String bioHeaderPage = request.getHeader(httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.pageHeader()) ? httpParamMap.pageHeader() : "X-Pagination-Page");
            if (!Strings.isNullOrEmpty(bioHeaderPage))
                result.pageOrig = bioHeaderPage;
        }
        if(Strings.isNullOrEmpty(result.offsetOrig)) {
            final String bioHeaderOffset = request.getHeader(httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.offsetHeader()) ? httpParamMap.offsetHeader() : "X-Pagination-Offset");
            if (!Strings.isNullOrEmpty(bioHeaderOffset))
                result.offsetOrig = bioHeaderOffset;
        }
        if(Strings.isNullOrEmpty(result.pageSizeOrig)) {
            final String bioHeaderPageSize = request.getHeader(httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.pageSizeHeader()) ? httpParamMap.pageSizeHeader() : "X-Pagination-Pagesize");
            if (!Strings.isNullOrEmpty(bioHeaderPageSize))
                result.pageSizeOrig = bioHeaderPageSize;
        }

//        if(Strings.isNullOrEmpty(result.pageSizeOrig) && !Strings.isNullOrEmpty(result.limitOrig))
//            result.pageSizeOrig = result.limitOrig;
//        if(Strings.isNullOrEmpty(result.pageSizeOrig) && !Strings.isNullOrEmpty(result.perPageOrig))
//            result.pageSizeOrig = result.perPageOrig;

        String userNameParam = httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.username()) ? httpParamMap.username() : null;
        String passwordParam = httpParamMap != null && !Strings.isNullOrEmpty(httpParamMap.password()) ? httpParamMap.password() : null;

        BasicAutenticationLogin bal = detectBasicAutentication(request);

        if(Strings.isNullOrEmpty(bal.username)) {
            if (result.method == "POST") {
                String usrname = null;
                String passwd = null;
                if(request.getParameterMap().containsKey("usrname"))
                    usrname = request.getParameter("usrname");
                else if(request.getParameterMap().containsKey("login"))
                    usrname = request.getParameter("login");
                if(request.getParameterMap().containsKey("passwd"))
                    passwd = request.getParameter("passwd");
                else if(request.getParameterMap().containsKey("password"))
                    passwd = request.getParameter("password");
                if (!Strings.isNullOrEmpty(usrname) && !Strings.isNullOrEmpty(passwd)) {
                    result.login = usrname + "/" + passwd;
                }
                if(Strings.isNullOrEmpty(result.login) && !Strings.isNullOrEmpty(userNameParam) && !Strings.isNullOrEmpty(passwordParam)){
                    if(request.getParameterMap().containsKey(userNameParam))
                        usrname = request.getParameter(userNameParam);
                    if(request.getParameterMap().containsKey(passwordParam))
                        passwd = request.getParameter(passwordParam);
                    if (!Strings.isNullOrEmpty(usrname) && !Strings.isNullOrEmpty(passwd)) {
                        result.login = usrname + "/" + passwd;
                    }
                }
            }
        } else
            result.login = bal.username + "/" + bal.password;


        if(Strings.isNullOrEmpty(result.login) && !Strings.isNullOrEmpty(result.jsonData)) {
            ABean obj = null;
            try {
                obj = Jsons.decodeABean(result.jsonData);
            } catch (Exception e) {
            }
            if (obj != null && obj.containsKey("login"))
                result.login = (String)obj.get("login");
            if(Strings.isNullOrEmpty(result.login) && !Strings.isNullOrEmpty(userNameParam) && !Strings.isNullOrEmpty(passwordParam)){
                if (obj != null && obj.containsKey(userNameParam) && obj.containsKey(passwordParam))
                    result.login = obj.get(userNameParam) + "/" + obj.get(passwordParam);
            }
        }

//        result.fcloudCmd = FCloudCommand.decode(result.fcloudCmdOrig);
//        result.rmtCommand = RmtCommand.decode(result.rmtCommandOrig);

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
        if(result.sort == null && !Strings.isNullOrEmpty(result.sortOrig)) {
            result.sort = Utl.restoreSimpleSort(result.sortOrig);
        }
        if(result.filter == null && !Strings.isNullOrEmpty(result.filterOrig)) {
            result.filter = Utl.restoreSimpleFilter(result.filterOrig);
        }
        if(result.sort == null && !Strings.isNullOrEmpty(result.sortOrig)) {
            SortAndFilterObj obj = Jsons.decode("{ \"sort\":" + result.sortOrig + " }", SortAndFilterObj.class);
            result.sort = obj.sort;
        }
        if(result.filter == null && !Strings.isNullOrEmpty(result.filterOrig)) {
            result.filter = Jsons.decode(result.filterOrig, Filter.class);
        }


        result.page = Converter.toType(result.pageOrig, Integer.class, true);
        result.offset = Converter.toType(result.offsetOrig, Integer.class, true);
        result.pageSize = Converter.toType(result.pageSizeOrig, Integer.class, true);
        if((result.page == null && result.pageOrig != null && result.pageOrig.equalsIgnoreCase("last")) ||
                (result.offset == null && result.offsetOrig != null && result.offsetOrig.equalsIgnoreCase("last"))) {
            result.offset = Sqls.UNKNOWN_RECS_TOTAL + 1 - result.pageSize;
        }
        if(result.pageSize == null && result.pageSizeOrig == null)
            result.pageSize = 50;
        if((result.page == null && result.pageOrig == null) || (result.page != null && result.page < 1))
            result.page = 1;
        if(result.offset == null && result.offsetOrig == null && result.page != null)
            result.offset = (result.page - 1) * result.pageSize;

        extractBioParamsFromQuery(result);
        setQueryParamsToBioParams(result);

        return result;
    }

    public WrappedRequest(final HttpServletRequest request) throws Exception {
        super(request);
        modParameters = new TreeMap<>();
        modHeaders = new HashMap();
        bioQueryParams = decodeBioQueryParams((HttpServletRequest)this.getRequest());
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

    public <T> T getBioQueryParam(String paramName, Class<T> paramType, T defaultValue) throws Exception {
        final BioQueryParams queryParams = this.getBioQueryParams();
        return Paramus.paramValue(queryParams.bioParams, paramName, paramType, defaultValue);
    }

    protected <T> T getBioQueryParam(String paramName, HttpServletRequest request, Class<T> paramType) throws Exception {
        return getBioQueryParam(paramName, paramType, null);
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

    private User user;
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    public User getUser(){
        return user;
    }
}