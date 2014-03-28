package ru.bio4j.service.sql.query.handlers;

import java.util.regex.Pattern;

/**
 * @title Провайдер обработчиков запросов по умолчанию
 */
public final class DefaultQueryHandlerProvider implements QueryHandlerProvider {


    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*select.+$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private final QueryHandler SELECT_HANDLER = new SimpleSelect();


//    private static final Pattern DELETE_PATTERN = Pattern.compile("^\\s*delete\\s+from.+$",
//            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//    private final QueryHandler DELETE_HANDLER = new DeleteHandler();
    // Let's forget about the deletion

    private final QueryHandler CALL_HANDLER = new CallProcedure();

    public DefaultQueryHandlerProvider() {
    }

    /**
     * @title Получение обработчика запроса
     * @param query
     * @return Обработчик запроса
     */
    @Override
    public QueryHandler get(String query) {
        if(SELECT_PATTERN.matcher(query).matches()) {
            return SELECT_HANDLER;
        }
//        if(DELETE_PATTERN.matcher(query).matches()) {
//            return DELETE_HANDLER;
//        }
        if(CallProcedure.CALL_PATTERN.matcher(query).matches()) {
            return CALL_HANDLER;
        }
        return null;
    }
}