package ru.fatrat.jsonmarshal.methods;

import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshalException;

public interface ResultMarshaller extends AutoCloseable {
    void process(JsonGeneratorHelper destination);
    @Override default void close() {}
}
