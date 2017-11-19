package ru.bio4j.ng.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sqls {

    public static final int UNKNOWN_RECS_TOTAL = 999999999;

    public static String deleteNonSQLSubstringsInSQL(String sql) {
        //sql = Regexs.replace(sql, "(')(.*?)\\1", "", Pattern.MULTILINE); // replace string consts by placeholders
        //sql = Regexs.replace(sql, "(\")(.*?)\\1", "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE); // replace double quoted string by placeholders
        //sql = Regexs.replace(sql, "--.*$", "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE); // replace singleline comments
        //sql = Regexs.replace(sql, "\\/\\*.*\\*\\/", "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL); // replace multiline comments
        sql = Regexs.replace(sql, "(\\/\\*([^*]|[\\r\\n]|(\\*+([^*\\/]|[\\r\\n])))*\\*+\\/)|'(?:[^']|'')*'|(--.*)", "", Pattern.MULTILINE);
        sql = Regexs.replace(sql, ":=", "", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL); // replace multiline comments
        return sql;
    }

    public static List<String> extractParamNamesFromSQL(String sql) {
        List<String> rslt = new ArrayList();

        final String doubleDotsPlaceholder = "/$doubleDotsPlaceholder$/";
        final String assignsPlaceholder = "/$assignsPlaceholder$/";
        String preparedQuery = Strings.replace(sql, "::", doubleDotsPlaceholder);
        preparedQuery = Strings.replace(preparedQuery, ":=", assignsPlaceholder);
        String clearSql = Sqls.deleteNonSQLSubstringsInSQL(preparedQuery);

//        sql = deleteNonSQLSubstringsInSQL(sql);

//        LOG.debug("Находим все параметры вида :qwe_ad");
        Matcher m = Regexs.match(clearSql, "(?<=:)\\b[\\w\\#\\$]+", Pattern.CASE_INSENSITIVE);
        while(m.find()) {
            String parName = m.group();
//            LOG.debug(" - parName["+m.start()+"]: " + parName);
            if(rslt.indexOf(parName) == -1)
                rslt.add(parName);
        }
//        LOG.debug("Найдено: " + rslt.size() + " параметров");
        return rslt;
    }

}

