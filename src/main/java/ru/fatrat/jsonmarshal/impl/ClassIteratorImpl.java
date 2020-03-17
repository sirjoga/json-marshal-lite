package ru.fatrat.jsonmarshal.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ClassIteratorImpl implements ClassIterator {

    private void addFieldsForClass(Class<?> current, List<Field> destination) {
        if (current == Object.class) return;
        Class<?> superClass = current.getSuperclass();
        addFieldsForClass(superClass, destination);
        destination.addAll(Arrays.asList(current.getDeclaredFields()));
    }

    @Nonnull
    @Override
    public Iterator<Field> fields(@Nonnull Class<?> cls) {
        List<Field> lst = new ArrayList<>();
        addFieldsForClass(cls, lst);
        return lst.iterator();
    }

    @Nullable
    @Override
    public <T extends Annotation> T getAnnotation(@Nonnull Class<?> cls, @Nonnull Class<T> annotationClass) {
        while (true) {
            if (cls == Object.class) return null;
            T res = cls.getAnnotation(annotationClass);
            if (res != null) return res;
            cls = cls.getSuperclass();
        }
    }
}
