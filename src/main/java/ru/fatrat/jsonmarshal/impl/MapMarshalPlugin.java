package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * Marshalling plugin for {@link Map}.
 */
public class MapMarshalPlugin implements JsonMarshalPlugin {
    @Override
    public void marshal(@Nonnull Object source, @Nonnull Type sourceType,
                        @Nullable JsonMarshalAnnotationSource annotationSource,
                        @Nonnull JsonMarshalContext context) {

        JsonGeneratorHelper helper = context.getGeneratorHelper();

        Type[] args = ((ParameterizedType) sourceType).getActualTypeArguments();
        Type keyType = args[0];
        Type valueType = args[1];

        if (keyType == String.class) {
            @SuppressWarnings("unchecked")
            Map<String, Object> sourceMap = (Map<String, Object>) source;
            helper.writeStartObject();
            sourceMap.forEach((key, value) -> {
               context.pushObjectFieldElementId(key);
               helper.setName(key);
               if (value == null) {
                   helper.writeNull();
               } else {
                   context.callback(value, valueType, null);
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
                    context.callback(key, keyType, null);
                }
                if (value == null) {
                    helper.writeNull();
                } else {
                    context.callback(value, valueType, null);
                }
                context.popElementId();
                helper.writeEnd();
            });
            helper.writeEnd();
        }
    }

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof ParameterizedType) && ((ParameterizedType)type).getRawType() == Map.class;
    }
}
