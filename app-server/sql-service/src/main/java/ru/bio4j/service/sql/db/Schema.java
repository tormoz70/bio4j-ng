package ru.bio4j.service.sql.db;

import java.util.List;

/**
 * Схема DB
 * @title Схема DB
 */
public interface Schema extends DBObject {

    /**
     * База данных которой принадлежит схема
     * @title Получение базы данных, которой принадлежит схема
     * @return База данных, которой принадлежит схема
     */
    DB getDB();

    /**
     * Возаращает процедуру
     * @title Получение процедуру
     * @param name
     * @return Процедура
     */
    Procedure getProcedure(String name);

    /**
     * Список процедур
     * @title Получение списка процедур
     * @return Список процедур
     */
    List<Procedure> getProcedures();
}