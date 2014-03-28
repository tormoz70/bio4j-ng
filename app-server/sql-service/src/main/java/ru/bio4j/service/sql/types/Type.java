package ru.bio4j.service.sql.types;

public interface Type<T> {

    /**
     * Обозначение, физическое имя (например таблицы в базе)
     * @return Физическое имя типа сущности
     * @title Получение физического имени типа сущности
     */
    String getName();

    /**
     * Java-тип
     * @return Java-тип типа сущности
     * @title Получение java-типа типа сущности
     */
    Class<T> getJavaType();

}
