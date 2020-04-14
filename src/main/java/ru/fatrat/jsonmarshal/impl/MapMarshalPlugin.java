package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class MapMarshalPlugin implements JsonMarshalPlugin {
    @Override
    public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass,
                        @Nullable JsonMarshalAnnotationSource annotationSource,
                        @Nonnull JsonMarshalContext context) {

        JsonGeneratorHelper helper = context.getGeneratorHelper();

        JsonMap annotation = Optional.ofNullable(annotationSource)
                .map(src -> src.getAnnotation(JsonMap.class))
                .orElse(null);
        if (annotation == null) throw new JsonMarshalException("Cannot marshal map without JsonMap annotation");
        Class<?> keyClass = annotation.asArray();
        if (keyClass == Object.class) {
            @SuppressWarnings("unchecked")
            Map<String, Object> sourceMap = (Map<String, Object>) source;
            helper.writeStartObject();
            sourceMap.forEach((key, value) -> {
               context.pushObjectFieldElementId(key);
               helper.setName(key);
               if (value == null) {
                   helper.writeNull();
               } else {
                   context.callback(value, annotation.value(), null);
               }
               context.popElementId();
            });
            helper.writeEnd();
        } else {
            @SuppressWarnings("unchecked")
            Map<Object, Object> sourceMap = (Map<Object, Object>) source;
            helper.writeStartArray();
            sourceMap.forEach((key, value) -> {
                helper.writeStartArray();
                context.pushObjectFieldElementId(Optional.ofNullable(key).map(Object::toString).orElse("null"));
                if (key == null) {
                    helper.writeNull();
                } else {
                    context.callback(key, keyClass, null);
                }
                if (value == null) {
                    helper.writeNull();
                } else {
                    context.callback(value, annotation.value(), null);
                }
                context.popElementId();
                helper.writeEnd();
            });
            helper.writeEnd();
        }
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return Map.class.equals(cls);
    }
}
