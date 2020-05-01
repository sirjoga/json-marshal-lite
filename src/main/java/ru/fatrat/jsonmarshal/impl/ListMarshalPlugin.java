package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ListMarshalPlugin implements JsonMarshalPlugin {

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Type sourceType, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        Type[] args = ((ParameterizedType) sourceType).getActualTypeArguments();
        Type elemType = args[0];
        @SuppressWarnings("unchecked")
        List<Object> sourceList = (List<Object>) source;
        JsonGeneratorHelper dest = context.getGeneratorHelper();
        dest.writeStartArray();
        for (int i=0; i<sourceList.size(); i++) {
            context.pushArrayElementId(i);
            Object val = sourceList.get(i);
            if (val == null)
                dest.writeNull();
            else
                context.callback(val, elemType, null);
            context.popElementId();
        }
        dest.writeEnd();
    }

    @Override
    public boolean canHandle(@Nonnull Type type) {
        return (type instanceof ParameterizedType) && ((ParameterizedType)type).getRawType() == List.class;
    }
}
