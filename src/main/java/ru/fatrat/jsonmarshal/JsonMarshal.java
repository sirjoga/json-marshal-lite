package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

public interface JsonMarshal {

    void marshal(
            @Nonnull Object source,
            @Nonnull Type sourceClass,
            @Nonnull JsonGeneratorHelper destination,
            @Nullable JsonMarshalAnnotationSource annotationSource
    );

    default void marshal(
            @Nonnull Object source,
            @Nonnull Type sourceClass,
            @Nonnull JsonGeneratorHelper destination
    ) {
        marshal(source, sourceClass, destination, null);
    }

    default void marshal(@Nullable Object source, @Nonnull JsonGeneratorHelper destination) {
        if (source == null) {
            destination.writeNull();
        } else {
            marshal(source, source.getClass(), destination);
        }
    }

    default <T> void marshal(
            @Nullable T source, @Nonnull JsonGenericType<T> genericType, @Nonnull JsonGeneratorHelper destination
    ) {
        if (source == null) {
            destination.writeNull();
        } else {
            marshal(source, genericType.getType(), destination);
        }
    }

}
