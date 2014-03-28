package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Универсальный конвертер.
 * @title Обработчик объектного типа
 * @author rad
 */
@HandledTypes(
    metaType = "object",
    java=Object.class,
    sql={}
)
public final class ObjectHandler extends AbstractTypeHandler<Object> {

    /**
     * @title Чтение экземпляров объектных типов
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Экземпляр объектного типа
     * @throws Exception
     */
    @Override
    public Object read(ResultSet resultSet,
                       int column,
                       Class<Object> type,
                       String sqlType) {
        try {
            return resultSet.getObject(column);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись экземпляра объектного типа
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
                      Class<Object> type,
                      String sqlType) {
        try {
            resultSet.updateObject(column, value);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись экземпляра объектного типа
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
                      Class<Object> type,
                      String sqlType) {
        try {
            statement.setObject(column, value);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
