package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.*;

import java.util.ArrayList;

public abstract class BioRequestFactory<T extends BioRequest> {
//    private static final String QRY_PARAM_NAME_JSON_DATA = "jsonData";

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
            final BioQueryParams qprms,
            final Class<T> clazz,
            final User usr) throws Exception {
        T bioRequest;
        if(!Strings.isNullOrEmpty(qprms.jsonData)) {
            try {
                bioRequest = Jsons.decode(qprms.jsonData, clazz);
            } catch (Exception e) {
                //throw new Exception(String.format("Unexpected error while decoding BioRequest JSON: %s\n" +
                //        " - Error: %s", qprms.jsonData, e.getMessage()), e);
                bioRequest = clazz.newInstance();
            }
        } else
            bioRequest = clazz.newInstance();
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
        if(qprms.bioParams == null)
            qprms.bioParams = new ArrayList<>();
        bioRequest.setBioParams(Paramus.set(qprms.bioParams).merge(bioRequest.getBioParams(), true).pop());

        if(bioRequest instanceof BioRequestJStoreGetDataSet){
            if(((BioRequestJStoreGetDataSet)bioRequest).getSort() == null && qprms.sort != null){
                ((BioRequestJStoreGetDataSet)bioRequest).setSort(qprms.sort);
            }
            if(((BioRequestJStoreGetDataSet)bioRequest).getFilter() == null && qprms.filter != null){
                ((BioRequestJStoreGetDataSet)bioRequest).setFilter(qprms.filter);
            }
        }

        SrvcUtils.applyCurrentUserParams(usr, bioRequest.getBioParams());
        return bioRequest;
    }

    public static class Ping extends BioRequestFactory<BioRequestPing> {
    }

    public static class Logout extends BioRequestFactory<BioRequestLogout> {
    }

    public static class Login extends BioRequestFactory<BioRequestLogin> {
    }

    public static class GetJson extends BioRequestFactory<BioRequestGetJson> {
        public BioRequestGetJson restore(
                final BioQueryParams qprms,
                final Class<BioRequestGetJson> clazz,
                final User usr) throws Exception {
            BioRequestGetJson rslt = super.restore(qprms, clazz, usr);
            setOffset(rslt, qprms);
            if(rslt.getLimit() == null && qprms.pageSize != null)
                rslt.setLimit(qprms.pageSize);
            if(rslt.getSort() == null && qprms.sort != null)
                rslt.setSort(qprms.sort);
            if (rslt.getFilter() == null && qprms.filter != null)
                rslt.setFilter(qprms.filter);
            return rslt;
        }
    }

    public static class GetFile extends BioRequestFactory<BioRequestGetFile> {
        public BioRequestGetFile restore(
                final BioQueryParams qprms,
                final Class<BioRequestGetFile> clazz,
                final User usr) throws Exception {
            BioRequestGetFile rslt = super.restore(qprms, clazz, usr);
            rslt.setFileHashCode(qprms.fileHashCode);
            return rslt;
        }
    }


    private static void setOffset(BioRequestPagination request, BioQueryParams qprms){
        int pageSize = (qprms.pageSize != null && qprms.pageSize > 0) ? qprms.pageSize : -1;
        int page = (qprms.page != null && qprms.page > 0) ? qprms.page : 0;
        int offset = (qprms.offset != null && qprms.offset >= 0) ? qprms.offset : 0;
        if(pageSize > 0) {
            if (page > 0)
                offset = (page - 1) * pageSize;
            else if (offset >= 0)
                page = (int) Math.floor(offset / pageSize) + 1;
        }
        request.setLimit(pageSize);
        request.setOffset(offset);
        request.setPage(page);
    }

    public static class GetDataSet extends BioRequestFactory<BioRequestJStoreGetDataSet> {
        public BioRequestJStoreGetDataSet restore(
                final BioQueryParams qprms,
                final Class<BioRequestJStoreGetDataSet> clazz,
                final User usr) throws Exception {
            BioRequestJStoreGetDataSet rslt = super.restore(qprms, clazz, usr);
            setOffset(rslt, qprms);
            if(rslt.getLocation() == null && !Strings.isNullOrEmpty(qprms.location))
                rslt.setLocation(Integer.getInteger(qprms.location));
            if(rslt.getSort() == null && qprms.sort != null)
                rslt.setSort(qprms.sort);
            if (rslt.getFilter() == null && qprms.filter != null)
                rslt.setFilter(qprms.filter);
            return rslt;
        }
    }

    public static class ExpDataSet extends BioRequestFactory<BioRequestJStoreExpDataSet> {
        public BioRequestJStoreExpDataSet restore(
                final BioQueryParams qprms,
                final Class<BioRequestJStoreExpDataSet> clazz,
                final User usr) throws Exception {
            BioRequestJStoreExpDataSet rslt = super.restore(qprms, clazz, usr);
            setOffset(rslt, qprms);
            if(rslt.getLimit() == null && qprms.pageSize != null)
                rslt.setLimit(qprms.pageSize);
            if(rslt.getLocation() == null && !Strings.isNullOrEmpty(qprms.location))
                rslt.setLocation(Integer.getInteger(qprms.location));

            if(rslt.getCmd() == null && qprms.rmtCommand != null)
                rslt.setCmd(qprms.rmtCommand);
            if(rslt.getSessionUid() == null && !Strings.isNullOrEmpty(qprms.rmtSessionUid))
                rslt.setSessionUid(qprms.rmtSessionUid);

            return rslt;
        }
    }

    public static class GetRecord extends BioRequestFactory<BioRequestJStoreGetRecord> {
        public BioRequestJStoreGetRecord restore(
                final BioQueryParams qprms,
                final Class<BioRequestJStoreGetRecord> clazz,
                final User usr) throws Exception {
            BioRequestJStoreGetRecord rslt = super.restore(qprms, clazz, usr);
            rslt.setId(qprms.id);
            return rslt;
        }
    }

    public static class DataSetPost extends BioRequestFactory<BioRequestJStorePost> {
    }

    public static class StoredProg extends BioRequestFactory<BioRequestStoredProg> {
        public BioRequestStoredProg restore(
                final BioQueryParams qprms,
                final Class<BioRequestStoredProg> clazz,
                final User usr) throws Exception {
            BioRequestStoredProg rslt = super.restore(qprms, clazz, usr);
            rslt.setCmd(qprms.rmtCommand);
            rslt.setSessionUid(qprms.rmtSessionUid);
            return rslt;
        }
    }

    public static class FCloud extends BioRequestFactory<BioRequestFCloud> {
        public BioRequestFCloud restore(
                final BioQueryParams qprms,
                final Class<BioRequestFCloud> clazz,
                final User usr) throws Exception {
            BioRequestFCloud rslt = super.restore(qprms, clazz, usr);
            rslt.setCmd(qprms.fcloudCmd);
            rslt.setFileUid(qprms.fcloudFileUid);
            rslt.setUploadUid(qprms.fcloudUploadUid);
            rslt.setUploadDesc(qprms.fcloudUploadDesc);
            rslt.setExtParam(qprms.fcloudExtParam);
            rslt.setUploadType(qprms.fcloudUploadType);
            return rslt;
        }
    }

}
