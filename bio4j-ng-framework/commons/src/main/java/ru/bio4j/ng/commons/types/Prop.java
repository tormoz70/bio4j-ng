package ru.bio4j.ng.commons.types;

import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {
    String name() default "";
    Param.Direction direction() default Param.Direction.IN;
    MetaType metaType() default MetaType.UNDEFINED;
}
