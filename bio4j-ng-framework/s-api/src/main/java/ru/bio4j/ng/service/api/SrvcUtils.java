package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.User;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

/**
 * Created by ayrat on 13.03.2016.
 */
public class SrvcUtils {

    public static final String QRY_PARAM_NAME_REQUEST_TYPE = "rqt";
    public static final String QRY_PARAM_NAME_MODULE = "bm";
    public static final String QRY_PARAM_NAME_BIOCODE = "biocd";
    public static final String QRY_PARAM_NAME_UID = "uid";
    public static final String QRY_PARAM_NAME_JSON_DATA = "jsonData";

    public static final String PARAM_CURUSR_UID        = "p_sys_curusr_uid";
    public static final String PARAM_CURUSR_ORG_UID    = "p_sys_curusr_org_uid";
    public static final String PARAM_CURUSR_ROLES      = "p_sys_curusr_roles";
    public static final String PARAM_CURUSR_GRANTS     = "p_sys_curusr_grants";
    public static final String PARAM_CURUSR_IP         = "p_sys_curusr_ip";

    public static class BioQueryParams {
        public String method;
        public String requestType;
        public String moduleKey;
        public String bioCode;
        public String uid;
        public String remoteIP;
        public String jsonData;
    }

}
