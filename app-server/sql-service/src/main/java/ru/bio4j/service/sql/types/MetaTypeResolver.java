package ru.bio4j.service.sql.types;


/**
 * Определитель метаттипов. Мета тип - название типа которому соответствует java-тип и sql-тип.
 * Это введено из-за того что 'id', например, может иметь тип UUID, или RAW(16)
 * в различных базах, и java.lang.String в java.
 * @title Интерфейс определителя метатипов
 * @author rad
 */
public interface MetaTypeResolver {

    /**
     * @title Получение sql-типа
     * @param metaType
     * @return SQL-тип
     */
    String toSqlType(String metaType);

    /**
     * @title Получение размера sql-типа
     * @param metaType
     * @param metaTypeSize
     * @return Размер sql-типа
     */
    int toSqlTypeSize(String metaType, int metaTypeSize);

    /**
     * @title Получение java-типа
     * @param metaType
     * @return Java-тип
     */
    Class<?> toJavaType(String metaType);
}
