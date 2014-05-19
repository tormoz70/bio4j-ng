package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers;

import ru.bio4j.ng.service.api.BioCursor;

/**
 * @title Интерфейс обертки запросов
 */
public interface Wrapper{

    /**
     * @title "Оборачивание" запроса
     * @param cursor
     * @return Обернутый запрос
     */
    BioCursor wrap(BioCursor cursor) throws Exception;
}
