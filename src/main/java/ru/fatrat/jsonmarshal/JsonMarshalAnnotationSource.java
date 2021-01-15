package ru.fatrat.jsonmarshal;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Annotation source, in case it differs from source object.
 */
public interface JsonMarshalAnnotationSource {
    @Nullable <T extends Annotation> T getAnnotation(Class<T> cls);
}
