package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JsonMarshalPlugin extends JsonPlugin {
    /**
     * Marshal classes known to the plugin.
     * 
     * @param source object to be marshalled
     * @param sourceClass class of source object definition
     * @param context marshalling context 
     */
    void marshal(
            @Nonnull Object source, 
            @Nonnull Class<?> sourceClass,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    );
    
}
