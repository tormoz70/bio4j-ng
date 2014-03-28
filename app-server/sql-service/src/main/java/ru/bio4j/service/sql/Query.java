package ru.bio4j.service.sql;

import ru.bio4j.collections.Parameter;
import ru.bio4j.model.transport.jstore.Sort;
import ru.bio4j.model.transport.jstore.filter.Expression;
import ru.bio4j.service.sql.result.Column;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @title Запрос-sql
 */
public class Query implements Serializable, Cloneable {

    /**
     * Строка запроса.
     */
    protected String sql;

    /**
     * Контекст запроса
     */
    protected Map<String, Parameter> context;

    /**
     * Параметры запроса
     */
    protected Map<String, Parameter> params;

    /**
     * С какой записи выбирать
     */
    protected int offset = 0;

    /**
     * Количество записей которые надо выбрать. '-1' - все
     */
    protected int count = -1;

    /**
     * Параметр сортировки
     */
    protected Sort sort = null;

    /**
     * Параметр фильтрации
     */
    protected Expression filter = null;

    /**
     * Возвращаемые значения
     */
    private final Map<String, Column> resultColumns = new HashMap<>();

    public Query(){
    }

    public Query(String sql) {
        this.sql = sql;
    }

    public Query(String sql, Map<String, Parameter> params) {
        this.sql = sql;
        this.params = params;
    }

    /**
     * Копирования params не производится
     * @param sql
     * @param params
     * @param offset
     * @param count
     */
    public Query(String sql,
                 Map<String, Parameter> params, int offset, int count) {
        this.sql = sql;
        this.params = params;
        this.offset = offset;
        this.count = count;
    }
    /**
     * Параметры запроса
     * @title Получение списка параметров запроса
     * @return Немодифицируемая карта параметров запроса
     */
    public final Map<String, Parameter> getParams() {
        if(params == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(params);
        }
    }

    /**
     * @title Получение списка параметров запроса
     * @return Модифицируемая карта параметров запроса
     */
    protected final Map<String, Parameter> params() {
        if(params == null){
            params = new HashMap<>();
        }
        return params;
    }

    /**
     * Контекст запроса. <p/>
     * @title Получение контекста запроса
     * @return Контекст запроса (немодифицируемая карта)
     */
    public final Map<String, Parameter> getContext() {
        if(context == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(context);
        }
    }

    /**
     * @title Получение модифицируемой карты контекста запроса
     * @return Контекст запроса (модифицируемая карта)
     */
    protected final Map<String, Parameter> context() {
        if(context == null){
            context = new HashMap<>();
        }
        return context;
    }

    /**
     * @title Установка контекста запроса
     * @param context
     */
    public final void setContext(Map<String, Parameter> context) {
        context().putAll(context);
    }

    /**
     * @title Установка параметров контекста запроса
     * @param name
     * @param value
     * @return Ссылка на текущий запрос
     */
    public Query setContextParam(String name, Parameter value){
        context().put(name, value);
        return this;
    }

    /**
     * Добавлет переданные параметры к имеющимся.
     * @title Добавление переданных параметров к имеющимся
     * @param params
     */
    public void setParams(Map<String, Parameter> params) {
        params().putAll(params);
    }

    /**
     * @title Добавление переданного параметра к имеющимся
     * @param name
     * @param value
     * @return Ссылка на текущий запрос
     */
    public Query setParam(String name, Parameter value) {
        params().put(name, value);
        return this;
    }

    /**
     * @title Получение строки запроса
     * @return Строка запроса
     */
    public String getSql() {
        return sql;
    }

    /**
     * @title Установка строки запроса
     * @param sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Количество записей для выборки
     * @title Получение количества записей для выборки
     * @return Количество записей для выборки
     */
    public int getCount() {
        return count;
    }

    /**
     * Количество записей для выборки
     * @title Установка количества записей для выборки
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Количество пропущенных записей, до первой выбранной
     * @title Получение количества пропущенных записей до первой выбранной
     * @return Количество пропущенных записей до первой выбранной
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Количество пропущенных записей, до первой выбранной
     * @title Установка количества пропущенных записей до первой выбранной
     * @param offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }


    public Sort getSort() {
        return this.sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Expression getFilter() {
        return this.filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    /**
     * @title Установка параметра
     * @param name
     * @param value
     * @param type
     * @return Ссылка на текущий sql-запрос
     */
    public Query setParam(String name,
                          Object value, String type) {
        this.setParam(name, new Parameter(value, type));
        return this;
    }

