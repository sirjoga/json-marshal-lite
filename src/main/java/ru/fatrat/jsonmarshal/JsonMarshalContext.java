package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

public interface JsonMarshalContext extends JsonMarshalStackContext {
     void callback(@Nonnull Object source, @Nonnull Type sourceType, @Nullable JsonMarshalAnnotationSource annotationSource);
     @Nonnull JsonGeneratorHelper getGeneratorHelper();
}
