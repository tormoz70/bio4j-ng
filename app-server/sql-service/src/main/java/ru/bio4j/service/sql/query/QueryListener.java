package ru.bio4j.service.sql.query;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;

/**
 * Слушатель SQL-Запросов выполняемых через {@link QueryHelper }
 * @title Слушатель SQL-Запросов выполняемых через QueryHelper
 */
public interface QueryListener {

    /**
     * Взывается перед выполнением запроса.
     * @param context
     * @param query запрос который будет выполнен сразу после выхода из этого метода
     * @title Обработка события начала выполнения запроса
     */
    void preQuery(QueryContext context, Query query);

    /**
     * Вызывается сразу после получения и обработки результатов запроса. Перед тем
     * как они будут полученны вызвашим {@link QueryHelper#query }
     * @param <T>
     * @param context
     * @param query
     * @param result
     * @title Обработка события окончания выполнения запроса
     */
    <T> void postQuery(QueryContext context, Query query, T result);
}