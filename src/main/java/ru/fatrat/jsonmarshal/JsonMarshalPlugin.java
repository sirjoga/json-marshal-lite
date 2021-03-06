package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Marshal plugin
 */
public interface JsonMarshalPlugin extends JsonPlugin {

    /**
     * Marshal supported types.
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
