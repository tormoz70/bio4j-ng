package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.util.List;

/**
 * Created by ayrat on 11.09.2014.
 */
public class StoredProgMetadata {
    private final String signature;
    private final List<Param> params;
    public StoredProgMetadata(String signature, List<Param> params) {
        this.signature = signature;
        this.params = params;
    }

    public String getSignature() {
        return signature;
    }

    public List<Param> getParams() {
        return params;
    }
}
