package ru.fatrat.jsonmarshal;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

public interface JsonMarshalAnnotationSource {
    @Nullable <T extends Annotation> T getAnnotation(Class<T> cls);
}
