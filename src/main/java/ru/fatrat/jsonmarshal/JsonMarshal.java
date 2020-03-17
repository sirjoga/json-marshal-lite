package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JsonMarshal {
    
    void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nonnull JsonGeneratorHelper destination);
    
    default void marshal(@Nullable Object source, @Nonnull JsonGeneratorHelper destination) {
        if (source == null) {
            destination.writeNull();
        } else {
            marshal(source, source.getClass(), destination);
        }
    }
}
