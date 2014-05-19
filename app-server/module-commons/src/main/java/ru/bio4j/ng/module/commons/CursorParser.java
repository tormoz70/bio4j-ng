package ru.bio4j.ng.module.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.jstore.Alignment;
import ru.bio4j.ng.model.transport.jstore.Column;
import ru.bio4j.ng.service.api.BioCursor;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CursorParser {
    private static final Logger LOG = LoggerFactory.getLogger(CursorParser.class);


    private static final String ATTRS_DELIMITER = ";";
    private static final String ATTRS_KEYVALUE_DELIMITER = ":";

    private static final String PARAM_PREFIX = "param.";
    private static final String REGEX_PARAMS = "(/\\*\\$\\{"+PARAM_PREFIX+".*?\\}\\*/)";
    private static final String REGEX_ATTRS = "(?<=/\\*\\$\\{).*(?=\\}\\*/)";
    private static final String REGEX_PARAM_KILLDEBUG = "debug:\\*/.*/\\*";

    private static Param parseParam(String paramDef) {
        String attrsList = Regexs.find(paramDef, REGEX_ATTRS);
        attrsList = Regexs.replace(attrsList, REGEX_PARAM_KILLDEBUG, "", true, true);
        String[] attrs = Strings.split(attrsList, ATTRS_DELIMITER);
        String name = null;
        MetaType type = null;
        String dir = "IN";
        for(String attr : attrs){
            String[] pair = Strings.split(attr.trim(), ATTRS_KEYVALUE_DELIMITER);
            if(pair.length == 1 && pair[0].startsWith(PARAM_PREFIX))
                name = pair[0].substring(PARAM_PREFIX.length()).trim().toLowerCase();
            if(pair.length == 2){
                if(pair[0].equals("type"))
                    type = MetaType.decode(pair[1].trim());
                if(pair[0].equals("dir"))
                    dir = pair[1].trim().toUpperCase();
            }
        }
        return Param.builder()
                .name(name)
                .type(type)
                .direction(Param.Direction.valueOf(dir))
                .build();
    }

    private static void parsParams(final BioCursor cursor) {
        final String sql = cursor.getSql();
        final StringBuffer out = new StringBuffer(sql.length());
        final Pattern pattern = Pattern.compile(REGEX_PARAMS, Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String text = matcher.group();
            Param param = parseParam(text);
            cursor.getParams().add(param);
            matcher.appendReplacement(out, ":" + param.getName());
        }
        matcher.appendTail(out);
        cursor.setPreparedSql(out.toString());
    }

    private static final String COL_PREFIX = "col.";
    private static final String REGEX_COLS = "(/\\*\\$\\{"+COL_PREFIX+".*?\\}\\*/)";
    private static final String REGEX_COLS_TITLE = "title:\".*?\"";
    private static final String REGEX_COLS_FORMAT = "format:\".*?\"";
    private static final String REGEX_COLS_NAME = "\\bcol.\\w+\\b";
    private static final String REGEX_QUOTES_REPLACER = "\\\\\"";
    private static final String QUOTES_PLACEHOLDER = "\\$quote\\$";

    private static Column parseCol(String colDef) {
        Column col = new Column();
        String attrsList = Regexs.find(colDef, REGEX_ATTRS);
        // Заменяем все внутренние(экранированные) ковычки на QUOTES_PLACEHOLDER
        attrsList = Regexs.replace(attrsList, REGEX_QUOTES_REPLACER, QUOTES_PLACEHOLDER, true, true);
        // Вытаскиваем имя колонки
        String name = Regexs.find(attrsList, REGEX_COLS_NAME, true, true);
        if(Strings.isNullOrEmpty(name))
            throw new IllegalArgumentException("Attribute \"col.name\" not found in descriptor!");
        name = Strings.split(name, ".")[1].trim().toLowerCase();
        attrsList = Regexs.replace(attrsList, REGEX_COLS_NAME, "", true, true);
        // Вытаскиваем title
        String title = Regexs.find(attrsList, REGEX_COLS_TITLE, true, true);
        if(!Strings.isNullOrEmpty(title)) {
            // Удаляем title из атрибутов
            attrsList = Regexs.replace(attrsList, REGEX_COLS_TITLE, "", true, true);
            // Вытаскиваем значение
            title = Strings.split(title, ATTRS_KEYVALUE_DELIMITER)[1].trim();
            // Удаляем ковычки заголовка
            title = Strings.trim(title, "\"");
            // Возвращаем назад внутренние ковычки
            title = Regexs.replace(title, QUOTES_PLACEHOLDER, "\"", true, true);
        }
        // Вытаскиваем format
        String format = Regexs.find(attrsList, REGEX_COLS_FORMAT, true, true);
        if(!Strings.isNullOrEmpty(format)) {
            // Удаляем format из атрибутов
            attrsList = Regexs.replace(attrsList, REGEX_COLS_FORMAT, "", true, true);
            // Вытаскиваем значение format
            format = Strings.split(format, ATTRS_KEYVALUE_DELIMITER)[1].trim();
            // Удаляем ковычки формата
            format = Strings.trim(format, "\"");
        }
        col.setName(name);
        col.setTitle(title);
        col.setFormat(format);

        String[] attrs = Strings.split(attrsList, ATTRS_DELIMITER);
        for(String attr : attrs){
            String[] pair = Strings.split(attr.trim(), ATTRS_KEYVALUE_DELIMITER);
            if(pair.length == 2){
                if(pair[0].equals("type"))
                    col.setType(MetaType.decode(pair[1].trim()));
                if(pair[0].equals("pk"))
                    col.setPk(Boolean.parseBoolean(pair[1].trim()));
                if(pair[0].equals("mandatory"))
                    col.setMandatory(Boolean.parseBoolean(pair[1].trim()));
                if(pair[0].equals("align"))
                    col.setAlign(Alignment.valueOf(pair[1].trim().toUpperCase()));
                if(pair[0].equals("width"))
                    col.setWidth(pair[1].trim());
                if(pair[0].equals("hidden"))
                    col.setHidden(Boolean.parseBoolean(pair[1].trim()));
                if(pair[0].equals("readonly"))
                    col.setReadonly(Boolean.parseBoolean(pair[1].trim()));
            }
        }
        return col;
    }

    private static void parsCols(final BioCursor cursor) {
        List<Column> cols = new ArrayList<>();
        final Pattern pattern = Pattern.compile(REGEX_COLS, Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(cursor.getSql());
        while (matcher.find())
            cols.add(parseCol(matcher.group()));
        cursor.getMetadata().setColumns(cols);
    }

    private static final String REGEX_HINTS = "(/\\*\\$\\{hints\\s+.*?\\}\\*/)";
    private static final String REGEX_HINTS_TYPE = "\\btype:(select|exec)\\b";
    private static final String REGEX_HINTS_WRAP = "\\bwrap:[\\w\\+]+\\b";

    private static BioCursor.Type parsType(String hint) {
        return BioCursor.Type.valueOf(hint.toUpperCase());
    }
    private static byte parsWrapMode(String hint) {
        final String wrapPrefix = "wrap:";
        hint = hint.trim().toLowerCase();
        if(hint.startsWith(wrapPrefix)){
            hint = hint.substring(wrapPrefix.length());
            String[] flags = Strings.split(hint, "+");
            byte rslt = 0;
            for(String flag : flags){
                BioCursor.WrapMode mode = BioCursor.WrapMode.NONE;
                try {
                    mode = BioCursor.WrapMode.valueOf(flag.toUpperCase());
                } catch (IllegalArgumentException e) {}
                if(mode == BioCursor.WrapMode.ALL) {
                    rslt = BioCursor.WrapMode.ALL.code();
                    break;
                } else
                    rslt += mode.code();
            }
            return rslt;
        }
        throw new IllegalArgumentException(String.format("Hint %s can't be interpreted as WrapMode!", hint));
    }

    private static void parsHints(final BioCursor cursor) {
        final Pattern pattern = Pattern.compile(REGEX_HINTS, Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(cursor.getSql());
        if (matcher.find()) {
            String hints = Regexs.find(matcher.group(), REGEX_ATTRS);
            Matcher m = Regexs.match(hints, REGEX_HINTS_TYPE, true, true);
            if(m.find())
                cursor.setType(parsType(m.group(1)));
            m = Regexs.match(hints, REGEX_HINTS_WRAP, true, true);
            if(m.find())
                cursor.setWrapMode(parsWrapMode(m.group(0)));

        }
    }

    public static BioCursor pars(final String bioCode, final String sql) throws Exception {
        BioCursor cursor = new BioCursor(bioCode, sql);
        parsParams(cursor);
        parsCols(cursor);
        parsHints(cursor);
        return cursor;
    }
    public static BioCursor pars(final String bioCode, final Document document) throws Exception {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        String sql = xPath.evaluate("/cursor/SQL/text/text()", document);
        BioCursor cursor = new BioCursor(bioCode, sql);
        parsParams(cursor);
        parsCols(cursor);
        parsHints(cursor);

        return cursor;
    }
}
