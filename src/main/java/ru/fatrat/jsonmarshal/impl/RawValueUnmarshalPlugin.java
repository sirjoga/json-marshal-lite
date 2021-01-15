package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;

/**
 * Do not unmarshal anything - just pass JsonValue as result.
 */
public class RawValueUnmarshalPlugin extends JsonClassUnmarshalPlugin {
    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Class<?> destClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        if (!destClass.isAssignableFrom(source.getClass()))
            throw new JsonMarshalException(String.format("Cannot convert %s to %s", source.getClass().getName(),
                    destClass.getName()));
        return source;
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return JsonValue.class.isAssignableFrom(cls);
    }
}
