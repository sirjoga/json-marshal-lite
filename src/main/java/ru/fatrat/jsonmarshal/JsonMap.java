package ru.fatrat.jsonmarshal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonMap {
    /**
     * if defined, map will be serialized/deserialized as Json array of [key,value] arrays.
     * Object.class is "serialize as Json object" marker
     */
    Class<?> asArray() default Object.class;
    Class<?> value();
}
