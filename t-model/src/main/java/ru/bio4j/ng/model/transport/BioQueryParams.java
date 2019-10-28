package ru.bio4j.ng.model.transport;

import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

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

    @Prop(name = "deviceuuid")
    public String deviceuuid;
    @Prop(name = "stoken")
    public String stoken;
    @Prop(name = "jsonData")
    public String jsonData;
    @Prop(name = "login")
    public String login;
    @Prop(name = CS_UPLOADTYPE)
    public String fcloudUploadType;

    @Prop(name = "page")
    public String pageOrig;
    public Integer page;
    @Prop(name = "offset")
    public String offsetOrig;
    public Integer offset;

//    @Prop(name = "limit")
//    public String limitOrig;
//    @Prop(name = "per-page")
//    public String perPageOrig;
    @Prop(name = "pageSize")
    public String pageSizeOrig;

    public Integer pageSize;
    public Integer totalCount;

    @Prop(name = "locate")
    public String location;

    @Prop(name = "asorter")
    public String sortOrig;
    public List<Sort> sort;

    @Prop(name = "afilter")
    public String filterOrig;
    public Filter filter;

    @Prop(name = "query")
    public String query;

    @Prop(name = "gcount")
    public String gcount;

    public List<Param> bioParams;
}
