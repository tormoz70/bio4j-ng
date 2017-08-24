package ru.bio4j.ng.content.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.collections.KeyValue;
import ru.bio4j.ng.commons.collections.Pair;
import ru.bio4j.ng.content.io.FileLoader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.bio4j.ng.content.io.FileLoader.buildPath;

public class QueryExtractor {

    private final static String QUERY_ENCODING_NAME = "windows-1251";
    public final static String QUERY_NAME = "(?<=^/\\*\\$\\{sql\\.).+(?=\\}\\*/$)";
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
        final List<String> strings = Files.readAllLines(fullPath, Charset.forName(QUERY_ENCODING_NAME));
        StringBuilder currentQueryText = null;
        final Map<String, StringBuilder> queries = new HashMap<>();
        boolean curSqlOpened = false;
        boolean curLineIsSqlName = false;
        for (String string : strings) {
            final Matcher matcher = pattern.matcher(string.trim());
            if (matcher.find()) {
                String currentQuery = matcher.group();
                currentQueryText = new StringBuilder();
                queries.put(currentQuery, currentQueryText);
                LOG.debug("currentQuery = {}", currentQuery);
                curSqlOpened = true;
                curLineIsSqlName = true;
            } else
                curLineIsSqlName = false;
            if (string.trim().equals("/"))
                curSqlOpened = false;
            if (curSqlOpened && !curLineIsSqlName) {
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
