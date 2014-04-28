package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 27.11.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class SQLCommandExecErrorEventAttrs {
    private List<Param> params;
    private Exception exception;

    public static SQLCommandExecErrorEventAttrs build (List<Param> params, Exception ex) {
        SQLCommandExecErrorEventAttrs rslt = new SQLCommandExecErrorEventAttrs();
        rslt.params = params;
        rslt.exception = ex;
        return rslt;
    }

    public List<Param> getParams() {
        return params;
    }

    public Exception getException() {
        return exception;
    }
}
