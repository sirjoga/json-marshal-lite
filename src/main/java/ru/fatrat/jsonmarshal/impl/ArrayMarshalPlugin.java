package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;
import ru.fatrat.jsonmarshal.JsonMarshalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;

public class ArrayMarshalPlugin implements JsonMarshalPlugin {
    
    @Override
    public boolean marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        if (!sourceClass.isArray()) return false;
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
        return true;
    }
}
