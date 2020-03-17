package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class JsonOptionalPlugin implements JsonMarshalPlugin {

    @Override
    public boolean marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        if (!JsonOptional.class.isAssignableFrom(sourceClass)) {
            return false;
        }


        annotationSource = Optional.ofNullable(annotationSource).orElseThrow(
                () -> new JsonMarshalException("Cannot use Optionals without annotation source"));

        JsonOptionalClass oClass = Optional.ofNullable(annotationSource.getAnnotation(JsonOptionalClass.class))
                .orElseThrow(() ->
                        new JsonMarshalException("Cannot use Optionals without JsonOptionalClass annotation")
                );

        JsonGeneratorHelper helper = context.getGeneratorHelper();

        @SuppressWarnings("unchecked")
        JsonOptional<Object> val = (JsonOptional<Object>) source;
        if (!val.hasValue) {
            helper.cancelName();
        } else if (val.value == null) {
            helper.writeNull();
        } else {
            context.callback(val.value, oClass.value(), annotationSource);
        }
        return true;
    }

}
