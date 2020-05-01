package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;
import ru.fatrat.jsonmarshal.JsonUnmarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Type;

public abstract class JsonClassUnmarshalPlugin implements JsonUnmarshalPlugin {

    @Nullable
    @Override
    public final Object unmarshal(
            @Nonnull JsonValue source, @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context
    ) {
        return unmarshal(source, (Class<?>) destType, annotationSource, context);
    }

    @Override
    public final boolean canHandle(@Nonnull Type type) {
        return type instanceof Class && canHandle((Class<?>) type);
    }

    protected abstract boolean canHandle(@Nonnull Class<?> cls);

    @Nullable
    protected abstract Object unmarshal(
            @Nonnull JsonValue source, @Nonnull Class<?> destClass,
            @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context
    );
}
