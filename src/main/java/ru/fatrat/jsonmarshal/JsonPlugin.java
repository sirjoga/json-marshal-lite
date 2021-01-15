package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * Base for marshal/unmarshal plugins.
 */
public interface JsonPlugin {
    boolean canHandle(@Nonnull Type type);
}
