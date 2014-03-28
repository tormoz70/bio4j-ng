package ru.bio4j.service.sql.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Результат запроса
 * @title Интерфейс результата запроса
 * @author rad
 */
public interface QueryResult extends Serializable {

    /**
     * Количество колонок
     * @title Получение количества колонок
     * @return Количество колонок
     */
    int cols();

    /**
     * Возвращает индекс колонки по её имени
     * @title Получение индекса колонки по ее имени
     * @param field
     * @return Индекс колнки или -1 если такой колонки нет
     */
    int getColByField(final String field);

    /**
     * @title Получение карты индексов заголовков колонок
     * @return Карта индексов заголовков колонок
     */
    Map<String, Integer> getFieldToIndexMap();

    /**
     * Возвращает набор всех имен колонок
     * @title Получение списка имен всех колонок
     * @return Список имен всех колонок
     */
    List<String> getFields();

    /**
     * Возвращает массив значений из строки
     * @title Получение массива значений из строки
     * @param row
     * @return Массив значений из строки
     */
    List<Object> getRow(final int row);

    /**
     * Возвращает значение по индексам.
     * @title Получение значения по индексам
     * @param row - индекс строки
     * @param col - индекс колонки
     * @return Значение
     */
    Object getValue(final int row, final int col);

    /**
     * Позволяет узнать пустой ли нет рузальтат.
     * @title Проверка пустоты результатов
     * @return true, если результат пустой
     */
    boolean isEmpty();

    /**
     * Количество строк
     * @title Получение количества строк
     * @return Количество строк
     */
    int rows();
}
