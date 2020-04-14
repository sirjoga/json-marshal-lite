package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MapUnmarshalPlugin implements JsonUnmarshalPlugin {

    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Class<?> destClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        JsonMap annotation = annotationSource.getAnnotation(JsonMap.class);
        Map<Object, Object> result = new LinkedHashMap<>();
        Class<?> keyClass = annotation.asArray();
        if (keyClass == Object.class) {
            if (!(source instanceof JsonObject))
                throw new JsonMarshalException("JsonMap unmarshal source must be an object");
            JsonObject objectSource = (JsonObject) source;
            objectSource.forEach((key, value) -> {
                result.put(key, Optional.ofNullable(value)
                        .map(v -> context.callback(v, annotation.value(), null))
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
                Object key = jKey == JsonValue.NULL ? null : context.callback(jKey, keyClass, null);
                Object value = jValue == JsonValue.NULL ? null : context.callback(jValue, annotation.value(), null);
                context.popElementId();
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return Map.class.equals(cls);
    }
}
