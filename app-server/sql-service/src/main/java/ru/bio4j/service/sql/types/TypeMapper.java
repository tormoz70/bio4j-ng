package ru.bio4j.service.sql.types;

/**
 * Интерфейс маппинга типов.
 * @title Интерфейс маппинга типов
 * @author rad
 */
public interface TypeMapper {

    /**
     * Режим
     * @title Режим
     */
    enum Mode {
        /**
         * Чтение из базы
         */
        READ,
        /**
         * Запись в базу
         */
        WRITE
    }

    /**
     * Метод позволяющий вывести соответсвие класса к sql типу
     * @title Метод, позволяющий вывести соответсвие класса к sql типу
     * @param clazz
     * @return Имя sql-типа
     * @return null если тип не найден
     */
    String getSqlTypeForClass(Class<?> clazz);

    /**
     *  Метод для маппинга sql типов на Java классы
     * @title Получение Java-типа, соответствующего переданному sql-типу
     * @param sqlType
     * @return Java-тип, соответствующий переданному sql-типу
     */
    Class<?> getClassForSqlType(String sqlType);

    /**
     * Ищет обработчик для заданных типов
     * @title Поиск обработчика для заданных типов
     * @param <T>
     * @param mode режим использования хендлера, от него зависит будет ли выполнятся поиск супер-типа или подтипа.
     * @param sqlType
     * @param javaType тип
     * @param metaType
     * @return Экземпляр обработчика типов
     */
    <T> TypeHandler<T> findHandler(Mode mode, String sqlType, Class<T> javaType, String metaType);

    /**
     * @title Регистрация обработчика типов
     * @param h
     */
    void register(AbstractTypeHandler<?> h);
}
