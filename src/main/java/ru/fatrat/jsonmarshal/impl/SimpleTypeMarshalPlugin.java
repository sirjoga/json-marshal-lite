package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleTypeMarshalPlugin extends JsonClassMarshalPlugin {
    
    @Override
    public void marshal(
            @Nonnull Object source, 
            @Nonnull Class<?> sourceClass, 
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    ) {
        JsonGeneratorHelper helper = context.getGeneratorHelper();
        Class<?> primitiveWrapperClass = SimpleTypes.primitiveTypeMap.get(sourceClass);
        if (primitiveWrapperClass != null) sourceClass = primitiveWrapperClass;
        
        boolean result = true;
        if (Long.class.equals(sourceClass)) {
            helper.writeInt((Long) source);
        } else if (Integer.class.equals(sourceClass)) {
            helper.writeInt((Integer) source);
        } else if (Short.class.equals(sourceClass)) {
            helper.writeInt((Short) source);
        } else if (Byte.class.equals(sourceClass)) {
            helper.writeInt((Byte) source);
        } else if (Float.class.equals(sourceClass)) {
            helper.writeFloat((Float) source);
        } else if (Double.class.equals(sourceClass)) {
            helper.writeFloat((Double) source);
        } else if (Boolean.class.equals(sourceClass)) {
            helper.writeBoolean((Boolean) source);
        } else if (String.class.equals(sourceClass)) {
            helper.writeString((String) source);
        } else {
            throw new JsonMarshalException("Cannot marshal non-simple types");
        }
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return SimpleTypes.types.contains(cls);
    }
}
