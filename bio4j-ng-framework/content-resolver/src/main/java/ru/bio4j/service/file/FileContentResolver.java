package ru.bio4j.service.file;

import java.io.IOException;

public interface FileContentResolver {

    String getQueryContent(String bioCode) throws IOException;

    String getContent(String bioCode) throws IOException;
}
