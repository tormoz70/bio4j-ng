package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.model.transport.User;

import javax.servlet.ServletContext;

public class ServletContextHolder {
    private static final ThreadLocal<ServletContext> threadLocalScope = new  ThreadLocal<>();
    private static final ThreadLocal<User> threadLocalUser = new  ThreadLocal<>();

    public final static ServletContext getServletContext() {
        return threadLocalScope.get();
    }

    public final static User getCurrentUser() {
        return threadLocalUser.get();
    }

    public final static void setServletContext(ServletContext context) {
        threadLocalScope.set(context);
    }

    public final static void setCurrentUser(User user) {
        threadLocalUser.set(user);
    }
}
