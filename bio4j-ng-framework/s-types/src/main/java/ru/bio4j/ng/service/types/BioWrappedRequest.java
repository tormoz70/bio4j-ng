package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Strings;
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

    public static SrvcUtils.BioQueryParams decodeBioQueryParams(HttpServletRequest request) throws IOException {
        SrvcUtils.BioQueryParams result = new SrvcUtils.BioQueryParams();
        result.method = request.getMethod();
        result.requestType = request.getParameter(SrvcUtils.QRY_PARAM_NAME_REQUEST_TYPE);
        result.moduleKey = request.getParameter(SrvcUtils.QRY_PARAM_NAME_MODULE);
        result.bioCode = request.getParameter(SrvcUtils.QRY_PARAM_NAME_BIOCODE);
        result.uid = request.getParameter(SrvcUtils.QRY_PARAM_NAME_UID);
        result.remoteIP = Httpc.extractRealRemoteAddr(request);
        result.fileHashCode = request.getParameter(SrvcUtils.QRY_PARAM_NAME_FILE_HASH_CODE);
        if(Strings.isNullOrEmpty(result.uid))
            result.uid = User.BIO_ANONYMOUS_UID;

        final String jsonDataAsQueryParam = request.getParameter(SrvcUtils.QRY_PARAM_NAME_JSON_DATA);
        StringBuilder jd = new StringBuilder();
        if(!isNullOrEmpty(jsonDataAsQueryParam))
            jd.append(jsonDataAsQueryParam);
        else
            Httpc.readDataFromRequest(request, jd);
        if(jd.length() == 0)
            jd.append("{}");
        result.jsonData = jd.toString();
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
