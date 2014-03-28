package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

    /**
        * Обработчик типов
        * @title Обработчик типов
        */
public interface TypeHandler<T> {

    /**
     * @title Чтение экземпляра типа
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Прочитанный экземпляр типа
     * @throws Exception
     */
    T read(ResultSet resultSet, int column, Class<T> type, String sqlType);

    /**
     * @title Запись экземпляра типа
     * @param resultSet
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    void write(ResultSet resultSet, Object value, int column, Class<T> type, String sqlType);

    /**
     * @title Запись экземпляра типа
     * @param statement
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    void write(PreparedStatement statement, Object value, int column, Class<T> type, String sqlType);
}

