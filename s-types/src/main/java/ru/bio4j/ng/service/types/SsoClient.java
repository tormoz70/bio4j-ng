package ru.bio4j.ng.service.types;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Jsons1;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.SsoResponse;
import ru.bio4j.ng.model.transport.User;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class SsoClient {
    private static final Logger LOG = LoggerFactory.getLogger(SsoClient.class);

    private final String ssoServiceUrl;
    private final HttpSimpleClient httpSimpleClient;

    private SsoClient(final String ssoServiceUrl) {
        this.ssoServiceUrl = ssoServiceUrl;
        httpSimpleClient = new HttpSimpleClient();
    }

    public static SsoClient create(final String ssoServiceUrl) {
        return new SsoClient(ssoServiceUrl);
    }

    private SsoResponse restoreResponseObject(HttpResponse response) throws Exception {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        return Jecksons.getInstance().decode(responseString, SsoResponse.class);
    }

    public User login(final BioQueryParams qprms) throws Exception {
        final String login = qprms.login;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        if (isNullOrEmpty(login))
            throw new BioError.Login.Unauthorized();
        String reqstJson = String.format("{\"login\":\"%s\"}", login);

        String requestUrl = String.format("%s/login", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestPost(requestUrl, null, reqstJson, remoteIP, remoteClient);
        SsoResponse lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.success && lrsp.user != null)
                return lrsp.user;
            if (lrsp.success && lrsp.user == null)
                throw new Exception("Unexpected error with code 11!");
            if (!lrsp.success && lrsp.exception != null)
                throw lrsp.exception;
        }
        throw new Exception("Unexpected error with code 22!");
    }

    public User restoreUser(final String stokenOrUsrUid, final String remoteIP, final String remoteClient) throws Exception {
        String requestUrl = String.format("%s/restoreUser/%s", ssoServiceUrl, stokenOrUsrUid);
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stokenOrUsrUid, remoteIP, remoteClient);
        SsoResponse lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.success && lrsp.user != null)
                return lrsp.user;
            if (lrsp.success && lrsp.user == null)
                throw new Exception("Unexpected error with code 11!");
            if (!lrsp.success && lrsp.exception != null)
                throw lrsp.exception;
        }
        throw new Exception("Unexpected error with code 22!");
    }

    public User curUser(final BioQueryParams qprms) throws Exception {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        String requestUrl = String.format("%s/curusr", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stoken, remoteIP, remoteClient);
        SsoResponse lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.success && lrsp.user != null)
                return lrsp.user;
            if (lrsp.success && lrsp.user == null)
                throw new Exception("Unexpected error with code 11!");
            if (!lrsp.success && lrsp.exception != null)
                throw lrsp.exception;
        }
        throw new Exception("Unexpected error with code 22!");
    }

    public void logoff(final BioQueryParams qprms) throws Exception {
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        final String stoken = qprms.stoken;
        String requestUrl = String.format("%s/logoff", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestPost(requestUrl, stoken, null, remoteIP, remoteClient);
        SsoResponse lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (!lrsp.success && lrsp.exception != null)
                throw lrsp.exception;
        }
        throw new Exception("Unexpected error with code 22!");
    }

    public Boolean loggedin(final BioQueryParams qprms) throws Exception {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        String requestUrl = String.format("%s/api/test", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stoken, remoteIP, remoteClient);
        SsoResponse lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.success)
                return true;
            if (!lrsp.success)
                throw lrsp.exception;
        }
        throw new Exception("Unexpected error with code 22!");
    }



}
