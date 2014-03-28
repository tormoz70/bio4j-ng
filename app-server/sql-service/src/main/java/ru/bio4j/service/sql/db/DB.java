package ru.bio4j.service.sql.db;

import ru.bio4j.service.sql.types.SqlTypes;
import ru.bio4j.service.sql.types.TypeMapper;
import ru.bio4j.service.sql.util.Closeable;

import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * Абстракций для управления сущностями БД (таблицами)
 * @title Абстракции для управления сущностями БД (таблицами)
 * @author rad
 */
public interface DB extends Closeable {

    /**
     * Имя объекта
     * @title Получение имени объекта
     * @return Имя объекта
     */
    String getName();

    /**
     * @see java.sql.Connection#getMetaData()
     * @title Получение метаданных базы данных
     * @return Метаданные базы данных
     */
    DatabaseMetaData getDatabaseMetaData();

    /**
     * @title Получение маппера типов
     * @return Маппер типов
     */
    TypeMapper getTypeMapper();

    /**
     * @title Получение типов СУБД
     * @return Типы СУБД
     */
    SqlTypes getTypes();

    /**
     * @title
     * @param name
     * @return
     */
    String convertIdentifier(String name);

    /**
     * @return Интерпретатор объектов Filter, Sort
     */
    WrapperInterpreter getWrapperInterpreter();

    /**
     * Возвращает схему по умолчанию, у нее может быть имя null.
     * @title Получение схемы по умолчанию
     * @return Схема по умолчанию
     */
    Schema getDefaultSchema();

    /**
     * @title Получение схемы по имени
     * @param name
     * @return Схема
     */
    Schema getSchema(String name);

    /**
     * @title Получение списка схем
     * @return Список схем
     */
    List<Schema> getSchemas();
}