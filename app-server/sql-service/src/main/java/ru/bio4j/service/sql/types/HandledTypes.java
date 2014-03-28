package ru.bio4j.service.sql.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация добавляет к handlers информацию о типах которые handlers может
 * обрабатывать
 * @title Аннотация, добавляющая к обработчику информацию о типах которые он может обрабатывать
 * @author rad
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandledTypes {
    /**
     * SQL типы
     * @return
     */
    int[] sql();
    /**
     * Проприетарные sql типы
     * @return
     */
    String[] sqlNames() default {};
    /**
     * Мета типы
     */
    String metaType();
    /**
     * java типы
     * @return
     */
    Class[] java();
}