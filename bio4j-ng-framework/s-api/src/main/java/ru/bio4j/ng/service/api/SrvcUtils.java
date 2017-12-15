package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.FCloudCommand;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.RmtCommand;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

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

    public static class BioQueryParams {
        public HttpServletRequest request;
        public String method;
        public String remoteIP;
        public String remoteClient;

        @Prop(name = "rqt")
        public String requestType;
        @Prop(name = "bm")
        public String moduleKey;
        @Prop(name = "biocd")
        public String bioCode;
        @Prop(name = "stoken")
        public String stoken;
        @Prop(name = "jsonData")
        public String jsonData;
        @Prop(name = "hf")
        public String fileHashCode;
        @Prop(name = "login")
        public String login;
        @Prop(name = "fcmd")
        public String fcloudCmdOrig;
        public FCloudCommand fcloudCmd;
        @Prop(name = "fluid")
        public String fcloudFileUid;
        @Prop(name = "uplduid")
        public String fcloudUploadUid;
        @Prop(name = "uplddsc")
        public String fcloudUploadDesc;
        @Prop(name = "upldprm")
        public String fcloudExtParam;

        @Prop(name = "page")
        public String pageOrig;
        public Integer page;
        @Prop(name = "offset")
        public String offsetOrig;
        public Integer offset;
        @Prop(name = "pageSize")
        public String pageSizeOrig;
        public Integer pageSize;

        @Prop(name = "locate")
        public String location;

        @Prop(name = "id")
        public String id;

        @Prop(name = "rmtcmd")
        public String rmtCommandOrig;
        public RmtCommand rmtCommand;
        @Prop(name = "rmtsessionuid")
        public String rmtSessionUid;

        @Prop(name = "sort")
        public String sortOrg;
        public List<Sort> sort;

        @Prop(name = "filter")
        public String filterOrg;
        public Filter filter;

        @Prop(name = "selection")
        public String selection;

        public List<Param> bioParams;
    }


}
