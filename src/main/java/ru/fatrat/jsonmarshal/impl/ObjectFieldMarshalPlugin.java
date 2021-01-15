package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Marshalling plugin for data classes (field accessors).
 */
public class ObjectFieldMarshalPlugin extends JsonClassMarshalPlugin {

    private final ClassFieldIterator classFieldIterator;

    public ObjectFieldMarshalPlugin(@Nonnull ClassFieldIterator classFieldIterator) {
        this.classFieldIterator = classFieldIterator;
    }

    @Override
    public void marshal(
            @Nonnull Object source,
            @Nonnull Class<?> sourceClass,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonMarshalContext context
    ) {
        Iterator<Field> fieldIterator = classFieldIterator.fields(sourceClass);
        JsonGeneratorHelper helper = context.getGeneratorHelper();
        helper.writeStartObject();
        while(fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(source);
            } catch (IllegalAccessException e) {
                throw new JsonMarshalException("Field get value error", e);
            }
            if (value == null) continue;
            String fieldName = field.getName();
            context.pushObjectFieldElementId(fieldName);
            helper.setName(fieldName);
            context.callback(value, field.getGenericType(), field::getAnnotation);
            context.popElementId();
        }
        helper.writeEnd();
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return cls.getAnnotation(JsonByFields.class) != null;
    }
}
