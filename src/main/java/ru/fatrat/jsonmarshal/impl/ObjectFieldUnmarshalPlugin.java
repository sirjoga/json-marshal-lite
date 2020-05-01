package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.function.Consumer;

public class ObjectFieldUnmarshalPlugin extends AbstractObjectUnmarshalPlugin {

    private final ClassFieldIterator classFieldIterator;
    
    public ObjectFieldUnmarshalPlugin(@Nonnull ClassFieldIterator classFieldIterator) {
        this.classFieldIterator = classFieldIterator;
    }

    @Override
    protected void unmarshalTo(
            @Nonnull JsonObject source, @Nonnull Class<?> destClass, @Nonnull Object dest, @Nonnull Consumer<String> handledCallback,
            @Nonnull JsonUnmarshalContext context
    ) {
        Iterator<Field> fieldIterator = classFieldIterator.fields(destClass);
        while(fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            JsonMarshalAnnotationSource annotationSource = field::getAnnotation;
            field.setAccessible(true);
            String fieldName = muteName(field.getName(), annotationSource);
            JsonValue jValue = source.get(fieldName);
            if (jValue == null) {
                if (isFieldRequired(annotationSource))
                    throw new JsonMarshalException(String.format("Field '%s' is required", fieldName));
                continue;
            }
            context.pushObjectFieldElementId(fieldName);
            handledCallback.accept(fieldName);
            try {
                field.set(dest, context.callback(jValue, field.getGenericType(), annotationSource));
            } catch (IllegalAccessException e) {
                throw new JsonMarshalException("Field set error", e);
            }
            context.popElementId();
        }
    }

}
