package ru.bio4j.service.sql.types;

import ru.bio4j.service.sql.db.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static java.sql.Types.*;

/**
 * Обработчик типа дат
 * @title Обработчик дат
 * @author rad
 */
@HandledTypes(
    metaType ="date",
    java={Date.class},
    sql={DATE, TIME, TIMESTAMP}
)
public class DateHandler extends AbstractTypeHandler<Date> {

    private final DB db;

    public DateHandler(DB db) {
        this.db = db;
    }

    /**
     * @title Чтение значение даты
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Прочитанное значение даты
     * @throws Exception
     */
    @Override
    public Date read(ResultSet resultSet,
                     int column,
                     Class<Date> type,
                     String sqlType) {
        Date res = null;
        Object o = null;
        try {
            o = resultSet.getObject(column);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
        if(o == null) {
            //result итак null
        } else if(o instanceof Date) {
            res = new Date(((Date)o).getTime());
        } else if (o instanceof Long) {
            res = new Date((Long)o);
        } else {
            throw new RuntimeException("Can not cast '" + o +"' to Date");
        }
        return res;
    }

    /**
     * @title Запись значения даты
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
                      Class<Date> type,
                      String sqlType) {
        Long d = toDate(value);
        try {
            Date date = (d != null)? toSqlDate(d, sqlType) : null;
            resultSet.updateObject(column, date);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись значения даты
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
                      Class<Date> type,
                      String sqlType) {
        Long d = toDate(value);
        try {
            Date date = (d != null)? toSqlDate(d, sqlType) : null;
            statement.setObject(column, date);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Приведение значения к типу sql-даты
     * @param date
     * @param typeName
     * @return Приведенное к типу sql-даты значение
     */
    private Date toSqlDate(long date, String typeName) {
        Date d = null;
        SqlTypes sqlTypes = db.getTypes();
        int typeInt = sqlTypes.toInt(typeName);
        switch(typeInt) {
            case DATE:
                d = new java.sql.Date(date);
                break;
            case TIME:
                d = new java.sql.Time(date);
                break;
            case TIMESTAMP:
            default:
                d = new java.sql.Timestamp(date);
                ;
        }
        return d;
    }

    /**
     * @title Приведение значения к дате или к типу Long
     * @param value
     * @return Приведенное к дате или к типу Long значение
     */
    private Long toDate(Object value) {
        if(value == null) {
            return null;
        }
        if(value instanceof Date) {
            return ((Date)value).getTime();
        }
        if(value instanceof Long) {
            return (Long)value;
        }
        throw new RuntimeException("Can not convert '" + value + "' to date.");
    }
}
