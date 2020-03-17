package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonGeneratorHelper;
import ru.fatrat.jsonmarshal.JsonMarshalPlugin;
import ru.fatrat.jsonmarshal.JsonMarshalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SimpleTypeMarshalPlugin implements JsonMarshalPlugin {
    
    private final Map<Class<?>, Class<?>> primitiveTypes;
    
    public SimpleTypeMarshalPlugin() {
        primitiveTypes = new HashMap<>();
        primitiveTypes.put(int.class, Integer.class);
        primitiveTypes.put(float.class, Float.class);
        primitiveTypes.put(double.class, Double.class);
        primitiveTypes.put(long.class, Long.class);
        primitiveTypes.put(short.class, Short.class);
        primitiveTypes.put(byte.class, Byte.class);
        primitiveTypes.put(boolean.class, Boolean.class);
    }            

    @Override
    public boolean marshal(
            @Nonnull Object source, 
            @Nonnull Class<?> sourceClass, 
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    ) {
        JsonGeneratorHelper helper = context.getGeneratorHelper();
        Class<?> primitiveWrapperClass = primitiveTypes.get(sourceClass);
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
            result = false;
        }
        return result;
    }
}
