package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonCustomUnmarshallable;
import ru.fatrat.jsonmarshal.JsonMarshalAnnotationSource;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonUnmarshalContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Custom unmarshal with JsonValue constructor argument
 */
public class CustomUnmarshalPlugin extends JsonClassUnmarshalPlugin {

    @Nullable
    @Override
    public Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<?> destClass,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonUnmarshalContext context
    ) {
        try {
            Constructor<?> constructor = destClass.getDeclaredConstructor(JsonValue.class);
            constructor.setAccessible(true);
            return constructor.newInstance(source);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonMarshalException(String.format("Reflection call error: %s", destClass.getName()), e);
        }
    }

    @Override
    public boolean canHandle(@Nonnull Class<?> cls) {
        return JsonCustomUnmarshallable.class.isAssignableFrom(cls);
    }
}
