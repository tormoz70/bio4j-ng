package ru.bio4j.service.sql.query.wrappers;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;

import java.sql.SQLException;

/**
 * @title Интерфейс обертки запросов
 */
public interface Wrapper{

    /**
     * @title "Оборачивание" запроса
     * @param src
     * @return Обернутый запрос
     */
    Query wrap(QueryContext context, Query src) throws SQLException;
}
