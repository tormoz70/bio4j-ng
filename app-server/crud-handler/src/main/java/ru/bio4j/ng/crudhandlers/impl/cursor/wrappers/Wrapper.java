package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers;

import ru.bio4j.ng.service.api.Cursor;

/**
 * @title Интерфейс обертки запросов
 */
public interface Wrapper{

    /**
     * @title "Оборачивание" запроса
     * @param cursor
     * @return Обернутый запрос
     */
    Cursor wrap(Cursor cursor) throws Exception;
}
