package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.BioError;

public class BioSQLException extends BioError {
    public BioSQLException(String msg, Exception parentException){
        super(msg, parentException);
        if(parentException instanceof java.sql.SQLException)
            this.errorCode = ((java.sql.SQLException)parentException).getErrorCode();
    }

    public BioSQLException(Exception parentException){
        super(parentException);
        if(parentException instanceof java.sql.SQLException)
            this.errorCode = ((java.sql.SQLException)parentException).getErrorCode();
    }

    public static BioSQLException create(Exception parentException){
        if(parentException instanceof BioSQLException)
            return (BioSQLException)parentException;
        else
            return new BioSQLException(parentException);
    }

    public static BioSQLException create(String msg, Exception parentException){
        if(parentException instanceof BioSQLException)
            return (BioSQLException)parentException;
        else
            return new BioSQLException(msg, parentException);
    }

    public BioSQLException(int errorCode, String msg){
        super(msg);
        this.errorCode = errorCode;
    }

    public BioSQLException(String msg){
        super(msg);
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        String causeMsg = getCause() != null ? getCause().getMessage() : null;
        if(causeMsg != null)
            return String.format("%s\nCause: %s", super.getMessage(), causeMsg);
        else
            return super.getMessage();
    }

}
