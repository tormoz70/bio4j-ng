package ru.bio4j.ng.commons.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
