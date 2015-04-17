package ru.bio4j.ng.database.api;

import java.sql.SQLException;

public interface Wrappers {

    //BioCursor wrapCursor(final BioCursor cursor) throws Exception;
    Wrapper getWrapper(WrapQueryType wrapQueryType) throws SQLException;
}
