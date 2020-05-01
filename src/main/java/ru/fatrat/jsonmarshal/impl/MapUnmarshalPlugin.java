package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MapUnmarshalPlugin implements JsonUnmarshalPlugin {

    @Nullable
    @Override
    public Object unmarshal(
            @Nonnull JsonValue source, @Nonnull Type destType,
            @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context
    ) {

        Map<Object, Object> result = new LinkedHashMap<>();

        Type[] args = ((ParameterizedType) destType).getActualTypeArguments();
        Type keyType = args[0];
        Type valueType = args[1];

        if (keyType == String.class) {
            if (!(source instanceof JsonObject))
                throw new JsonMarshalException("JsonMap unmarshal source must be an object");
            JsonObject objectSource = (JsonObject) source;
            objectSource.forEach((key, value) -> {
                result.put(key, Optional.ofNullable(value)
                        .map(v -> context.callback(v, valueType, null))
                        .orElse(null));
            });
        } else {
            if (!(source instanceof JsonArray))
                throw new JsonMarshalException("JsonMap unmarshal source must be an array");
            JsonArray arraySource = (JsonArray) source;
            for (JsonValue item: arraySource) {
                if (!(item instanceof JsonArray))
                    throw new JsonMarshalException("JsonMap array item must be an array");
                JsonArray arrayItem = (JsonArray) item;
                if (arrayItem.size() != 2)
                    throw new JsonMarshalException("JsonMap array item size must be 2");
                JsonValue jKey = arrayItem.get(0);
                JsonValue jValue = arrayItem.get(1);
                context.pushObjectFieldElementId(jKey == null ? "null" : jKey.toString());
                Object key = jKey == JsonValue.NULL ? null : context.callback(jKey, keyType, null);
                Object value = jValue == JsonValue.NULL ? null : context.callback(jValue, valueType, null);
                context.popElementId();
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof ParameterizedType) && ((ParameterizedType)type).getRawType() == Map.class;
    }

    public static void main(String[] args) {
    }

}
