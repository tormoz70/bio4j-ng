package ru.bio4j.ng.service.api;

import ru.bio4j.ng.database.api.BioCursor;

import java.io.IOException;

public interface FileContentResolver extends BioService {

    BioCursor getCursor(String bioCode) throws Exception;

}
