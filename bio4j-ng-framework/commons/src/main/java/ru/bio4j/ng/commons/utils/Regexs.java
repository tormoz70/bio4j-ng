package ru.bio4j.ng.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regexs {
    public static Matcher match(String line, String regex, int flags) {
        Pattern pattern = Pattern.compile(regex, flags);
        return pattern.matcher(line);
    }

    public static String find(String line, String regex, int flags) {
        Matcher m = match(line, regex, flags);
        return m.find() ? m.group() : null;
    }

    public static int pos(String line, String regex, int flags) {
        Matcher m = match(line, regex, flags);
        return m.find() ? m.start() : -1;
    }

    public static String replace(String line, String regex, String replacement, int flags) {
        Matcher m = match(line, regex, flags);
        return m.replaceAll(replacement);
    }

}
