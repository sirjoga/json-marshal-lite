package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;

public interface JsonUnmarshalContext extends JsonMarshalStackContext {
     @Nullable Object callback(
             @Nonnull JsonValue source,
             @Nonnull Class<?> destClass,
             @Nullable JsonMarshalAnnotationSource annotationSource
     );
}
