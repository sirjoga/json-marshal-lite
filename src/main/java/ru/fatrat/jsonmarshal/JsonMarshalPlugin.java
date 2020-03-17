package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JsonMarshalPlugin {
    /**
     * Marshal classes known to the plugin.
     * 
     * @param source object to be marshalled
     * @param sourceClass class of source object definition
     * @param context marshalling context 
     * @return true if plugin knows how to marshal this class and completed it successfully.
     */
    boolean marshal(
            @Nonnull Object source, 
            @Nonnull Class<?> sourceClass,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    ); 
}
