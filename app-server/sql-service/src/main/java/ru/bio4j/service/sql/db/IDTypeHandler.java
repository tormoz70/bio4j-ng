package ru.bio4j.service.sql.db;

import ru.bio4j.service.sql.types.AbstractTypeHandler;
import ru.bio4j.service.sql.types.HandledTypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Types.*;

/**
 * @title Обработчик типов базы данных Oracle
 * @author rad
 */
@HandledTypes(
    java={
        String.class,
        CharSequence.class,
        UUID.class,
        byte[].class,
        Integer.class,
        Long.class
    },
    sql={INTEGER, VARBINARY, OTHER},
    sqlNames="raw",
    metaType ="id"
)
public class IDTypeHandler extends AbstractTypeHandler<Object> {

    private static final int UUID_LEN = 36;//36 = 32 (длина uuid) + 4 символы '-'

    /**
     * @title Чтение типа
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Прочитанный тип
     * @throws Exception
     */
    @Override
    public Object read(ResultSet resultSet,
                       int column,
                       Class<Object> type,
                       String sqlType) {
        try {
            if(Integer.class.isAssignableFrom(type)) {
                return resultSet.getInt(column);
            } else if(Long.class.isAssignableFrom(type)) {
                return resultSet.getLong(column);
            } else {
                String id = resultSet.getString(column);
                return idConverter(id);
            }
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись типа
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
            if(isRaw(sqlType)) {
                resultSet.updateString(column, toString(value));
            } else {
                resultSet.updateObject(column, value);
            }
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись типа
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
            if(isRaw(sqlType)) {
                statement.setString(column, toString(value));
            } else {
                statement.setObject(column, value);
            }
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Проверка того, что тип равен RAW
     * @param sqlType
     * @return true, если тип равен RAW
     */
    private boolean isRaw(String sqlType) {
        return sqlType != null && "RAW".equals(sqlType.toUpperCase());
    }

    /**
     * @title Преобразование объекта к строке
     * @param val
     * @return Текстовое представление переданного объекта
     */
    private String toString(Object val) {
        if(val == null) {
            return null;
        } else {
            if(val instanceof String) {
                String id = (String) val;
                if(id.length() == UUID_LEN) {
                    id = id.replace("-","").toUpperCase();
                }
                return id;
            } else {
                return val.toString();
            }
        }
    }

    /**
     * Преобразует непрерывный поток hex чисел в вид принятый для uuid, по схеме
     * 8-4-4-4-12
     * @title Преобразование непрерывного потока hex чисел в вид, принятый для uuid, по схеме 8-4-4-4-12
     * @param id
     * @return Строка, содержащая переданный поток hex чисел в виде, принятом для uuid
     */
    private String idConverter(String id) {
        if(id == null || id.length() != 32/*raw(16) in string len*/) {
            return id;
        }
        id = id.toLowerCase();
        StringBuilder sb = new StringBuilder(id.length() + 4);
        sb.append(id.substring(0,8))
            .append('-')
            .append(id.substring(8, 12))
            .append('-')
            .append(id.substring(12, 16))
            .append('-')
            .append(id.substring(16, 20))
            .append('-')
            .append(id.substring(20));
        return sb.toString();
    }
}
