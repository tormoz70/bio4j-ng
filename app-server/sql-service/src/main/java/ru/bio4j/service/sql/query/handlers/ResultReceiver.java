package ru.bio4j.service.sql.query.handlers;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;

/**
 * Приемник результата.
 * @title Приемник результата
 * @author rad
 */
public interface ResultReceiver<R, V> {

    /**
     * @title Прием результата
     * @param r
     * @param query
     * @param context
     * @return Экземпляр класса результата запроса
     * @throws Exception
     */
    V handle(R r, Query query, QueryContext context) throws Exception;
}
