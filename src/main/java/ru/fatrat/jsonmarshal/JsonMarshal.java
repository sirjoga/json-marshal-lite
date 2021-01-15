package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Core marshal interface.
 */
public interface JsonMarshal {

    /**
     * Marshalling logic core.
     *
     * @param source source object
     * @param sourceClass Type info
     * @param destination marshalling destination
     * @param annotationSource marshalling annotation supplier
     */
    void marshal(
            @Nonnull Object source,
            @Nonnull Type sourceClass,
            @Nonnull JsonGeneratorHelper destination,
            @Nullable JsonMarshalAnnotationSource annotationSource
    );

    /**
     * Marshalling without annotation source
     *
     * @param source source object
     * @param sourceClass Type info
     * @param destination marshalling destination
     */
    default void marshal(
            @Nonnull Object source,
            @Nonnull Type sourceClass,
            @Nonnull JsonGeneratorHelper destination
    ) {
        marshal(source, sourceClass, destination, null);
    }

    /**
     * Marshalling without annotation source and generic info
     *
     * @param source source object
     * @param destination marshalling destination
     */
    default void marshal(@Nullable Object source, @Nonnull JsonGeneratorHelper destination) {
        if (source == null) {
            destination.writeNull();
        } else {
            marshal(source, source.getClass(), destination);
        }
    }

    /**
     * Marshalling with generic type info
     *
     * @param source source object
     * @param genericType helper for passing generic type info
     * @param destination marshalling destination
     */
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
