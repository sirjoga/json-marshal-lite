package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Type;

/**
 * Context helper for unmarshaller plugins.
 */
public interface JsonUnmarshalContext extends JsonMarshalStackContext {

     /**
      * Unmarshal callback.
      *
      * @param source Json source
      * @param destType type info
      * @param annotationSource annotation source
      */
     @Nullable Object callback(
             @Nonnull JsonValue source,
             @Nonnull Type destType,
             @Nullable JsonMarshalAnnotationSource annotationSource
     );
}
