package ru.bio4j.ng.commons.converter;

import ru.bio4j.ng.commons.converter.hanlers.*;

public class TypeHandlerMapper {
    private static final TypeHandler[] handlerMap = {
        new DateHandler(),
        new StringHandler(),
        new NumberHandler(),
        new BooleanHandler(),
        new ResultSetHandler()
    };

    public static TypeHandler getHandler(Class<?> type) {
        for(TypeHandler h : handlerMap) {
            if(h.isHandler(type))
                return h;
        }
        return null;
    }
}
