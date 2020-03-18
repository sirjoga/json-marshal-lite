package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalContext;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnumMarshalPlugin implements JsonMarshalPlugin {

    private final EnumStringer stringer;

    public EnumMarshalPlugin(@Nonnull EnumStringer stringer) {
        this.stringer = stringer;
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return cls.isEnum();
    }

    @Override
    public void marshal(@Nonnull Object source, @Nonnull Class<?> sourceClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonMarshalContext context) {
        if (!sourceClass.isEnum()) throw new JsonMarshalException("Non-enum passed to enum marshaller");
        @SuppressWarnings({"rawtypes", "unchecked"})
        Class<Enum> eClass = (Class<Enum>) sourceClass;
        context.getGeneratorHelper().writeString(stringer.encode((Enum<?>)source));
    }
}
