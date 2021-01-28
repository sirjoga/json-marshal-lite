package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Marshalling plugin for {@link WildcardType}.
 */
public class WildcardMarshalPlugin implements JsonMarshalPlugin {

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Type sourceType, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        WildcardType wildcardType = (WildcardType) sourceType;
         context.callback(source, wildcardType.getUpperBounds()[0], annotationSource);
    }

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof WildcardType) && ((WildcardType)type).getLowerBounds().length == 0;
    }
}
