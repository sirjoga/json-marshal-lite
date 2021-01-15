package ru.fatrat.jsonmarshal;

/**
 * Custom marshal logic interface
 */
public interface JsonCustomMarshallable {
    void jsonMarshal(JsonGeneratorHelper dest);
}
