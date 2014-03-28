package ru.bio4j.service.sql.result;

import java.util.Map;

public interface Column {

    /**
     * Видимость пользователю
     * @title Видимость пользователю
     */
    public static final String VISIBLE = "isVisible";
    /**
     * Редактируемо пользователем
     * @title Редактируемо пользователем
     */
    public static final String EDITABLE = "isEditable";
    /**
     * Требуется заполнение пользоватлем.
     * @title Требуется заполнение пользоватлем
     */
    public static final String REQUIRED = "isRequired";
    /**
     * Ключевое поле таблицы
     * @title Ключевое поле таблицы
     */
    public static final String KEY = "isTableKey";

    /**
     * Устанавливает отображаемое имя поля
     * @return Отображаемое имя колонки/поля
     * @title Получение отображаемого имя колонки/поля
     */
    String getTitle();

    /**
     * Возвращает идентификатор класса на который ссылается поле
     * @return
     */
    // getClassId();

    /**
     * Возвращает идентификатор поля в запросе
     * @return Идентификатор колонки/поля в запросе
     * @title Получение идентификатора колонки/поля в запросе
     */
    String getField();

    /**
     * @return Размер колонки/поля
     * @title Получение размера колонки/поля
     */
    int getSize();

    /**
     * Возвращает весовой коэффициент по которому производится сортировка полей
     * при выводе
     * @return Весовой коэффициент сортировки колонок/полей при выводе
     * @title Получение весового коэффициента сортировки колонок/полей при выводе
     */
    int getWeight();

    String getType();

    /**
     * Аналог {@link #is(java.lang.String, boolean)  } cо вторым параметром false
     * @param attributeName
     * @return Истина, если у атрибута есть значение
     * @title Проверка существования значения атрибута
     */
    boolean is(String attributeName);

    /**
     * @param attributeName
     * @param defaultValue
     * @return Проверка существования значения атрибута
     * @title Проверка существования значения атрибута
     */
    boolean is(String attributeName, boolean defaultValue);

    /**
     * @param attributeName
     * @return Значение атрибута
     * @title Получение значения атрибута
     */
    Object get(String attributeName);

    /**
     * Возвращает иммутабельную карту со списком всех аттрибутов.
     * <p/> для чтения определенного аттрибута используте {@link #get(java.lang.String)  }
     * @return Немодифицируемая карта аттрибутов поля/колонки
     * @title Получение списка аттрибутов поля/колонки
     */
    Map<String, Object> getAttributes();

}
