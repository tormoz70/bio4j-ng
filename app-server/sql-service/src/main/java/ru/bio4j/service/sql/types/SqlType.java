package ru.bio4j.service.sql.types;

import ru.bio4j.service.sql.db.Typeable;

public interface SqlType extends Typeable {
    /**
     * parameters used in creating the type (may be null)
     * @title Получение параметров, используемых при создании типа
     * @return Параметры, используемые при создании типа
     */
    String getCreateParams();

    /**
     * prefix used to quote a literal (may be null)
     * @title Получение префикса, используемого для ссылки
     * @return Префикс, используемый для ссылки
     */
    String getLiteralPrefix();

    /**
     * suffix used to quote a literal (may be null)
     * @title Получение суффикса для ссылки на литерал
     * @return Суффикс для ссылки на литерал
     */
    String getLiteralSuffix();

    /**
     * localized version of type name (may be null)
     * @title Получение локализованной версии имени типа
     * @return Локализованная версия имени типа
     */
    String getLocalTypeName();

    /**
     * maximum scale supported
     * @title Получение максимального значения типа
     * @return Максимальное значение типа
     */
    int getMaximumScale();

    /**
     * minimum scale supported
     * @title Получение минимального значения типа
     * @return Минимальное значение типа
     */
    int getMinimumScale();

    /**
     * usually 2 or 10
     * @title Получение точности корня
     * @return Точность корня
     */
    int getNumberPrecisionRadix();

    /**
     * SQL type from java.sql.Types
     * @title Получение sql-типа
     * @return SQL-тип
     */
    @Override
    int getSQLType();

    /**
     *  int => length in bytes of data
     * maximum precision
     * @title Получение максимальной точности
     * @return Максимальная точность
     */
    @Override
    int getSize();

    /**
     *  Имя типа, может быть как имененм SQL типа, так и именем произвольного
     * типа определенного пользователем, это зависит от реализации
     * @title Получение имени типа
     * @return Имя типа
     */
    @Override
    String getTypeName();

    /**
     * can it be used for an auto-increment value.
     * @title Проверка автоинкремента значения
     * @return true, если автоинкремент значения доступен
     */
    boolean isAutoIncrement();

    /**
     * is it case sensitive.
     * @title Проверка уровня чувствительности
     * @title true, если
     */
    boolean isCaseSensitive();

    /**
     * can it be a money value.
     * @title Проверка неизменности корня точности
     */
    boolean isFixedPrecisionScale();

    /**
     * is it unsigned.
     * @title Проверка беззнаковости
     * @title true, если тип беззнаковый
     */
    boolean isUnsigned();
    /**
     * true, false or null
     * @title Проверка на null
     * @return
     */
    Boolean isNullable();

}