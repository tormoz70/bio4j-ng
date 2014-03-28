package ru.bio4j.service.sql.result;

import java.io.Serializable;
import java.util.*;

/**
 * Базовый класс для всяческих результатов
 * @title Базовый класс для результатов запросов
 * @author rad
 */
public abstract class AbstractResult<T extends AbstractResult> implements QueryResult, Serializable, Cloneable {
    // Индекс заголовков колонок
    protected Map<String, Integer> colToIndex;
    protected Map<String, Object> tags;
    /**
     * данные возвращенные запросом [строка][колонка]
     */
    protected Object[][] data = new Object[][]{};

    /**
     * Позволяет узнать пустой ли нет рузальтат.
     * @title Проверка пустоты результатов
     * @return true, если результат пустой
     */
    @Override
    public boolean isEmpty(){
        return data == null || data.length == 0;
    }

    /**
     * Возвращает значение по индексам.
     * @title Получение значения по индексам
     * @param row - индекс строки
     * @param col - индекс колонки
     * @return Значение
     */
    @Override
    public Object getValue(int row, int col){
        return data[row][col];
    }

    /**
     * Количество строк
     * @title Получение количества строк
     * @return Количество строк
     */
    @Override
    public int rows(){
        return data.length;
    }

    /**
     * Возвращает массив значений из строки
     * @title Получение массива значений из строки
     * @param row
     * @return Массив значений из строки
     */
    @Override
    public List<Object> getRow(int row){
        return Arrays.asList(data[row]);
    }

    /**
     * Возвращает индекс колонки по её имени
     * @title Получение индекса колонки по ее имени
     * @param field
     * @return Индекс колнки или -1 если такой колонки нет
     */
    @Override
    public int getColByField(String field){
        Integer col = colToIndex.get(field);
        return (col != null)? col: -1;
    }

    /**
     * @title Получение карты индексов заголовков колонок
     * @return Карта индексов заголовков колонок
     */
    @Override
    public Map<String, Integer> getFieldToIndexMap(){
        return Collections.unmodifiableMap(colToIndex);
    }

    /**
     * Дополнительная произвольная информация прикрепляемая к запросу.
     * @title Получение карты, содержащей дополнительную информацию, прикрепляемую к запросу
     * @return Карта, содержащая дополнительную информацию, прикрепляемую к запросу
     */
    public Map<String, Object> getTags() {
        if(tags == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(tags);
        }
    }

    /**
     * @title Получение дополнительной информации, прикрепляемой к запросу по ключу
     * @param <T>
     * @param key
     * @return Дополнительная информация, прикрепляемая к запросу
     */
    @SuppressWarnings("unchecked")
    public<T> T getTag(String key) {
        if(tags == null) {
            return null;
        } else {
            return (T)tags.get(key);
        }
    }

    /**
     * @title Клонирование экземпляра базового класса для результатов запросов
     * @return Новый экземпляр базового класса для результатов запросов, полностью повторяющий старый
     */
    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnse){
            throw new RuntimeException(cnse);
        }
    }
}
