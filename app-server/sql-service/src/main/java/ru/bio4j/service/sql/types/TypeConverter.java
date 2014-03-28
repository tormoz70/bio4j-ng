package ru.bio4j.service.sql.types;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;


/**
 * Преобразователь типов.
 * @title Преобразователь типов
 * @author rad
 */
public class TypeConverter implements  TypeHandler<Object> {

    private final List<TypeHandlerWrapper> handlers;

    TypeConverter(List<TypeHandlerWrapper> handlers) {
        this.handlers = handlers;
    }

    /**
     * @title Получение обработчика типа
     * @param column
     * @return Обработчик типа
     */
    @SuppressWarnings("unchecked")
    public TypeHandlerWrapper getHandler(int column) {
        TypeHandlerWrapper context = handlers.get(column - 1);
        if(context == null) {
            throw new NullPointerException("Null handlers for column: " + column);
        }
        return context;
    }

    /**
     * @title Чтение значения типа
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public Object read(ResultSet resultSet,
                       int column,
                       Class<Object> type,
                       String sqlType) {
        return getHandler(column).read(resultSet, column, type, sqlType);
    }

    /**
     * @title Запись значения типа
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
        getHandler(column).write(resultSet, value, column, type, sqlType);
    }

    /**
     * @title Запись экземпляра типа
     * @param statement
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public void write(PreparedStatement statement,
                      Object value,
                      int column,
                      Class<Object> type,
                      String sqlType) {
        getHandler(column).write(statement, value, column, type, sqlType);
    }
}
