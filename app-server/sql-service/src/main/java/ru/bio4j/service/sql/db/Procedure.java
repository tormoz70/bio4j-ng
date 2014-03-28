package ru.bio4j.service.sql.db;

import java.util.List;

/**
 * Процедура
 * @title Интерфейс процедуры
 */
public interface Procedure extends DBObject {

    /**
     * @title Получение списка параметров (колонок) процедуры
     * @return Список параметров (колонок) процедуры
     */
    List<Parameter> getColumns();

    /**
     * @title Получение схемы процедуры
     * @return Схема процедуры
     */
    Schema getSchema();
}