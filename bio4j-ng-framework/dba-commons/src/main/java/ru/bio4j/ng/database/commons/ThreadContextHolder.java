package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.model.transport.User;

import java.sql.Connection;
import java.util.Stack;

public class ThreadContextHolder {

    private static final ThreadContextHolder instance = new ThreadContextHolder();

    private ThreadContextHolder() {
    }

    public static ThreadContextHolder instance(){
        return instance;
    }

    private static class DbThreadContext {
        public User user;
        public Connection connection;
    }

    private final ThreadLocal<Stack<DbThreadContext>> context = new ThreadLocal<>();

    public void setContext(User user, Connection connection){
        if(context.get() == null)
            context.set(new Stack<>());
        DbThreadContext newContext = new DbThreadContext();
        newContext.user = user;
        newContext.connection = connection;
//        context.get().add(newContext);
        context.get().push(newContext);
    }

    private void checkContext() {
        if(context.get() == null)
            throw new IllegalArgumentException("Call set() method to set context first!");
    }

    public User getCurrentUser(){
        checkContext();
//        return context.get().get(0).user;
        return context.get().peek().user;
    }

    public Connection getCurrentConnection(){
        checkContext();
//        return context.get().get(0).connection;
        return context.get().peek().connection;
    }

    public void close(){
        checkContext();
//        context.get().get(0);
        context.get().pop();
    }

}
