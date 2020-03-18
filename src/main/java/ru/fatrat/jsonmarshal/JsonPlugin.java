package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;

public interface JsonPlugin {
    boolean canHandle(@Nonnull Class<?> cls);
}
