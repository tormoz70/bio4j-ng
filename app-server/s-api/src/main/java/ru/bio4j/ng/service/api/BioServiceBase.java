package ru.bio4j.ng.service.api;

public abstract class BioServiceBase implements BioService {
    protected volatile boolean redy;

    @Override
    public boolean isRedy() {
        return redy;
    }

}
