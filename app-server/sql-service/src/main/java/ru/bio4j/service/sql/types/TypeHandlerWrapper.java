package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @title Обертка обработчика типов
 * @author rad
 */
public class TypeHandlerWrapper implements TypeHandler<Object> {

    private final TypeHandler<?> typeHandler;
    private final String sqlType;
    private final String metaType;
    private final Class<?> type;

    public TypeHandlerWrapper(TypeHandler<?> typeHandler, String sqlType, String metaType, Class<?> type) {
        this.typeHandler = typeHandler;
        this.sqlType = sqlType;
        this.metaType = metaType;
        this.type = type;
    }

    /**
     * @title Получение типа, для которого сделана обертка
     * @return
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @title Получение метатипа
     * @return Метатипа
     */
    public String getMetaType() {
        return metaType;
    }

    /**
     * @title Получение sql-типа
     * @return SQL-тип
     */
    public String getSqlType() {
        return sqlType;
    }

    /**
     * @title Получение обработчика типа
     * @return Обработчик типа
     */
    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    /**
     * @title Чтение экземпляра типа
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Прочитанный экземпляр типа
     * @throws Exception
     */
    @Override
    public Object read(ResultSet resultSet, int column, Class<Object> type, String sqlType) {
        return typeHandler.read(resultSet, column,
            type == null? this.type : (Class)type,
            sqlType == null? this.sqlType : sqlType);
    }

    /**
     * @title Запись экземпляра типа
     * @param resultSet
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    @Override
    public void write(ResultSet resultSet, Object value, int column, Class<Object> type, String sqlType) {
        typeHandler.write(resultSet, value, column,
            type == null? this.type : (Class)type,
            sqlType == null? this.sqlType : sqlType);
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
    public void write(PreparedStatement statement, Object value, int column, Class<Object> type, String sqlType) {
        typeHandler.write(statement, value, column,
            type == null? this.type : (Class)type,
            sqlType == null? this.sqlType : sqlType);
    }
}

