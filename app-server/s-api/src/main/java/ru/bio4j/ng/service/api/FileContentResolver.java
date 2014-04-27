package ru.bio4j.ng.service.api;

import java.io.IOException;

public interface FileContentResolver {

    String getQueryContent(String bioCode) throws IOException;

    String getContent(String bioCode) throws IOException;
}
