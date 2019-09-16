package ru.bio4j.ng.database.commons;

import java.sql.SQLException;

public class SQLExceptionExt extends RuntimeException {
    private int sqlErrorCode = 0;
    private SQLExceptionExt(String msg, Exception parentException){
        super(msg, parentException);
        if(parentException instanceof java.sql.SQLException)
            this.sqlErrorCode = ((java.sql.SQLException)parentException).getErrorCode();
    }

    private SQLExceptionExt(Exception parentException){
        super(parentException);
        if(parentException instanceof java.sql.SQLException)
            this.sqlErrorCode = ((java.sql.SQLException)parentException).getErrorCode();
    }

    public static SQLExceptionExt create(Exception parentException){
        if(parentException instanceof SQLExceptionExt)
            return (SQLExceptionExt)parentException;
        else
            return new SQLExceptionExt(parentException);
    }

    public static SQLExceptionExt create(String msg, Exception parentException){
        if(parentException instanceof SQLExceptionExt)
            return (SQLExceptionExt)parentException;
        else
            return new SQLExceptionExt(msg, parentException);
    }

    public SQLExceptionExt(String msg, int errorCode){
        super(msg);
        this.sqlErrorCode = errorCode;
    }

    public SQLExceptionExt(String msg){
        super(msg);
    }

    public int getErrorCode() {
        return sqlErrorCode;
    }

    @Override
    public String getMessage() {
        return String.format("%s\nCause: %s", super.getMessage(), getCause().getMessage());
    }
}
