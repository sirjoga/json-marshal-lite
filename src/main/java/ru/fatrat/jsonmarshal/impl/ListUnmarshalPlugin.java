package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;
import ru.fatrat.jsonmarshal.JsonUnmarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArray;
import javax.json.JsonValue;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Unmarshalling plugin for {@link List}.
 */
public class ListUnmarshalPlugin implements JsonUnmarshalPlugin {

    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Type destType, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        if (!(source instanceof JsonArray)) throw new JsonMarshalException("source for List is not json array");
        JsonArray jArr = (JsonArray) source;
        Type[] args = ((ParameterizedType) destType).getActualTypeArguments();
        Type elemType = args[0];
        List<Object> result = new ArrayList<>();
        for (int i=0; i<jArr.size(); i++) {
            JsonValue value = jArr.get(i);
            context.pushArrayElementId(i);
            result.add(context.callback(value, elemType, null));
            context.popElementId();
        }
        return result;
    }

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof ParameterizedType) && ((ParameterizedType)type).getRawType() == List.class;
    }
}
