package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.model.transport.FCloudCommand;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.RmtCommand;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Created by ayrat on 13.03.2016.
 */
public class SrvcUtils {

//    public static final String QRY_PARAM_NAME_REQUEST_TYPE = "rqt";
//    public static final String QRY_PARAM_NAME_MODULE = "bm";
//    public static final String QRY_PARAM_NAME_BIOCODE = "biocd";
//    public static final String QRY_PARAM_NAME_STOKEN = "stoken";
//    public static final String QRY_PARAM_NAME_LOGIN = "login";
//    public static final String QRY_PARAM_NAME_JSON_DATA = "jsonData";
//    public static final String QRY_PARAM_NAME_FILE_HASH_CODE = "hf";
//    public static final String QRY_PARAM_NAME_FCLOUD_CMD = "fcmd";
//    public static final String QRY_PARAM_NAME_FCLOUD_FILEUID = "fluid";
//    public static final String QRY_PARAM_NAME_FCLOUD_UPLOADUID = "uplduid";
//    public static final String QRY_PARAM_NAME_FCLOUD_UPLOADDESC = "uplddsc";

    public static final String PARAM_CURUSR_UID        = "p_sys_curusr_uid";
    public static final String PARAM_CURUSR_ORG_UID    = "p_sys_curusr_org_uid";
    public static final String PARAM_CURUSR_ROLES      = "p_sys_curusr_roles";
    public static final String PARAM_CURUSR_GRANTS     = "p_sys_curusr_grants";
    public static final String PARAM_CURUSR_IP         = "p_sys_curusr_ip";
    public static final String PARAM_CURUSR_CLIENT     = "p_sys_curusr_client";
//    public static final String QUERYPARAM_NAME_ID      = "p_queryparam_id";
//    public static final String QUERYPARAM_NAME_FILTER  = "p_queryparam_filter";
//    public static final String QUERYPARAM_NAME_SORT    = "p_queryparam_sort";
//    public static final String QUERYPARAM_NAME_ID      = "p_queryparam_id";
//    public static final String PAGINPARAM_NAME_PAGE    = "p_paginparam_page";
//    public static final String PAGINPARAM_NAME_OFFSET  = "p_paginparam_offset";
//    public static final String PAGINPARAM_NAME_PAGESIZE  = "p_paginparam_pagesize";

    public static void applyCurrentUserParams(final User usr, final List<Param> params) {
        if (usr != null) {
            try (Paramus p = Paramus.set(params)) {
                p.setValue(SrvcUtils.PARAM_CURUSR_UID, usr.getInnerUid(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_ORG_UID, usr.getOrgId(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_ROLES, usr.getRoles(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_GRANTS, usr.getGrants(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_IP, usr.getRemoteIP(), Param.Direction.IN, true);
                p.setValue(SrvcUtils.PARAM_CURUSR_CLIENT, usr.getRemoteClient(), Param.Direction.IN, true);
            }
        }
    }

}
