package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonCustomMarshallable;
import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Handle classes with JsonCustomMarshallable support.
 */
public class CustomMarshalPlugin extends JsonClassMarshalPlugin {

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        ((JsonCustomMarshallable) source).jsonMarshal(context.getGeneratorHelper());
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return JsonCustomMarshallable.class.isAssignableFrom(cls);
    }
}
