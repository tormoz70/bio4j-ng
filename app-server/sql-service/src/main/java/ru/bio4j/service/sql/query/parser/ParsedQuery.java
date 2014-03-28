package ru.bio4j.service.sql.query.parser;

import java.util.List;

/**
 * @author rad
 * @title Интерфейс разобранного запроса
 */
public interface ParsedQuery {

    /**
     * @return Строка запроса
     * @title Получение строки запроса
     */
    String getQuery();

    /**
     * @return Список параметров
     * @title Получение списка параметров
     */
    List<ParsedParameter> getParameters();

}
