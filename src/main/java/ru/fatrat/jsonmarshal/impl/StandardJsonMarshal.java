package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalPlugin;

import javax.annotation.Nonnull;
import java.util.*;

public class StandardJsonMarshal extends PluginBasedJsonMarshal {

    private static final List<JsonMarshalPlugin> BEFORE = Arrays.asList(
            new SimpleTypeMarshalPlugin(), new ArrayMarshalPlugin(), new JsonOptionalMarshalPlugin(),
            new EnumMarshalPlugin(new StandardEnumStringer(StandardEnumStringer.STANDARD_NAME_FUNCTION)));
    private static final List<JsonMarshalPlugin> AFTER = Collections.singletonList(
            new ObjectFieldMarshalPlugin(new ClassIteratorImpl()));

    @Override
    @Nonnull
    final protected Iterator<JsonMarshalPlugin> getPlugins() {
        List<JsonMarshalPlugin> before = new ArrayList<>(BEFORE);
        List<JsonMarshalPlugin> after = new ArrayList<>(AFTER);
        Iterator<JsonMarshalPlugin> custom = getCustomPlugins();
        return new Iterator<JsonMarshalPlugin>() {
            @Override
            public boolean hasNext() {
                return before.size() > 0 || custom.hasNext() || after.size() > 0;
            }

            @Override
            public JsonMarshalPlugin next() {
                if (before.size() > 0) return before.remove(0);
                if (custom.hasNext()) return custom.next();
                if (after.size() > 0) return after.remove(0);
                throw new IllegalStateException();
            }
        };
    }

    protected @Nonnull Iterator<JsonMarshalPlugin> getCustomPlugins() {
        return new Iterator<JsonMarshalPlugin>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public JsonMarshalPlugin next() {
                throw new IllegalStateException();
            }
        };
    }

}
