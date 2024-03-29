package ru.bio4j.ng.sso.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.service.types.HttpSimpleClient;

import java.io.IOException;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class SsoClient {
    private static final Logger LOG = LoggerFactory.getLogger(SsoClient.class);

    private final String ssoServiceUrl;
    private final HttpSimpleClient httpSimpleClient;

    private SsoClient(final String ssoServiceUrl) {
        this.ssoServiceUrl = String.format("%s/api", ssoServiceUrl);
        httpSimpleClient = new HttpSimpleClient();
    }

    public static SsoClient create(final String ssoServiceUrl) {
        return new SsoClient(ssoServiceUrl);
    }

    private static User extractUserFromRsp(LoginResult lrsp) {
        User rslt = new User();
        Utl.applyValuesToBeanFromBean(lrsp.getUser(), rslt);
        return rslt;
    }

    private LoginResult restoreResponseObject(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        String responseString = null;
        try {
            responseString = EntityUtils.toString(entity, "UTF-8");
        } catch(IOException e) {
            throw BioError.wrap(e);
        }
        return Jecksons.getInstance().decode(responseString, LoginResult.class);
    }

    public User login(final BioQueryParams qprms) {
        final String login = qprms.login;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        if (isNullOrEmpty(login))
            throw new BioError.Login.Unauthorized();
        String reqstJson = String.format("{\"login\":\"%s\"}", login);

        String requestUrl = String.format("%s/login", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestPost(requestUrl, null, reqstJson, remoteIP, remoteClient);
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.isSuccess() && lrsp.getUser() != null)
                return extractUserFromRsp(lrsp);
            if (lrsp.isSuccess() && lrsp.getUser() == null)
                throw new BioError(6021, "Unexpected error on sso server!");
            if (!lrsp.isSuccess() && lrsp.getException() != null)
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError(6022, "Unexpected error on sso server!");
    }

    public User restoreUser(final String stokenOrUsrUid, final String remoteIP, final String remoteClient) {
        String requestUrl = String.format("%s/restoreUser/%s", ssoServiceUrl, stokenOrUsrUid);
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stokenOrUsrUid, remoteIP, remoteClient);
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.isSuccess() && lrsp.getUser() != null) {
                return extractUserFromRsp(lrsp);
            }
            if (lrsp.isSuccess() && lrsp.getUser() == null)
                throw new BioError(6021, "Unexpected error on sso server!");
            if (!lrsp.isSuccess() && lrsp.getException() != null)
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError(6022, "Unexpected error on sso server!");
    }

    public User curUser(final BioQueryParams qprms) {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        String requestUrl = String.format("%s/curusr", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stoken, remoteIP, remoteClient);
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.isSuccess() && lrsp.getUser() != null)
                return extractUserFromRsp(lrsp);
            if (lrsp.isSuccess() && lrsp.getUser() == null)
                throw new BioError("Unexpected error with code 11!");
            if (!lrsp.isSuccess() && lrsp.getException() != null)
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError("Unexpected error with code 22!");
    }

    public void logoff(final BioQueryParams qprms) {
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        final String stoken = qprms.stoken;
        String requestUrl = String.format("%s/logoff", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestPost(requestUrl, stoken, null, remoteIP, remoteClient);
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (!lrsp.isSuccess() && lrsp.getException() != null)
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError("Unexpected error with code 22!");
    }

    public Boolean loggedin(final BioQueryParams qprms) {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        String requestUrl = String.format("%s/test", ssoServiceUrl);
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stoken, remoteIP, remoteClient);
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.isSuccess())
                return true;
            if (!lrsp.isSuccess())
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError("Unexpected error with code 22!");
    }



}
