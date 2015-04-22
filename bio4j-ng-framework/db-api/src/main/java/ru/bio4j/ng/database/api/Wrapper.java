package ru.bio4j.ng.database.api;

import ru.bio4j.ng.database.api.BioCursor;

/**
 * @title Интерфейс обертки запросов
 */
public interface Wrapper<T extends BioCursor.SQLDef> {

    /**
     * @title "Оборачивание" запроса
     * @param sqlDef
     * @return Обернутый запрос
     */
    T wrap(T sqlDef) throws Exception;
}
