package ru.bio4j.ng.fcloud.restful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioQueryParams;
import ru.bio4j.ng.service.types.BioWrappedRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

public class RestSrvcBase {
    private static final Logger LOG = LoggerFactory.getLogger(RestSrvcBase.class);

    @Context
    private ServletContext servletContext;

    private FCloudProvider fcloudProvider;
    private FCloudProvider getFCloudProvider() {
        if(fcloudProvider == null)
            fcloudProvider = Utl.getService(servletContext, FCloudProvider.class);
        return fcloudProvider;
    }

    protected BioFCloudApiModule getModule() throws Exception {
        FCloudProvider fcProvider = getFCloudProvider();
        return fcProvider.getApi();
    }

    protected List<FileSpec> _getList(HttpServletRequest request) throws Exception {
        BioFCloudApiModule module = getModule();

        List<Param> bioParams = ((BioWrappedRequest)request).getBioQueryParams().bioParams;

//        final String fileNameFilter = Paramus.paramValue(bioParams, "qfcname", String.class, null);
//        final String fileDescFilter = Paramus.paramValue(bioParams, "qfcdesc", String.class, null);
//        final String fileParamFilter = Paramus.paramValue(bioParams, "qfcprm", String.class, null);
//        final String fileCTypeFilter = Paramus.paramValue(bioParams, "qfctype", String.class, null);
//        final String fileUpldUIDFilter = Paramus.paramValue(bioParams, "qfcupld", String.class, null);
//        final String fileHostFilter = Paramus.paramValue(bioParams, "qfchost", String.class, null);
//        final String fileUserFilter = Paramus.paramValue(bioParams, "qfcusr", String.class, null);
//        final String regFrom = Paramus.paramValue(bioParams, "qfcregfrm", String.class, null);
//        final String regTo = Paramus.paramValue(bioParams, "qfcregto", String.class, null);
//        final String fileFrom = Paramus.paramValue(bioParams, "qfcflfrom", String.class, null);
//        final String fileTo = Paramus.paramValue(bioParams, "qfcflto", String.class, null);
//        final String sizeFrom = Paramus.paramValue(bioParams, "qfcszfrom", String.class, null);
//        final String sizeTo = Paramus.paramValue(bioParams, "qfcszto", String.class, null);
        final User usr = ((BioWrappedRequest)request).getUser();

        return module.getFileList(bioParams, usr);
    }

    private static void addFieldMeta(final List<Field> flds, final String fldName, final MetaType fldType, final String fldTitle){
        Field fld = new Field();
        fld.setId(flds.size());fld.setName(fldName);fld.setMetaType(fldType);fld.setTitle(fldTitle);flds.add(fld);
    }

    protected StoreMetadata _getMetadata() throws Exception {
        StoreMetadata meta = new StoreMetadata();
        List<Field> flds = new ArrayList<>();
        meta.setFields(flds);
        addFieldMeta(flds, "uploadUID", MetaType.STRING, "Уникальный идентификатор. Присваивается на стороне клиента, для однозначной идентификации после загрузки в хранилище");
        addFieldMeta(flds, "fileUUID", MetaType.STRING, "Уникальный идентификатор. Присваивается на стороне сервера после регистрации в базе данных хранилища");
        addFieldMeta(flds, "creDatetime", MetaType.DATE, "Дата/время регистрации файла в базе данных хранилища");
        addFieldMeta(flds, "fileNameOrig", MetaType.STRING, "Оригинальное имя файла, которое передано клиентом в хранилище");
        addFieldMeta(flds, "fileSize", MetaType.INTEGER, "Размер файла в байтах");
        addFieldMeta(flds, "fileDatetime", MetaType.DATE, "Дата/время файла, которое передано клиентом в хранилище (если не передано, тогда == creDatetime)");
        addFieldMeta(flds, "md5", MetaType.STRING, "Хэш-код MD5 для файла. Вычисляется при сохранении в хранилище");
        addFieldMeta(flds, "contentType", MetaType.STRING, "Сontent type - значение, которое передано клиентом в хранилище");
        addFieldMeta(flds, "remoteIpAddress", MetaType.STRING, "IP-адрес, с которого был загружен файл");
        addFieldMeta(flds, "adesc", MetaType.STRING, "Описание файла, которое передано клиентом в хранилище");
        addFieldMeta(flds, "threadUID", MetaType.STRING, "ID потока, который обработал файл");
        return meta;
    }


    protected ABean _getSuccess() {
        ABean rslt = new ABean();
        rslt.put("success", true);
        return rslt;
    }


    protected void _delete(List<String> ids, HttpServletRequest request) throws Exception {
        final BioFCloudApiModule module = getModule();
        final User usr = ((BioWrappedRequest)request).getUser();
        for(String id : ids) {
            module.removeFile(id, usr);
        }
    }

    protected List<ABean> _save(HttpServletRequest request) throws Exception {
//        BioAppModule module = getModule();
//        return DataReaderApi.saveBeans(bioCode, request, module, abeans);
        return null;
    }
}
