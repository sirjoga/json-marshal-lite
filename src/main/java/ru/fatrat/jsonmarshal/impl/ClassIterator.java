package ru.fatrat.jsonmarshal.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Iterator;

public interface ClassIterator {
    @Nonnull Iterator<Field> fields(@Nonnull Class<?> cls);
    @Nullable <T extends Annotation> T getAnnotation(@Nonnull Class<?> cls, @Nonnull Class<T> annotationClass);
}
