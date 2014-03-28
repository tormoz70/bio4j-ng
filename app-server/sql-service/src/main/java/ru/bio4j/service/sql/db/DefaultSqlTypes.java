package ru.bio4j.service.sql.db;

import ru.bio4j.func.Function;
import ru.bio4j.service.sql.types.SqlType;
import ru.bio4j.service.sql.types.SqlTypes;
import ru.bio4j.util.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Список типов субд
 * @title Список типов субд
 * @author rad
 */
public final class DefaultSqlTypes implements SqlTypes {

    private final List<SqlType> types;
    private Map<Integer, SqlType> typeByIntMap;
    private final Function<Integer, SqlType> typeByIntFunc = new Function<Integer, SqlType>() {

        @Override
        public SqlType apply(Integer key) throws RuntimeException {
            if(key == null) {
                throw new NullPointerException("Int type is null.");
            }
            checkMaps();
            return typeByIntMap.get(key);
        }
    };

    private Map<String, SqlType> typeByNameMap;
    private final Function<String, SqlType> typeByNameFunc = new Function<String, SqlType>() {

        @Override
        public SqlType apply(String key) throws RuntimeException {
            if(key == null) {
                throw new NullPointerException("Type name is null.");
            }
            checkMaps();
            return typeByNameMap.get(key);
        }
    };

    public DefaultSqlTypes(List<SqlType> types) {
        this.types = types;
    }

    /**
     * @title Проверка существования карт имен и идентификаторов типов
     */
    private void checkMaps() {
        if(typeByIntMap != null && typeByNameMap != null) {
            return;
        }
        typeByIntMap = new HashMap<>();
        typeByNameMap = new HashMap<>();
        for(SqlType sqlType : types) {
            typeByIntMap.put(sqlType.getSQLType(), sqlType);
            typeByNameMap.put(sqlType.getTypeName(), sqlType);
        }
    }

    /**
     * Возвращает функцию определение типа по идентификатору типа из {@link java.sql.Types }
     * @title Получение функции определения типа по идентификатору типа из java.sql.Types
     * @return Функция определения типа по идентификатору типа из java.sql.Types
     */
    @Override
    public Function<Integer, SqlType> getTypeByInt() {
        return typeByIntFunc;
    }

    /**
     * Возвращает функцию определения типа {@link java.sql.Types } по имени.
     * @title Получение функции определения типа java.sql.Types по имени.
     * @return Функция определения типа java.sql.Types по имени
     */
    @Override
    public Function<String, SqlType> getTypeByName() {
        return typeByNameFunc;
    }

    /**
     * Преобразует идетификатор типа в специфичное для СУБД название
     * @title Преобразование идетификатора типа в специфичное для СУБД название
     * @param type
     * @return Строка, содержащая специфичное для СУБД название типа
     */
    @Override
    public String toName(int type) {
        checkMaps();
        SqlType sqlType = typeByIntMap.get(type);
        return sqlType == null? null : sqlType.getTypeName();
    }

    /**
     * Преобразует специфичное для СУБД название в идентификатор типа
     * @title Преобразование специфичного для СУБД названия в идентификатор типа
     * @param type
     * @return Идентификатор типа
     */
    @Override
    public int toInt(String type) {
        checkMaps();
        SqlType sqlType = typeByNameMap.get(type);
        if(sqlType == null) {
            String list = Strings.join(typeByNameMap.keySet(), ',');
            throw new RuntimeException(String.format("Can not found type with name: %s in registry [%s]", type, list));
        }
        return sqlType.getSQLType();
    }
}