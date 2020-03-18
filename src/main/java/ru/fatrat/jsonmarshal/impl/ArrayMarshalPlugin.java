package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;

public class ArrayMarshalPlugin implements JsonMarshalPlugin {


    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return cls.isArray();
    }

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        if (!sourceClass.isArray()) throw new JsonMarshalException("Cannot marshal non-array");
        Class<?> elementClass = sourceClass.getComponentType();
        JsonGeneratorHelper helper = context.getGeneratorHelper();
        helper.writeStartArray();
        for (int i=0; i< Array.getLength(source); i++) {
            context.pushArrayElementId(i);
            Object o = Array.get(source, i);
            context.callback(o, elementClass, annotationSource);
            context.popElementId();
        }
        helper.writeEnd();
    }
}
