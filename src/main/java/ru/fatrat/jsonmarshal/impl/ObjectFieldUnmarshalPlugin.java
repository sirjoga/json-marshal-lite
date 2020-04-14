package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

public class ObjectFieldUnmarshalPlugin implements JsonUnmarshalPlugin {

    private final ClassFieldIterator classFieldIterator;
    
    public ObjectFieldUnmarshalPlugin(@Nonnull ClassFieldIterator classFieldIterator) {
        this.classFieldIterator = classFieldIterator;
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return true;
    }
    
    @Nullable
    @Override
    public Object unmarshal(@Nonnull JsonValue source, @Nonnull Class<?> destClass, @Nullable JsonMarshalAnnotationSource annotationSource, @Nonnull JsonUnmarshalContext context) {
        if (source == JsonValue.NULL) return null;
        if (!(source instanceof JsonObject)) throw new JsonMarshalException("Cannot unmarshal JSON as array");
        JsonObject object = (JsonObject) source;
        Iterator<Field> fieldIterator = classFieldIterator.fields(destClass);
        Object result = createByType(destClass);        
        while(fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            String fieldName = field.getName();
            JsonValue jValue = object.get(fieldName);
            if (jValue == null) continue;
            context.pushObjectFieldElementId(fieldName);
            try {
                field.set(result, context.callback(jValue, field.getType(), field::getAnnotation));
            } catch (IllegalAccessException e) {
                throw new JsonMarshalException("Field set error", e);
            }
            context.popElementId();
        }
        return result;
        
    }

    @Nonnull
    private static Object createByType(@Nonnull Class<?> type) throws JsonException {
        try {
            Constructor<?> cons = type.getDeclaredConstructor();
            cons.setAccessible(true);
            return cons.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new JsonMarshalException(String.format("Reflection call error: %s", type.getName()), e);
        }
    }
    
    
}
