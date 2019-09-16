package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;
import java.util.List;

public interface SortingWrapper {

    String wrap(String sql, List<Sort> sort, List<Field> fields);
}
