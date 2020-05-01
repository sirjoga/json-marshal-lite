package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Type;

public interface JsonUnmarshalContext extends JsonMarshalStackContext {
     @Nullable Object callback(
             @Nonnull JsonValue source,
             @Nonnull Type destType,
             @Nullable JsonMarshalAnnotationSource annotationSource
     );
}
