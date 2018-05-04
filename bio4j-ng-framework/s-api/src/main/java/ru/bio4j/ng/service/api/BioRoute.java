package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreExpDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStorePost;


public enum BioRoute {
    UNKNOWN("unknown", null),
    PING("ping", BioRequestPing.class),
    LOGIN("login", BioRequestLogin.class),
    LOGOUT("logout", BioRequestLogout.class),
    CRUD_JSON_GET("crud.json.get", BioRequestGetJson.class),
    CRUD_FILE_GET("crud.file.get", BioRequestGetFile.class),
    CRUD_DATASET_GET("crud.ds.get", BioRequestJStoreGetDataSet.class),
    CRUD_DATASET_EXP("crud.ds.exp", BioRequestJStoreExpDataSet.class),
    CRUD_RECORD_GET("crud.rec.get", BioRequestJStoreGetRecord.class),
    CRUD_DATASET_POST("crud.ds.post", BioRequestJStorePost.class),
    CRUD_EXEC("crud.exec", BioRequestStoredProg.class),
    CRUD_FCLOUD("crud.fcloud", BioRequestFCloud.class);

    private String alias;
    private Class<? extends BioRequest> clazz;

    private BioRoute(String alias, Class<? extends BioRequest> clazz) {
        this.alias = alias;
        this.clazz = clazz;
    }

    public static BioRoute getType(String alias) {
        for (BioRoute v : BioRoute.values())
            if(v.alias.equals(alias.toLowerCase()))
                return v;
        return UNKNOWN;
    }

    public String getAlias(){
        return alias;
    }
    public Class<? extends BioRequest> getClazz() {
        if(clazz == null)
            throw new IllegalArgumentException(String.format("Property \"clazz\" is not defined for value %s!", this.name()));
        return clazz;
    }
}
