package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;

public interface JsonMarshalStackContext {
    void pushArrayElementId(int id);
    void pushObjectFieldElementId(@Nonnull String id);
    void popElementId();
}
