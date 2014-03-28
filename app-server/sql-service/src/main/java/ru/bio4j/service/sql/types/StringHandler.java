package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.*;

/**
 * Конвертер строк
 * @title Обработчик строк
 * @author rad
 */
@HandledTypes(
    metaType = "string",
    java={String.class, CharSequence.class},
    sql={CLOB, VARCHAR, OTHER}
)
public final class StringHandler extends AbstractTypeHandler<String> {

    /**
     * @title Чтение строки
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Строка
     * @throws Exception
     */
    @Override
    public String read(ResultSet resultSet,
                       int column,
                       Class<String> type,
                       String sqlType) {
        try {
            return resultSet.getString(column);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись строки
     * @param resultSet
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    @Override
    public void write(ResultSet resultSet,
                      Object value,
                      int column,
                      Class<String> type,
                      String sqlType) {
        try {
            resultSet.updateString(column, toString(value));
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись строки
     * @param statement
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    @Override
    public void write(PreparedStatement statement,
                      Object value,
                      int column,
                      Class<String> type,
                      String sqlType) {
        try {
            statement.setString(column, toString(value));
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Приведение объекта к строке
     * @param val
     * @return Строковое представление переданного объекта
     */
    private String toString(Object val) {
        return val == null? null : val.toString();
    }
}
