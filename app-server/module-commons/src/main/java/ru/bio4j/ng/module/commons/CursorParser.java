package ru.bio4j.ng.module.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.types.DelegateCheck;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.*;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Alignment;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.database.api.BioCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
        String attrsList = Regexs.find(paramDef, REGEX_ATTRS, 0);
        attrsList = Regexs.replace(attrsList, REGEX_PARAM_KILLDEBUG, "", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
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

    private static void addParamsFromSQLDesc(final BioCursor cursor) throws Exception {
        final String sql = cursor.getSql();
        final StringBuffer out = new StringBuffer(sql.length());
        final Pattern pattern = Pattern.compile(REGEX_PARAMS, Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(sql);
        try(Paramus p = Paramus.set(cursor.getParams());) {
            while (matcher.find()) {
                String text = matcher.group();
                Param param = parseParam(text);
                p.apply(param);
                matcher.appendReplacement(out, ":" + param.getName());
            }
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

    private static void parseCol(List<Field> cols, String colDef) {
        String attrsList = Regexs.find(colDef, REGEX_ATTRS, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE+Pattern.DOTALL);
        // Заменяем все внутренние(экранированные) ковычки на QUOTES_PLACEHOLDER
        attrsList = Regexs.replace(attrsList, REGEX_QUOTES_REPLACER, QUOTES_PLACEHOLDER, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
        // Вытаскиваем имя колонки
        String name = Regexs.find(attrsList, REGEX_COLS_NAME, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
        if(Strings.isNullOrEmpty(name))
            throw new IllegalArgumentException("Attribute \"col.name\" not found in descriptor!");
        name = Strings.split(name, ".")[1].trim().toLowerCase();

        Field col = findCol(name, cols);
        if(col == null) {
            col = new Field();
            cols.add(col);
            col.setName(name);
            col.setId(cols.size());
        }


        attrsList = Regexs.replace(attrsList, REGEX_COLS_NAME, "", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
        // Вытаскиваем title
        String title = Regexs.find(attrsList, REGEX_COLS_TITLE, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
        if(!Strings.isNullOrEmpty(title)) {
            // Удаляем title из атрибутов
            attrsList = Regexs.replace(attrsList, REGEX_COLS_TITLE, "", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
            // Вытаскиваем значение
            title = Strings.split(title, ATTRS_KEYVALUE_DELIMITER)[1].trim();
            // Удаляем ковычки заголовка
            title = Strings.trim(title, "\"");
            // Возвращаем назад внутренние ковычки
            title = Regexs.replace(title, QUOTES_PLACEHOLDER, "\"", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
        }
        // Вытаскиваем format
        String format = Regexs.find(attrsList, REGEX_COLS_FORMAT, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
        if(!Strings.isNullOrEmpty(format)) {
            // Удаляем format из атрибутов
            attrsList = Regexs.replace(attrsList, REGEX_COLS_FORMAT, "", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
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
    }

    private static void addColsFromSQLDesc(final BioCursor cursor) {
        List<Field> cols = cursor.getFields();
        if(cols == null) {
            cols = new ArrayList<>();
            cursor.setFields(cols);
        }
        final Matcher matcher = Regexs.match(cursor.getSql(), REGEX_COLS, Pattern.MULTILINE+Pattern.CASE_INSENSITIVE+Pattern.DOTALL);
        while (matcher.find()) {
            parseCol(cols, matcher.group());
        }
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
            String hints = Regexs.find(matcher.group(), REGEX_ATTRS, 0);
            Matcher m = Regexs.match(hints, REGEX_HINTS_TYPE, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
            if(m.find())
                cursor.setType(parsType(m.group(1)));
            m = Regexs.match(hints, REGEX_HINTS_WRAP, Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
            if(m.find())
                cursor.setWrapMode(parsWrapMode(m.group(0)));

        }
    }

    public static BioCursor pars(final String bioCode, final String sql) throws Exception {
        BioCursor cursor = new BioCursor(bioCode, sql);
        addParamsFromSQLDesc(cursor);
        addColsFromSQLDesc(cursor);
        parsHints(cursor);
        return cursor;
    }

    private static void addParamsFromXml(final BioCursor cursor, final Document document) throws Exception {
        Element sqlElem = Doms.findElem(document, "/cursor/SQL");
        NodeList paramNodes = sqlElem.getElementsByTagName("param");
        try(Paramus p = Paramus.set(cursor.getParams());) {
            for(int i=0; i<paramNodes.getLength(); i++) {
                Element paramElem = (Element)paramNodes.item(i);
                String paramName = Doms.getAttribute(paramElem, "name", "", String.class);
                MetaType paramType = Converter.toType(Doms.getAttribute(paramElem, "type", "string", String.class), MetaType.class);
                Param.Direction paramDir = Converter.toType(Doms.getAttribute(paramElem, "direction", "IN", String.class), Param.Direction.class);
                Param param = p.getParam(paramName, true);
                if(param == null) {
                    param = Param.builder()
                            .name(paramName)
                            .type(paramType)
                            .direction(paramDir)
                            .build();
                    p.add(param);
                } else {
                    param.setType(paramType);
                    param.setDirection(paramDir);
                }
            }
        }
    }

    private static String bkpSubstring(final String str, final String regex, final Stack<BackupPair> bkpSubstrings) {
        final StringBuffer out = new StringBuffer(str.length());
        final Matcher matcher = Regexs.match(str, regex, Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        while (matcher.find()) {
            String foundSubstr = matcher.group();
            String placeHolder = "{backup-substr-before-kill-sql-comments-"+bkpSubstrings.size()+"}";
            bkpSubstrings.push(new BackupPair(placeHolder, foundSubstr));
            matcher.appendReplacement(out, placeHolder);
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private static String restoreSubstring(String str, Stack<BackupPair> bkpSubstrings) {
        while (!bkpSubstrings.empty()) {
            BackupPair bp = bkpSubstrings.pop();
            str = str.replace(bp.placeholder, bp.substring);
        }
        return str;
    }

    private static class BackupPair {
        public String placeholder;
        public String substring;
        public BackupPair(String placeholder, String substring) {
            this.placeholder = placeholder;
            this.substring = substring;
        }
    }

    public static String backupNonSQLSubstringsInSQL(String sql, final Stack<BackupPair> bkpSubstrings) {
        sql = bkpSubstring(sql, "(['])(.*?)\\1", bkpSubstrings); // backup - replace string consts by placeholders
        sql = bkpSubstring(sql, "([\"])(.*?)\\1", bkpSubstrings); // backup - replace double quoted string by placeholders
        sql = bkpSubstring(sql, "--.*$", bkpSubstrings); // backup - single line comments
        sql = bkpSubstring(sql, "\\/\\*.*\\*\\/", bkpSubstrings); // backup - single line comments

        return sql;
    }

    private static void addParamsFromSQLBody(final BioCursor cursor) throws Exception {
        final String sql0 = cursor.getPreparedSql();
        final StringBuffer out = new StringBuffer(sql0.length());
        final List<String> paramsNames = Sqls.extractParamNamesFromSQL(sql0);
        try(Paramus p = Paramus.set(cursor.getParams());) {
            for (String paramActual : paramsNames) {
                Param param = Param.builder()
                        .name(paramActual)
                        .type(MetaType.STRING)
                        .direction(Param.Direction.IN)
                        .build();
                p.add(param);
            }
        }
    }

    private static Field findCol(final String name, final List<Field> cols) {
        return Lists.first(cols, new DelegateCheck<Field>() {
            @Override
            public Boolean callback(Field item) {
                return Strings.compare(name, item.getName(), true);
            }
        });
    }

    private static void addColsFromXml(final BioCursor cursor, final Document document) throws Exception {
        Element sqlElem = Doms.findElem(document, "/cursor/fields");
        NodeList colNodes = sqlElem.getElementsByTagName("field");
        List<Field> cols = cursor.getFields();
        if(cols == null) {
            cols = new ArrayList<>();
            cursor.setFields(cols);
        }
        for(int i=0; i<colNodes.getLength(); i++) {
            Element paramElem = (Element)colNodes.item(i);
            boolean generate = Converter.toType(Doms.getAttribute(paramElem, "generate", "true", String.class), boolean.class);
            if(generate) {
                String fieldName = Doms.getAttribute(paramElem, "name", "", String.class);
                Field col = findCol(fieldName, cols);
                if (col == null) {
                    col = new Field();
                    cols.add(col);
                    col.setName(fieldName);
                }
                col.setId(i + 1);
                col.setFormat(Doms.getAttribute(paramElem, "format", null, String.class));
                col.setTitle(Doms.getAttribute(paramElem, "header", null, String.class));
                col.setType(Converter.toType(Doms.getAttribute(paramElem, "type", "string", String.class), MetaType.class));
                col.setAlign(Converter.toType(Doms.getAttribute(paramElem, "align", "left", String.class), Alignment.class));
                col.setHidden(Converter.toType(Doms.getAttribute(paramElem, "hidden", "false", String.class), boolean.class));
                col.setDefaultVal(Doms.getAttribute(paramElem, "defaultVal", null, String.class));
                col.setPk(Converter.toType(Doms.getAttribute(paramElem, "pk", "false", String.class), boolean.class));
                col.setUseNull(Converter.toType(Doms.getAttribute(paramElem, "useNull", "true", String.class), boolean.class));
                col.setReadonly(Converter.toType(Doms.getAttribute(paramElem, "readOnly", "true", String.class), boolean.class));
                col.setWidth(Doms.getAttribute(paramElem, "width", null, String.class));
            }
        }

    }

    public static BioCursor pars(final String bioCode, final Document document) throws Exception {
//        XPathFactory factory = XPathFactory.newInstance();
//        XPath xPath = factory.newXPath();
        Element sqlTextElem = Doms.findElem(document, "/cursor/SQL/text");
        String sql = sqlTextElem.getTextContent();

        BioCursor cursor = new BioCursor(bioCode, sql);
        addColsFromXml(cursor, document); // добавляем колонки из XML
        addColsFromSQLDesc(cursor); // добавляем колонки описанные в SQL, в коментах
        addParamsFromSQLBody(cursor); // добавляем переменные из SQL
        addParamsFromSQLDesc(cursor); // добавляем переменные описанные в SQL, в коментах
        addParamsFromXml(cursor, document); // добавляем переменные из XML
        parsHints(cursor);

        LOG.debug("BioCursor parsed: \n{}", Utl.buildBeanStateInfo(cursor, "Cursor", "  "));
        return cursor;
    }
}
