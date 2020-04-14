package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;

public class RawValueMarshalPlugin implements JsonMarshalPlugin {
    @Override public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        context.getGeneratorHelper().writeValue((JsonValue) source);
    }

    @Override public boolean canHandle(@Nonnull Class<?> cls) {
        return JsonValue.class.isAssignableFrom(cls);
    }
}
