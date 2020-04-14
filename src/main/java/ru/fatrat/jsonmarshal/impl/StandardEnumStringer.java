package ru.fatrat.jsonmarshal.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ru.fatrat.jsonmarshal.JsonMarshalException;
import ru.fatrat.jsonmarshal.JsonName;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.WeakHashMap;
import java.util.function.Function;

public class StandardEnumStringer implements EnumStringer{

    public static final Function<Field, String> STANDARD_NAME_FUNCTION = field -> {
        JsonName jsonName = field.getAnnotation(JsonName.class);
        if (jsonName == null) return field.getName();
        return jsonName.value();
    };

    private final Function<Field, String> nameFunction;

    public StandardEnumStringer(@Nonnull Function<Field, String> nameFunction) {
        this.nameFunction = nameFunction;
    }

    private WeakHashMap<Class<?>, BiMap<String, Object>> maps = new WeakHashMap<>();

    private synchronized BiMap<String, Object> getMap(Class<?> cls) {
        BiMap<String, Object> res = maps.get(cls);
        if (res == null) {
            res = HashBiMap.create();
            for (Field f: cls.getDeclaredFields()) {
                if (!f.isEnumConstant()) continue;
                f.setAccessible(true);
                String s = nameFunction.apply(f);
                try {
                    res.put(s, f.get(cls));
                } catch (IllegalAccessException e) {
                    throw new JsonMarshalException("Enum access error");
                }
            }
            maps.put(cls, res);
        }
        return res;
    }

    @Nonnull
    @Override
    public <T extends Enum<T>> T decode(@Nonnull String source, @Nonnull Class<T> type) {
        BiMap<String, Object> m = getMap(type);
        @SuppressWarnings("unchecked")
        T res = (T) m.get(source);
        if (res == null) throw new JsonMarshalException(String.format("Illegal enum constant: %s", source));
        return res;
    }

    @Nonnull
    @Override
    public String encode(@Nonnull Enum<?> source) {
        BiMap<String, Object> m = getMap(source.getClass());
        String s = m.inverse().get(source);
        if (s == null) throw new JsonMarshalException("Invalid enum element");
        return s;
    }
}
