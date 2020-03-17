package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JsonMarshalContext extends JsonMarshalStackContext {
     void callback(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource);
     @Nonnull JsonGeneratorHelper getGeneratorHelper();
}
