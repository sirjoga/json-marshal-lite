package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

public interface JsonMarshalPlugin extends JsonPlugin {
    /**
     * Marshal classes known to the plugin.
     *
     * @param source object to be marshalled
     * @param sourceType type of source object definition
     * @param annotationSource optional annotations
     * @param context marshalling context
     */
    void marshal(
            @Nonnull Object source, 
            @Nonnull Type sourceType,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    );
    
}
