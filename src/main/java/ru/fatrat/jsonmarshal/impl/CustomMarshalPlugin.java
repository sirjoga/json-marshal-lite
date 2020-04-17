package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonCustomMarshallable;
import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CustomMarshalPlugin implements JsonMarshalPlugin {

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        ((JsonCustomMarshallable) source).jsonMarshal(context.getGeneratorHelper());
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return JsonCustomMarshallable.class.isAssignableFrom(cls);
    }
}