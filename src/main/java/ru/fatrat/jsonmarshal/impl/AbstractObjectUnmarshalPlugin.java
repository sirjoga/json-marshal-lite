package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractObjectUnmarshalPlugin implements JsonUnmarshalPlugin {

    protected final boolean isFieldRequired(@Nonnull JsonMarshalAnnotationSource annotationSource) {
        return annotationSource.getAnnotation(JsonRequired.class) != null;
    }

    @Nonnull protected final String muteName(@Nonnull String name, @Nonnull JsonMarshalAnnotationSource annotationSource) {
        JsonName jsonName = annotationSource.getAnnotation(JsonName.class);
        if (jsonName == null) return name;
        return jsonName.value();
    }

    @Nullable
    @Override public final Object unmarshal(
            @Nonnull JsonValue source,
            @Nonnull Class<?> destClass,
            @Nullable JsonMarshalAnnotationSource annotationSource,
            @Nonnull JsonUnmarshalContext context
    ) {
        if (source == JsonValue.NULL) return null;
        if (!(source instanceof JsonObject)) throw new JsonMarshalException("Cannot unmarshal JSON as object");
        Object result = createByType(destClass);
        Set<String> handled = new HashSet<>();
        unmarshalTo((JsonObject) source, destClass, result, handled::add, context);
        return result;
    }

    protected abstract void unmarshalTo(
            @Nonnull JsonObject source,
            @Nonnull Class<?> destClass,
            @Nonnull Object dest,
            @Nonnull Consumer<String> handledCallback,
            @Nonnull JsonUnmarshalContext context
    );

    @Override public final boolean canHandle(@Nonnull Class<?> cls) {
        return true;
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
