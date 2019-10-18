package ru.bio4j.ng.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regexs {
    public static Matcher match(String line, String regex, int flags) {
        if(Strings.isNullOrEmpty(line)) return null;
        Pattern pattern = Pattern.compile(regex, flags);
        return pattern != null ? pattern.matcher(line) : null;
    }

    public static String find(String line, String regex, int flags) {
        Matcher m = match(line, regex, flags);
        return (m != null && m.find()) ? m.group() : null;
    }

    public static List<String> findAll(String line, String regex, int flags) {
        List<String> rslt = new ArrayList<>();
        final Pattern pattern = Pattern.compile(regex, flags);
        final Matcher matcher = pattern.matcher(line);
        if(matcher != null) {
            while (matcher.find())
                rslt.add(matcher.group());
        }
        return rslt;
    }

    public static int pos(String line, String regex, int flags) {
        Matcher m = match(line, regex, flags);
        if(m != null)
            return m.find() ? m.start() : -1;
        return -1;
    }

    public static String replace(String line, String regex, String replacement, int flags) {
        Matcher m = match(line, regex, flags);
        if(m != null)
            return m.replaceAll(replacement);
        return line;
    }

}
