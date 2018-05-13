package ru.bio4j.ng.crudhandlers.impl;

import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;

public class StoreDataFactory {
    public static StoreData storeData(){
        StoreData storeData = new StoreData();
        storeData.setMetadata(new StoreMetadata());
        return storeData;
    }
}
