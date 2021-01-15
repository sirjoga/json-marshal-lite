package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Unmarshalling for {@link JsonOptional}.
 */
public class JsonOptionalUnmarshalPlugin implements JsonUnmarshalPlugin {

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof ParameterizedType) && ((ParameterizedType)type).getRawType() == JsonOptional.class;
    }

    @Nullable
    @Override
    public Object unmarshal(
            @Nonnull JsonValue source, @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context
    ) {
        if (source == JsonValue.NULL) return new JsonOptional<>(null);
        Type type = ((ParameterizedType) destType).getActualTypeArguments()[0];
        return new JsonOptional<>(context.callback(source, type, annotationSource));
    }
}
