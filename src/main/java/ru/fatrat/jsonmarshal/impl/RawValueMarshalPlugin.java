package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;

/**
 * Passing raw JsonValue as marshalling result.
 */
public class RawValueMarshalPlugin extends JsonClassMarshalPlugin {
    @Override public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        context.getGeneratorHelper().writeValue((JsonValue) source);
    }

    @Override public boolean canHandle(@Nonnull Class<?> cls) {
        return JsonValue.class.isAssignableFrom(cls);
    }
}
