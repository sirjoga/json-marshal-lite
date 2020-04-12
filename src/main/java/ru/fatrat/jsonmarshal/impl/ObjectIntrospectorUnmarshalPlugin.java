package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;
import ru.fatrat.jsonmarshal.JsonUnmarshalPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class ObjectIntrospectorUnmarshalPlugin implements JsonUnmarshalPlugin {

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
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(destClass);

            Object result = createByType(destClass);
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                String name = descriptor.getName();
                Method readMethod = descriptor.getReadMethod();
                Method writeMethod = descriptor.getWriteMethod();
                if (readMethod == null || writeMethod == null) continue;
                if (writeMethod.getDeclaringClass() == Object.class) continue;
                JsonValue jValue = object.get(name);
                if (jValue == null) continue;
                context.pushObjectFieldElementId(name);
                try {
                    writeMethod.invoke(result, context.callback(jValue, descriptor.getPropertyType(),
                            readMethod::getAnnotation));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new JsonMarshalException("Field set error", e);
                }
                context.popElementId();
            }
            return result;
        } catch (IntrospectionException e) {
            throw new JsonMarshalException("Reflection call error", e);
        }
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
