package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;

import javax.annotation.Nonnull;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 *  Unmarshalling plugin for data classes (introspector accessors).
 */
public class ObjectIntrospectorUnmarshalPlugin extends AbstractObjectUnmarshalPlugin {

    @Override
    protected void unmarshalTo(
            @Nonnull JsonObject source,
            @Nonnull Class<?> destClass,
            @Nonnull Object dest,
            @Nonnull Consumer<String> handledCallback,
            @Nonnull JsonUnmarshalContext context
    ) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(destClass);
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                Method readMethod = descriptor.getReadMethod();
                Method writeMethod = descriptor.getWriteMethod();
                if (readMethod == null || writeMethod == null) continue;
                JsonMarshalAnnotationSource annotationSource = readMethod::getAnnotation;
                String name = muteName(descriptor.getName(), annotationSource);

                if (writeMethod.getDeclaringClass() == Object.class) continue;
                JsonValue jValue = source.get(name);
                if (jValue == null) {
                    if (isFieldRequired(annotationSource))
                        throw new JsonMarshalException(String.format("Field '%s' is required", name));
                    continue;
                }
                context.pushObjectFieldElementId(name);
                handledCallback.accept(name);
                try {
                    writeMethod.invoke(dest, context.callback(jValue, descriptor.getReadMethod().getGenericReturnType(),
                            annotationSource));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new JsonMarshalException("Field set error", e);
                }
                context.popElementId();
            }
        } catch (IntrospectionException e) {
            throw new JsonMarshalException("Reflection call error", e);
        }
    }

    @Override
    protected boolean canHandle(@Nonnull Class<?> cls) {
        return true;
    }
}
