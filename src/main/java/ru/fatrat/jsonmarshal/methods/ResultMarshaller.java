package ru.fatrat.jsonmarshal.methods;

import ru.fatrat.jsonmarshal.JsonGeneratorHelper;

/**
 * Automarshal execution result.
 */
public interface ResultMarshaller extends AutoCloseable {
    void process(JsonGeneratorHelper destination);
    @Override default void close() {}
}
