package ru.bio4j.service.sql.query.handlers;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;

/**
 * Обработчик запроса. <b>В конструкторе нельзя выделять какие либо ресурсы, </b>
 * так как при проверке условия на допустимость создает экземпляр класса.
 * @title Обработчик запроса
 * @author rad
 */
public interface QueryHandler<R> {
    /**
     * Обрабатывает запрос
     * @title Обработка запроса
     * @param sql
     * @return
     */
    public<T> T handle(Query sql, QueryContext context, ResultReceiver<R, T> handler) throws Exception;
}