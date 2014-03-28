
package ru.bio4j.service.sql.result.handlers;

import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.handlers.ResultReceiver;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Обработчик {@link ResultSet }, получает в качестве аргументов исходное ксловие, контекст
 * собственно {@link ResultSet }
 * @title Интерфейс обработчика результирующего набора данных выборки из базы данных
 * @author rad
 */
public interface ResultHandler<T> extends ResultReceiver<ResultSet, T> {

    /**
     * @param rs
     * @param query
     * @param context
     * @return
     * @throws SQLException
     * @title Обработка результирующего набора данных выборки из базы данных
     */
    @Override
    T handle(ResultSet rs, Query query, QueryContext context) throws SQLException;
}
