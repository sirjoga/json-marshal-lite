package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;

public interface JsonUnmarshalPlugin {

    /**
     * Returns null if cannot unmarshal.
     */
    @Nullable Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<?> destClass,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonUnmarshalContext context
    ); 
}
