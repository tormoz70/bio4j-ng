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
public interface SQLCommandAfterEvent {

    public static class Attributes {
        private List<Param> params;
        private Object resultScalarValue;
        private Exception exception;

        public static Attributes build (List<Param> params, Object resultValue, Exception ex) {
            Attributes rslt = new Attributes();
            rslt.params = params;
            rslt.resultScalarValue = resultValue;
            rslt.exception = ex;
            return rslt;
        }

        public static Attributes build (List<Param> params, Exception ex) {
            Attributes rslt = new Attributes();
            rslt.params = params;
            rslt.exception = ex;
            return rslt;
        }

        public List<Param> getParams() {
            return params;
        }

        public Object getResultScalarValue() {
            return resultScalarValue;
        }

        public Exception getException() {
            return exception;
        }
    }

    void handle(SQLCommand sender, Attributes attrs);
}
