package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ayrat on 13.03.2016.
 */
public class SrvcUtils {

    public static final String QRY_PARAM_NAME_REQUEST_TYPE = "rqt";
    public static final String QRY_PARAM_NAME_MODULE = "bm";
    public static final String QRY_PARAM_NAME_BIOCODE = "biocd";
    public static final String QRY_PARAM_NAME_UID = "uid";

    public static final String PARAM_CURUSR_UID =    "p_sys_curusr_uid";
    public static final String PARAM_CURUSR_ROLES =  "p_sys_curusr_roles";
    public static final String PARAM_CURUSR_GRANTS = "p_sys_curusr_grants";

    public static class BioQueryParams {
        public String moduleKey;
        public String bioCode;
        public String loginOrUid;
    }

    public static BioQueryParams decodeBioQueryParams(HttpServletRequest request) {
        BioQueryParams result = new BioQueryParams();
        result.moduleKey = request.getParameter(QRY_PARAM_NAME_MODULE);
        result.bioCode = request.getParameter(QRY_PARAM_NAME_BIOCODE);
        result.loginOrUid = request.getParameter(QRY_PARAM_NAME_UID);
        if(Strings.isNullOrEmpty(result.loginOrUid))
            result.loginOrUid = User.BIO_ANONYMOUS_USER_LOGIN;
        return result;
    }

}
