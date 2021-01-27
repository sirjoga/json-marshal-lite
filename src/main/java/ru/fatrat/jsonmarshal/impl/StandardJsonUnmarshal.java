package ru.fatrat.jsonmarshal.impl;

import ru.fatrat.jsonmarshal.JsonUnmarshal;
import ru.fatrat.jsonmarshal.JsonUnmarshalPlugin;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Pre-composed plugin-based unmarshaller.
 */
public class StandardJsonUnmarshal extends PluginBasedJsonUnmarshal {

    public static final List<JsonUnmarshalPlugin> COMMON = Arrays.asList(
            new SimpleTypeUnmarshalPlugin(), new JsonOptionalUnmarshalPlugin(), new ArrayUnmarshalPlugin(),
            new EnumUnmarshalPlugin(new StandardEnumStringer(StandardEnumStringer.STANDARD_NAME_FUNCTION)),
            new MapUnmarshalPlugin(),
            new WildcardUnmarshalPlugin(),
            new ListUnmarshalPlugin(),
            new RawValueUnmarshalPlugin()
    );
    public static final List<JsonUnmarshalPlugin> FIELD =
            Collections.singletonList(new ObjectFieldUnmarshalPlugin(new ClassFieldIteratorImpl()));

    public static final List<JsonUnmarshalPlugin> INTROSPECT =
            Collections.singletonList(new ObjectIntrospectorUnmarshalPlugin());

    private final List<JsonUnmarshalPlugin> plugins;

    public StandardJsonUnmarshal(@Nonnull List<JsonUnmarshalPlugin> plugins) {
        this.plugins = plugins;
    }

    @Nonnull public static JsonUnmarshal standardUnmarshal() {
        return new StandardJsonUnmarshal(Stream.concat(Stream.concat(COMMON.stream(), FIELD.stream()),
                INTROSPECT.stream()).collect(Collectors.toList()));
    }

    @Override
    @Nonnull
    final protected Iterator<JsonUnmarshalPlugin> getPlugins() {
        return plugins.iterator();
    }

}