    /**
     * @title Установка типа параметра
     * @param name
     * @param type
     * @return Ссылка на текущий sql-запрос
     */
    public Query setParamType(String name, String type) {
        Parameter parameter = params().get(name);
        if(parameter == null) {
            parameter = new Parameter(null, type);
        } else {
            parameter = parameter.newType(type);
        }
        this.setParam(name, parameter);
        return this;
    }

    /**
     * @title Установка значения параметра
     * @param name
     * @param value
     * @return Ссылка на текущий sql-запрос
     */
    public Query setParamValue(String name, Object value) {
        Parameter parameter = params().get(name);
        if(parameter == null) {
            parameter = new Parameter(value, null);
        } else {
            parameter = parameter.newValue(value);
        }
        this.setParam(name, parameter);
        return this;
    }

    /**
     * Карта типов результов запроса
     * @title Получение карты результатов запроса
     * @return Карта результатов запроса
     */
    public final Map<String, String> getResultTypes() {

        Map<String, String> resultTypes = new HashMap<>();
        for (Column column : resultColumns.values()) {
            resultTypes.put(column.getField(), column.getType());
        }
        return resultTypes;
    }

    /**
     * @title Добавление результирующей колонки
     * @param column
     * @return Ссылка на текущий sql-запрос
     */
    public Query addResult(Column column) {
        resultColumns.put(column.getField(), column);
        return this;
    }

    public Column getColumns(String field) {
        return resultColumns.get(field);
    }

    /**
     * @title Получение типа результата для поля
     * @param field
     * @return Тип результата
     */
    public String getResultType(String field) {
        return getColumns(field).getType();
    }

    /**
     * @title Проверка на равенство ткущему объекты переданного объекта
     * @param obj
     * @return true, если объекты равны
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Query other = (Query) obj;
        if ((this.sql == null) ? (other.sql != null)
                : !this.sql.equals(other.sql)) {
            return false;
        }
        if (this.context != other.context &&
                (this.context == null || !this.context.equals(other.context))) {
            return false;
        }
        if (this.params != other.params &&
                (this.params == null || !this.params.equals(other.params))) {
            return false;
        }
        if (this.offset != other.offset) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        return true;
    }

    /**
     * @title Вычисление хеш-кода
     * @return Хеш-код
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.sql != null ? this.sql.hashCode() : 0);
        hash = 83 * hash + (this.context != null ? this.context.hashCode() : 0);
        hash = 83 * hash + (this.params != null ? this.params.hashCode() : 0);
        hash = 83 * hash + this.offset;
        hash = 83 * hash + this.count;
        return hash;
    }

    /**
     * @title Клонирование запроса
     * @return Новый запрос, полностью повторяющий старый
     */
    @Override
    @SuppressWarnings("unchecked")
    public Query clone() {
        Query r;
        try {
            r = (Query) super.clone();
        } catch(CloneNotSupportedException c) {
            throw new UnsupportedOperationException("clone not supported", c);
        }
        if(params != null){
            r.params = new HashMap<>(params);
        }
        if(context != null){
            r.context = new HashMap<>(context);
        }
        return r;
    }

    /**
     * Вызывается из {@link #toString() }
     * @title Преобразование к строке
     * @param sb
     * @return Текстовое представление запроса
     */
    protected StringBuilder toString(StringBuilder sb){
        sb.append("query:\"");
        sb.append(this.sql).append('\"');
        if(params != null){
            sb.append(", params:");
            sb.append(params);
        }
        if(context != null){
            sb.append(", context:");
            sb.append(context);
        }

        sb.append(", resultTypes:");
        sb.append(getResultTypes());

        sb.append(", filter:");
        sb.append(filter);
        sb.append(", sort:");
        sb.append(sort);
        sb.append(", offset:");
        sb.append(offset);
        sb.append(", count:");
        sb.append(count);
        return sb;
    }

    /**
     * @title Преобразование к строке
     * @return Текстовое представление запроса
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append('[');
        toString(sb);
        sb.append(']');
        return sb.toString();
    }
}

