package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Marshalling for {@link JsonOptional}.
 */
public class JsonOptionalMarshalPlugin implements JsonMarshalPlugin {

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Type sourceType, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {

        Type type = ((ParameterizedType) sourceType).getActualTypeArguments()[0];

        annotationSource = Optional.ofNullable(annotationSource).orElseThrow(
                () -> new JsonMarshalException("Cannot use Optionals without annotation source"));

        JsonGeneratorHelper helper = context.getGeneratorHelper();

        @SuppressWarnings("unchecked")
        JsonOptional<Object> val = (JsonOptional<Object>) source;
        if (val.value == null) {
            helper.writeNull();
        } else {
            context.callback(val.value, type, annotationSource);
        }
    }

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof ParameterizedType) && ((ParameterizedType)type).getRawType() == JsonOptional.class;
    }

}
