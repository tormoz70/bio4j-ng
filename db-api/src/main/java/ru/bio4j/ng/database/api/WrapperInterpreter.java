package ru.bio4j.ng.database.api;


import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.util.List;

/**
 * Интерпретатор объектов Filter, Sort
 * Генерит SQL операторы для WHERE, ORDER BY соответственно
 */
public interface WrapperInterpreter {
    /**
     * Интерпретирует filter.Expression to SQL
     * @param alias
     * @param filter
     * @return
     */
    String filterToSQL(String alias, Filter filter, List<Field> fields);
    String sortToSQL(String alias, List<Sort> sort, List<Field> fields);
}
