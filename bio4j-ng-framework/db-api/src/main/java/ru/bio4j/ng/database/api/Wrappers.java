package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.SQLException;
import java.util.List;

public interface Wrappers {

    Wrapper getWrapper(WrapQueryType wrapQueryType) throws SQLException;
}
