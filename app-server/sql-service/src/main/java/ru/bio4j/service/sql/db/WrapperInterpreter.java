package ru.bio4j.service.sql.db;

import ru.bio4j.model.transport.jstore.Sort;
import ru.bio4j.model.transport.jstore.filter.Expression;

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
    String filterToSQL(String alias, Expression filter);
    String sortToSQL(String alias, Sort sort);
}
