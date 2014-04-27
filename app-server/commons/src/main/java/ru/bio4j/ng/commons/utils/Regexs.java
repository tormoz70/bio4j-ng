package ru.bio4j.ng.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regexs {
    public static Matcher match(String line, String regex, boolean ignoreCase, boolean multiline, boolean dotall) {
        int flags = ((ignoreCase) ? Pattern.CASE_INSENSITIVE : 0) |
                ((multiline) ? Pattern.MULTILINE : 0) |
                ((dotall) ? Pattern.DOTALL : 0);
        Pattern pattern = Pattern.compile(regex, flags);
        return pattern.matcher(line);
    }
    public static Matcher match(String line, String regex, boolean ignoreCase, boolean multiline) {
        return match(line, regex, ignoreCase, multiline, true);
    }
    public static Matcher match(String line, String regex, boolean ignoreCase) {
        return match(line, regex, ignoreCase, true, true);
    }
    public static Matcher match(String line, String regex) {
        return match(line, regex, true, true, true);
    }

    public static String find(String line, String regex, boolean ignoreCase, boolean multiline, boolean dotall) {
        Matcher m = match(line, regex, ignoreCase, multiline, dotall);
        return m.find() ? m.group() : null;
    }
    public static String find(String line, String regex, boolean ignoreCase, boolean multiline) {
        return find(line, regex, ignoreCase, multiline, true);
    }
    public static String find(String line, String regex, boolean ignoreCase) {
        return find(line, regex, ignoreCase, true, true);
    }
    public static String find(String line, String regex) {
        return find(line, regex, true, true, true);
    }

    public static int pos(String line, String regex, boolean ignoreCase, boolean multiline, boolean dotall) {
        Matcher m = match(line, regex, ignoreCase, multiline, dotall);
        return m.find() ? m.start() : -1;
    }
    public static int pos(String line, String regex, boolean ignoreCase, boolean multiline) {
        return pos(line, regex, ignoreCase, multiline, true);
    }
    public static int pos(String line, String regex, boolean ignoreCase) {
        return pos(line, regex, ignoreCase, true, true);
    }
    public static int pos(String line, String regex) {
        return pos(line, regex, true, true, true);
    }

    public static String replace(String line, String regex, String replacement, boolean ignoreCase, boolean multiline, boolean dotall) {
        Matcher m = match(line, regex, ignoreCase, multiline, dotall);
        return m.replaceAll(replacement);
    }
    public static String replace(String line, String regex, String replacement, boolean ignoreCase, boolean multiline) {
        return replace(line, regex, replacement, ignoreCase, multiline, true);
    }
    public static String replace(String line, String regex, String replacement, boolean ignoreCase) {
        return replace(line, regex, replacement, ignoreCase, true, true);
    }
    public static String replace(String line, String regex, String replacement) {
        return replace(line, regex, replacement, true, true, true);
    }

}
