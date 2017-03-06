package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.FCloudCommand;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.SrvcUtils;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

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

    public static class BioParamObj {
        private List<Param> bioParams;

        public List<Param> getBioParams() {
            return bioParams;
        }

        public void setBioParams(List<Param> bioParams) {
            this.bioParams = bioParams;
        }
    }

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
        return rslt;
    }

    private static void extractBioParamsFromQuery(SrvcUtils.BioQueryParams qparams) {
        List<String> sysParamNames = extractSysParamNames();
        qparams.bioParams = new ArrayList<>();
        while(qparams.request.getParameterNames().hasMoreElements()){
            String paramName = qparams.request.getParameterNames().nextElement();
            String val = qparams.request.getParameter(paramName);
            if(sysParamNames.indexOf(paramName) == -1){
                qparams.bioParams.add(Param.builder().name(paramName).value(val).build());
            }
        }
        if(!Strings.isNullOrEmpty(qparams.jsonData)) {
            BioParamObj bioParamObj = null;
            try {
                bioParamObj = Jsons.decode(qparams.jsonData, BioParamObj.class);
            } catch (Exception e) {
            }
            if (bioParamObj != null && bioParamObj.getBioParams() != null)
                qparams.bioParams = Paramus.set(qparams.bioParams).merge(bioParamObj.getBioParams(), true).pop();
        }

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
            try {
                LoginParamObj loginParamObj = Jsons.decode(result.jsonData, LoginParamObj.class);
                if (loginParamObj != null && !Strings.isNullOrEmpty(loginParamObj.getLogin()))
                    result.login = loginParamObj.getLogin();
            } catch (Exception e) {
            }
        }

        result.fcloudCmd = FCloudCommand.decode(result.fcloudCmdOrig);

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
