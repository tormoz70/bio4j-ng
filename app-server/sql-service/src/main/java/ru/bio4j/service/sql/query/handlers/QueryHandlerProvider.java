package ru.bio4j.service.sql.query.handlers;

/**
 * Провайдер сервисов QueryHandler
 */
public interface QueryHandlerProvider {

    /**
     * @title Получение обработчика запроса
     * @param query
     * @return Обработчик запроса
     */
    QueryHandler get(String query);
}