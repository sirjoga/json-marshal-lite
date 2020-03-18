package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonMarshalPlugin;
import ru.fatrat.jsonmarshal.JsonUnmarshalPlugin;

import javax.annotation.Nonnull;
import java.util.*;

public class StandardJsonUnmarshal extends PluginBasedJsonUnmarshal {

    private static final List<JsonUnmarshalPlugin> BEFORE = Arrays.asList(
            new SimpleTypeUnmarshalPlugin(), new JsonOptionalUnmarshalPlugin(), new ArrayUnmarshalPlugin());
    private static final List<JsonUnmarshalPlugin> AFTER = 
            Collections.singletonList(new ObjectFieldUnmarshalPlugin(new ClassIteratorImpl()));
    
    @Override
    @Nonnull
    final protected Iterator<JsonUnmarshalPlugin> getPlugins() {
        List<JsonUnmarshalPlugin> before = new ArrayList<>(BEFORE);
        List<JsonUnmarshalPlugin> after = new ArrayList<>(AFTER);
        Iterator<JsonUnmarshalPlugin> custom = getCustomPlugins();
        return new Iterator<JsonUnmarshalPlugin>() {
            @Override
            public boolean hasNext() {
                return before.size() > 0 || custom.hasNext() || after.size() > 0;
            }

            @Override
            public JsonUnmarshalPlugin next() {
                if (before.size() > 0) return before.remove(0);
                if (custom.hasNext()) return custom.next();
                if (after.size() > 0) return after.remove(0);
                throw new IllegalStateException();
            }
        };
    }

    protected @Nonnull Iterator<JsonUnmarshalPlugin> getCustomPlugins() {
        return new Iterator<JsonUnmarshalPlugin>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public JsonUnmarshalPlugin next() {
                throw new IllegalStateException();
            }
        };
    }

}
