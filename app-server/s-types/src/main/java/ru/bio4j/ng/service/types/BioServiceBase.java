package ru.bio4j.ng.service.types;

import ru.bio4j.ng.service.api.BioService;

public abstract class BioServiceBase implements BioService {
    protected volatile boolean redy;

    @Override
    public boolean isRedy() {
        return redy;
    }

}
