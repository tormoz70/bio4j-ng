package ru.bio4j.service.sql.query.parser;

import ru.bio4j.collections.Parameter;
import ru.bio4j.func.Function;
import ru.bio4j.service.sql.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * @title Фунция выражения
 */
public class StatementFunction implements Function<String, Parameter> {

    private final Map<String, Parameter> params = new HashMap<>();
    private final Map<String, Parameter> context = new HashMap<>();

    public StatementFunction(Query query) {
        for (Map.Entry<String, Parameter> entry : query.getParams().entrySet()) {
            params.put(entry.getKey().toUpperCase(), entry.getValue());
        }
        for (Map.Entry<String, Parameter> entry : query.getContext().entrySet()) {
            context.put(entry.getKey().toUpperCase(), entry.getValue());
        }
    }

    /**
     * @param key
     * @return Параметр запроса
     * @title Выполнение функции
     */
    @Override
    public Parameter apply(final String key) throws RuntimeException {
        Parameter p = params.get(key.toUpperCase());
        if (p == null) {
            p = context.get(key);
        }
        return p;
    }
}
