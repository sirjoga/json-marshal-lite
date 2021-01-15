package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Type;

/**
 * Unmarshal plugin.
 */
public interface JsonUnmarshalPlugin extends JsonPlugin {

    /**
     * Unmarshal supported types.
     *
     * @param source Json source
     * @param destType type info
     * @param annotationSource unmarshal annotation source
     * @param context unmarshalling context helper
     */
    @Nullable Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonUnmarshalContext context
    ); 
}
