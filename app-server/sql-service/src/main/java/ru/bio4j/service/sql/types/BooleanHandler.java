package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.*;

/**
 * Обработчик булевых значений.
 * @title Обработчик булевых значений
 */
@HandledTypes(
    metaType ="boolean",
    java={boolean.class, Boolean.class},
    sql={BOOLEAN, BIT, NUMERIC, BIGINT, INTEGER, TINYINT, SMALLINT, VARCHAR}
)
public class BooleanHandler extends AbstractTypeHandler<Boolean> {

    /**
     * @title Чтение значение булевого типа
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Значение булевого типа
     * @throws Exception
     */
    @Override
    public Boolean read(ResultSet resultSet,
                        int column,
                        Class<Boolean> type,
                        String sqlType) {
        Object res;
        try {
            res = resultSet.getObject(column);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
        Boolean br = null;
        if(res != null) {
            if(res instanceof Boolean) {
                br = (Boolean) res;
            } else if(res instanceof Number) {
                // 0 == false
                br = ((Number)res).doubleValue() != 0.0;
            } else if(res instanceof String) {
                // true
                String vs = ((String)res).toLowerCase();
                br = Boolean.parseBoolean(vs);
            } else {
                throw new Error("Can not convert '" + res + "' to Boolean.");
            }
        }
        return br;
    }

    /**
     * @title Запись значения булевого типа
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
                      Class<Boolean> type,
                      String sqlType) {
        Boolean b = toBoolean(value);
        try {
            resultSet.updateObject(column, b);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись значения булевого типа
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
                      Class<Boolean> type,
                      String sqlType) {
        Boolean b = toBoolean(value);
        try {
            statement.setObject(column, b);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Приведение значения к типу Boolean
     * @param value
     * @return Приведенное значение
     */
    private Boolean toBoolean(Object value) {
        if(value == null || value instanceof Boolean) {
            return (Boolean)value;
        } else if(value instanceof String) {
            String vs = ((String)value).toLowerCase();
            return vs.equals("t") || vs.equals("true");
        }
        throw new RuntimeException("Can not convert '" + value + "' to boolean.");
    }
}
