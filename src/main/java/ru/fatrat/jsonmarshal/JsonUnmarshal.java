package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;

public interface JsonUnmarshal {

    @Nullable <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<?> destClass,
            @Nullable JsonMarshalAnnotationSource annotationSource
    );

    @Nullable default <T> T unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<?> destClass
    ) {
        return unmarshal(source, destClass, null);
    }

}
