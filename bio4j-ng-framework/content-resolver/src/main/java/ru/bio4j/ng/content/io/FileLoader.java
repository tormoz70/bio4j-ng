package ru.bio4j.ng.content.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
* Класс загружающий файл по коду и находящий код по файлу
*
* */
public class FileLoader {

    private static final Logger LOG = LoggerFactory.getLogger(FileLoader.class);
//    public static final String SQL_EXTENSION = ".sql";
    public static final String XML_EXTENSION = ".xml";

//    public static String loadFile(String code, String path) throws IOException {
//        final Path fullPath = buildPath(code, path, "");
//        if (fullPath != null) {
//            return new String(Files.readAllBytes(fullPath));
//        } else {
//            return null;
//        }
//    }

    public static String buildCode(Path path, String rootDir) {
        final String stringPath = path.toString();
        final int start = stringPath.indexOf(rootDir) + File.pathSeparator.length();
        final int end = stringPath.length() - XML_EXTENSION.length();
        final String result = stringPath.substring(start, end).replace(File.separator, ".");
        LOG.debug("Code is {}", result);
        return result;
    }

}
