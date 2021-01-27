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
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

/**
 * Unmarshalling plugin for {@link java.lang.reflect.WildcardType}.
 */
public class WildcardUnmarshalPlugin implements JsonUnmarshalPlugin {

    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Type destType, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        WildcardType wildcardType = (WildcardType) destType;
        return context.callback(source, wildcardType.getUpperBounds()[0], annotationSource);
    }

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof WildcardType) && ((WildcardType)type).getLowerBounds().length == 0;
    }
}
