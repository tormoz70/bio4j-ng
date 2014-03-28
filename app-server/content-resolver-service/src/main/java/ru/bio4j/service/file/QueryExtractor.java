package ru.bio4j.service.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.collections.KeyValue;
import ru.bio4j.collections.Pair;
import ru.bio4j.service.file.io.FileLoader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.bio4j.service.file.io.FileLoader.buildPath;

public class QueryExtractor {

    public final static String QUERY_NAME = "/\\*#(.*)\\*+/";
    private final static String SEPARATOR = System.lineSeparator();
    private static final Logger LOG = LoggerFactory.getLogger(QueryExtractor.class);


    public static Map<String, String> loadQueries(String code, String path) throws IOException {
        final Path fullPath = buildPath(code, path, FileLoader.SQL_EXTENSION);
        LOG.info("fullPath = {}", fullPath);
        if (fullPath != null) {
            return buildQueriesMap(fullPath);
        } else {
            return null;
        }
    }

    public static Pair<String, String> extractName(String code) {
        final int end = code.lastIndexOf('.');
        final String fileName = code.substring(0, end);
        final String name = code.substring(end + 1);
        return new KeyValue<>(name, fileName);
    }

    private static Map<String, String> buildQueriesMap(Path fullPath) throws IOException {
        final Pattern pattern = Pattern.compile(QUERY_NAME);
        final List<String> strings = Files.readAllLines(fullPath, Charset.defaultCharset());
        StringBuilder currentQueryText = null;
        final Map<String, StringBuilder> queries = new HashMap<>();
        for (String string : strings) {
            final Matcher matcher = pattern.matcher(string);
            if (matcher.find()) {
                String currentQuery = matcher.group(1);
                currentQueryText = new StringBuilder();
                queries.put(currentQuery, currentQueryText);
                LOG.debug("currentQuery = {}", currentQuery);
            } else {
                if (currentQueryText == null) {
                    throw new IllegalArgumentException("query name must be fist");
                }
                currentQueryText.append(string).append(SEPARATOR);
            }
        }
        final Map<String, String> queriesResult = new HashMap<>();
        for (Map.Entry<String, StringBuilder> entry : queries.entrySet()) {
            queriesResult.put(entry.getKey(), entry.getValue().toString());
        }
        return queriesResult;
    }

}
