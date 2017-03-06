package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.converter.hanlers.MetaTypeHandler;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.filter.*;

import javax.servlet.http.HttpServletRequest;
import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public abstract class BioRequestFactory<T extends BioRequest> {
    private static final String QRY_PARAM_NAME_JSON_DATA = "jsonData";

    private void prepareBioParams(BioRequest bioRequest){
        if(bioRequest.getBioParams() != null && !bioRequest.getBioParams().isEmpty()){
            try(Paramus pms = Paramus.set(bioRequest.getBioParams());){
                for(Param p : pms.get()){
                    if(p.getType() == null) {
                        MetaType valueType = MetaTypeConverter.read(p.getValue() != null ? p.getValue().getClass() : String.class);
                        p.setType(valueType);
                    }
                }
            }
        }

    }

    public T restore(
            final SrvcUtils.BioQueryParams qprms,
            final Class<T> clazz,
            final User usr) throws Exception {
        T bioRequest;
        try {
            bioRequest = Jsons.decode(qprms.jsonData, clazz);
        } catch (Exception e) {
            throw new Exception(String.format("Unexpected error while decoding BioRequest JSON: %s\n"+
                    " - Error: %s", qprms.jsonData, e.getMessage()), e);
        }
        bioRequest.setHttpRequest(qprms.request);
        bioRequest.setOrigJson(qprms.jsonData);
        bioRequest.setModuleKey(qprms.moduleKey);
        bioRequest.setBioCode(qprms.bioCode);
        bioRequest.setRequestType(qprms.requestType);
        bioRequest.setRemoteIP(qprms.remoteIP);
        bioRequest.setRemoteClient(qprms.remoteClient);
        if(!Strings.isNullOrEmpty(qprms.login))
            bioRequest.setLogin(qprms.login);
        bioRequest.setUser(usr);
        bioRequest.setBioParams(Paramus.set(qprms.bioParams).merge(bioRequest.getBioParams(), true).pop());

        return bioRequest;
    }

    public static class Ping extends BioRequestFactory<BioRequestPing> {
    }

    public static class Logout extends BioRequestFactory<BioRequestLogout> {
    }

    public static class Login extends BioRequestFactory<BioRequestLogin> {
    }

    public static class GetJson extends BioRequestFactory<BioRequestGetJson> {
    }

    public static class GetFile extends BioRequestFactory<BioRequestGetFile> {
        public BioRequestGetFile restore(
                final SrvcUtils.BioQueryParams qprms,
                final Class<BioRequestGetFile> clazz,
                final User usr) throws Exception {
            BioRequestGetFile rslt = super.restore(qprms, clazz, usr);
            rslt.setFileHashCode(qprms.fileHashCode);
            return rslt;
        }
    }

    public static class GetDataSet extends BioRequestFactory<BioRequestJStoreGetDataSet> {
    }

    public static class ExpDataSet extends BioRequestFactory<BioRequestJStoreExpDataSet> {
    }

    public static class GetRecord extends BioRequestFactory<BioRequestJStoreGetRecord> {
    }

    public static class DataSetPost extends BioRequestFactory<BioRequestJStorePost> {
    }

    public static class StoredProg extends BioRequestFactory<BioRequestStoredProg> {
    }

    public static class FCloud extends BioRequestFactory<BioRequestFCloud> {
        public BioRequestFCloud restore(
                final SrvcUtils.BioQueryParams qprms,
                final Class<BioRequestFCloud> clazz,
                final User usr) throws Exception {
            BioRequestFCloud rslt = super.restore(qprms, clazz, usr);
            rslt.setCmd(qprms.fcloudCmd);
            rslt.setFileUid(qprms.fcloudFileUid);
            rslt.setUploadUid(qprms.fcloudUploadUid);
            rslt.setUploadDesc(qprms.fcloudUploadDesc);
            return rslt;
        }
    }

}
