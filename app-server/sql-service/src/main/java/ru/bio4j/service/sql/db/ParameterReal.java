package ru.bio4j.service.sql.db;

/**
 * Реализация интерфейса колонки процедуры
 * @title Параметр (колонка) процедуры
 */
public class ParameterReal implements Parameter {

    private String name;
    private int sqlType;
    private ParameterType parameterType;
    private String typeName;
    private int size;
    private String comment;
    private Object defaultValue;
    private int position;

    /**
     * Имя объекта
     * @title Получение имени типа
     * @return Имя типа
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @title Получение имени типа
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * kind of column/parameter:
     * @title Получение типа параметра
     * @return Тип параметра
     */
    @Override
    public ParameterType getParameterType() {
        return parameterType;
    }

    /**
     * @title Установка типа параметра
     * @param parameterType
     */
    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType;
    }

    /**
     * SQL type from java.sql.Types
     * @title Получение sql-типа
     * @return SQL-тип
     */
    @Override
    public int getSQLType() {
        return sqlType;
    }

    /**
     * @title Установка sql-типа
     * @param sqlType
     */
    public void setSQLType(int sqlType) {
        this.sqlType = sqlType;
    }

    /**
     *  Имя типа, может быть как имененм SQL типа, так и именем произвольного
     * типа определенного пользователем, это зависит от реализации
     * @title Получение имени типа
     * @return Имя типа
     */
    @Override
    public String getTypeName() {
        return typeName;
    }

    /**
     * @title Установка имени типа
     * @param typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     *  int => length in bytes of data
     * @title Получение размера типа
     * @return Размер типа
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * @title Установка размера
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *  String => comment describing parameter/column
     * @title Получение описания типа
     * @return Описание типа
     */
    @Override
    public String getComment() {
        return comment;
    }

    /**
     * @title Установка описания типа
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
     * The string NULL (not enclosed in quotes) - if NULL was specified as the default value
     * TRUNCATE (not enclosed in quotes) - if the specified default value cannot be represented without truncation
     * NULL - if a default value was not specified
     * @title Получение значения по умолчанию для типа
     * @return Значение по умолчанию для типа
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @title Установка значения по умолчанию для типа
     * @param defaultValue
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * int => the ordinal position, starting from 1, for the input and output parameters for a procedure.
     * A value of 0 is returned if this row describes the procedure's return value.
     * For result set columns, it is the ordinal position of the column in the result set starting from 1.
     * If there are multiple result sets, the column ordinal positions are implementation defined.
     * @title Получение порядкового номера параметра
     * @return Порядковый номер параметра
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * @title Установка порядкового номера параметра
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @title Проверка существования значения по умолчанию для типа
     * @return true, если значение по умолчанию существует
     */
    @Override
    public boolean isHasDefaultValue() {
        return defaultValue != null;
    }

    /**
     * @title Преобразование к строке
     * @return Текстовое представление параметра процедуры
     */
    @Override
    public String toString() {
        return "ParameterReal{" + "name=" + name + ", sqlType=" + sqlType + ", parameterType=" + parameterType +
                ", typeName=" + typeName + ", size=" + size + ", comment=" + comment + ", defaultValue=" + defaultValue + ", position=" + position + '}';
    }
}
