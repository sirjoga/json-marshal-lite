package ru.fatrat.jsonmarshal.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Iterator;

public interface ClassFieldIterator {
    @Nonnull Iterator<Field> fields(@Nonnull Class<?> cls);
}
