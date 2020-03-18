package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.util.Optional;

public class JsonOptionalUnmarshalPlugin implements JsonUnmarshalPlugin {
    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return JsonOptional.class.isAssignableFrom(cls);
    }

    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Class<?> destClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        if (!JsonOptional.class.isAssignableFrom(destClass))
            throw new JsonMarshalException("Only optionals are supported");
        if (source == JsonValue.NULL) return new JsonOptional<>(null);
        if (annotationSource == null) 
            throw new JsonMarshalException("Cannot use JsonOptional on non-field sources");
        JsonOptionalClass annotation = Optional.ofNullable(annotationSource.getAnnotation(JsonOptionalClass.class))
                .orElseThrow(
                        () -> new JsonMarshalException("Cannot use JsonOptional without JsonOptionalClass annotation")
                );
        return new JsonOptional<>(context.callback(source, annotation.value(), annotationSource));
    }
}
