package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;
import ru.fatrat.jsonmarshal.JsonUnmarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonString;
import javax.json.JsonValue;

public class EnumUnmarshalPlugin implements JsonUnmarshalPlugin {

    private final EnumStringer stringer;

    public EnumUnmarshalPlugin(@Nonnull EnumStringer stringer) {
        this.stringer = stringer;
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return cls.isEnum();
    }

    @Nullable
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Class<?> destClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        if (!destClass.isEnum()) throw new JsonMarshalException("Non-enum passed to enum unmarshaller");
        if (!(source instanceof JsonString)) {
            throw new JsonMarshalException("Non-string jsonValue passed to enum unmarshaller");
        }
        String sourceString = ((JsonString) source).getString();
        return stringer.decode(sourceString, (Class<Enum>) destClass);
    }
}
