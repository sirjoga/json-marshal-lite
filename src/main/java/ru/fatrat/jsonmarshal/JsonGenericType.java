package ru.fatrat.jsonmarshal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Helper for passing generic types to marshal/unmarshal logic.
 */
public abstract class JsonGenericType<T> {

    public Type getType() {
        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
