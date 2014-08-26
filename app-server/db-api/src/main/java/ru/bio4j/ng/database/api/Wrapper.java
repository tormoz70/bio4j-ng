package ru.bio4j.ng.database.api;

import ru.bio4j.ng.database.api.BioCursor;

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
