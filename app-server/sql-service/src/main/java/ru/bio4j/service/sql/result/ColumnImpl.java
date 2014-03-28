package ru.bio4j.service.sql.result;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ColumnImpl implements Column {
    /**
     * Отображаемое имя поля
     */
    private final String title;
    /**
     * Идентификатор поля из запроса, у разных Column может быть одинаковым
     */
    private final String field;
    /**
     * Размер поля
     */
    private int size = 0;

    private final String type;
    private final int weight;
    private final Map<String, Object> attributes;

    public ColumnImpl(String title, String field, int size, String typeName, int weight, Map<String, Object> attributes) {
        this.title = title;
        this.field = field;
        this.size = size;
        this.type = typeName;
        this.weight = weight;
        this.attributes = attributes;
    }

    /**
     * Возвращает идентификатор поля в запросе
     * @return Идентификатор колонки/поля в запросе
     * @title Получение идентификатора колонки/поля в запросе
     */
    @Override
    public String getField() {
        return field;
    }

    /**
     * Устанавливает отображаемое имя поля
     * @return Отображаемое имя колонки/поля
     * @title Получение отображаемого имя колонки/поля
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @return Размер колонки/поля
     * @title Получение размера колонки/поля
     */
    @Override
    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    /**
     * Возвращает весовой коэффициент по которому производится сортировка полей
     * при выводе
     * @return Весовой коэффициент сортировки колонок/полей при выводе
     * @title Получение весового коэффициента сортировки колонок/полей при выводе
     */
    @Override
    public int getWeight() {
        return weight;
    }

    /**
     * Аналог {@link #is(java.lang.String, boolean)  } cо вторым параметром false
     * @param attributeName
     * @return Истина, если у атрибута есть значение
     * @title Проверка существования значения атрибута
     */
    @Override
    public boolean is(String attributeName) {
        return is(attributeName, false);
    }

    /**
     * @param attributeName
     * @param defaultValue
     * @return Проверка существования значения атрибута
     * @title Проверка существования значения атрибута
     */
    @Override
    public boolean is(String attributeName, boolean defaultValue) {
        Object res = attributes.get(attributeName);
        if(res instanceof Boolean) {
            return (Boolean)res;
        }
        return res != null || defaultValue;
    }

    /**
     * @param attributeName
     * @return Значение атрибута
     * @title Получение значения атрибута
     */
    @Override
    public Object get(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * @param name
     * @param value
     * @title Установка значения атрибута
     */
    public void set(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Возвращает иммутабельную карту со списком всех аттрибутов.
     * <p/> для чтения определенного аттрибута используте {@link #get(java.lang.String)  }
     * @return Немодифицируемая карта аттрибутов поля/колонки
     * @title Получение списка аттрибутов поля/колонки
     */
    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Устанавливает все аттрибуты, для установки одного аттрибута
     * используйте {@link #set(java.lang.String, java.lang.Object)  }
     * @param attributes
     * @title Установка списка атрибутов
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes.clear();
        this.attributes.putAll(attributes);
    }

    /**
     * @title Преобразование поля/колонки к строке
     * @return Строка, содержащая текстовове представление колонки/поля
     */
    @Override
    public String toString() {
        return this.getClass().getName()+"{" +
            ", name:"+title+
            ", sinType:"+ type + "}";
    }

    public static class Builder {
        private String title;
        private String field;
        private int size;
        private String typeName;
        private int weight;
        private final Map<String, Object> attributes = new HashMap<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder field(String field) {
            this.field = field;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder typeName(String typeName) {
            this.typeName = typeName;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder addAttribute(String attributeName, String value) {
            this.attributes.put(attributeName, value);
            return this;
        }

        public ColumnImpl build() {
            return new ColumnImpl(title, field, size, typeName, weight, attributes);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
