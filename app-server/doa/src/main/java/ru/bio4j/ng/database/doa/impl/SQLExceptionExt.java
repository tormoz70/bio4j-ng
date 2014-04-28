package ru.bio4j.ng.database.doa.impl;

public class SQLExceptionExt extends java.sql.SQLException {
    private int sqlErrorCode;
    public SQLExceptionExt(String msg, java.sql.SQLException parentException){
        super(msg, parentException);
        this.sqlErrorCode = parentException.getErrorCode();
    }

    @Override
    public int getErrorCode() {
        return sqlErrorCode;
    }
}
