package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.util.List;

public interface SQLCommandExecErrorEvent {

    public static class Attributes {
        private List<Param> params;
        private Exception exception;

        public static Attributes build (List<Param> params, Exception ex) {
            Attributes rslt = new Attributes();
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

    void handle(SQLCommand sender, Attributes attrs);
}
