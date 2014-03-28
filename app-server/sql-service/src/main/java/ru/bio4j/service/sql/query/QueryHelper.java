package ru.bio4j.service.sql.query;

import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.QueryListener;
import ru.bio4j.service.sql.query.handlers.*;

import java.sql.SQLException;

public class QueryHelper {

    private final static QueryHandlerProvider handlerProvider = new DefaultQueryHandlerProvider();

    /**
     * @title Выполнение запроса
     * @param <R> -- Тип результата
     * @param <T> -- тип
     * @param query
     * @param handler
     * @return Результат выполнения запроса
     * @throws SQLException
     */

    public static <R, T> T query(final Query query, final ResultReceiver<R, T> handler) throws SQLException {
        return QueryContext.call(new UnsafeFunction<QueryContext, T, Exception>() {

            @Override
            public T apply(QueryContext context) throws Exception {
                firePreQuery(context, query);
                final QueryHandler<R> queryHandler = handlerProvider.get(query.getSql());
                T res = queryHandler.handle(query, context, handler);
                firePostQuery(context, query, res);
                return res;
            }
        });
    }

    /**
     * @title Рассылка сообщения о начале выполнения запроса
     * @param context
     * @param query
     */
    private static void firePreQuery(QueryContext context, Query query){
        for(QueryListener l : context.getListeners()){
            l.preQuery(context, query);
        }
    }

    /**
     * @title Рассылка сообщения об окончании выполнения запроса
     * @param <T>
     * @param context
     * @param query
     * @param result
     */
    private static <T> void firePostQuery(QueryContext context, Query query, T result){
        for(QueryListener l : context.getListeners()){
            l.postQuery(context, query, result);
        }
    }
}
