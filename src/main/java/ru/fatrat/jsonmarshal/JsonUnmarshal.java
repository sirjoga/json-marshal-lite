package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

public interface JsonUnmarshal {

    @Nullable
    Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource
    );

    @SuppressWarnings("unchecked")
    @Nullable
    default <T> T unmarshalNullable(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destType
    ) {
        return (T) unmarshal(source, destType, null);
    }

    @Nonnull
    default <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {
        return Optional.ofNullable(unmarshalNullable(source, destClass)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

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

    @Nonnull
    default <T> T unmarshalIgnoreGarbage(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {
        return Optional.ofNullable(unmarshalIgnoreGarbageNullable(source, destClass)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    default <T> T unmarshalNullable(
            @Nonnull JsonValue source, JsonGenericType<T> genericType
    ) {
        return (T) unmarshal(source, genericType.getType(), null);
    }

    @Nullable
    default <T> T unmarshal(
            @Nonnull JsonValue source, JsonGenericType<T> genericType
    ) {
        return Optional.ofNullable(unmarshalNullable(source, genericType)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

}
