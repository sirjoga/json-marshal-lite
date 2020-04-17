package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.util.Optional;

public interface JsonUnmarshal {

    @Nullable <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass,
            @Nullable JsonMarshalAnnotationSource annotationSource
    );

    @Nullable default <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {
        return unmarshal(source, destClass, null);
    }

    @Nonnull default <T> T unmarshalNonNull(
            @Nonnull JsonValue source,
            @Nonnull Class<T> destClass
    ) {
        return Optional.ofNullable(unmarshal(source, destClass)).orElseThrow(() ->
                new JsonMarshalException("Null unmarshal result"));
    }

}
