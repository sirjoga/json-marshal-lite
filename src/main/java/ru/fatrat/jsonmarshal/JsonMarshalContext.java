package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Context helper for marshaller plugins.
 */
public interface JsonMarshalContext extends JsonMarshalStackContext {
     /**
      * Marshal callback.
      *
      * @param source source object
      * @param sourceType type info
      * @param annotationSource annotation source if differs from source
      */
     void callback(@Nonnull Object source, @Nonnull Type sourceType, @Nullable JsonMarshalAnnotationSource annotationSource);

     /**
      * Destination supplier.
      */
     @Nonnull JsonGeneratorHelper getGeneratorHelper();
}
