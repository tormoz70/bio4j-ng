package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.*;

/**
 * Обработчик цифр
 * @title Обработчик целых и дробных чисел
 * @author rad
 */
@HandledTypes(
    metaType = "float",
    java={Float.class, float.class, Double.class, double.class},
    sql={DOUBLE, FLOAT, REAL, INTEGER, TINYINT, SMALLINT, BIGINT})
public class FloatHandler extends AbstractTypeHandler<Number> {

    /**
     * @title Чтение числового значения
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Прочитанное числовое значение
     * @throws Exception
     */
    @Override
    public Number read(ResultSet resultSet,
                       int column,
                       Class<Number> type,
                       String sqlType) {
        Number res;
        try {
            res = (Number)resultSet.getObject(column);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
        if(res != null) {
            Class<?> c = type;
            if(c == float.class || c == Float.class) {
                res = res.floatValue();
            } else {
                res = res.doubleValue();
            }
        }
        return res;
    }

    /**
     * @title Запись числового значения
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
                      Class<Number> type,
                      String sqlType) {
        Number num = toNumber(value);
        try {
            resultSet.updateObject(column, num);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись числового значения
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
                      Class<Number> type,
                      String sqlType) {
        Number num = toNumber(value);
        try {
            statement.setObject(column, num);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Приведение значения к Number или к Double
     * @param value
     * @return Приведенное к Number или к Double значение
     */
    private Number toNumber(Object value) {
        if(value == null || value instanceof Number) {
            return (Number)value;
        } else if(value instanceof String) {
            return Double.parseDouble((String)value);
        }
        throw new RuntimeException("Can not cast '" + value + "' to number.");
    }

}
