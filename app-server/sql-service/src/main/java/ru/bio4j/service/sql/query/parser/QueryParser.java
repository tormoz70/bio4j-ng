package ru.bio4j.service.sql.query.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.collections.Parameter;
import ru.bio4j.func.Function;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.result.ColumnImpl;
import ru.bio4j.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.bio4j.util.Strings.empty;

/**
 * Обработчик запроса, на данный момент занимается извлечением параметров из запроса
 *
 * @title Обработчик запроса, извлекающий параметры из запроса
 */
public class QueryParser {
    private static final Logger LOG = LoggerFactory.getLogger(QueryParser.class);

    private static final char PARAM_RESULT = '@';
    private static final char PARAM_INLINE = '$';
    private static final String ESC_START = "{";
    private static final String ESC_STOP = "}";
    private static final String PARAM_OUTER = "OUT";
    private static final String COMMENT = "/*";

    private static final String REGEX = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";

    /**
     * @param query
     * @param variables
     * @return Разобранный запрос
     * @title Разбор запроса
     */
    public ParsedQuery parse(Query query, Function<String, Parameter> variables) {
        final String sql = query.getSql();
        final int len = sql.length();
        final StringBuilder out = new StringBuilder(len);
        final ParsedQueryImpl.Builder builder = ParsedQueryImpl.builder();

        final Pattern pattern = Pattern.compile(REGEX, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(sql);
        if (matcher.groupCount() == 0) {
            out.append(query);
        } else {
            int lastEnd = 0;
            boolean hasEscapedCharacters;
            while (matcher.find()) {
                final int start = matcher.start();
                final String expr = matcher.group();
                hasEscapedCharacters = expr.contains(ESC_START) || !expr.contains(ESC_STOP);
                if (hasEscapedCharacters) {
                    out.append(sql.substring(lastEnd, start));
                    final ParsedParameter param = processExpression(variables, builder.getParametersCount() + 1, expr, query);
                    if (param != null) {
                        LOG.debug("added parameter {}", param);
                        builder.addParameter(param);
                        out.append('?');
                    }
                }
                lastEnd = matcher.end();
            }
            out.append(sql.substring(lastEnd, len));
        }


        return builder.query(out.toString()).build();
    }

    /**
     * @param parameters
     * @param position
     * @param expression
     * @param query
     * @return Обработанный параметр запроса
     * @title Обработка выражения
     */
    private ParsedParameter processExpression(final Function<String, Parameter> parameters, final int position, final String expression, final Query query) {
        final String trimmedExpression = expression.substring(COMMENT.length(), expression.length() - COMMENT.length()).replace("{", "").replace("}", "");
        final int coma = trimmedExpression.indexOf(',');
        String args = null;
        String paramName = trimmedExpression;
        if (coma >= 0) {
            args = trimmedExpression.substring(coma + 1);
            paramName = trimmedExpression.substring(0, coma);
        }
        if (paramName == null) {
            return null;
        }
        final char paramType = paramName.charAt(0);
        paramName = paramName.substring(1).toUpperCase();
        switch (paramType) {
            case PARAM_INLINE:
                final Parameter parameter = parameters.apply(paramName);
                if (parameter == null  && empty(args)) {
                    LOG.warn("Can't find parameter with name {} at position # {}", paramName, position);
                    return null;
                }
                ParsedParameterImpl.Builder builder = ParsedParameterImpl.builder()
                        .name(paramName)
                        .parameter(parameter)
                        .position(position)
                        .input(true);
                if (!empty(args)) {
                    for (String arg : Strings.iterable(args, ',')) {
                        arg = arg.trim();
                        paramResolver(arg, builder);
                    }
                }
                return builder.build();
            case PARAM_RESULT:
                ColumnImpl.Builder cBuilder = ColumnImpl.builder()
                        .field(paramName);
                if (!empty(args)) {
                    for (String arg : Strings.iterable(args, ',')) {
                        resultParamResolver(arg.trim(), cBuilder);
                    }
                }
                query.addResult(cBuilder.build());
        }
        return null;
    }

    private void paramResolver(final String arg, final ParsedParameterImpl.Builder builder) {
        final String key = parseKey(arg);
        final String value = parseValue(arg);
        switch (key) {
            case "TYPE":
                char c = value.charAt(0);
                if (c == '-' || Character.isDigit(c)) {//цифровое обозначение типа
                    builder.sqlType(Integer.valueOf(value));
                } else {
                    builder.sqlTypeName(value);
                }
                break;
            case "METATYPE":
                builder.metaType(value);
                break;
            case PARAM_OUTER:
                builder.output(true);
                break;
        }
    }

    private void resultParamResolver(final String arg, final ColumnImpl.Builder builder) {
        final String key = parseKey(arg);
        final String value = parseValue(arg);
        switch (key) {
            case "TITLE":
                builder.title(value);
                break;
            case "JAVATYPE":
                builder.typeName(value);
                break;
            default:
                if (!empty(key) && !empty(value)) {
                    builder.addAttribute(key, value);
                }
                break;
        }
    }

    private String parseKey(String arg) {
        final int to = arg.indexOf('=') == -1 ? arg.length() : arg.indexOf('=');
        final String key = arg.toUpperCase().substring(0, to);
        return key.trim();
    }

    private String parseValue(String arg) {
        final String value = arg.substring(arg.indexOf('=') + 1);
        if (!empty(value.trim())) {
            return value;
        }
        return null;
    }
}
