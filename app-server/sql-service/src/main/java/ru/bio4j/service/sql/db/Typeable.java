package ru.bio4j.service.sql.db;

/**
 * Нечто имеющее тип
 * @title Интерфейс объекта, имеющего sql-тип
 * @author rad
 */
public interface Typeable {

    /**
     * SQL type from java.sql.Types
     * @return SQL-тип
     * @title Получение sql-типа
     */
    int getSQLType();

    /**
     *  Имя типа, может быть как имененм SQL типа, так и именем произвольного
     * типа определенного пользователем, это зависит от реализации
     * @title Получение имени типа
     * @return Имя типа
     */
    String getTypeName();

    /**
     *  int => length in bytes of data
     * @return Размер типа
     * @title Получение размера типа
     */
    int getSize();
}
