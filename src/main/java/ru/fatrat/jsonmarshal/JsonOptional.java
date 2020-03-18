package ru.fatrat.jsonmarshal;

import javax.annotation.Nullable;

public class JsonOptional<T> {
    @Nullable public final T value;
    public JsonOptional(T value) {
        this.value = value;
    }
}
