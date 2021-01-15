package ru.fatrat.jsonmarshal.impl;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class field iteration strategy implementing.
 */
public class ClassFieldIteratorImpl implements ClassFieldIterator {

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
}
