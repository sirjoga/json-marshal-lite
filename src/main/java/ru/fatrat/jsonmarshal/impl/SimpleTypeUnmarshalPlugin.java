package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;
import ru.fatrat.jsonmarshal.JsonUnmarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

public class SimpleTypeUnmarshalPlugin extends JsonClassUnmarshalPlugin {
    
    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Class<?> destClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        if (source == JsonValue.NULL) return null;
        
        Class<?> primitiveWrapperClass = SimpleTypes.primitiveTypeMap.get(destClass);
        if (primitiveWrapperClass != null) destClass = primitiveWrapperClass;

        if (destClass.equals(Float.class)) return ((Double)((JsonNumber) source).doubleValue()).floatValue();
        if (destClass.equals(Double.class)) return ((JsonNumber) source).doubleValue();
        if (destClass.equals(Boolean.class)) {
            if (source == JsonValue.TRUE) return true;
            if (source == JsonValue.FALSE) return false;
            throw new JsonMarshalException("Cannot read boolean from incompatible JsonValue");
        }
        if (destClass.equals(String.class)) return ((JsonString) source).getString();
        if (destClass.equals(Long.class)) return ((JsonNumber) source).longValueExact();
        if (destClass.equals(Integer.class) || destClass.equals(Short.class) || destClass.equals(Byte.class)) {
            long l = ((JsonNumber) source).longValueExact();
            if (destClass.equals(Byte.class)) {
                byte r = (byte) l;
                if (r!=l) throw new JsonMarshalException(String.format("byte overflow: %d", l));
                return r;
            }
            if (destClass.equals(Integer.class)) {
                int r = (int) l;
                if (r!=l) throw new JsonMarshalException(String.format("int overflow: %d", l));
                return r;
            }
            if (destClass.equals(Short.class)) {
                short r = (short) l;
                if (r!=l) throw new JsonMarshalException(String.format("short overflow: %d", l));
                return r;
            }
            throw new JsonMarshalException("Internal logic error");

        }
        throw new JsonMarshalException("Cannot unmarshal simple type");
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return SimpleTypes.types.contains(cls);
    }
}
