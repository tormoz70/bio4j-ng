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
public class SQLCommandBeforeEventAttrs {
    private List<Param> params;
    private Boolean cancel;

    public SQLCommandBeforeEventAttrs(Boolean cancel, List<Param> params) {
        this.cancel = cancel;
        this.params = params;
    }

    public List<Param> getParams() {
        return params;
    }

    public Boolean getCancel() {
        return cancel;
    }
}
