package ru.bio4j.service.sql.types;

import ru.bio4j.func.Function;

/**
 * Типы СУБД
 * @title Типы СУБД
 * @author rad
 */
public interface SqlTypes {

    /**
     * Возвращает функцию определение типа по идентификатору типа из {@link java.sql.Types }
     * @title Получение функции определения типа по идентификатору типа из java.sql.Types
     * @return Функция определения типа по идентификатору типа из java.sql.Types
     */
    public Function<Integer, SqlType> getTypeByInt();

    /**
     * Возвращает функцию определения типа {@link java.sql.Types } по имени.
     * @title Получение функции определения типа java.sql.Types по имени.
     * @return Функция определения типа java.sql.Types по имени
     */
    public Function<String, SqlType> getTypeByName();

    /**
     * Преобразует идетификатор типа в специфичное для СУБД название
     * @title Преобразование идетификатора типа в специфичное для СУБД название
     * @param type
     * @return Строка, содержашая, специфичное для СУБД название
     */
    public String toName(int type);

    /**
     * Преобразует специфичное для СУБД название в идентификатор типа
     * @title Преобразование специфичного для СУБД названия в идентификатор типа
     * @param type
     * @return Идентификатор типа
     */
    public int toInt(String type);
}