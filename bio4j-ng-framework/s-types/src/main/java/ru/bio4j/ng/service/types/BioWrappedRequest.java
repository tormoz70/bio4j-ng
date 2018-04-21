package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.service.api.BioQueryParams;
import ru.bio4j.ng.service.api.RestParamNames;
import ru.bio4j.ng.service.api.SrvcUtils;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class BioWrappedRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> modParameters;
    private final HashMap<String, String> modHeaders;

    private BioQueryParams bioQueryParams;

    public BioQueryParams getBioQueryParams() {
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

    private static void extractBioParamsFromQuery(BioQueryParams qparams) {
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
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_PAGE, qprms.page);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_PAGESIZE, qprms.pageSize);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_OFFSET, qprms.offset);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_TOTALCOUNT, qprms.totalCount);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.PAGINATION_PARAM_LAST, qprms.offset+qprms.pageSize);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.GETROW_PARAM_PKVAL, qprms.id);
        Paramus.setParamValue(qprms.bioParams, RestParamNames.RAPI_PARAM_FILEHASHCODE, qprms.fileHashCode);
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


    public static BioQueryParams decodeBioQueryParams(HttpServletRequest request) throws Exception {
        BioQueryParams result = Httpc.createBeanFromHttpRequest(request, BioQueryParams.class);

        result.request = request;
        result.method = request.getMethod();
        result.remoteIP = Httpc.extractRealRemoteAddr(request);
        result.remoteClient = Httpc.extractRealRemoteClient(request);

        if(Strings.isNullOrEmpty(result.moduleKey)) {
            final String bioHeaderModuleKey = request.getHeader("X-Module");
            if (!Strings.isNullOrEmpty(bioHeaderModuleKey))
                result.moduleKey = bioHeaderModuleKey;
        }
        final String bioHeaderClientName = request.getHeader("X-Client");
        if(!Strings.isNullOrEmpty(bioHeaderClientName)) {
            result.remoteClient = bioHeaderClientName;
        }
        if(Strings.isNullOrEmpty(result.remoteClientVersion)) {
            final String bioHeaderClientVersion = request.getHeader("X-Client-Ver");
            if (!Strings.isNullOrEmpty(bioHeaderClientVersion))
                result.remoteClientVersion = bioHeaderClientVersion;
        }
        if(Strings.isNullOrEmpty(result.stoken)) {
            final String bioHeaderSToken = request.getHeader("X-SToken");
            if (!Strings.isNullOrEmpty(bioHeaderSToken))
                result.stoken = bioHeaderSToken;
        }
        if(Strings.isNullOrEmpty(result.stoken)) result.stoken = "anonymouse";

        if(Strings.isNullOrEmpty(result.pageOrig)) {
            final String bioHeaderPage = request.getHeader("X-Pagination-Page");
            if (!Strings.isNullOrEmpty(bioHeaderPage))
                result.pageOrig = bioHeaderPage;
        }
        if(Strings.isNullOrEmpty(result.offsetOrig)) {
            final String bioHeaderOffset = request.getHeader("X-Pagination-Offset");
            if (!Strings.isNullOrEmpty(bioHeaderOffset))
                result.offsetOrig = bioHeaderOffset;
        }
        if(Strings.isNullOrEmpty(result.pageSizeOrig)) {
            final String bioHeaderPageSize = request.getHeader("X-Pagination-Pagesize");
            if (!Strings.isNullOrEmpty(bioHeaderPageSize))
                result.pageSizeOrig = bioHeaderPageSize;
        }

        BasicAutenticationLogin bal = detectBasicAutentication(request);

        if(Strings.isNullOrEmpty(bal.username)) {
            if (result.method == "POST") {
                String usrname = request.getParameter("usrname");
                String passwd = request.getParameter("passwd");
                if (!Strings.isNullOrEmpty(usrname) && !Strings.isNullOrEmpty(passwd)) {
                    result.login = usrname + "/" + passwd;
                }
            }
        } else
            result.login = bal.username + "/" + bal.password;


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
        if(result.sort == null && !Strings.isNullOrEmpty(result.sortOrg)) {
            SortAndFilterObj obj = Jsons.decode("{ \"sort\":" + result.sortOrg + " }", SortAndFilterObj.class);
            result.sort = obj.sort;
        }
        if(result.filter == null && !Strings.isNullOrEmpty(result.filterOrg))
            result.filter = Jsons.decode(result.filterOrg, Filter.class);


        result.page = Converter.toType(result.pageOrig, Integer.class, true);
        result.offset = Converter.toType(result.offsetOrig, Integer.class, true);
        result.pageSize = Converter.toType(result.pageSizeOrig, Integer.class, true);
        if(result.pageSize == null && result.pageSizeOrig == null) result.pageSize = 50;
        if((result.page == null && result.pageOrig != null && result.pageOrig.equalsIgnoreCase("last")) ||
                (result.offset == null && result.offsetOrig != null && result.offsetOrig.equalsIgnoreCase("last"))) {
            result.offset = Sqls.UNKNOWN_RECS_TOTAL + 1 - result.pageSize;
        }

        extractBioParamsFromQuery(result);
        setQueryParamsToBioParams(result);

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
