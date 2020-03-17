package ru.fatrat.jsonmarshal;

import javax.annotation.Nullable;

public class JsonOptional<T> {
    @Nullable public final T value;
    public final boolean hasValue;
    public JsonOptional(T value, boolean hasValue) {
        if (value != null && !hasValue) {
            throw new IllegalArgumentException("Inconsistent JsonOptional state");
        }
        this.value = value;
        this.hasValue = hasValue;
    }
}
