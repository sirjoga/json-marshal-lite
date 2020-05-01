package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Type;

public interface JsonUnmarshalPlugin extends JsonPlugin {

    @Nullable Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonUnmarshalContext context
    ); 
}
