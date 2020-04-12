package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectIntrospectorMarshalPlugin implements JsonMarshalPlugin {

    @Override public void marshal(
            @Nonnull Object source,
            @Nonnull Class<?> sourceClass,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    ) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(sourceClass);
            JsonGeneratorHelper helper = context.getGeneratorHelper();
            helper.writeStartObject();
            for (PropertyDescriptor descriptor: beanInfo.getPropertyDescriptors()) {
                String name = descriptor.getName();
                Method method = descriptor.getReadMethod();
                if (method == null) continue;
                if (method.getDeclaringClass() == Object.class) continue;
                Class<?> clz = descriptor.getPropertyType();
                Object value = method.invoke(source);
                if (value == null) continue;
                helper.setName(name);
                context.pushObjectFieldElementId(name);
                context.callback(value, clz, method::getAnnotation);
                context.popElementId();
            }
            helper.writeEnd();
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonMarshalException("Reflection call error", e);
        }
    }

    @Override public boolean canHandle(@Nonnull Class<?> cls) { return true; }
}
