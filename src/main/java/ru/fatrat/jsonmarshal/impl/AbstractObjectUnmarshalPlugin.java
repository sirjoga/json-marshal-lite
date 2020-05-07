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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractObjectUnmarshalPlugin extends JsonClassUnmarshalPlugin {

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
        if (Optional.ofNullable(annotationSource)
                .map(aSource -> aSource.getAnnotation(JsonIgnoreGarbage.class)).orElse(null) == null) {
            List<String> garbage = ((JsonObject) source).keySet().stream()
                    .filter(key -> !handled.contains(key)).collect(Collectors.toList());
            if (!garbage.isEmpty())
                throw new JsonMarshalException(String.format("Garbage elements: [%s]",
                        String.join(", ", garbage)));
        }
        return result;
    }

    protected abstract void unmarshalTo(
            @Nonnull JsonObject source,
            @Nonnull Class<?> destClass,
            @Nonnull Object dest,
            @Nonnull Consumer<String> handledCallback,
            @Nonnull JsonUnmarshalContext context
    );


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
