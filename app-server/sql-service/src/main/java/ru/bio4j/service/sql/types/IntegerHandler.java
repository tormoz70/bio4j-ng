package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.*;

/**
 * Обработчик цифр
 * @title Обработчик цифр
 * @author rad
 */
@HandledTypes(
    metaType = "int",
    java={Integer.class, int.class, Long.class, long.class},
    sql={BIGINT, DECIMAL, INTEGER, NUMERIC, SMALLINT, TINYINT})
public class IntegerHandler extends AbstractTypeHandler<Number> {

    /**
     * @title Чтение цифрового значения
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Прочитанное цифровое значение
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
            if(c == int.class || c == Integer.class) {
                res = res.intValue();
            } else {
                res = res.longValue();
            }
        }
        return res;
    }

    /**
     * @title Запись цифрового значения
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
     * @title Запись цифрового значения
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
     * @title Приведение значения к Number или к Long
     * @param value
     * @return Приведенное к Number или к Long цифровое значение
     */
    private Number toNumber(Object value) {
        if(value == null || value instanceof Number) {
            return (Number)value;
        } else if(value instanceof String) {
            return Long.parseLong((String)value);
        }
        throw new RuntimeException("Can not cast '" + value + "' to number.");
    }

}
