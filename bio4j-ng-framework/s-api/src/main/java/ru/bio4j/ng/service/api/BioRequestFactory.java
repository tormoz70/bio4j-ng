package ru.bio4j.ng.service.api;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.converter.hanlers.MetaTypeHandler;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Httpc;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;
import ru.bio4j.ng.model.transport.jstore.filter.*;

import javax.servlet.http.HttpServletRequest;
import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public abstract class BioRequestFactory {
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

    public BioRequest restore(
            final SrvcUtils.BioQueryParams qprms,
            final Class<? extends BioRequest> bioRequestClass,
            final User usr) throws Exception {
        BioRequest bioRequest;
        String bioRequestJson = qprms.jsonData;
        try {
            bioRequest = Jsons.decode(bioRequestJson, bioRequestClass);
        } catch (Exception e) {
            throw new Exception(String.format("Unexpected error while decoding BioRequest JSON: %s\n"+
                    " - Error: %s", bioRequestJson, e.getMessage()), e);
        }
        bioRequest.setModuleKey(qprms.moduleKey);
        bioRequest.setBioCode(qprms.bioCode);
        bioRequest.setRequestType(qprms.requestType);
        bioRequest.setRemoteIP(qprms.remoteIP);
        bioRequest.setRemoteClient(qprms.remoteIP);
        bioRequest.setUser(usr);
        return bioRequest;
    }

    public static class Ping extends BioRequestFactory {
    }

    public static class Logout extends BioRequestFactory {
    }

    public static class Login extends BioRequestFactory {
    }

    public static class GetJson extends BioRequestFactory {
    }

    public static class GetFile extends BioRequestFactory {

        @Override
        public BioRequest restore(
                final SrvcUtils.BioQueryParams qprms,
                final Class<? extends BioRequest> bioRequestClass,
                final User usr) throws Exception {
            BioRequest bioRequest = super.restore(qprms, bioRequestClass, usr);
            ((BioRequestGetFile)bioRequest).setFileHashCode(qprms.fileHashCode);
            return bioRequest;
        }

    }

    public static class GetDataSet extends BioRequestFactory {
    }

    public static class GetRecord extends BioRequestFactory {
    }

    public static class DataSetPost extends BioRequestFactory {
    }

    public static class StoredProg extends BioRequestFactory {
    }

    public static class FormUpload extends BioRequestFactory {
        @Override
        public BioRequest restore(
                final SrvcUtils.BioQueryParams qprms,
                final Class<? extends BioRequest> bioRequestClass,
                final User usr) throws Exception {
            return null;
        }

    }

}
