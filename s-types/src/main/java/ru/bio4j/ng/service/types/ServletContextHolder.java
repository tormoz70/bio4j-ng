package ru.bio4j.ng.service.types;

import javax.servlet.ServletContext;

public class ServletContextHolder {
    private static final ThreadLocal<ServletContext> threadLocalScope = new  ThreadLocal<>();

    public final static ServletContext getServletContext() {
        return threadLocalScope.get();
    }

    public final static void setServletContext(ServletContext context) {
        threadLocalScope.set(context);
    }
}
