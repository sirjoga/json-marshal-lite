package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

public interface JsonPlugin {
    boolean canHandle(@Nonnull Type type);
}
