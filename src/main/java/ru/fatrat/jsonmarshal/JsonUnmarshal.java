package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Core unmarshal interface.
 */
public interface JsonUnmarshal {

    /**
     * Unmarshalling logic core.
     *
     * @param source Json source
     * @param destType type info
     * @param annotationSource marshalling annotation supplier
     */
    @Nullable
    Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource
    );

    /**
     * Unmarshal nullable without annotation source and generic info.
     *
     * @param source Json source
     * @param destType result class
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default <T> T unmarshalNullable(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destType
    ) {
        return (T) unmarshal(source, destType, null);
    }

    /**
     * Unmarshal and non-null result check.
     *
     * @param source Json source
     * @param destClass result class
     */
    @Nonnull
    default <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {
        return Optional.ofNullable(unmarshalNullable(source, destClass)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

    /**
     * Unmarshal and ignore garbage object elements.
     *
     * @param source Json source
     * @param destClass result class
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default <T> T unmarshalIgnoreGarbageNullable(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {

        JsonMarshalAnnotationSource annotationSource = new JsonMarshalAnnotationSource() {
            @Nullable
            @Override
            public <AT extends Annotation> AT getAnnotation(Class<AT> cls) {
                if (cls != JsonIgnoreGarbage.class) return null;
                return (AT) new JsonIgnoreGarbage() {
                    @Override
                    public Class<? extends Annotation> annotationType() { return null; }
                };
            }
        };

        return (T) unmarshal(source, destClass, annotationSource);
    }

    /**
     * Unmarshal, ignore garbage object elements and result non-null check.
     *
     * @param source Json source
     * @param destClass result class
     */
    @Nonnull
    default <T> T unmarshalIgnoreGarbage(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {
        return Optional.ofNullable(unmarshalIgnoreGarbageNullable(source, destClass)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

    /**
     * Unmarshal with generic type info.
     *
     * @param source Json source
     * @param genericType generic type helper
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default <T> T unmarshalNullable(
            @Nonnull JsonValue source, JsonGenericType<T> genericType
    ) {
        return (T) unmarshal(source, genericType.getType(), null);
    }

    /**
     * Unmarshal and non-null result check with generic type info.
     *
     * @param source Json source
     * @param genericType generic type helper
     */
    @Nonnull
    default <T> T unmarshal(
            @Nonnull JsonValue source, JsonGenericType<T> genericType
    ) {
        return Optional.ofNullable(unmarshalNullable(source, genericType)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

}
