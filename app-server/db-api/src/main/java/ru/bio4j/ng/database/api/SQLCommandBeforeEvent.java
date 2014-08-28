package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.util.List;

public interface SQLCommandBeforeEvent {

    public static class Attributes {
        private List<Param> params;
        private Boolean cancel;

        public Attributes(Boolean cancel, List<Param> params) {
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

    void handle(SQLCommand sender, Attributes attrs);
}
