package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.FCloudCommand;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.RmtCommand;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.service.api.Prop;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class BioQueryParams {
    public static final String CS_UPLOADEXTPARAM = "upldprm";
    public static final String CS_UPLOADTYPE = "upldType";

    public HttpServletRequest request;
    public String method;
    public String remoteIP;
    public String remoteClient;
    public String remoteClientVersion;

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
    @Prop(name = CS_UPLOADTYPE)
    public String fcloudUploadType;
    @Prop(name = "uplddsc")
    public String fcloudUploadDesc;
    @Prop(name = CS_UPLOADEXTPARAM)
    public String fcloudExtParam;

    @Prop(name = "page")
    public String pageOrig;
    public Integer page;
    @Prop(name = "offset")
    public String offsetOrig;
    public Integer offset;
    @Prop(name = "limit")
    public String limitOrig;
    @Prop(name = "pageSize")
    public String pageSizeOrig;
    public Integer pageSize;
    public Integer totalCount;

    @Prop(name = "locate")
    public String location;

    @Prop(name = "id")
    public String id;

    @Prop(name = "rmtcmd")
    public String rmtCommandOrig;
    public RmtCommand rmtCommand;
    @Prop(name = "rmtsessionuid")
    public String rmtSessionUid;

    @Prop(name = "asorter")
    public String sortOrig;
    @Prop(name = "sort")
    public String sortOrig1;
    public List<Sort> sort;

    @Prop(name = "afilter")
    public String filterOrig;
    public Filter filter;

    @Prop(name = "selection")
    public String selection;

    @Prop(name = "query")
    public String query;

    @Prop(name = "gcount")
    public String gcount;

    public List<Param> bioParams;
}
