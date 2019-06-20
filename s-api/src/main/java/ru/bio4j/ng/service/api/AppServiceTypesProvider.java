package ru.bio4j.ng.service.api;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.lang.annotation.Target;


@Retention(SOURCE)
@Documented
@Target(TYPE)
public @interface AppServiceTypesProvider {
    Class value() default void.class;
}
