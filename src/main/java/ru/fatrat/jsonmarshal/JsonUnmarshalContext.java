package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;

public interface JsonUnmarshalContext extends JsonMarshalStackContext {
     <T> T callback(
             @Nonnull JsonValue source,
             @Nonnull Class<T> destClass,
             @Nullable JsonMarshalAnnotationSource annotationSource
     );
}
