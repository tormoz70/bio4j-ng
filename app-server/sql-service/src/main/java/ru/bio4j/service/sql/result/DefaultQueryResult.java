package ru.bio4j.service.sql.result;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Данный класс возвращаеся сервером клиенту, после выполнения
 * произвольных запросов и предназначен для сохранения результатов запроса и
 * некоторой информации о этихъ резкльтатах, такой как  названий полей и т.п.
 * @title Класс для сохранения результатов запроса и некоторой информации об этих результатах
 * @author rad
 */
public class DefaultQueryResult extends AbstractResult implements Serializable {

    private String[] colNames;

    DefaultQueryResult() {
    }

    public DefaultQueryResult(Object data[][], String colNames[], Map<String, Integer> colToIndex) {
        this.data = data;
        this.colToIndex = colToIndex;
        this.colNames = colNames;
    }

    /**
     * Количество колонок
     * @title Получение количества колонок
     * @return Количество колонок
     */
    @Override
    public int cols(){
        return colNames.length;
    }

    /**
     * Возвращает набор всех имен колонок
     * @title Получение списка имен всех колонок
     * @return Список имен всех колонок
     */
    @Override
    public List<String> getFields() {
        return Arrays.asList(colNames);
    }
// package private Setters

    /**
     * @title Установка массива имен колонок
     * @param colNames
     */
    void setColNames(String[] colNames) {
        this.colNames = colNames;
    }
}
