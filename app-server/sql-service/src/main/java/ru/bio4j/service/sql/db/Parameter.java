package ru.bio4j.service.sql.db;

import java.sql.DatabaseMetaData;

/**
 * Параметр процедуры
 *
 * @title Интерфейс параметра процедуры
 */
public interface Parameter extends DBObject, Typeable {

    /**
     * @title Типы параметров
     */
    public enum ParameterType {
        /**
         * nobody knows
         */
        UNKNOWN(DatabaseMetaData.procedureColumnUnknown),
        /**
         * IN parameter
         */
        IN(DatabaseMetaData.procedureColumnIn),
        /**
         * INOUT parameter
         */
        IN_OUT(DatabaseMetaData.procedureColumnInOut),
        /**
         * OUT parameter
         */
        OUT(DatabaseMetaData.procedureColumnOut),
        /**
         * procedure return value
         */
        RETURN(DatabaseMetaData.procedureColumnReturn),
        /**
         * result column in ResultSet
         */
        RESULT(DatabaseMetaData.procedureColumnResult);

        private final int type;

        private ParameterType(int type) {
            this.type = type;
        }

        /**
         * @return Типа
         * @title Получение типа
         */
        public int getType() {
            return type;
        }

        public static ParameterType typeForInt(final int type) {
            for (ParameterType parameterType : values()) {
                if (parameterType.getType() == type) {
                    return parameterType;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * Имя объекта
     *
     * @return Имя типа
     * @title Получение имени типа
     */
    @Override
    String getName();

    /**
     * kind of column/parameter:
     *
     * @return Тип параметра
     * @title Получение типа параметра
     */
    ParameterType getParameterType();

    /**
     * String => comment describing parameter/column
     *
     * @return Описание типа
     * @title Получение описания типа
     */
    String getComment();

    /**
     * default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
     * The string NULL (not enclosed in quotes) - if NULL was specified as the default value
     * TRUNCATE (not enclosed in quotes) - if the specified default value cannot be represented without truncation
     * NULL - if a default value was not specified
     *
     * @return Значение по умолчанию для типа
     * @title Получение значения по умолчанию для типа
     */
    Object getDefaultValue();

    /**
     * @return true, если значение по умолчанию существует
     * @title Проверка существования значения по умолчанию для типа
     */
    boolean isHasDefaultValue();

    /**
     * int => the ordinal position, starting from 1, for the input and output parameters for a procedure.
     * A value of 0 is returned if this row describes the procedure's return value.
     * For result set columns, it is the ordinal position of the column in the result set starting from 1.
     * If there are multiple result sets, the column ordinal positions are implementation defined.
     *
     * @return Порядковый номер параметра
     * @title Получение порядкового номера параметра
     */
    int getPosition();

    /** String => ISO rules are used to determine the nullability for a column.
     * YES --- if the parameter can include NULLs
     * NO --- if the parameter cannot include NULLs
     * empty string --- if the nullability for the parameter is unknown
     * /
     @MapTo("IS_NULLABLE")
     Boolean isNullable();
     */
}