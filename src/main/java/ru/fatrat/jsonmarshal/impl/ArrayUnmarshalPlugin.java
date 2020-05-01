package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonValue;
import java.lang.reflect.Array;

public class ArrayUnmarshalPlugin extends JsonClassUnmarshalPlugin {

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return cls.isArray();
    }

    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Class<?> destClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        if (!destClass.isArray()) throw new JsonMarshalException("Cannot marshal non-array");
        if (source == JsonValue.NULL) return null;
        Class<?> elementClass = destClass.getComponentType();
        if (!(source instanceof JsonArray)) throw new JsonMarshalException("Cannot unmarshal JSON as array");
        JsonArray array = (JsonArray) source;
        Object res = Array.newInstance(elementClass, array.size());
        for (int i=0; i<array.size(); i++) {
            context.pushArrayElementId(i);
            Array.set(res, i, context.callback(array.get(i), elementClass, annotationSource));
            context.popElementId();
        }
        return res;
    }

}
