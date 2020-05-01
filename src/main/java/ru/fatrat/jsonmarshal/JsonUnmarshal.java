package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Type;
import java.util.Optional;

public interface JsonUnmarshal {

    @Nullable Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource
    );

    @SuppressWarnings("unchecked")
    @Nullable default <T> T unmarshalNullable(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destType
    ) {
        return (T) unmarshal(source, destType, null);
    }

    @Nonnull default <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {
        return Optional.ofNullable(unmarshalNullable(source, destClass)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

}
