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
import java.lang.reflect.Type;

/**
 *  Marshalling plugin for data classes (introspector accessors).
 */
public class ObjectIntrospectorMarshalPlugin extends JsonClassMarshalPlugin {

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
                Type typ = descriptor.getReadMethod().getGenericReturnType();
                Object value = method.invoke(source);
                if (value == null) continue;
                helper.setName(name);
                context.pushObjectFieldElementId(name);
                context.callback(value, typ, method::getAnnotation);
                context.popElementId();
            }
            helper.writeEnd();
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonMarshalException("Reflection call error", e);
        }
    }

    @Override public boolean canHandle(@Nonnull Class<?> cls) { return true; }
}
