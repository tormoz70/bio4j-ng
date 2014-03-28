package ru.bio4j.service.sql.types;

import java.sql.Ref;
import java.sql.Struct;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.*;

public class JDBCTypeMapping {
    private static final Map<Integer, Class<?>> itoc;
    private static final Map<Class<?>, Integer> ctoi;

    static {
        Map<Integer, Class<?>> itocm = new HashMap<>();
// 8.9.3 JDBC Types Mapped to Java Object Types
        itocm.put(CHAR, String.class);
        itocm.put(VARCHAR, String.class);
        itocm.put(LONGVARCHAR, String.class);
        itocm.put(NUMERIC, BigDecimal.class);
        itocm.put(DECIMAL, BigDecimal.class);
        itocm.put(BIT, Boolean.class);
        itocm.put(TINYINT, Byte.class);
        itocm.put(SMALLINT, Short.class);
        itocm.put(INTEGER, Integer.class);
        itocm.put(BIGINT, Long.class);
        itocm.put(REAL, Float.class);
        itocm.put(FLOAT, Double.class);
        itocm.put(DOUBLE, Double.class);
        itocm.put(BINARY, byte[].class);
        itocm.put(VARBINARY, byte[].class);
        itocm.put(LONGVARBINARY, byte[].class);
        itocm.put(DATE, Date.class);
        itocm.put(TIME, Time.class);
        itocm.put(TIMESTAMP, Timestamp.class);
        itocm.put(CLOB, Clob.class);
        itocm.put(BLOB, Blob.class);
        itocm.put(ARRAY, Array.class);
        itocm.put(STRUCT, Struct.class);
        itocm.put(REF, Ref.class);
        itoc = Collections.unmodifiableMap(itocm);

        Map<Class<?>, Integer> ctoim = new HashMap<>();
        ctoim.put(String.class, /*CHAR, */VARCHAR/*, or LONGVARCHAR*/);
        ctoim.put(BigDecimal.class, NUMERIC);
        ctoim.put(Boolean.class, BIT);
        ctoim.put(Integer.class, INTEGER);
        ctoim.put(Long.class, BIGINT);
        ctoim.put(Float.class, REAL);
        ctoim.put(Double.class, DOUBLE);
        ctoim.put(byte[].class, /*BINARY, */VARBINARY/*, or LONGVARBINARY*/);
        ctoim.put(Date.class, DATE);
        ctoim.put(Time.class, TIME);
        ctoim.put(Timestamp.class, TIMESTAMP);
//в спецификации этого нет, и непосредственнаязапись этого
//класса в СУБД обычно не допустима, но у нас все проходет через конверторы типов
        ctoim.put(java.util.Date.class, TIMESTAMP);
        ctoim.put(Clob.class, CLOB);
        ctoim.put(Blob.class, BLOB);
        ctoim.put(Array.class, ARRAY);
        ctoim.put(Struct.class, STRUCT);
        ctoim.put(Ref.class, REF);
        ctoi = Collections.unmodifiableMap(ctoim);
    }

    /**
     * Возвращает значение поля из {@link java.sql.Types } для класса,
     * если  для класса нет значения то возвращает 'null'
     *
     * @param clazz
     * @return Значение поля из java.sql.Types для класса
     * @title Получение значения поля из java.sql.Types для класса
     */
    public static Integer getTypeByClass(Class<?> clazz) {
        return ctoi.get(clazz);
    }

    /**
     * Возвращает класс для значения поля из {@link java.sql.Types }, если
     * класса не окажется то вернет 'null'
     *
     * @param type
     * @return Класс для значения поля из java.sql.Types
     * @title Получение класса для значения поля из java.sql.Types
     */
    public static Class<?> getClassByType(int type) {
        return itoc.get(type);
    }
}
