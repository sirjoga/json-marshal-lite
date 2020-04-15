package ru.fatrat.jsonmarshal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface JsonMap {
    /**
     * if defined, map will be serialized/deserialized as Json array of [key,value] arrays.
     * <p>Object.class is "serialize as Json object with String key" marker
     */
    Class<?> asArray() default Object.class;

    /**
     * Value class (required).
     */
    Class<?> value();
}
