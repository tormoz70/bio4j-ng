package ru.bio4j.ng.router;

import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

/**
 * Created by ayrat on 08.05.14.
 */
public enum BioRoute {
    UNKNOWN("unknown", null),
    CRUD_DATA_GET("crud.dt.gt", BioRequestJStoreGet.class);

    private String alias;
    private Class<? extends BioRequest> clazz;

    private BioRoute(String alias, Class<? extends BioRequest> clazz) {
        if(isNullOrEmpty(alias))
            throw new IllegalArgumentException(String.format("Argument \"%s\" cannot be null!", "alias"));
        this.alias = alias;
        this.clazz = clazz;
    }

    public static BioRoute getType(String alias) {
        if(!isNullOrEmpty(alias))
            for (BioRoute v : BioRoute.values())
                if(v.alias.equals(alias.toLowerCase()))
                    return v;
        return UNKNOWN;
    }

    public Class<? extends BioRequest> getClazz() {
        return clazz;
    }
}
