package ru.bio4j.service.sql.types;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Построитель конвертера
 * @title Построитель конвертера
 * @author rad
 */
public class TypeConverterBuilder {

    private final TypeHandlerWrapper handlers[];
    private final TypeMapper mapper;

    public TypeConverterBuilder(TypeMapper typeMapper, int columnCount) {
        this.mapper = typeMapper;
        this.handlers = new TypeHandlerWrapper[columnCount];
    }

    /**
     *
     * @param typeMapper
     * @param metaTypeResolver
     * @param rsmd
     * @param metaTypes
     * @return
     * @throws SQLException
     */
    public static TypeConverterBuilder fill(
        TypeMapper typeMapper,
        MetaTypeResolver metaTypeResolver,
        ResultSetMetaData rsmd,
        Map<String, String> metaTypes) throws SQLException {
        return fill(null, typeMapper, metaTypeResolver, rsmd, metaTypes);
    }

    /**
     *
     * @param mode
     * @param typeMapper
     * @param metaTypeResolver
     * @param rsmd
     * @param metaTypes
     * @return
     * @throws SQLException
     */
    public static TypeConverterBuilder fill(
        TypeMapper.Mode mode,
        TypeMapper typeMapper,
        MetaTypeResolver metaTypeResolver,
        ResultSetMetaData rsmd,
        Map<String, String> metaTypes) throws SQLException {
        final int cc = rsmd.getColumnCount();
        TypeConverterBuilder tcb = new TypeConverterBuilder(typeMapper, cc);
        for(int i = 1; i <= cc; ++i) {
            String name = rsmd.getColumnLabel(i).toUpperCase();
            String sqlType = rsmd.getColumnTypeName(i);
            String metaType = metaTypes.get(name);
            Class<?> clazz = Object.class;
            if(metaType != null) {
                clazz = metaTypeResolver.toJavaType(metaType);
            }
            tcb.setByMetaType(mode, sqlType, clazz, metaType, i);
        }
        return tcb;
    }

    public void setByMetaType(TypeMapper.Mode mode, String sqlType, Class<?> type, String metaType, int column) {
        TypeHandler<?> typeHandler = mapper.findHandler(mode, sqlType, type, metaType);
        handlers[column - 1] = new TypeHandlerWrapper(typeHandler, sqlType, metaType, type);
    }

    /**
     * @title Построение преобразователя типов
     * @return Преобразователь типов
     */
    public TypeConverter build() {
        checkHandlers();
        return new TypeConverter(Collections.unmodifiableList(Arrays.asList(handlers.clone())));
    }

    /**
     * Проверяет заполнены ли все обработчики типов
     * @title Проверка того, что все обработчики типов заполнены
     */
    private void checkHandlers() {
        for(int i = 0; i < handlers.length; ++i) {
            if(handlers[i] == null) {
                handlers[i] = new TypeHandlerWrapper(new ObjectHandler(), null, null, null);
            }
        }
    }

    /**
     * @title Построение преобразователя типов
     * @param typeMapper
     * @param metaTypeResolver
     * @param rsmd
     * @param metaTypes
     * @return Преобразователь типов
     * @throws SQLException
     */
    public static TypeConverter build(
        TypeMapper typeMapper,
        MetaTypeResolver metaTypeResolver,
        ResultSetMetaData rsmd,
        Map<String, String> metaTypes) throws SQLException {
        return fill(typeMapper, metaTypeResolver, rsmd, metaTypes).build();
    }

    /**
     * @title Построение преобразователя типов
     * @param mode
     * @param typeMapper
     * @param metaTypeResolver
     * @param rsmd
     * @param metaTypes
     * @return Преобразователь типов
     * @throws SQLException
     */
    public static TypeConverter build(
        TypeMapper.Mode mode,
        TypeMapper typeMapper,
        MetaTypeResolver metaTypeResolver,
        ResultSetMetaData rsmd,
        Map<String, String> metaTypes) throws SQLException {
        return fill(mode, typeMapper, metaTypeResolver, rsmd, metaTypes).build();
    }
}