package ru.bio4j.ng.service.types;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioRequestLogout;
import ru.bio4j.ng.model.transport.BioRequestPing;
import ru.bio4j.ng.model.transport.BioRequestStoredProg;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetRecord;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStorePost;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public enum BioRoute {
    UNKNOWN("unknown", null, null),
    PING("ping", BioRequestPing.class, BioRequestFactory.Ping.class),
    LOGOUT("logout", BioRequestLogout.class, BioRequestFactory.Logout.class),
    CRUD_DATASET_GET("crud.ds.get", BioRequestJStoreGetDataSet.class, BioRequestFactory.GetDataSet.class),
    CRUD_RECORD_GET("crud.rec.get", BioRequestJStoreGetRecord.class, BioRequestFactory.GetRecord.class),
    CRUD_DATASET_POST("crud.ds.post", BioRequestJStorePost.class, BioRequestFactory.DataSetPost.class),
    CRUD_EXEC("crud.exec", BioRequestStoredProg.class, BioRequestFactory.StoredProg.class),
    CRUD_FORM_UPLOAD("crud.form.upload", BioRequestJStorePost.class, BioRequestFactory.FormUpload.class);

    private String alias;
    private Class<? extends BioRequest> clazz;
    private Class<? extends BioRequestFactory> factoryClazz;
    private BioRequestFactory factory;

    private BioRoute(String alias, Class<? extends BioRequest> clazz, Class<? extends BioRequestFactory> factoryClazz) {
        if(isNullOrEmpty(alias))
            throw new IllegalArgumentException(String.format("Argument \"%s\" cannot be null!", "alias"));
        this.alias = alias;
        this.clazz = clazz;
        this.factoryClazz = factoryClazz;
    }

    public static BioRoute getType(String alias) {
        if(!isNullOrEmpty(alias))
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
    public Class<? extends BioRequestFactory> getFactoryClazz() {
        if(factoryClazz == null)
            throw new IllegalArgumentException(String.format("Property \"factoryClazz\" is not defined for value %s!", this.name()));
        return factoryClazz;
    }
    public BioRequestFactory getFactory() throws Exception {
        if(factory == null)
            factory = factoryClazz.newInstance();
        return factory;
    }
}
