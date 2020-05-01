package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

public abstract class JsonClassMarshalPlugin implements JsonMarshalPlugin {

    @Override
    public final void marshal(
            @Nonnull Object source, @Nonnull Type sourceType, @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    ) {
        if (!(sourceType instanceof Class)) throw new JsonMarshalException("source type is not class");
        marshal(source, (Class<?>) sourceType, annotationSource, context);
    }

    @Override
    public final boolean canHandle(@Nonnull Type type) {
        return type instanceof Class && canHandle((Class<?>) type);
    }

    protected abstract boolean canHandle(@Nonnull Class<?> cls);

    protected abstract void marshal(
            @Nonnull Object source, @Nonnull Class<?> sourceClass,
            @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context
    );
}
